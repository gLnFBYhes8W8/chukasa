package pro.hirooka.chukasa.domain.model;

import lombok.Data;

import java.util.UUID;

@Data
public class AbstractModel {
    private UUID uuid = UUID.randomUUID();
}
