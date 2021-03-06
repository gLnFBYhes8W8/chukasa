package pro.hirooka.chukasa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.hirooka.chukasa.domain.config.SpringConfiguration;
import pro.hirooka.chukasa.domain.service.aaa.IChukasaUserDetailsService;

import static java.util.Objects.requireNonNull;

@RequestMapping("login")
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SpringConfiguration springConfiguration;
    private final IChukasaUserDetailsService chukasaUserDetailsService;

    @Autowired
    public LoginController(
            SpringConfiguration springConfiguration,
            IChukasaUserDetailsService chukasaUserDetailsService
    ) {
        this.springConfiguration = requireNonNull(springConfiguration);
        this.chukasaUserDetailsService = requireNonNull(chukasaUserDetailsService);
    }

    @GetMapping("")
    public String login() {

        if(springConfiguration.getProfiles().contains("mongodb")
                || springConfiguration.getProfiles().contains("postgresql")
                || springConfiguration.getProfiles().contains("mysql")
                || springConfiguration.getProfiles().contains("hsqldb")){
            if(chukasaUserDetailsService.readAllUserDetails().size() == 0){
                chukasaUserDetailsService.createInitialUser();
            }
        }

        return "login";
    }
}
