package pro.hirooka.chukasa.domain.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;
import pro.hirooka.chukasa.domain.model.recorder.Program;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

@Service
public class HyarukaClientService implements IHyarukaClientService {

    private static final Logger log = LoggerFactory.getLogger(HyarukaClientService.class);

    private final HyarukaConfiguration hyarukaConfiguration;

    @Autowired
    public HyarukaClientService(HyarukaConfiguration hyarukaConfiguration) {
        this.hyarukaConfiguration = requireNonNull(hyarukaConfiguration);
    }

    @Override
    public List<Program> getProgramListNow() {
        final String HYARUKA_URI = getHyarukaUri("/programs/now");
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<Program[]> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    @Override
    public List<Program> getProgramListByChannelRecording(int channel) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channel);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<Program[]> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    @Override
    public Program getProgramByChannelRecordingNow(int channel) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channel + "/now");
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<Program> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program.class);
        return responseEntity.getBody();
    }

    @Async
    @Override
    public Future<ResponseEntity<File>> getStream(TunerType tunerType, int channelRecording, long duration, File file) {
        final String HYARUKA_URI = getHyarukaUri("/streams/" + tunerType.name().toUpperCase() + "/" + channelRecording);
        final RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<File> responseEntity = restTemplate.execute(HYARUKA_URI, HttpMethod.GET, null, new ResponseExtractor<ResponseEntity<File>>() {
            @Override
            public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
                FileCopyUtils.copy(response.getBody(), new FileOutputStream(file));
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(file);
            }
        });
        return new AsyncResult<>(responseEntity);
    }

    private String getHyarukaUri(String path){
        final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name().toLowerCase();
        final String HYARUKA_HOST = hyarukaConfiguration.getHost();
        final int HYARUKA_PORT = hyarukaConfiguration.getPort();
        final String HYARUKA_API_VERSION = hyarukaConfiguration.getApiVersion();
        final String HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://" + HYARUKA_HOST + ":" + HYARUKA_PORT
                + "/api/" + HYARUKA_API_VERSION + path;
        return HYARUKA_URI;
    }
}
