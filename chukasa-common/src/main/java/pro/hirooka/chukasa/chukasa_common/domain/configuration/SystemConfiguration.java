package pro.hirooka.chukasa.chukasa_common.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = {"classpath:system.properties"})
@ConfigurationProperties(prefix = "system")
public class SystemConfiguration {
    String ffmpegPath;
    String recxxxPath;
    String webcamDeviceName;
    int webcamAudioChannel;
    String temporaryPath;
    String filePath;
    int ffmpegThreads;
}
