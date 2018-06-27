package pro.hirooka.chukasa.domain.service.common;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;
import pro.hirooka.chukasa.domain.model.recorder.Program;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HyarukaClientServiceTest {

    @Autowired
    IHyarukaClientService hyarukaClientService;

    @Ignore
    @Test
    public void testGetProgramListNow(){
        requireNonNull(hyarukaClientService);
        List<Program> programList = hyarukaClientService.getProgramListNow();
        assertThat(programList.size()).isGreaterThan(0);
    }

    @Ignore
    @Test
    public void testGetProgramListByChannelRecording(){
        requireNonNull(hyarukaClientService);
        List<Program> programList = hyarukaClientService.getProgramListByChannelRecording(0);
        assertThat(programList.size()).isGreaterThan(0);
    }

    @Ignore
    @Test
    public void testGetProgramByChannelRecordingNow(){
        requireNonNull(hyarukaClientService);
        Program program = hyarukaClientService.getProgramByChannelRecordingNow(0);
        assertThat(program.getChannelRecording()).isEqualTo(0);
    }

    @Ignore
    @Test
    public void testGetStream(){
        requireNonNull(hyarukaClientService);
        File file = new File("/tmp/もきゅ.ts");
        Future<ResponseEntity<File>> future = hyarukaClientService.getStream(TunerType.GR, 0, 10_000, file);
        try {
            Thread.sleep(10_000 + 3_000);
            if(!future.isDone()){
                future.cancel(true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
