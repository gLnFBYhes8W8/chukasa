package pro.hirooka.chukasa.domain.model.common;

import pro.hirooka.chukasa.domain.model.common.type.M4vType;

public class M4vFile {
    private String name;
    private M4vType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public M4vType getType() {
        return type;
    }

    public void setType(M4vType type) {
        this.type = type;
    }
}
