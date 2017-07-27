package pro.hirooka.chukasa.chukasa_hls.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hls")
public class HlsConfiguration {
    int duration;
    int uriInPlaylist;
}
