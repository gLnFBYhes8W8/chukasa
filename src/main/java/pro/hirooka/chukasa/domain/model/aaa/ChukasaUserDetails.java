package pro.hirooka.chukasa.domain.model.aaa;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChukasaUserDetails implements UserDetails {

    @Id
    private UUID uuid = UUID.randomUUID();

    private String username;
    private String password;
    private List<ChukasaUserRole> userRoleList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> authorityList = userRoleList.stream().map(ChukasaUserRole::getAuthority).collect(Collectors.toList());
        return AuthorityUtils.createAuthorityList(authorityList.toArray(new String[0]));
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ChukasaUserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<ChukasaUserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }
}
