//package pro.hirooka.chukasa.domain.service.common;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import pro.hirooka.chukasa.domain.config.ChukasaConstants;
//import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
//import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
//import pro.hirooka.chukasa.domain.config.common.type.RecxxxDriverType;
//import pro.hirooka.chukasa.domain.config.common.type.TunerUseType;
//import pro.hirooka.chukasa.domain.model.epg.ChannelConfiguration;
//import pro.hirooka.chukasa.domain.model.epg.Tuner;
//import pro.hirooka.chukasa.domain.model.epg.TunerStatus;
//
//import javax.annotation.PostConstruct;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//@Service
//public class TunerManagementService implements ITunerManagementService {
//
//    private static final Logger log = LoggerFactory.getLogger(TunerManagementService.class);
//
//    final String DVB_DEVICE = ChukasaConstants.DVB_DEVICE;
//    final String CHADEV_DEVICE = ChukasaConstants.CHARACTER_DEVICE;
//
//    private List<TunerStatus> tunerStatusList = new ArrayList<>(); //Collections.synchronizedList(new ArrayList<>());
//
//    @Autowired
//    private SystemConfiguration systemConfiguration;
//    @Autowired
//    private CommonUtilityService commonUtilityService;
//
//    @PostConstruct
//    void init(){
//        List<Tuner> tunerList = commonUtilityService.getTunerList();
//        for(Tuner tuner : tunerList){
//            TunerStatus tunerStatus = new TunerStatus();
//            tunerStatus.setChannelType(tuner.getChannelType());
//            tunerStatus.setDeviceName(tuner.getDeviceName());
//            tunerStatus.setCanUse(true);
//            tunerStatus.setIndex(Integer.parseInt(tuner.getDeviceName().substring(tuner.getDeviceName().length() - 1)));
//            if(tuner.getDeviceName().startsWith(DVB_DEVICE)){
//                tunerStatus.setRecxxxDriverType(RecxxxDriverType.DVB);
//            }else if(tuner.getDeviceName().startsWith(CHADEV_DEVICE)){
//                tunerStatus.setRecxxxDriverType(RecxxxDriverType.CHARDEV);
//            }
//            tunerStatusList.add(tunerStatus);
//        }
//        log.info("tunerStatusList = {}", tunerStatusList.toString());
//    }
//
//    @Override
//    public TunerStatus create(TunerStatus tunerStatus) {
//        return null;
//    }
//
//    @Override
//    public List<TunerStatus> get() {
//        return tunerStatusList;
//    }
//
//    @Override
//    public List<TunerStatus> get(ChannelType channelType) {
//        Predicate<TunerStatus> predicate = tunerStatus -> tunerStatus.getChannelType() == channelType;
//        return tunerStatusList.stream().filter(predicate).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<TunerStatus> available(ChannelType channelType) {
//        Predicate<TunerStatus> predicate = tunerStatus -> tunerStatus.getChannelType() == channelType && tunerStatus.isCanUse();
//        List<TunerStatus> availableTunerStatusList = tunerStatusList.stream().filter(predicate).collect(Collectors.toList());
//        return availableTunerStatusList;
//    }
//
//    @Override
//    public TunerStatus findOne(ChannelType channelType) {
//        List<TunerStatus> tunerStatusList = available(channelType);
//        return tunerStatusList.stream().findFirst().orElse(null);
//    }
//
//    @Override
//    public TunerStatus get(String deviceName) {
//        return null;
//    }
//
//    @Override
//    public TunerStatus update(TunerStatus tunerStatus) {
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getChannelType() == tunerStatus.getChannelType()
//                    && tunerStatusList.get(i).getDeviceName().equals(tunerStatus.getDeviceName())) {
//                tunerStatusList.get(i).setCanUse(tunerStatus.isCanUse());
//            }
//        }
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getChannelType() == tunerStatus.getChannelType()
//                    && tunerStatusList.get(i).getDeviceName().equals(tunerStatus.getDeviceName())) {
//                return tunerStatusList.get(i);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public TunerStatus update(TunerStatus tunerStatus, boolean canUse) {
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getChannelType() == tunerStatus.getChannelType()
//                    && tunerStatusList.get(i).getDeviceName().equals(tunerStatus.getDeviceName())) {
//                tunerStatusList.get(i).setCanUse(canUse);
//            }
//        }
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getChannelType() == tunerStatus.getChannelType()
//                    && tunerStatusList.get(i).getDeviceName().equals(tunerStatus.getDeviceName())) {
//                return tunerStatusList.get(i);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public TunerStatus update(String deviceName, boolean canUse) {
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getDeviceName().equals(deviceName)) {
//                tunerStatusList.get(i).setCanUse(canUse);
//            }
//        }
//        for(int i = 0; i < tunerStatusList.size(); i++) {
//            if (tunerStatusList.get(i).getDeviceName().equals(deviceName)) {
//                return tunerStatusList.get(i);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public RecxxxDriverType getRecxxxDriverType() {
//        for(TunerStatus tunerStatus : tunerStatusList){
//            return tunerStatus.getRecxxxDriverType(); // TODO:
//        }
//        return null;
//    }
//
//    @Override
//    public String getDeviceOption() {
//        final RecxxxDriverType recxxxDriverType = getRecxxxDriverType();
//        if(recxxxDriverType == RecxxxDriverType.DVB){
//            return "--dev";
//        }else{
//            return "--device";
//        }
//    }
//
//    @Override
//    public String getDeviceArgument(TunerStatus tunerStatus) {
//        final RecxxxDriverType recxxxDriverType = getRecxxxDriverType();
//        if(recxxxDriverType == RecxxxDriverType.DVB){
//            return Integer.toString(tunerStatus.getIndex());
//        }else{
//            return tunerStatus.getDeviceName();
//        }
//    }
//
//    @Override
//    public String getDeviceArgument(TunerUseType tunerUseType, int physicalLogicalChannel, List<ChannelConfiguration> channelConfigurationList) {
//        TunerStatus tunerStatus = null;
//        for(ChannelConfiguration channelConfiguration : channelConfigurationList){
//            if(channelConfiguration.getPhysicalLogicalChannel() == physicalLogicalChannel){
//                if(channelConfiguration.getChannelType() == ChannelType.GR){
//                    tunerStatus = findOne(ChannelType.GR);
//                    tunerStatus.setTunerUseType(tunerUseType);
//                    //update(tunerStatus, false); // TODO:
//                }else if(channelConfiguration.getChannelType() == ChannelType.BS){
//                    tunerStatus = findOne(ChannelType.BS);
//                    tunerStatus.setTunerUseType(tunerUseType);
//                    //update(tunerStatus, false);
//                }
//            }
//        }
//        if(tunerStatus != null){
//            return getDeviceArgument(tunerStatus);
//        }
//        return null;
//    }
//
//    @Override
//    public void releaseAll() {
//        // TODO: common process killer
//        final String[] commandArray = {"ps", "aux"};
//        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
//        try {
//            final Process process = processBuilder.start();
//            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String str;
//            while((str = bufferedReader.readLine()) != null){
//                log.debug("{}", str);
//                final String trimmedString = str.trim();
//                if(trimmedString.contains(systemConfiguration.getRecxxxPath())){
//                    final String[] trimmedStringArray = trimmedString.split(" ");
//                    final List<String> pidList = new ArrayList<>();
//                    for(int i = 0; i < trimmedStringArray.length; i++) {
//                        if (!(trimmedStringArray[i].equals(""))) {
//                            pidList.add(trimmedStringArray[i]);
//                        }
//                    }
//                    String pid = pidList.get(1);
//                    log.debug("{}", pid);
//                    stopPID(pid);
//                }
//            }
//            bufferedReader.close();
//            process.getInputStream().close();
//            process.getErrorStream().close();
//            process.getOutputStream().close();
//            process.destroy();
//            tunerStatusList.forEach(tunerStatus -> {
//                update(tunerStatus, true);
//            });
//        } catch (IOException e) {
//            // TODO:
//        }
//    }
//
//    private void stopPID(String pid){
//
//        final String[] commandArray = {"kill", "-KILL", pid };
//        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
//        try {
//            final Process process = processBuilder.start();
//            log.info("{} stopped ffmpeg (PID: {}).", this.getClass().getName(), pid);
//        } catch (IOException e) {
//            log.error("{} {}", e.getMessage(), e);
//        }
//    }
//}
//
