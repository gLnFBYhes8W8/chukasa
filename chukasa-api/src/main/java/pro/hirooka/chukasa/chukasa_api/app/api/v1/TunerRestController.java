package pro.hirooka.chukasa.chukasa_api.app.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.chukasa_api.app.api.v1.exception.ChukasaInternalServerErrorException;
import pro.hirooka.chukasa.chukasa_api.domain.model.ChukasaResponse;
import pro.hirooka.chukasa.chukasa_common.domain.service.ITunerManagementService;

@Slf4j
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
