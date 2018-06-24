package pro.hirooka.chukasa.domain.model;

import java.util.UUID;

public class AbstractModel {
    private UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
