package pro.hirooka.chukasa.chukasa_common.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = {"classpath:common.properties"})
@ConfigurationProperties(prefix = "common")
public class CommonConfiguration {
    private String tuner;
    private String channelConfiguration;
    private String[] videoFileExtension;
}
