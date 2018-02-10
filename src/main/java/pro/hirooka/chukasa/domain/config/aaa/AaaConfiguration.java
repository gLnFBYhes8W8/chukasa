package pro.hirooka.chukasa.domain.config.aaa;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aaa")
public class AaaConfiguration {
    private boolean enabled;
}
