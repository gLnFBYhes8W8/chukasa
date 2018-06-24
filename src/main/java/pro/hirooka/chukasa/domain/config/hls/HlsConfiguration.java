package pro.hirooka.chukasa.domain.config.hls;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hls")
public class HlsConfiguration {
    int duration;
    int uriInPlaylist;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getUriInPlaylist() {
        return uriInPlaylist;
    }

    public void setUriInPlaylist(int uriInPlaylist) {
        this.uriInPlaylist = uriInPlaylist;
    }
}
