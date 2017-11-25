package pro.hirooka.chukasa.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.hirooka.chukasa.domain.config.aaa.AaaConfiguration;
import pro.hirooka.chukasa.domain.service.aaa.IChukasaUserDetailsService;

import java.util.Objects;

@Slf4j
@Controller
public class LoginController {

    private final AaaConfiguration aaaConfiguration;
    private final IChukasaUserDetailsService chukasaUserDetailsService;

    @Autowired
    public LoginController(AaaConfiguration aaaConfiguration,
                           IChukasaUserDetailsService chukasaUserDetailsService) {
        this.aaaConfiguration =
                Objects.requireNonNull(aaaConfiguration, "aaaConfiguration");
        this.chukasaUserDetailsService =
                Objects.requireNonNull(chukasaUserDetailsService, "chukasaUserDetailsService");
    }

    @RequestMapping("/login")
    public String login() {

        if(aaaConfiguration.getProfiles().contains("postgresql")
                || aaaConfiguration.getProfiles().contains("mysql")
                || aaaConfiguration.getProfiles().contains("hsqldb")){
            if(chukasaUserDetailsService.readAllUserDetails().size() == 0){
                chukasaUserDetailsService.createInitialUser();
            }
        }

        return "login";
    }
}
