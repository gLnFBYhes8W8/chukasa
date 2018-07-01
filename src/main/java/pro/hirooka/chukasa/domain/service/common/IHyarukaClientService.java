package pro.hirooka.chukasa.domain.service.common;

import org.springframework.http.ResponseEntity;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;
import pro.hirooka.chukasa.domain.model.recorder.Program;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public interface IHyarukaClientService {
    List<Program> getProgramListNow();
    List<Program> getProgramListByChannelRecording(int channel);
    Program getProgramByChannelRecordingNow(int channel);
    Future<ResponseEntity<File>> getStream(TunerType tunerType, int channelRecording, long duration, File file);
    String getUnixDomainSocketPath(TunerType tunerType, int channelRecording);
}
