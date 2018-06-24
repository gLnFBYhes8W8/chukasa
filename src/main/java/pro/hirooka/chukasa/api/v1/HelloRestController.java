package pro.hirooka.chukasa.api.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.domain.model.api.ChukasaResponse;

@RestController
@RequestMapping("api/v1")
public class HelloRestController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ChukasaResponse hello(){
        return new ChukasaResponse("hello");
    }
}
