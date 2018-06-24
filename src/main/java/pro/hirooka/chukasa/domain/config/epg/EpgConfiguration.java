package pro.hirooka.chukasa.domain.config.epg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "epg")
public class EpgConfiguration {
    private String epgdumpExecuteScheduleCron;
    private long epgdumpExecuteOnBootIgnoreInterval;
    String epgdumpTemporaryPath;
    int epgdumpRecordingDuration;

    public String getEpgdumpExecuteScheduleCron() {
        return epgdumpExecuteScheduleCron;
    }

    public void setEpgdumpExecuteScheduleCron(String epgdumpExecuteScheduleCron) {
        this.epgdumpExecuteScheduleCron = epgdumpExecuteScheduleCron;
    }

    public long getEpgdumpExecuteOnBootIgnoreInterval() {
        return epgdumpExecuteOnBootIgnoreInterval;
    }

    public void setEpgdumpExecuteOnBootIgnoreInterval(long epgdumpExecuteOnBootIgnoreInterval) {
        this.epgdumpExecuteOnBootIgnoreInterval = epgdumpExecuteOnBootIgnoreInterval;
    }

    public String getEpgdumpTemporaryPath() {
        return epgdumpTemporaryPath;
    }

    public void setEpgdumpTemporaryPath(String epgdumpTemporaryPath) {
        this.epgdumpTemporaryPath = epgdumpTemporaryPath;
    }

    public int getEpgdumpRecordingDuration() {
        return epgdumpRecordingDuration;
    }

    public void setEpgdumpRecordingDuration(int epgdumpRecordingDuration) {
        this.epgdumpRecordingDuration = epgdumpRecordingDuration;
    }
}
