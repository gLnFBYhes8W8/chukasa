package pro.hirooka.chukasa.chukasa_aaa.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring")
public class AaaConfiguration {
    private String profiles;
}
