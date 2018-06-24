package pro.hirooka.chukasa.domain.entity.aaa;

import pro.hirooka.chukasa.domain.entity.aaa.type.OperationType;

import javax.persistence.*;
import java.util.Set;

@Entity
public class OperationEntity extends AbstractEntity {

    @Column(unique = true)
    private String name;

    private OperationType operationType;

    @ManyToMany(mappedBy = "operationEntitySet", fetch = FetchType.EAGER)
    private Set<PermissionEntity> permissionEntitySet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
}
