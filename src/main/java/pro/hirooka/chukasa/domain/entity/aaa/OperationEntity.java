package pro.hirooka.chukasa.domain.entity.aaa;

import lombok.Getter;
import lombok.Setter;
import pro.hirooka.chukasa.domain.entity.aaa.type.OperationType;

import javax.persistence.*;
import java.util.Set;

@Entity
public class OperationEntity extends AbstractEntity {

    @Getter
    @Setter
    @Column(unique = true)
    private String name;

    @Getter
    @Setter
    private OperationType operationType;

    @ManyToMany(mappedBy = "operationEntitySet", fetch = FetchType.EAGER)
    private Set<PermissionEntity> permissionEntitySet;
}
