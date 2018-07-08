package pro.hirooka.chukasa.domain.model.aaa;

import java.io.Serializable;
import java.util.UUID;

public class ChukasaUserRole implements Serializable {

    private UUID uuid = UUID.randomUUID();
    private String name;
    private String authority;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
