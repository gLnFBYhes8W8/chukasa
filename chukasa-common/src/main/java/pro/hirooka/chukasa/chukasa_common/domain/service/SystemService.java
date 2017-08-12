package pro.hirooka.chukasa.chukasa_common.domain.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.chukasa_common.domain.configuration.MongoDBConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.configuration.SystemConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants;
import pro.hirooka.chukasa.chukasa_common.domain.enums.FfmpegVcodecType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.HardwareAccelerationType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class SystemService implements ISystemService {

    private final String DVB_DEVICE = ChukasaConstants.DVB_DEVICE + "0"; // TODO:
    private final String CHARACTER_DEVICE = ChukasaConstants.CHARACTER_DEVICE + "0";;

    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    MongoDBConfiguration mongoDBConfiguration;

    @Override
    public boolean isFFmpeg() {
        File ffmpeg = new File(systemConfiguration.getFfmpegPath());
        return ffmpeg.exists();
    }

    @Override
    public boolean isWebCamera() {
        String webCameraDeviceName = systemConfiguration.getWebcamDeviceName();
        File file = new File(webCameraDeviceName);
        return file.exists();
    }

    @Override
    public String getWebCameraDeviceName() {
        return systemConfiguration.getWebcamDeviceName();
    }

    @Override
    public boolean isTuner() {
        return new File(DVB_DEVICE).exists() || new File(CHARACTER_DEVICE).exists();
    }

    @Override
    public boolean isRecxxx() {
        String recxxxPath = systemConfiguration.getRecxxxPath().split(" ")[0];
        File recpt1 = new File(recxxxPath);
        return recpt1.exists();
    }

    @Override
    public boolean isEpgdump() {
        File epgdump = new File(systemConfiguration.getEpgdumpPath());
        return epgdump.exists();
    }

    @Override
    public boolean isMongoDB() {
        if(mongoDBConfiguration.getHost() == null){
            return false;
        }
        if(mongoDBConfiguration.getHost().equals("mongo")){
            return true;
        }
        ServerAddress serverAddress = new ServerAddress(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort());
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().serverSelectionTimeout(mongoDBConfiguration.getServerSelectionTimeout()).build();
        MongoClient mongoClient = new MongoClient(serverAddress, mongoClientOptions);
        try {
            mongoClient.getServerAddressList();
            //mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
            mongoClient.close();
            log.info("MongoDB is running.");
            return true;
        } catch (Exception e) {
            log.info("MongoDB is down or not installed.");
            mongoClient.close();
            return false;
        }
    }

    @Override
    public boolean canWebCameraStreaming() {
        return isFFmpeg() && isWebCamera();
    }

    @Override
    public boolean canFileStreaming() {
        return isFFmpeg();
    }

    @Override
    public boolean canPTxStreaming() {
        return isFFmpeg() && isTuner() && isRecxxx();
    }

    @Override
    public boolean canRecording() {
        return isFFmpeg() && isTuner() && isRecxxx() && isMongoDB();
    }

    @Override
    public HardwareAccelerationType getHardwareAccelerationType() {
        final String H264_QSV = "--enable-libmfx";
        final String H264_X264 = "--enable-libx264";
        final String H264_OMX = "--enable-omx-rpi";
        String ffmpeg = systemConfiguration.getFfmpegPath();
        String[] command = {ffmpeg, "-version"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while((str = bufferedReader.readLine()) != null){
                log.info(str);
                if(str.contains(H264_QSV)){
                    bufferedReader.close();
                    process.destroy();
                    return HardwareAccelerationType.H264_QSV;
                }
                if(str.contains(H264_OMX)){
                    bufferedReader.close();
                    process.destroy();
                    return HardwareAccelerationType.H264_OMX;
                }
                if(str.contains(H264_X264)){
                    bufferedReader.close();
                    process.destroy();
                    return HardwareAccelerationType.H264_X264;
                }
            }
            bufferedReader.close();
            process.destroy();
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
        return HardwareAccelerationType.H264_NVENC;
    }

    @Override
    public FfmpegVcodecType getFfmpegVcodecType() {
        final String H264_QSV = "--enable-libmfx";
        final String H264_X264 = "--enable-libx264";
        final String H264_OMX = "--enable-omx-rpi";
        final String ffmpeg = systemConfiguration.getFfmpegPath();
        final String[] command = {ffmpeg, "-version"};
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while((str = bufferedReader.readLine()) != null){
                log.info(str);
                if(str.contains(H264_QSV)){
                    bufferedReader.close();
                    process.destroy();
                    return FfmpegVcodecType.H264_QSV;
                }
                if(str.contains(H264_OMX)){
                    bufferedReader.close();
                    process.destroy();
                    return FfmpegVcodecType.H264_OMX;
                }
                if(str.contains(H264_X264)){
                    bufferedReader.close();
                    process.destroy();
                    return FfmpegVcodecType.H264_X264;
                }
            }
            bufferedReader.close();
            process.destroy();
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
        return FfmpegVcodecType.H264_NVENC;
    }

    @Override
    public FfmpegVcodecType getFfmpegVcodecType(String userAgent) {
        final String H264_QSV = "--enable-libmfx";
        final String H264_X264 = "--enable-libx264";
        final String H264_OMX = "--enable-omx-rpi";
        final String ffmpeg = systemConfiguration.getFfmpegPath();
        final String[] command = {ffmpeg, "-version"};
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while((str = bufferedReader.readLine()) != null){
                log.info(str);
                if(str.contains(H264_QSV)){
                    bufferedReader.close();
                    process.destroy();
                    if(canHevc(userAgent)){
                        //return FfmpegVcodecType.HEVC_QSV;
                        return FfmpegVcodecType.H264_QSV; // TODO: check FFmpeg
                    }else{
                        return FfmpegVcodecType.H264_QSV;
                    }
                }
                if(str.contains(H264_OMX)){
                    bufferedReader.close();
                    process.destroy();
                    return FfmpegVcodecType.H264_OMX;
                }
                if(str.contains(H264_X264)){
                    bufferedReader.close();
                    process.destroy();
                    return FfmpegVcodecType.H264_X264;
                }
            }
            bufferedReader.close();
            process.destroy();
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
        if(canHevc(userAgent)){
            return FfmpegVcodecType.HEVC_NVENC;
        }else{
            return FfmpegVcodecType.H264_NVENC;
        }
    }

    private boolean canHevc(String userAgent){
        final String SAFARI = "Safari/";
        final String CHROME = "Chrome/";
        final String CHROMIUM = "Chromium/";
        final String IPHONE = "iPhone; CPU iPhone OS ";
        final String IPOD = "iPod touch; CPU iPhone OS ";
        final String IPAD = "iPad; CPU OS ";
        final String MAC = "Macintosh; Intel Mac OS X ";
        if(userAgent.contains(SAFARI) && !userAgent.contains(CHROME) && !userAgent.contains(CHROMIUM)){
            if(userAgent.contains(IPHONE)){
                final String versionUnderscoreString = userAgent.split(IPHONE)[1].split(" ")[0];
                final String versionString = versionUnderscoreString.split("_")[0]
                        + "." + versionUnderscoreString.split("_")[1];
                final double version = Double.parseDouble(versionString);
                if(version >= 11.0){
                    return true;
                }
            }else if(userAgent.contains(IPOD)){
                final String versionUnderscoreString = userAgent.split(IPOD)[1].split(" ")[0];
                final String versionString = versionUnderscoreString.split("_")[0]
                        + "." + versionUnderscoreString.split("_")[1];
                final double version = Double.parseDouble(versionString);
                if (version >= 11.0) {
                    return true;
                }
            }else if(userAgent.contains(IPAD)){
                final String versionUnderscoreString = userAgent.split(IPAD)[1].split(" ")[0];
                final String versionString = versionUnderscoreString.split("_")[0]
                        + "." + versionUnderscoreString.split("_")[1];
                final double version = Double.parseDouble(versionString);
                if(version >= 11.0){
                    return true;
                }
            }else if(userAgent.contains(MAC)){
                final String versionUnderscoreString = userAgent.split(MAC)[1].split(" ")[0];
                final String versionString = versionUnderscoreString.split("_")[0]
                        + "." + versionUnderscoreString.split("_")[1];
                final double version = Double.parseDouble(versionString);
                if(version >= 10.13){
                    return true;
                }
            }
        }
        return false;
    }
}

