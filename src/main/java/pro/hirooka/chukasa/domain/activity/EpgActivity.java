package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.epg.EpgConfiguration;
import pro.hirooka.chukasa.domain.model.epg.LatestEpgAcquisition;
import pro.hirooka.chukasa.domain.operator.IEpgOperator;

import javax.annotation.PostConstruct;

import java.util.Date;

import static java.util.Objects.requireNonNull;

@Service
public class EpgActivity implements IEpgActivity {

    private static final Logger log = LoggerFactory.getLogger(EpgActivity.class);

    private final EpgConfiguration epgConfiguration;
    private final IEpgOperator epgOperator;

    @Autowired
    public EpgActivity(
            EpgConfiguration epgConfiguration,
            IEpgOperator epgOperator
    ) {
        this.epgConfiguration = requireNonNull(epgConfiguration);
        this.epgOperator = requireNonNull(epgOperator);
    }

    @PostConstruct
    void init(){
        // TODO: if isMongoDB
        LatestEpgAcquisition latestEpgAcquisition = epgOperator.readLatestEpgAcquisition(1);
        if(latestEpgAcquisition == null){
            epgOperator.persist();
        }else{
            final Date date = new Date();
            final long now = date.getTime();
            long latest = latestEpgAcquisition.getDate();
            final long diff = latest - now;
            if(diff > epgConfiguration.getAcquisitionOnBootIgnoredInterval()){
                epgOperator.persist();
            }else{
                log.info("epgOperator: diff <= interval");
            }
        }
    }

    @Scheduled(cron = "${epg.acquisition-schedule-cron}")
    void cron(){
        log.info("cron -----> ");
        // TODO: if isMongoDB
        epgOperator.persist();
    }
}
