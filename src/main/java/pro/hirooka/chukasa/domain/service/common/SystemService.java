package pro.hirooka.chukasa.domain.service.common;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.ChukasaConstants;
import pro.hirooka.chukasa.domain.config.common.MongoDBConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.STREAM_ROOT_PATH_NAME;

@Service
public class SystemService implements ISystemService {

    private static final Logger log = LoggerFactory.getLogger(SystemService.class);

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
        return isFFmpeg();
    }

    @Override
    public boolean canRecording() {
        return isFFmpeg() && isMongoDB();
    }

    @Override
    public FfmpegVcodecType getFfmpegVcodecType(String userAgent) {
        final String H264_QSV = "--enable-libmfx";
        final String H264_X264 = "--enable-libx264";
        final String H264_OMX = "--enable-omx-rpi";
        final String VIDEOTOOLBOX = "--enable-videotoolbox";
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
                if(str.contains(VIDEOTOOLBOX)){
                    bufferedReader.close();
                    process.destroy();
                    if(canHevc(userAgent)){
                        return FfmpegVcodecType.H264_VIDEOTOOLBOX; // TODO: check
                    }else{
                        return FfmpegVcodecType.H264_VIDEOTOOLBOX;
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

    @Override
    public String getStreamRootPath(String servletRealPath) {
        if(servletRealPath.substring(servletRealPath.length() - 1).equals(FILE_SEPARATOR)) {
            return servletRealPath + STREAM_ROOT_PATH_NAME; // e.g. Tomcat
        } else {
            return servletRealPath + FILE_SEPARATOR + STREAM_ROOT_PATH_NAME; // e.g. Jetty
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
        final String DARWIN = "Darwin/";
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
                final String versionUnderscoreString = userAgent.split(MAC)[1].split(" ")[0].split("\\)")[0];
                final String versionString = versionUnderscoreString.split("_")[0]
                        + "." + versionUnderscoreString.split("_")[1];
                final double version = Double.parseDouble(versionString);
                if(version >= 10.13){
                    return true;
                }
            }
        }else if(userAgent.contains(DARWIN)){
            final String versionUnderscoreString = userAgent.split(DARWIN)[1];
            final String versionString = versionUnderscoreString.split("\\.")[0];
            final int version = Integer.parseInt(versionString);
            if(version >= 17){
                return true;
            }
        }
        return false;
    }
}

