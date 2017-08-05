package pro.hirooka.chukasa.chukasa_filer.domain.service;

import pro.hirooka.chukasa.chukasa_common.domain.model.VideoFile;

import java.util.List;

public interface IVideoFileService {
    List<VideoFile> getAll();
}
