package pro.hirooka.chukasa.domain.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemConfiguration {
    String ffmpegPath;
    String recxxxPath;
    String epgdumpPath;
    String webcamDeviceName;
    String webcamAudioName;
    int webcamAudioChannel;
    String temporaryPath;
    String filePath;
    int ffmpegThreads;
}
