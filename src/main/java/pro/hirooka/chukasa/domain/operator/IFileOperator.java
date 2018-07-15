package pro.hirooka.chukasa.domain.operator;

import pro.hirooka.chukasa.domain.model.common.VideoFile;

import java.util.List;

public interface IFileOperator {
    List<VideoFile> getVideoFileList();
}
