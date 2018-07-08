package pro.hirooka.chukasa.domain.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemConfiguration {
    String ffmpegPath;
    String recxxxPath;
//    String epgdumpPath;
    String webcamDeviceName;
    String webcamAudioName;
    int webcamAudioChannel;
    String temporaryPath;
    String filePath;
    int ffmpegThreads;

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getRecxxxPath() {
        return recxxxPath;
    }

    public void setRecxxxPath(String recxxxPath) {
        this.recxxxPath = recxxxPath;
    }

//    public String getEpgdumpPath() {
//        return epgdumpPath;
//    }
//
//    public void setEpgdumpPath(String epgdumpPath) {
//        this.epgdumpPath = epgdumpPath;
//    }

    public String getWebcamDeviceName() {
        return webcamDeviceName;
    }

    public void setWebcamDeviceName(String webcamDeviceName) {
        this.webcamDeviceName = webcamDeviceName;
    }

    public String getWebcamAudioName() {
        return webcamAudioName;
    }

    public void setWebcamAudioName(String webcamAudioName) {
        this.webcamAudioName = webcamAudioName;
    }

    public int getWebcamAudioChannel() {
        return webcamAudioChannel;
    }

    public void setWebcamAudioChannel(int webcamAudioChannel) {
        this.webcamAudioChannel = webcamAudioChannel;
    }

    public String getTemporaryPath() {
        return temporaryPath;
    }

    public void setTemporaryPath(String temporaryPath) {
        this.temporaryPath = temporaryPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFfmpegThreads() {
        return ffmpegThreads;
    }

    public void setFfmpegThreads(int ffmpegThreads) {
        this.ffmpegThreads = ffmpegThreads;
    }
}
