package pro.hirooka.chukasa.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.api.v1.exception.ChukasaInternalServerErrorException;
import pro.hirooka.chukasa.domain.model.api.ChukasaResponse;
import pro.hirooka.chukasa.domain.service.common.ITunerManagementService;

@RestController
@RequestMapping("api/v1/tuners")
public class TunerRestController {

    @Autowired
    private ITunerManagementService tunerManagementService;

    @RequestMapping(value = "/release", method = RequestMethod.GET)
    ChukasaResponse relaseAll() throws ChukasaInternalServerErrorException {
        tunerManagementService.releaseAll();
        ChukasaResponse chukasaResponse = new ChukasaResponse("release all tuner");
        return chukasaResponse;
    }
}
