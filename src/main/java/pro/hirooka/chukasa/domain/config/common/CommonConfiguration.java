package pro.hirooka.chukasa.domain.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "common")
public class CommonConfiguration {

    private String[] videoFileExtension;

    public String[] getVideoFileExtension() {
        return videoFileExtension;
    }

    public void setVideoFileExtension(String[] videoFileExtension) {
        this.videoFileExtension = videoFileExtension;
    }
}
