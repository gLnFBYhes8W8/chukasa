package pro.hirooka.chukasa.domain.service.hls.remover;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Service
public class ChukasaHlsFileRemoverService implements IChukasaHlsFileRemoverService {

    private static final Logger log = LoggerFactory.getLogger(ChukasaHlsFileRemoverService.class);

    private final SystemConfiguration systemConfiguration;

    @Autowired
    public ChukasaHlsFileRemoverService(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = requireNonNull(systemConfiguration, "systemConfiguration");
    }

    @Async
    @Override
    public void remove(String streamRootPath) {

        String tempPath = systemConfiguration.getTemporaryPath();

        log.info("remove command is called.");
        log.info("streamRootPath: {} and tempPath: {} are to be removed.", streamRootPath, tempPath);

        try {
            FileUtils.cleanDirectory(new File(streamRootPath));
            FileUtils.cleanDirectory(new File(tempPath));
            if((new File(streamRootPath)).delete() && (new File(tempPath)).delete()){
                log.info("all Chukasa HLS files have been removed completely.");
            }else{
                log.warn("all Chukasa HLS files have not been removed completely.");
            }
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
    }
}
