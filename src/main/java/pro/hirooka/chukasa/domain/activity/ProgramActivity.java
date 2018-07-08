package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.operator.IProgramOperator;

import javax.annotation.PostConstruct;

import static java.util.Objects.requireNonNull;

@Service
public class ProgramActivity implements IProgramActivity {

    private static final Logger log = LoggerFactory.getLogger(ProgramActivity.class);

    private final IProgramOperator programOperator;

    @Autowired
    public ProgramActivity(
            IProgramOperator programOperator
    ) {
        this.programOperator = requireNonNull(programOperator);
    }

    @PostConstruct
    void init(){
        programOperator.deleteOldProgramList();
    }

    @Scheduled(cron = "${epg.old-program-deletion-schedule-cron}")
    void cron(){
        log.info("cron -----> ");
        programOperator.deleteOldProgramList();
    }
}
