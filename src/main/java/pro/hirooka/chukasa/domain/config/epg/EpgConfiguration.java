package pro.hirooka.chukasa.domain.config.epg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "epg")
public class EpgConfiguration {

    private String tuner;
    private String channelConfiguration;
    String acquisitionScheduleCron;
    long acquisitionOnBootIgnoredInterval;

    public String getTuner() {
        return tuner;
    }

    public void setTuner(String tuner) {
        this.tuner = tuner;
    }

    public String getChannelConfiguration() {
        return channelConfiguration;
    }

    public void setChannelConfiguration(String channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    public String getAcquisitionScheduleCron() {
        return acquisitionScheduleCron;
    }

    public void setAcquisitionScheduleCron(String acquisitionScheduleCron) {
        this.acquisitionScheduleCron = acquisitionScheduleCron;
    }

    public long getAcquisitionOnBootIgnoredInterval() {
        return acquisitionOnBootIgnoredInterval;
    }

    public void setAcquisitionOnBootIgnoredInterval(long acquisitionOnBootIgnoredInterval) {
        this.acquisitionOnBootIgnoredInterval = acquisitionOnBootIgnoredInterval;
    }
}
