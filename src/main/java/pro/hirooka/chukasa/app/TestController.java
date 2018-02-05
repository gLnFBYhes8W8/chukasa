package pro.hirooka.chukasa.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/test")
@Controller
public class TestController {
    @GetMapping("")
    public String test(){
        return "test";
    }
}
