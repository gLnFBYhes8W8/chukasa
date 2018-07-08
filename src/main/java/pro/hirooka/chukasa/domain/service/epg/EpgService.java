package pro.hirooka.chukasa.domain.service.epg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.MongoDBConfiguration;
import pro.hirooka.chukasa.domain.config.epg.EpgConfiguration;
import pro.hirooka.chukasa.domain.model.epg.*;
import pro.hirooka.chukasa.domain.repository.epg.ILatestEpgAcquisitionRepository;
import pro.hirooka.chukasa.domain.repository.epg.IProgramRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@Service
public class EpgService implements IEpgService {

    private static final Logger log = LoggerFactory.getLogger(EpgService.class);

    private final EpgConfiguration epgConfiguration;
    private IProgramRepository programRepository;
    private ILatestEpgAcquisitionRepository latestEpgAcquisitionRepository;
    private MongoDBConfiguration mongoDBConfiguration;

    @Autowired
    public EpgService(
            EpgConfiguration epgConfiguration,
            IProgramRepository programRepository,
            ILatestEpgAcquisitionRepository latestEpgAcquisitionRepository,
            MongoDBConfiguration mongoDBConfiguration
    ) {
        this.epgConfiguration = requireNonNull(epgConfiguration);
        this.programRepository = requireNonNull(programRepository);
        this.latestEpgAcquisitionRepository = requireNonNull(latestEpgAcquisitionRepository);
        this.mongoDBConfiguration = requireNonNull(mongoDBConfiguration);
    }

    @Override
    public List<ChannelConfiguration> getChannelConfigurationList() {
        List<ChannelConfiguration> channelConfigurationList = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource(epgConfiguration.getChannelConfiguration());
            ObjectMapper objectMapper = new ObjectMapper();
            channelConfigurationList = objectMapper.readValue(resource.getInputStream(), ChannelConfigurationWrapper.class).getChannelConfigurationList();
            log.info("channelConfigurationList = {}", channelConfigurationList.toString());
        } catch (IOException e) {
            log.error("invalid channel_settings.json: {} {}", e.getMessage(), e);
        }
        return channelConfigurationList;
    }

    @Override
    public List<Tuner> getTunerList() {
        final String path = IEpgService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String[] pathArray = path.split(FILE_SEPARATOR);
        String currentPath = "";
        for(int i = 1; i < pathArray.length - 3; i++){
            currentPath = currentPath + FILE_SEPARATOR + pathArray[i];
        }
        log.info("currentPath = {}", currentPath);

        List<Tuner> tunerList = new ArrayList<>();
        try {
            Resource resource = new FileSystemResource(currentPath + FILE_SEPARATOR + epgConfiguration.getTuner());
            if(!resource.exists()){
                resource = new ClassPathResource(epgConfiguration.getTuner());
            }
            final ObjectMapper objectMapper = new ObjectMapper();
            tunerList = objectMapper.readValue(resource.getInputStream(), TunerWrapper.class).getTunerList();
            log.info("tunerList = {}", tunerList.toString());
        } catch (IOException e) {
            log.error("invalid tuner.json: {} {}", e.getMessage(), e);
        }
        return tunerList;
    }

    @Override
    public TunerType getTunerType(int channelRecording) {
        for(ChannelConfiguration channelConfiguration : getChannelConfigurationList()){
            if(channelConfiguration.getChannelRecording() == channelRecording){
                if(channelConfiguration.getType() == TunerType.GR){
                    return TunerType.GR;
                }else if(channelConfiguration.getType() == TunerType.BS){
                    return TunerType.BS;
                }
            }
        }
        return null;
    }

    @Override
    public Program create(Program program) {
        return programRepository.save(program);
    }

    @Override
    public LatestEpgAcquisition createLatestEpgAcquisition(LatestEpgAcquisition latestEpgAcquisition) {
        return latestEpgAcquisitionRepository.save(latestEpgAcquisition);
    }

    @Override
    public LatestEpgAcquisition readLatestEpgAcquisition(int unique) {
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort()), mongoDBConfiguration.getDatabase()));
        //Query query = new Query(Criteria.where("unique").is(unique)).with(new Sort(Sort.Direction.ASC,"unique"));
        List<LatestEpgAcquisition> latestEpgAcquisitionList = mongoTemplate.findAll(LatestEpgAcquisition.class);
        if(latestEpgAcquisitionList.size() != 1){
            log.error("LatestEpgAcquisition is duplicated or none.");
            return null;
        }else{
            return latestEpgAcquisitionList.get(0);
        }
    }

    @Override
    public LatestEpgAcquisition updateLatestEpgAcquisition(LatestEpgAcquisition latestEpgAcquisition) {
        return latestEpgAcquisitionRepository.save(latestEpgAcquisition);
    }

    @Override
    public void deleteLatestEpgAcquisition(int unique) {
        latestEpgAcquisitionRepository.deleteById(unique);
    }
}
