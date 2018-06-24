package pro.hirooka.chukasa.domain.entity.aaa;

import javax.persistence.*;
import java.util.Set;

@Entity
public class RoleEntity extends AbstractEntity {

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String authority;

    @ManyToMany(mappedBy = "roleEntitySet", fetch = FetchType.EAGER)
    private Set<UserDetailsEntity> userDetailsEntitySet;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission")
    private Set<PermissionEntity> permissionEntitySet;

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

    public Set<UserDetailsEntity> getUserDetailsEntitySet() {
        return userDetailsEntitySet;
    }

    public void setUserDetailsEntitySet(Set<UserDetailsEntity> userDetailsEntitySet) {
        this.userDetailsEntitySet = userDetailsEntitySet;
    }

    public Set<PermissionEntity> getPermissionEntitySet() {
        return permissionEntitySet;
    }

    public void setPermissionEntitySet(Set<PermissionEntity> permissionEntitySet) {
        this.permissionEntitySet = permissionEntitySet;
    }
}

