package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.service.filer.IVideoFileService;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class FileOperator implements IFileOperator {

    private static final Logger log = LoggerFactory.getLogger(FileOperator.class);

    private final IVideoFileService videoFileService;

    @Autowired
    public FileOperator(
            IVideoFileService videoFileService
    ) {
        this.videoFileService = requireNonNull(videoFileService);
    }

    @Override
    public List<VideoFile> getVideoFileList() {
        return videoFileService.getVideoFileList();
    }
}
