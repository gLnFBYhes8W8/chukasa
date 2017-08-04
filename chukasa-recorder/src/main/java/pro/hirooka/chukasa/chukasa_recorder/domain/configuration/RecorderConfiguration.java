package pro.hirooka.chukasa.chukasa_recorder.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = {"classpath:recorder.properties"})
@ConfigurationProperties(prefix = "recorder")
public class RecorderConfiguration {
    long startMargin;
    long stopMargin;
}
