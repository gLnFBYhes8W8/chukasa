package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.epg.EpgConfiguration;
import pro.hirooka.chukasa.domain.model.epg.LatestEpgAcquisition;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.service.common.IHyarukaClientService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;

import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class EpgOperator implements IEpgOperator {

    private static final Logger log = LoggerFactory.getLogger(EpgOperator.class);

    private final EpgConfiguration epgConfiguration;
    private final IEpgService epgService;
    private final IHyarukaClientService hyarukaClientService;
    private final ISystemService systemService;

    @Autowired
    public EpgOperator(
            EpgConfiguration epgConfiguration,
            IEpgService epgService,
            IHyarukaClientService hyarukaClientService,
            ISystemService systemService
    ) {
        this.epgConfiguration = requireNonNull(epgConfiguration);
        this.epgService = requireNonNull(epgService);
        this.hyarukaClientService = requireNonNull(hyarukaClientService);
        this.systemService = requireNonNull(systemService);
    }

    @Async
    @Override
    public void persist() {
        epgService
                .getChannelConfigurationList()
                .forEach(channelConfiguration -> {
                    List<Program> programList =
                            hyarukaClientService
                                    .getProgramListByChannelRecording(channelConfiguration.getChannelRecording());
                    programList.forEach(epgService::create);
                });

        // TODO: simple
        final LatestEpgAcquisition previousLatestEpgAcquisition = epgService.readLatestEpgAcquisition(1);
        final LatestEpgAcquisition newLatestEpgAcquisition;
        final Date date = new Date();
        if(previousLatestEpgAcquisition == null){
            newLatestEpgAcquisition = new LatestEpgAcquisition();
            newLatestEpgAcquisition.setDate(date.getTime());
            newLatestEpgAcquisition.setUnique(1);
        }else{
            previousLatestEpgAcquisition.setDate(date.getTime());
            newLatestEpgAcquisition = previousLatestEpgAcquisition;
        }
        final LatestEpgAcquisition updatedLatestEpgAcquisition = epgService.updateLatestEpgAcquisition(newLatestEpgAcquisition);
        log.info("updatedLatestEpgAcquisition = {}", updatedLatestEpgAcquisition.getDate());
    }

    @Override
    public LatestEpgAcquisition readLatestEpgAcquisition(int unique) {
        return epgService.readLatestEpgAcquisition(unique);
    }
}
