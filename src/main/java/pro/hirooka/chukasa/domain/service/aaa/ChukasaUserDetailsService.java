package pro.hirooka.chukasa.domain.service.aaa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserDetails;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserRole;
import pro.hirooka.chukasa.domain.repository.aaa.*;

import java.util.*;

import static java.util.Objects.requireNonNull;

@Service
public class ChukasaUserDetailsService implements IChukasaUserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(ChukasaUserDetailsService.class);

    private final ChukasaUserDetailsRepository chukasaUserDetailsRepository;

    @Autowired
    public ChukasaUserDetailsService(ChukasaUserDetailsRepository chukasaUserDetailsRepository) {
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

        ChukasaUserRole userRole = new ChukasaUserRole();
        userRole.setName("GUEST");
        userRole.setAuthority("ROLE_GUEST");
        ChukasaUserDetails chukasaUserDetails = new ChukasaUserDetails();
        chukasaUserDetails.setUsername("guest");
        chukasaUserDetails.setPassword(passwordEncoder.encode("guest"));
        chukasaUserDetails.setUserRoleList(Collections.singletonList(userRole));
        chukasaUserDetailsRepository.save(chukasaUserDetails);

        userRole = new ChukasaUserRole();
        userRole.setName("ADMIN");
        userRole.setAuthority("ROLE_ADMIN");
        chukasaUserDetails = new ChukasaUserDetails();
        chukasaUserDetails.setUsername("admin");
        chukasaUserDetails.setPassword(passwordEncoder.encode("admin"));
        chukasaUserDetails.setUserRoleList(Collections.singletonList(userRole));
        chukasaUserDetailsRepository.save(chukasaUserDetails);
    }

    @Override
    public List<ChukasaUserDetails> readAllUserDetails() {
        return chukasaUserDetailsRepository.findAll();
    }
}
