package pro.hirooka.chukasa.domain.entity.aaa;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class UserDetailsEntity extends AbstractEntity implements UserDetails {

    @Column(unique = true)
    private String username;

    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roleNameSet = roleEntitySet.stream().map(RoleEntity::getAuthority).collect(Collectors.toSet());
        return AuthorityUtils.createAuthorityList(roleNameSet.toArray(new String[0]));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private Set<RoleEntity> roleEntitySet;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleEntitySet(Set<RoleEntity> roleEntitySet) {
        this.roleEntitySet = roleEntitySet;
    }
}
