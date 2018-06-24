package pro.hirooka.chukasa.domain.entity.aaa;

import javax.persistence.*;
import java.util.Set;

@Entity
public class PermissionEntity extends AbstractEntity {

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "permissionEntitySet", fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntitySet;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permission_operation")
    private Set<OperationEntity> operationEntitySet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleEntity> getRoleEntitySet() {
        return roleEntitySet;
    }

    public void setRoleEntitySet(Set<RoleEntity> roleEntitySet) {
        this.roleEntitySet = roleEntitySet;
    }

    public Set<OperationEntity> getOperationEntitySet() {
        return operationEntitySet;
    }

    public void setOperationEntitySet(Set<OperationEntity> operationEntitySet) {
        this.operationEntitySet = operationEntitySet;
    }
}
