package pro.hirooka.chukasa.domain.config.recorder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "recorder")
public class RecorderConfiguration {
    long startMargin;
    long stopMargin;

    public long getStartMargin() {
        return startMargin;
    }

    public void setStartMargin(long startMargin) {
        this.startMargin = startMargin;
    }

    public long getStopMargin() {
        return stopMargin;
    }

    public void setStopMargin(long stopMargin) {
        this.stopMargin = stopMargin;
    }
}
