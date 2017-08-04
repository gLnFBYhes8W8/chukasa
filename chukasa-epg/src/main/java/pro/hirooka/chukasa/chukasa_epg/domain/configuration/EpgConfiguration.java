package pro.hirooka.chukasa.chukasa_epg.domain.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = {"classpath:epg.properties"})
@ConfigurationProperties(prefix = "epg")
public class EpgConfiguration {
    private String epgdumpExecuteScheduleCron;
    private long epgdumpExecuteOnBootIgnoreInterval;
    String epgdumpPath;
    String epgdumpTemporaryPath;
    int epgdumpRecordingDuration;
}
