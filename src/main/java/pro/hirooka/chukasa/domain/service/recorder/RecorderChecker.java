package pro.hirooka.chukasa.domain.service.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

// TODO: update

public class RecorderChecker {

    private static final Logger log = LoggerFactory.getLogger(RecorderChecker.class);

    static public boolean isAlreadyRun(ReservedProgram reservedProgram){
        return false;
    }
}
