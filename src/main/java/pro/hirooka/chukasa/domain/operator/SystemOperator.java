package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.service.common.ISystemService;

import static java.util.Objects.requireNonNull;

@Service
public class SystemOperator implements ISystemOperator {

    private static final Logger log = LoggerFactory.getLogger(SystemOperator.class);

    private final ISystemService systemService;

    @Autowired
    public SystemOperator(
            ISystemService systemService
    ) {
        this.systemService = requireNonNull(systemService);
    }

    @Override
    public boolean hasFfmpeg() {
        return systemService.isFFmpeg();
    }

    @Override
    public boolean hasMongoDb() {
        return systemService.isFFmpeg();
    }

    @Override
    public boolean hasWebcam() {
        return systemService.isWebCamera();
    }
}
