package pro.hirooka.chukasa.domain.service.recorder;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

import java.util.List;
import java.util.Objects;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IRecorderServiceTest {

    @Autowired
    IRecorderService recorderService;

    @Ignore
    @Test
    public void test(){
        Objects.requireNonNull(recorderService, "null");
        List<ReservedProgram> programList = recorderService.read();
        programList.toString();
        log.info("");
    }
}
