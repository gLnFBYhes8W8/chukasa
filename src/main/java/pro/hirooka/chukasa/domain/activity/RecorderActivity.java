package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.operator.IRecorderOperator;

import javax.annotation.PostConstruct;

import static java.util.Objects.requireNonNull;

@Service
public class RecorderActivity implements IRecorderActivity {

    private static final Logger log = LoggerFactory.getLogger(RecorderActivity.class);

    private final IRecorderOperator recorderOperator;

    @Autowired
    public RecorderActivity(
            IRecorderOperator recorderOperator
    ) {
        this.recorderOperator = requireNonNull(recorderOperator);
    }

    @PostConstruct
    void init(){
        // TODO:
        //recorderOperator.onBoot();
    }

    @Override
    public ReservedProgram create(ReservedProgram reservedProgram) {
        return recorderOperator.create(reservedProgram);
    }
}
