package pro.hirooka.chukasa.domain.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "common")
public class CommonConfiguration {
    private String tuner;
    private String channelConfiguration;
    private String[] videoFileExtension;

    public String getTuner() {
        return tuner;
    }

    public void setTuner(String tuner) {
        this.tuner = tuner;
    }

    public String getChannelConfiguration() {
        return channelConfiguration;
    }

    public void setChannelConfiguration(String channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    public String[] getVideoFileExtension() {
        return videoFileExtension;
    }

    public void setVideoFileExtension(String[] videoFileExtension) {
        this.videoFileExtension = videoFileExtension;
    }
}
