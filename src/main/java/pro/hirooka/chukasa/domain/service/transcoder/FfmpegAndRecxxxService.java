package pro.hirooka.chukasa.domain.service.transcoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.model.common.TunerStatus;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.service.common.ICommonUtilityService;
import pro.hirooka.chukasa.domain.service.common.ITunerManagementService;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Future;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.*;

@Slf4j
@Service
public class FfmpegAndRecxxxService implements IFfmpegAndRecxxxService {

    private final IChukasaModelManagementComponent chukasaModelManagementComponent;
    private final ICommonUtilityService commonUtilityService;

    @Autowired
    private ITunerManagementService tunerManagementService;

    @Autowired
    public FfmpegAndRecxxxService(IChukasaModelManagementComponent chukasaModelManagementComponent, ICommonUtilityService commonUtilityService){
        this.chukasaModelManagementComponent = chukasaModelManagementComponent;
        this.commonUtilityService = commonUtilityService;
    }

    @Async
    @Override
    public Future<Integer> submit(int adaptiveBitrateStreaming) {

        // TODO: final
        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();
        log.debug("StreamPath: {}", chukasaModel.getStreamPath());

        final FfmpegVcodecType ffmpegVcodecType = chukasaModel.getFfmpegVcodecType();

        final boolean canEncrypt = chukasaModel.getChukasaSettings().isCanEncrypt();
        final String ffmpegOutputPath;
        final String fmp4InitFileOutputPath;
        if(canEncrypt){
            ffmpegOutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
            fmp4InitFileOutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4";
        } else {
            ffmpegOutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
            fmp4InitFileOutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4";
        }
        final String ffmpegM3U8OutputPath;
        if(canEncrypt){
            ffmpegM3U8OutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        } else {
            ffmpegM3U8OutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        }

        //final List<ChannelConfiguration> channelConfigurationList = commonUtilityService.getChannelConfigurationList();
        final ChannelType channelType = commonUtilityService.getChannelType(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel());
        TunerStatus tunerStatus = tunerManagementService.findOne(channelType);
        if(tunerStatus != null) {
            tunerStatus = tunerManagementService.update(tunerStatus, false);
        }else{
            log.warn("Tuner for HLS is not available.");
            return new AsyncResult<>(-1);
        }
        chukasaModel.setTunerDeviceName(tunerStatus.getDeviceName());
        chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

        final String DEVICE_OPTION = tunerManagementService.getDeviceOption();
        final String DEVICE_ARGUMENT = tunerManagementService.getDeviceArgument(tunerStatus);
        final String[] commandArray;

        if(ffmpegVcodecType == FfmpegVcodecType.H264_OMX){
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "copy",
                    //"-acodec", "aac",
                    //"-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    //"-ar", "44100",
                    //"-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-c:v", "h264_omx",
                    "-vf", "yadif",
                    //"-vcodec", "h264_qsv",
                    //"-g", "60",
                    //"-profile:v", "high",
                    //"-level", "4.2",
                    //"-b:v", chukasaModel.getChukasaSettings().getVideoBitrate()+"k",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "segment",
                    "-segment_format", "mpegts",
                    "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                  "-segment_list", m3u8OutputPath,
                    ffmpegOutputPath
            };
        } else if(ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "aac",
                    "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    "-ar", "48000",
                    "-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-vcodec", "h264_qsv",
                    "-init_hw_device", "qsv:hw",
                    "-vf", "yadif",
                    "-g", "60",
                    "-profile:v", "high",
                    "-level", "4.2",
                    "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "segment",
                    "-segment_format", "mpegts",
                    "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                    "-segment_list", m3u8OutputPath,
                    ffmpegOutputPath
            };
        }else if(ffmpegVcodecType == FfmpegVcodecType.HEVC_QSV){
            // TODO: not working
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "aac",
                    "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    "-ar", "48000",
                    "-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-vcodec", "hevc_qsv",
                    "-init_hw_device", "qsv:hw",
                    "-load_plugin", "hevc_hw",
                    "-maxrate", "50000k",
                    "-tag:v", "hvc1",
                    "-vf", "yadif",
                    "-g", "60",
                    "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "hls",
                    "-hls_segment_type", "fmp4",
                    "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                    "-hls_fmp4_init_filename", fmp4InitFileOutputPath,
                    //"-hls_segment_filename", ffmpegOutputPath,
                    ffmpegM3U8OutputPath
            };
        }else if(ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "aac",
                    "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    "-ar", "48000",
                    "-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-vcodec", "libx264",
                    "-vf", "yadif",
                    "-profile:v", "high",
                    "-level", "4.1",
                    "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                    "-preset:v", "superfast",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "segment",
                    "-segment_format", "mpegts",
                    "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                    "-segment_list", m3u8OutputPath,
                    "-x264opts", "keyint=10:min-keyint=10",
                    ffmpegOutputPath
            };
        } else if(ffmpegVcodecType == FfmpegVcodecType.H264_NVENC){
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "aac",
                    "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    "-ar", "48000",
                    "-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-vcodec", "h264_nvenc",
                    "-vf", "yadif",
                    "-g", "10",
                    "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "hls",
                    "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                    "-hls_segment_filename", ffmpegOutputPath,
                    ffmpegM3U8OutputPath
            };
        } else if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC){
            commandArray = new String[]{
                    chukasaModel.getSystemConfiguration().getRecxxxPath(),
                    DEVICE_OPTION, DEVICE_ARGUMENT,
                    Integer.toString(chukasaModel.getChukasaSettings().getPhysicalLogicalChannel()),
                    "-", "-",
                    "|",
                    chukasaModel.getSystemConfiguration().getFfmpegPath(),
                    "-i", "-",
                    "-acodec", "aac",
                    "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                    "-ar", "48000",
                    "-ac", "2",
                    "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                    "-vcodec", "hevc_nvenc",
                    "-tag:v", "hvc1",
                    "-vf", "yadif",
                    "-g", "10",
                    "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                    "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                    "-f", "hls",
                    "-hls_segment_type", "fmp4",
                    "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                    "-hls_fmp4_init_filename", FMP4_INIT_FILE_NAME + FMP4_INIT_FILE_EXTENSION,
                    ffmpegM3U8OutputPath
            };
        } else {
            commandArray = new String[]{};
        }

        String command = "";
        for(int i = 0; i < commandArray.length; i++){
            command += commandArray[i] + " ";
        }
        log.info("{}", command);

        final String captureShell = chukasaModel.getSystemConfiguration().getTemporaryPath() + FILE_SEPARATOR + "capture.sh";
        File file = new File(captureShell);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("#!/bin/bash");
            bufferedWriter.newLine();
            bufferedWriter.write(command);
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }

        // chmod 755 capture.sh
        if(true){
            final String[] chmodCommandArray = {"chmod", "755", captureShell};
            final ProcessBuilder processBuilder = new ProcessBuilder(chmodCommandArray);
            try {
                final Process process = processBuilder.start();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String str = "";
                while((str = bufferedReader.readLine()) != null){
                    log.debug("{}", str);
                }
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
                bufferedReader.close();
                process.destroy();
            } catch (IOException e) {
                log.error("{} {}", e.getMessage(), e);
            }
        }

        // run capture.sh
        if(true){
            final String[] capureCommandArray = {captureShell};
            final ProcessBuilder processBuilder = new ProcessBuilder(capureCommandArray);
            try {
                final Process process = processBuilder.start();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                // TODO: sh だから意味無し
                long pid = -1;
                try {
                    if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                        final Field field = process.getClass().getDeclaredField("pid");
                        field.setAccessible(true);
                        pid = field.getLong(process);
                        chukasaModel.setFfmpegPID(pid);
                        chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
                        field.setAccessible(false);
                    }
                } catch (Exception e) {
                    log.error("{} {}", e.getMessage(), e);
                }

                String str = "";
                boolean isTranscoding = false;
                while((str = bufferedReader.readLine()) != null){
                    log.debug("{}", str);
                    if(str.startsWith("frame=")){
                        if(!isTranscoding){
                            isTranscoding = true;
                            chukasaModel.setTrascoding(isTranscoding);
                            chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
                        }
                    }
                }
                isTranscoding = false;
                chukasaModel.setTrascoding(isTranscoding);
                chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
                bufferedReader.close();
                process.destroy();
            } catch (IOException e) {
                log.error("{} {}", e.getMessage(), e);
            }
        }

        return new AsyncResult<>(0);
    }

    @Override
    public void execute(int adaptiveBitrateStreaming) {

    }

    @Override
    public void cancel(int adaptiveBitrateStreaming) {
        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        tunerManagementService.update(chukasaModel.getTunerDeviceName(), true);
        chukasaModel.setTunerDeviceName("");
        chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
    }
}
