package pro.hirooka.chukasa.domain.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.hirooka.chukasa.domain.config.common.type.SchemeType;

@Data
@Configuration
@ConfigurationProperties(prefix = "hyaruka")
public class HyarukaConfiguration {
    private SchemeType scheme;
    private String host;
    private int port;
    private String apiVersion;
    private boolean enabled;
}
