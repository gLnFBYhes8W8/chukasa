package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.operator.IFileOperator;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class FileActivity implements IFileActivity {

    private static final Logger log = LoggerFactory.getLogger(FileActivity.class);

    private final IFileOperator fileOperator;

    @Autowired
    public FileActivity(
            IFileOperator fileOperator
    ) {
        this.fileOperator = requireNonNull(fileOperator);
    }

    @Override
    public List<VideoFile> getVideoFileList() {
        return fileOperator.getVideoFileList();
    }
}
