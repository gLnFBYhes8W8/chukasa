package pro.hirooka.chukasa.domain.service.common;

import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;

public interface ISystemService {
    boolean isFFmpeg();
    boolean isWebCamera();
    String getWebCameraDeviceName();
    boolean isMongoDB();
    boolean canWebCameraStreaming();
    boolean canFileStreaming();
    boolean canPTxStreaming();
    boolean canRecording();
    FfmpegVcodecType getFfmpegVcodecType(String userAgent);
    String getStreamRootPath(String servletRealPath);
}
