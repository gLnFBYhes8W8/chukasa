package pro.hirooka.chukasa.chukasa_filer.domain.service;

import pro.hirooka.chukasa.chukasa_common.domain.model.M4vFile;
import pro.hirooka.chukasa.chukasa_common.domain.model.VideoFile;
import pro.hirooka.chukasa.chukasa_common.domain.model.enums.M4vType;

import java.util.List;

public interface IVideoFileService {
    List<VideoFile> getAll();
    List<M4vFile> getAllM4v(M4vType m4vType);
}
