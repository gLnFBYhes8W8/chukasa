package pro.hirooka.chukasa.domain.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.epg.TunerType;

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
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        final ResponseEntity<Program[]> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    @Override
    public List<Program> getProgramListByChannelRecording(int channelRecording) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channelRecording);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        final ResponseEntity<Program[]> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    @Override
    public List<Program> getProgramListByChannelRemoteControl(int channelRemoteControl) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channelRemoteControl);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        final ResponseEntity<Program[]> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    @Override
    public Program getProgramByChannelRecordingNow(int channelRecording) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channelRecording + "/now");
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        final ResponseEntity<Program> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program.class);
        return responseEntity.getBody();
    }

    @Override
    public Program getProgramByChannelRemoteControlNow(int channelRemoteControl) {
        final String HYARUKA_URI = getHyarukaUri("/programs/" + channelRemoteControl + "/now");
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        final ResponseEntity<Program> responseEntity = restTemplate.getForEntity(HYARUKA_URI, Program.class);
        return responseEntity.getBody();
    }

    @Async
    @Override
    public Future<ResponseEntity<File>> getStream(int channelRemoteControl, long duration, File file) {
        final String HYARUKA_URI = getHyarukaUri("/streams" + "/" + channelRemoteControl);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        ResponseEntity<File> responseEntity = restTemplate.execute(HYARUKA_URI, HttpMethod.GET, null, new ResponseExtractor<ResponseEntity<File>>() {
            @Override
            public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
                FileCopyUtils.copy(response.getBody(), new FileOutputStream(file));
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(file);
            }
        });
        return new AsyncResult<>(responseEntity);
    }

    @Override
    public String getUnixDomainSocketPath(int channelRemoteControl) {
        final String HYARUKA_URI = getHyarukaUri("/streams" + "/" + channelRemoteControl);
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(hyarukaConfiguration.getUsername(), hyarukaConfiguration.getPassword()));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(HYARUKA_URI, String.class);
        return responseEntity.getBody().toString();
    }

    private String getHyarukaUri(String path){
        final String HYARUKA_USERNAME = hyarukaConfiguration.getUsername();
        final String HYARUKA_PASSWORD = hyarukaConfiguration.getPassword();
        final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name().toLowerCase();
        final String HYARUKA_HOST = hyarukaConfiguration.getHost();
        final int HYARUKA_PORT = hyarukaConfiguration.getPort();
        final String HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://"
                + HYARUKA_USERNAME + ":" + HYARUKA_PASSWORD + "@"
                + HYARUKA_HOST + ":" + HYARUKA_PORT
                + "/api" + path;
        return HYARUKA_URI;
    }
}
