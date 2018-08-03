package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.epg.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;
import pro.hirooka.chukasa.domain.service.epg.IProgramService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class ProgramOperator implements IProgramOperator {

    private static final Logger log = LoggerFactory.getLogger(ProgramOperator.class);

    private final IProgramService programService;
    private final IEpgService epgService;
    private final ISystemService systemService;

    @Autowired
    public ProgramOperator(
            IProgramService programService,
            IEpgService epgService,
            ISystemService systemService
    ) {
        this.programService = requireNonNull(programService);
        this.epgService = requireNonNull(epgService);
        this.systemService = requireNonNull(systemService);
    }

    @Async
    @Override
    public void deleteOldProgramList() {

        if(systemService.isMongoDB()) {
            final Date date = new Date();
            final Instant instant = Instant.ofEpochMilli(date.getTime());
            final ZonedDateTime zonedDateTime = ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).minusDays(1);
            final int year = zonedDateTime.getYear();
            final int month = zonedDateTime.getMonthValue();
            final int day = zonedDateTime.getDayOfMonth();
            final ZonedDateTime thresholdZonedDateTime = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault());

            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            final String thresholdZonedDateTimeString = thresholdZonedDateTime.format(dateTimeFormatter);
            log.info("thresholdZonedDateTime = {}, {}", thresholdZonedDateTimeString, thresholdZonedDateTime.toEpochSecond());

            final List<Program> toBeDeletedProgramList = programService.deleteByEnd(thresholdZonedDateTime.toEpochSecond() * 1000);
            log.info("toBeDeletedProgramList.size() = {}", toBeDeletedProgramList.size());
            toBeDeletedProgramList.forEach(program -> programService.delete(program.getId()));
        }
    }

    @Override
    public List<Program> getProgramListNow() {

        List<Program> programList = new ArrayList<>();
        boolean hasEpg = false;
        final List<ChannelConfiguration> channelConfigurationList = epgService.getChannelConfigurationList();
        if(systemService.isMongoDB()){
            programList = programService.readByNow(new Date().getTime()).stream()
                    .sorted(Comparator.comparing(Program::getChannelRemoteControl))
                    .collect(Collectors.toList());
            if(programList != null
                    && programList.size() > 0
                    && epgService.readLatestEpgAcquisition(1) != null){
                hasEpg = true;
            }
        }
        if(!hasEpg){
            for(ChannelConfiguration channelConfiguration : channelConfigurationList){
                try {
                    Program program = new Program();
                    program.setChannelRecording(channelConfiguration.getChannelRecording());
                    program.setChannelRemoteControl(channelConfiguration.getChannelRemoteControl());
                    assert programList != null;
                    programList.add(program);
                }catch (NumberFormatException e){
                    log.error("invalid value : {}", e.getCause().toString());
                }
            }
        }
        assert programList != null;
        programList.sort(Comparator.comparingInt(Program::getChannelRemoteControl));

        return programList;
    }


}
