package pro.hirooka.chukasa.domain.config.epg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "epg")
public class EpgConfiguration {
    private String epgdumpExecuteScheduleCron;
    private long epgdumpExecuteOnBootIgnoreInterval;
    String epgdumpTemporaryPath;
    int epgdumpRecordingDuration;
}
