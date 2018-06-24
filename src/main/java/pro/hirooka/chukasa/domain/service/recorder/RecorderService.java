package pro.hirooka.chukasa.domain.service.recorder;

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
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.recorder.RecorderConfiguration;
import pro.hirooka.chukasa.domain.model.recorder.RecordingProgramModel;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.repository.recorder.IReservedProgramRepository;
import pro.hirooka.chukasa.domain.service.common.ISystemService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecorderService implements IRecorderService {

    private static final Logger log = LoggerFactory.getLogger(RecorderService.class);

    @Autowired
    RecorderConfiguration recorderConfiguration;
    @Autowired
    SystemConfiguration systemConfiguration;
    @Autowired
    IReservedProgramRepository reservedProgramRepository;
    @Autowired
    ISystemService systemService;
    @Autowired
    IRecordingProgramManagementComponent recordingProgramManagementComponent;
    @Autowired
    private IRecorderRunnerService recorderRunnerService;
    @Autowired
    private MongoDBConfiguration mongoDBConfiguration;
    @Autowired
    private Recorder recorder;

    @PostConstruct
    public void init(){

        if(systemService.isTuner() && systemService.isRecxxx() && systemService.isFFmpeg() && systemService.isMongoDB()){
            List<ReservedProgram> reservedProgramList = read();
            for(ReservedProgram reservedProgram : reservedProgramList){
                if(true){ // TODO: checker

                    long startRecording = reservedProgram.getStartRecording();
                    long stopRecording = reservedProgram.getStopRecording();
                    long now = new Date().getTime();
                    if (startRecording > now && stopRecording > now) {

                        // reserve
                        log.info("reservation: {}", reservedProgram.toString());

                        Recorder recorder = new Recorder();
                        recorder.reserve(reservedProgram);

                    } else if (now > startRecording && stopRecording > now) {

                        // start recording immediately
                        log.info("no reservation, direct recording");

                        long recordingDuration = (stopRecording - now) / 1000;
                        reservedProgram.setRecordingDuration(recordingDuration);
                        recorderRunnerService.submit(reservedProgram);

                        RecordingProgramModel recordingProgramModel = new RecordingProgramModel();
                        recordingProgramModel.setFileName(reservedProgram.getFileName());
                        recordingProgramModel.setStartRecording(reservedProgram.getStartRecording());
                        recordingProgramModel.setStopRecording(reservedProgram.getStopRecording());
                        recordingProgramManagementComponent.create(reservedProgram.getId(), recordingProgramModel);

                    } else if (now > startRecording && now > stopRecording) {

                        //  nothing to do... (as error)
                        log.info("no reservation, no recording");

                    } else {
                        // todo
                    }

                }else{
                    log.info("skip (in recording...) {}", reservedProgram.toString());
                }
            }
        }
    }

    @Override
    public ReservedProgram create(ReservedProgram reservedProgram) {

        List<ReservedProgram> reservedProgramList = reservedProgramRepository.findAll();
        if(reservedProgramList.size() > 0) {
            int n = Collections.max(reservedProgramList.stream().map(ReservedProgram::getId).collect(Collectors.toList()));
            n++;
            reservedProgram.setId(n);
        }else{
            reservedProgram.setId(0);
        }

        long startRecording = reservedProgram.getBegin() - recorderConfiguration.getStartMargin() * 1000;
        long stopRecording = reservedProgram.getEnd() + recorderConfiguration.getStopMargin() * 1000;
        long recordingDuration = (stopRecording - startRecording) / 1000;
        reservedProgram.setStartRecording(startRecording);
        reservedProgram.setStopRecording(stopRecording);
        reservedProgram.setRecordingDuration(recordingDuration);

        String fileName = reservedProgram.getPhysicalLogicalChannel() + "_" + reservedProgram.getBegin() + "_" + reservedProgram.getTitle()  + ".ts";
        reservedProgram.setFileName(fileName);

        long now = new Date().getTime();

        if(startRecording > now && stopRecording > now){

            // reserve
            log.info("reservation");

            recorder.reserve(reservedProgram);

        }else if(now > startRecording && stopRecording > now){

            // start recording immediately
            log.info("no reservation, direct recording");

            recordingDuration = (stopRecording - now) / 1000;
            reservedProgram.setRecordingDuration(recordingDuration);
            recorderRunnerService.submit(reservedProgram);

            RecordingProgramModel recordingProgramModel = new RecordingProgramModel();
            recordingProgramModel.setFileName(reservedProgram.getFileName());
            recordingProgramModel.setStartRecording(reservedProgram.getStartRecording());
            recordingProgramModel.setStopRecording(reservedProgram.getStopRecording());
            recordingProgramManagementComponent.create(reservedProgram.getId(), recordingProgramModel);

        }else if(now > startRecording && now > stopRecording){

            //  nothing to do... (as error)
            log.info("no reservation, no recording");

        }else{
            //
        }

        return reservedProgramRepository.save(reservedProgram);
    }

    @Override
    public List<ReservedProgram> read() {
        return reservedProgramRepository.findAll();
    }

    @Override
    public ReservedProgram read(int id) {
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(mongoDBConfiguration.getHost(), mongoDBConfiguration.getPort()), mongoDBConfiguration.getDatabase()));
        Query query = new Query(Criteria.where("id").is(id)).with(new Sort(Sort.Direction.ASC,"id"));
        List<ReservedProgram> reservedProgramList = mongoTemplate.find(query, ReservedProgram.class);
        if(reservedProgramList.size() != 1){
            log.error("e");
            return null;
        }else{
            return reservedProgramList.get(0);
        }
    }

    @Override
    public ReservedProgram update(ReservedProgram reservedProgram) {
        return reservedProgramRepository.save(reservedProgram);
    }

    @Override
    public void delete(int id) {
        reservedProgramRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        reservedProgramRepository.deleteAll();
    }
}

