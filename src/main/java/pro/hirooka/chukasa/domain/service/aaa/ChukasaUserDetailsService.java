package pro.hirooka.chukasa.domain.service.aaa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.aaa.AaaConfiguration;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserDetails;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserRole;
import pro.hirooka.chukasa.domain.repository.aaa.*;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.DEFAULT_PASSWORD;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.DEFAULT_USERNAME;

@Service
public class ChukasaUserDetailsService implements IChukasaUserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(ChukasaUserDetailsService.class);

    private final AaaConfiguration aaaConfiguration;
    private final ChukasaUserDetailsRepository chukasaUserDetailsRepository;

    @Autowired
    public ChukasaUserDetailsService(
            AaaConfiguration aaaConfiguration,
            ChukasaUserDetailsRepository chukasaUserDetailsRepository
    ) {
        this.aaaConfiguration = requireNonNull(aaaConfiguration);
        this.chukasaUserDetailsRepository = requireNonNull(chukasaUserDetailsRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return chukasaUserDetailsRepository.findOneByUsername(username);
    }

    @Override
    public void createInitialUser() {

        log.info("create initial user");

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        ChukasaUserRole adminChukasaUserRole = new ChukasaUserRole();
        adminChukasaUserRole.setName("ADMIN");
        adminChukasaUserRole.setAuthority("ROLE_ADMIN");

        ChukasaUserRole guestChukasaUserRole = new ChukasaUserRole();
        guestChukasaUserRole.setName("GUEST");
        guestChukasaUserRole.setAuthority("ROLE_GUEST");

        List<ChukasaUserRole> chukasaUserRoleList = new ArrayList<>();
        chukasaUserRoleList.add(adminChukasaUserRole);
        chukasaUserRoleList.add(guestChukasaUserRole);

        final String username;
        if(aaaConfiguration.getInitialUsername().equals("")){
            username = DEFAULT_USERNAME;
        }else{
            username = aaaConfiguration.getInitialUsername();
        }
        final String password;
        if(aaaConfiguration.getInitialPassword().equals("")){
            password = DEFAULT_PASSWORD;
        }else{
            password = aaaConfiguration.getInitialPassword();
        }
        ChukasaUserDetails chukasaUserDetails = new ChukasaUserDetails();
        chukasaUserDetails.setUsername(aaaConfiguration.getInitialUsername());
        chukasaUserDetails.setPassword(passwordEncoder.encode(aaaConfiguration.getInitialPassword()));
        chukasaUserDetails.setUserRoleList(chukasaUserRoleList);
        chukasaUserDetailsRepository.save(chukasaUserDetails);
    }

    @Override
    public List<ChukasaUserDetails> readAllUserDetails() {
        return chukasaUserDetailsRepository.findAll();
    }
}
