package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class ProgramOperator implements IProgramOperator {

    private static final Logger log = LoggerFactory.getLogger(ProgramOperator.class);

    private final IProgramService programService;
    private final ISystemService systemService;

    @Autowired
    public ProgramOperator(
            IProgramService programService,
            ISystemService systemService
    ) {
        this.programService = requireNonNull(programService);
        this.systemService = requireNonNull(systemService);
    }

    @Async
    @Override
    public void deleteOldProgramList() {

        if(systemService.isMongoDB()) {
            Date date = new Date();
            Instant instant = Instant.ofEpochMilli(date.getTime());
            ZonedDateTime zonedDateTime = ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).minusDays(1);
            int year = zonedDateTime.getYear();
            int month = zonedDateTime.getMonthValue();
            int day = zonedDateTime.getDayOfMonth();
            ZonedDateTime thresholdZonedDateTime = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault());

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            String thresholdZonedDateTimeString = thresholdZonedDateTime.format(dateTimeFormatter);
            log.info("thresholdZonedDateTime = {}, {}", thresholdZonedDateTimeString, thresholdZonedDateTime.toEpochSecond());

            List<Program> toBeDeletedProgramList = programService.deleteByEnd(thresholdZonedDateTime.toEpochSecond() * 1000);
            log.info("toBeDeletedProgramList.size() = {}", toBeDeletedProgramList.size());
            toBeDeletedProgramList.forEach(program -> programService.delete(program.getId()));
        }
    }


}
