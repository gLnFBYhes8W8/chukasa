package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.operator.ISystemOperator;

import static java.util.Objects.requireNonNull;

@Service
public class SystemActivity implements ISystemActivity {

    private static final Logger log = LoggerFactory.getLogger(SystemActivity.class);

    private final ISystemOperator systemOperator;

    @Autowired
    public SystemActivity(
            ISystemOperator systemOperator
    ) {
        this.systemOperator = requireNonNull(systemOperator);
    }

    @Override
    public boolean hasFfmpeg() {
        return systemOperator.hasFfmpeg();
    }

    @Override
    public boolean hasMongoDb() {
        return systemOperator.hasMongoDb();
    }

    @Override
    public boolean hasWebcam() {
        return systemOperator.hasWebcam();
    }
}
