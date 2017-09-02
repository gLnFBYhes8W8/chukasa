package pro.hirooka.chukasa.domain.config.recorder;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "recorder")
public class RecorderConfiguration {
    long startMargin;
    long stopMargin;
}
