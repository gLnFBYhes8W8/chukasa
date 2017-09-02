package pro.hirooka.chukasa.domain.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "common")
public class CommonConfiguration {
    private String tuner;
    private String channelConfiguration;
    private String[] videoFileExtension;
}
