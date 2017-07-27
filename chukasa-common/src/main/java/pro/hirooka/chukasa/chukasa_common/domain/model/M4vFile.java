package pro.hirooka.chukasa.chukasa_common.domain.model;

import lombok.Data;
import pro.hirooka.chukasa.chukasa_common.domain.model.enums.M4vType;

@Data
public class M4vFile {
    private String name;
    private M4vType type;
}
