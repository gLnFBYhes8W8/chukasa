package pro.hirooka.chukasa.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.hirooka.chukasa.domain.config.SpringConfiguration;
import pro.hirooka.chukasa.domain.service.aaa.IChukasaUserDetailsService;

import java.util.Objects;

@Slf4j
@Controller
public class LoginController {

    private final SpringConfiguration springConfiguration;
    private final IChukasaUserDetailsService chukasaUserDetailsService;

    @Autowired
    public LoginController(SpringConfiguration springConfiguration,
                           IChukasaUserDetailsService chukasaUserDetailsService) {
        this.springConfiguration =
                Objects.requireNonNull(springConfiguration, "springConfiguration");
        this.chukasaUserDetailsService =
                Objects.requireNonNull(chukasaUserDetailsService, "chukasaUserDetailsService");
    }

    @RequestMapping("/login")
    public String login() {

        if(springConfiguration.getProfiles().contains("postgresql")
                || springConfiguration.getProfiles().contains("mysql")
                || springConfiguration.getProfiles().contains("hsqldb")){
            if(chukasaUserDetailsService.readAllUserDetails().size() == 0){
                chukasaUserDetailsService.createInitialUser();
            }
        }

        return "login";
    }
}
