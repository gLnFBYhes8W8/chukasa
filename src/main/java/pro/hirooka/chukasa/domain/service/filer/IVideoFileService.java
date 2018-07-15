package pro.hirooka.chukasa.domain.service.filer;

import pro.hirooka.chukasa.domain.model.common.M4vFile;
import pro.hirooka.chukasa.domain.model.common.VideoFile;
import pro.hirooka.chukasa.domain.model.common.type.M4vType;

import java.util.List;

public interface IVideoFileService {
    List<VideoFile> getAll();
    List<M4vFile> getAllM4v(M4vType m4vType);
    List<VideoFile> getVideoFileList();
}
