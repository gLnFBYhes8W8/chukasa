package pro.hirooka.chukasa.domain.service.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.TunerStatus;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.common.ICommonUtilityService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.common.ITunerManagementService;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@Component
public class RecorderRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RecorderRunner.class);

    @Autowired
    private SystemConfiguration systemConfiguration;
    @Autowired
    private ICommonUtilityService commonUtilityService;
    @Autowired
    private ITunerManagementService tunerManagementService;
    @Autowired
    private ISystemService systemService;

    private ReservedProgram reservedProgram;

    @Override
    public void run() {

        final int physicalLogicalChannel = reservedProgram.getPhysicalLogicalChannel();
        final long startRecording = reservedProgram.getStartRecording();
        final long stopRecording = reservedProgram.getStopRecording();
        final long duration = reservedProgram.getRecordingDuration();
        final long thumbnailPoint = duration / 3;
        final String title = reservedProgram.getTitle();
        final String fileName = reservedProgram.getFileName();

        log.info("start recording... [{}] {}", physicalLogicalChannel, title);

        long now = new Date().getTime();

        // start recording immediately
        // Create do-record.sh (do-record_ch_yyyyMMdd_yyyyMMdd.sh)
        final String doRecordFileName = "do-record_" + physicalLogicalChannel + "_" + startRecording + "_" + stopRecording + ".sh";
        final List<ChannelConfiguration> channelConfigurationList = commonUtilityService.getChannelConfigurationList();
        final ChannelType channelType = commonUtilityService.getChannelType(physicalLogicalChannel);
        TunerStatus tunerStatus = tunerManagementService.findOne(channelType);
        if(tunerStatus != null) {
            tunerStatus = tunerManagementService.update(tunerStatus, false);
        }else{
            // TODO: priority
            log.warn("Tuner for recording is not available.");
            return;
        }
        final String DEVICE_OPTION = tunerManagementService.getDeviceOption();
        final String DEVICE_ARGUMENT = tunerManagementService.getDeviceArgument(tunerStatus);
        try{
            final File doRecordFile = new File(systemConfiguration.getFilePath() + FILE_SEPARATOR + doRecordFileName);
            log.info("doRecordFile: {}", doRecordFileName);
            if (!doRecordFile.exists()) {
                doRecordFile.createNewFile();
                final BufferedWriter bw = new BufferedWriter(new FileWriter(doRecordFile));

                bw.write("#!/bin/bash");
                bw.newLine();

                final String commandRecording = systemConfiguration.getRecxxxPath() + " " + DEVICE_OPTION + " "
                        + DEVICE_ARGUMENT + " " + physicalLogicalChannel + " " + duration + " \""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + "\"" + " >/dev/null";
                log.info(commandRecording);
                bw.write(commandRecording);
                bw.newLine();

                // TODO: separate sh into recoding and transcoding
                final String commandThumbnail = systemConfiguration.getFfmpegPath() +  " -i " + "\""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + "\"" + " -ss "
                        + thumbnailPoint + " -vframes 1 -f image2 " + "\"" + systemConfiguration.getFilePath()
                        + FILE_SEPARATOR + fileName + ".jpg\"" + " >/dev/null";
                log.info(commandThumbnail);
                bw.write(commandThumbnail);
                bw.newLine();

                final FfmpegVcodecType ffmpegVcodecType = systemService.getFfmpegVcodecType();
                final String SPECIFIC_OPTIONS;
                if(ffmpegVcodecType == FfmpegVcodecType.H264_QSV){
                    SPECIFIC_OPTIONS = "h264_qsv";
                }else if(ffmpegVcodecType == FfmpegVcodecType.H264_NVENC){
                    SPECIFIC_OPTIONS = "h264_nvenc";
                }else if(ffmpegVcodecType == FfmpegVcodecType.H264_OMX){
                    SPECIFIC_OPTIONS = "h264_omx";
                }else if(ffmpegVcodecType == FfmpegVcodecType.H264_X264){
                    SPECIFIC_OPTIONS = "libx264";
                }else{
                    SPECIFIC_OPTIONS = "";
                }
                // TODO: separate sh into recoding and transcoding
                final String commandTranscodingM4v = systemConfiguration.getFfmpegPath() + " -i " + "\""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + "\""
                        + " -acodec aac -ab 160k -ar 44100 -ac 2 -s 1280x720 -vcodec " + SPECIFIC_OPTIONS
                        + " -profile:v high -level 4.2 -b:v 2400k -threads 1 -y " + "\""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + ".m4v\"" + " >/dev/null";
                log.info(commandTranscodingM4v);
                bw.write(commandTranscodingM4v);
                bw.newLine();

                final String commandTranscodingWatchM4v = systemConfiguration.getFfmpegPath() + " -i " + "\""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + "\""
                        + " -acodec aac -ab 32k -ar 44100 -ac 2 -s 320x180 -vcodec " + SPECIFIC_OPTIONS
                        + " -profile:v high -level 4.1 -b:v 160k -threads 1 -y " + "\""
                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + ".watch.m4v\"" + " >/dev/null";
                log.info(commandTranscodingWatchM4v);
                bw.write(commandTranscodingWatchM4v);
                bw.newLine();

                //final String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
                final String encodedFileName = UUID.randomUUID().toString();
                final String hlsDirectory = systemConfiguration.getFilePath() + FILE_SEPARATOR + "hls"
                        + FILE_SEPARATOR + encodedFileName + FILE_SEPARATOR + "640x360-1200-128";
                log.info(hlsDirectory);
                final String commandCreateHlsDirectory = "mkdir -p " + hlsDirectory;
                log.info(commandCreateHlsDirectory);
                bw.write(commandCreateHlsDirectory);
                bw.newLine();

                // TODO: -> transcoding service
//                final String commandTranscodingHls = systemConfiguration.getFfmpegPath() + " -i " + "\""
//                        + systemConfiguration.getFilePath() + FILE_SEPARATOR + fileName + "\""
//                        + " -acodec aac -ab 128k -ar 48000 -ac 2 -s 640x360 -vcodec h264_nvenc -vf yadif -g 10 -b:v 1200k -threads 1 -f hls -hls_time 2 "
//                        + hlsDirectory + FILE_SEPARATOR + "chukasa.m3u8";
//                log.info(commandTranscodingHls);
//                bw.write(commandTranscodingHls);
                bw.close();
            }

            final String[] chmod = {"chmod", "755", systemConfiguration.getFilePath() + FILE_SEPARATOR + doRecordFileName};
            final ProcessBuilder chmodProcessBuilder = new ProcessBuilder(chmod);
            final Process chmodProcess = chmodProcessBuilder.start();
            final InputStream chmodInputStream = chmodProcess.getErrorStream();
            final InputStreamReader chmodInputStreamReader = new InputStreamReader(chmodInputStream);
            final BufferedReader chmodBufferedReader = new BufferedReader(chmodInputStreamReader);
            String chmodString = "";
            while ((chmodString = chmodBufferedReader.readLine()) != null){
                log.info(chmodString);
            }
            chmodBufferedReader.close();
            chmodInputStreamReader.close();
            chmodInputStream.close();
            chmodProcess.destroy();

            final String[] run = {systemConfiguration.getFilePath() + FILE_SEPARATOR + doRecordFileName};
            final ProcessBuilder runProcessBuilder = new ProcessBuilder(run);
            final Process runProcess = runProcessBuilder.start();
            final InputStream runInputStream = runProcess.getErrorStream();
            final InputStreamReader runInputStreamReader = new InputStreamReader(runInputStream);
            final BufferedReader runBufferedReader = new BufferedReader(runInputStreamReader);
            String runString = "";
            while ((runString = runBufferedReader.readLine()) != null){
                log.info(runString);
            }
            runBufferedReader.close();
            runInputStreamReader.close();
            runInputStream.close();
            runProcess.destroy();
            log.info("recording is done.");

            doRecordFile.delete();

        }catch(IOException e){
            log.error("cannot run do-record.sh: {} {}", e.getMessage(), e);
            tunerManagementService.update(tunerStatus, true);
            return;
        }

        tunerManagementService.update(tunerStatus, true);
    }

    public ReservedProgram getReservedProgram() {
        return reservedProgram;
    }

    public void setReservedProgram(ReservedProgram reservedProgram) {
        this.reservedProgram = reservedProgram;
    }
}

