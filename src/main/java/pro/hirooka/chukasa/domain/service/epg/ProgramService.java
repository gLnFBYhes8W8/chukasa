package pro.hirooka.chukasa.domain.service.epg;

import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.MongoDBConfiguration;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.repository.epg.IProgramRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class ProgramService implements IProgramService {

    private static final Logger log = LoggerFactory.getLogger(ProgramService.class);

    private final IProgramRepository programRepository;
    private final MongoDBConfiguration mongoDBConfiguration;

    @Autowired
    public ProgramService(
            IProgramRepository programRepository,
            MongoDBConfiguration mongoDBConfiguration
    ) {
        this.programRepository = requireNonNull(programRepository);
        this.mongoDBConfiguration = requireNonNull(mongoDBConfiguration);
    }

    @Override
    public Program create(Program program) {
        return programRepository.save(program);
    }

    @Override
    public List<Program> read() {
        return programRepository.findAll();
    }

    @Override
    public List<Program> read(int channelRemoteControl) {
        return programRepository.findAllByChannelRemoteControl(channelRemoteControl);
    }

    @Override
    public List<Program> readByBeginDate(String beginDate) {
        return null;
    }

    @Override
    public List<Program> read(int ch, String beginDate) {
        return null;
    }

    @Override
    public List<Program> readByNow(long now) {
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort()), mongoDBConfiguration.getDatabase()));
        Query query = new Query(Criteria.where("start").lte(now).and("end").gte(now)).with(new Sort(Sort.Direction.ASC, "channelRemoteControl"));
        return mongoTemplate.find(query, Program.class);
    }

    @Override
    public List<Program> readOneDayByNow(long now) {
        Instant instant = Instant.ofEpochMilli(now);
        ZonedDateTime nowZonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        ZonedDateTime tomorrowZonedDateTime = ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).plusDays(1);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        String nowZonedDateTimeString = nowZonedDateTime.format(dateTimeFormatter);
        String tomorrowZonedDateTimeString = tomorrowZonedDateTime.format(dateTimeFormatter);
        log.info("nowZonedDateTime = {}, {}", nowZonedDateTimeString, nowZonedDateTime.toEpochSecond());
        log.info("tomorrowZonedDateTime = {}, {}", tomorrowZonedDateTimeString, tomorrowZonedDateTime.toEpochSecond());

        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort()), mongoDBConfiguration.getDatabase()));
        Query query = new Query(Criteria.where("begin").lte(now).and("end").lte(tomorrowZonedDateTime.toEpochSecond() * 1000)).with(new Sort(Sort.Direction.ASC, "channelRemoteControl"));
        return mongoTemplate.find(query, Program.class);
    }

    @Override
    public Program read(String id) {
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort()), mongoDBConfiguration.getDatabase()));
        Query query = new Query(Criteria.where("id").is(id)).with(new Sort(Sort.Direction.ASC,"id"));
        List<Program> programList = mongoTemplate.find(query, Program.class);
        if(programList.size() != 1){
            log.error("e");
            return null;
        }else{
            return programList.get(0);
        }
    }

    @Override
    public Program readNow(int ch, long now) {
        return null;
    }

    @Override
    public Program update(Program Program) {
        return programRepository.save(Program);
    }

    @Override
    public void delete(String id) {
        programRepository.deleteById(id);
    }

    @Override
    public List<Program> deleteByEnd(long threshold) {
        return programRepository.deleteByEnd(threshold);
    }

    @Override
    public void deleteAll() {
        programRepository.deleteAll();
    }

    @Override
    public int getNumberOfPhysicalLogicalChannels() {
        return programRepository.findAll().stream().map(Program::getChannel).collect(Collectors.toSet()).size();
    }
}
