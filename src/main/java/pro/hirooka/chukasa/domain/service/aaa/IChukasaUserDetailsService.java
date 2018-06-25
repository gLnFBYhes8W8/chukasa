package pro.hirooka.chukasa.domain.service.aaa;

import org.springframework.security.core.userdetails.UserDetailsService;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserDetails;

import java.util.List;

public interface IChukasaUserDetailsService extends UserDetailsService {
    void createInitialUser();
    List<ChukasaUserDetails> readAllUserDetails();
}
