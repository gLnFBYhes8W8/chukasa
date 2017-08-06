package pro.hirooka.chukasa.chukasa_api.app.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.chukasa_api.domain.model.ChukasaResponse;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class HelloRestController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ChukasaResponse hello(){
        return new ChukasaResponse("hello");
    }

    @RequestMapping(value="/csrf-token", method=RequestMethod.GET)
    public @ResponseBody
    String getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken)request.getAttribute(CsrfToken.class.getName());
        return token.getToken();
    }
}
