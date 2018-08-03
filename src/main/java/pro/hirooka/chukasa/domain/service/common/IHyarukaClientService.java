package pro.hirooka.chukasa.domain.service.common;

import org.springframework.http.ResponseEntity;
import pro.hirooka.chukasa.domain.model.epg.Program;
import pro.hirooka.chukasa.domain.model.epg.TunerType;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public interface IHyarukaClientService {
    List<Program> getProgramListNow();
    List<Program> getProgramListByChannelRecording(int channelRecording);
    List<Program> getProgramListByChannelRemoteControl(int channelRemoteControl);
    Program getProgramByChannelRecordingNow(int channelRecording);
    Program getProgramByChannelRemoteControlNow(int channelRemoteControl);
    Future<ResponseEntity<File>> getStream(int channelRemoteControl, long duration, File file);
    String getUnixDomainSocketPath(int channelRemoteControl);
}
