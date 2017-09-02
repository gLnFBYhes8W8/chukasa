package pro.hirooka.chukasa.domain.entity.aaa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class RoleEntity extends AbstractEntity {

    @Getter
    @Setter
    @Column(unique = true)
    private String name;

    @Getter
    @Setter
    @Column(unique = true)
    private String authority;

    @ManyToMany(mappedBy = "roleEntitySet", fetch = FetchType.EAGER)
    private Set<UserDetailsEntity> userDetailsEntitySet;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission")
    private Set<PermissionEntity> permissionEntitySet;
}

