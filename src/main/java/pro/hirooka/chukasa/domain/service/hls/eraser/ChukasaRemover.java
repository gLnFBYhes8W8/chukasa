package pro.hirooka.chukasa.domain.service.hls.eraser;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Component
public class ChukasaRemover {

    private static final Logger log = LoggerFactory.getLogger(ChukasaRemover.class);

    private String streamRootPath;

    public void setStreamRootPath(String streamRootPath) {
        this.streamRootPath = streamRootPath;
    }

    @Autowired
    SystemConfiguration systemConfiguration;

    public void remove(){

        requireNonNull(systemConfiguration, "systemConfiguration");
        String tempPath = systemConfiguration.getTemporaryPath();

        log.info("remove command is called.");
        log.info("streamRootPath: {} and tempPath: {} are to be removed.", streamRootPath, tempPath);

        try {
            FileUtils.cleanDirectory(new File(streamRootPath));
            FileUtils.cleanDirectory(new File(tempPath));
            if((new File(streamRootPath)).delete() && (new File(tempPath)).delete()){
                log.info("all Chukasa files have been removed completely.");
            }else{
                log.warn("all Chukasa files have not been removed completely.");
            }
        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }

    }
}
