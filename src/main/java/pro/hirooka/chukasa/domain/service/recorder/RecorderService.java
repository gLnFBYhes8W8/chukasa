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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import pro.hirooka.chukasa.domain.config.common.MongoDBConfiguration;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.repository.recorder.IReservedProgramRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.requireNonNull;

@Service
public class RecorderService implements IRecorderService {

    private static final Logger log = LoggerFactory.getLogger(RecorderService.class);

    private final MongoDBConfiguration mongoDBConfiguration;
    private final IReservedProgramRepository reservedProgramRepository;

    private Map<Integer, ScheduledFuture> scheduledFutureMap = new HashMap<>();

    @Autowired
    public RecorderService(
            MongoDBConfiguration mongoDBConfiguration,
            IReservedProgramRepository reservedProgramRepository
    ) {
        this.mongoDBConfiguration = requireNonNull(mongoDBConfiguration);
        this.reservedProgramRepository = requireNonNull(reservedProgramRepository);
    }

    @Override
    public ReservedProgram create(ReservedProgram reservedProgram) {
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

    @Async
    @Override
    public void recordDirectly(ReservedProgram reservedProgram, String hyarukaUri) {

        final int channelRecording = reservedProgram.getChannelRecording();
        final long startRecording = reservedProgram.getStartRecording();
        final long stopRecording = reservedProgram.getStopRecording();
        final long duration = reservedProgram.getRecordingDuration();
        final long thumbnailPoint = duration / 3;
        final String title = reservedProgram.getTitle();
        final String fileName = reservedProgram.getFileName();

        log.info("start recording... [{}] {} --> {}", channelRecording, title, hyarukaUri);

        final File file = new File(fileName);
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<File> responseEntity = restTemplate.execute(hyarukaUri, HttpMethod.GET, null, new ResponseExtractor<ResponseEntity<File>>() {
            @Override
            public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
                FileCopyUtils.copy(response.getBody(), new FileOutputStream(file));
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(file);
            }
        });
        log.info("{}", responseEntity.getStatusCode());
    }

    @Override
    public void reserve(RecorderRunnable recorderRunnable) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(scheduledExecutorService);
        final Date date = new Date(recorderRunnable.getReservedProgram().getStart());
        final ScheduledFuture scheduledFuture = taskScheduler.schedule(recorderRunnable, date);
        scheduledFutureMap.put(recorderRunnable.getReservedProgram().getId(), scheduledFuture);
        log.info("scheduler: {}", date.toString());
    }

    @Override
    public void cancel(int id) {
        if(scheduledFutureMap.containsKey(id)){
            scheduledFutureMap.get(id).cancel(true);
        }
    }

    @Override
    public void cancelAll() {

    }

    @Override
    public Map<Integer, ScheduledFuture> getScheduledFutureMap() {
        return scheduledFutureMap;
    }
}

