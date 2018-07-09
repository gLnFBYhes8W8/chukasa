package pro.hirooka.chukasa.domain.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.hirooka.chukasa.domain.config.common.type.SchemeType;

@Configuration
@ConfigurationProperties(prefix = "hyaruka")
public class HyarukaConfiguration {

    private String username;
    private String password;
    private SchemeType scheme;
    private String host;
    private int port;
    private String apiVersion;
    private boolean enabled;
    private boolean unixDomainSocketEnabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SchemeType getScheme() {
        return scheme;
    }

    public void setScheme(SchemeType scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUnixDomainSocketEnabled() {
        return unixDomainSocketEnabled;
    }

    public void setUnixDomainSocketEnabled(boolean unixDomainSocketEnabled) {
        this.unixDomainSocketEnabled = unixDomainSocketEnabled;
    }
}
