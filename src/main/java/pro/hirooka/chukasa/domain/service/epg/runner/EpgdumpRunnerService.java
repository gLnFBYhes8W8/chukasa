//package pro.hirooka.chukasa.domain.service.epg.runner;
//
//import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.AsyncResult;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.stereotype.Service;
//import pro.hirooka.chukasa.domain.config.ChukasaConstants;
//import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
//import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
//import pro.hirooka.chukasa.domain.config.epg.EpgConfiguration;
//import pro.hirooka.chukasa.domain.model.epg.ChannelConfiguration;
//import pro.hirooka.chukasa.domain.model.epg.TunerStatus;
//import pro.hirooka.chukasa.domain.model.epg.LastEpgdumpExecuted;
//import pro.hirooka.chukasa.domain.service.common.ITunerManagementService;
//import pro.hirooka.chukasa.domain.service.epg.ILastEpgdumpExecutedService;
//import pro.hirooka.chukasa.domain.service.epg.parser.IEpgdumpParser;
//
//import java.io.*;
//import java.util.Date;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.Future;
//
//@EnableAsync
//@Service
//public class EpgdumpRunnerService implements IEpgdumpRunnerService {
//
//    private static final Logger log = LoggerFactory.getLogger(EpgdumpRunnerService.class);
//
//    private final String FILE_SEPARATOR = ChukasaConstants.FILE_SEPARATOR;
//
//    @Autowired
//    private EpgConfiguration epgConfiguration;
//    @Autowired
//    private SystemConfiguration systemConfiguration;
//    @Autowired
//    private IEpgdumpParser epgdumpParser;
//    @Autowired
//    private ILastEpgdumpExecutedService lastEpgdumpExecutedService;
//    @Autowired
//    private ITunerManagementService tunerManagementService;
//
//    @Async
//    @Override
//    public Future<Integer> submit(List<ChannelConfiguration> channelConfigurationList) {
//
//        // TODO: null
//        TunerStatus tunerStatusGR = tunerManagementService.findOne(ChannelType.GR);
//        if(tunerStatusGR != null){
//            tunerStatusGR = tunerManagementService.update(tunerStatusGR, false);
//        }
//        TunerStatus tunerStatusBS = tunerManagementService.findOne(ChannelType.BS);
//        if(tunerStatusGR != null){
//            tunerStatusBS = tunerManagementService.update(tunerStatusBS, false);
//        }
//
//        final File temporaryEpgdumpPathFile = new File(epgConfiguration.getEpgdumpTemporaryPath());
//
//        cleanupTemporaryEpgdumpPath(temporaryEpgdumpPathFile);
//        if(temporaryEpgdumpPathFile.mkdirs()){
//            log.info("epgdump temporary path: {}", temporaryEpgdumpPathFile);
//        }else{
//            log.error("cannot create epgdump temporary path: {}", temporaryEpgdumpPathFile);
//            if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//            if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//            return new AsyncResult<>(-1);
//        }
//        final String epgdumpShellPath = epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump.sh";
//
//        final File epgdumpShellFile = new File(epgdumpShellPath);
//        try {
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(epgdumpShellFile));
//            bufferedWriter.write("#!/bin/bash");
//            bufferedWriter.newLine();
//            final String DEVICE_OPTION = tunerManagementService.getDeviceOption();
//            final Set<Integer> epgdumpChannelSet = new LinkedHashSet<>();
//            boolean isBS = false;
//            for(ChannelConfiguration channelConfiguration : channelConfigurationList){
//                if(channelConfiguration.getChannelType() == ChannelType.GR || !isBS) {
//                    try {
//                        final int physicalLogicalChannel = channelConfiguration.getPhysicalLogicalChannel();
//                        final String recxxxCommand;
//                        if(channelConfiguration.getChannelType() == ChannelType.GR) {
//                            final String DEVICE_ARGUMENT = tunerManagementService.getDeviceArgument(tunerStatusGR);
//                            recxxxCommand = systemConfiguration.getRecxxxPath() + " " + DEVICE_OPTION + " " + DEVICE_ARGUMENT + " " + physicalLogicalChannel + " " + epgConfiguration.getEpgdumpRecordingDuration() + " " + epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump" + physicalLogicalChannel + ".ts";
//                        }else if(channelConfiguration.getChannelType() == ChannelType.BS){
//                            final String DEVICE_ARGUMENT = tunerManagementService.getDeviceArgument(tunerStatusBS);
//                            recxxxCommand = systemConfiguration.getRecxxxPath() + " " + DEVICE_OPTION + " " + DEVICE_ARGUMENT + " " + physicalLogicalChannel + " " + epgConfiguration.getEpgdumpRecordingDuration() + " " + epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump" + physicalLogicalChannel + ".ts";
//                        }else{
//                            log.error("unknown ChannelType");
//                            if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//                            if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//                            return new AsyncResult<>(-1);
//                        }
//                        final String epgdumpCommand = systemConfiguration.getEpgdumpPath() + " json " + epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump" + physicalLogicalChannel + ".ts " + epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump" + physicalLogicalChannel + ".json";
//                        bufferedWriter.write(recxxxCommand);
//                        bufferedWriter.newLine();
//                        bufferedWriter.write(epgdumpCommand);
//                        bufferedWriter.newLine();
//                        if(channelConfiguration.getChannelType() == ChannelType.BS){
//                            isBS = true;
//                        }
//                        epgdumpChannelSet.add(channelConfiguration.getPhysicalLogicalChannel());
//                    } catch (NumberFormatException e) {
//                        log.error("invalid value", e.getMessage(), e);
//                    }
//                }
//            }
//            bufferedWriter.close();
//
//            final String[] chmodCommandArray = {"chmod", "755", epgdumpShellPath};
//            executeCommand(chmodCommandArray);
//
//            final long begin = System.currentTimeMillis();
//
//            final String[] epgdumpCommandArray = {epgdumpShellPath};
//            executeCommand(epgdumpCommandArray);
//
//            for(ChannelConfiguration channelConfiguration : channelConfigurationList) {
//                if(epgdumpChannelSet.contains(channelConfiguration.getPhysicalLogicalChannel())) {
//                    final String jsonStringPath = epgConfiguration.getEpgdumpTemporaryPath() + FILE_SEPARATOR + "epgdump" + channelConfiguration.getPhysicalLogicalChannel() + ".json";
//                    if (new File(jsonStringPath).exists() && new File(jsonStringPath).length() > 0) {
//                        try {
//                            epgdumpParser.parse(jsonStringPath, channelConfiguration.getPhysicalLogicalChannel(), channelConfiguration.getRemoteControllerChannel());
//                        } catch (IOException e) {
//                            log.error("cannot parse epgdump output: {} {}", e.getMessage(), e);
//                            if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//                            if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//                            return new AsyncResult<>(-1);
//                        }
//                    } else {
//                        log.error("no epgdump output JSON file: {}", jsonStringPath);
//                        if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//                        if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//                        return new AsyncResult<>(-1);
//                    }
//                }
//            }
//
//            final long end = System.currentTimeMillis();
//            log.info((end - begin) / 1000 + "s");
//
//            final LastEpgdumpExecuted previousLastEpgdumpExecuted = lastEpgdumpExecutedService.read(1);
//            final LastEpgdumpExecuted newLastEpgdumpExecuted;
//            final Date date = new Date();
//            if (previousLastEpgdumpExecuted == null) {
//                newLastEpgdumpExecuted = new LastEpgdumpExecuted();
//                newLastEpgdumpExecuted.setDate(date.getTime());
//                newLastEpgdumpExecuted.setUnique(1);
//            }else{
//                previousLastEpgdumpExecuted.setDate(date.getTime());
//                newLastEpgdumpExecuted = previousLastEpgdumpExecuted;
//            }
//            lastEpgdumpExecutedService.update(newLastEpgdumpExecuted);
//            log.info("lastEpgdumpExecuted = {}", newLastEpgdumpExecuted.getDate());
//
//            cleanupTemporaryEpgdumpPath(temporaryEpgdumpPathFile);
//
//            if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//            if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//            return new AsyncResult<>(0);
//
//        } catch (IOException e) {
//            log.error("{} {}", e.getMessage(), e);
//            if(tunerStatusGR != null) releaseTuner(tunerStatusGR);
//            if(tunerStatusBS != null) releaseTuner(tunerStatusBS);
//            return new AsyncResult<>(-1);
//        }
//    }
//
//    @Override
//    public void cancel() {
//
//    }
//
//    private void executeCommand(String[] commandArray){
//        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
//        try {
//            final Process process = processBuilder.start();
//            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String s = "";
//            while((s = bufferedReader.readLine()) != null){
//                log.info("{}", s);
//            }
//            bufferedReader.close();
//            process.destroy();
//        } catch (IOException e) {
//            log.error("{} {}", e.getMessage(), e);
//        }
//    }
//
//    private void releaseTuner(TunerStatus tunerStatus){
//        tunerManagementService.update(tunerStatus, true);
//    }
//
//    private boolean cleanupTemporaryEpgdumpPath(File file){
//        try {
//            if(file.exists()) {
//                FileUtils.cleanDirectory(file);
//                if (file.delete()) {
//                    return true;
//                } else {
//                    log.info("cannot delete temporary epgdump path: {}", file.getAbsolutePath());
//                }
//            }
//        } catch (IOException e) {
//            log.info("cannot clean temporary epgdump path:{} {}", e.getMessage(), e);
//        }
//        return false;
//    }
//}
//
