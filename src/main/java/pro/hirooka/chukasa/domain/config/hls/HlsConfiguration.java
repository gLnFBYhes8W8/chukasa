package pro.hirooka.chukasa.domain.config.hls;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "hls")
public class HlsConfiguration {
    int duration;
    int uriInPlaylist;
}
