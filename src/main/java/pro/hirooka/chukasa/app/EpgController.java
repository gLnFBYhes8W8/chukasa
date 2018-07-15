package pro.hirooka.chukasa.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.hirooka.chukasa.domain.operator.IEpgOperator;

import static java.util.Objects.requireNonNull;

@RequestMapping("/epg")
@Controller
public class EpgController {

    private final IEpgOperator epgOperator;

    @Autowired
    public EpgController(IEpgOperator epgOperator) {
        this.epgOperator = requireNonNull(epgOperator);
    }

    @GetMapping("")
    public String index(){
        epgOperator.persist();
        return "index";
    }
}
