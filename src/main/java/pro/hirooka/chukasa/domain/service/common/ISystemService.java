package pro.hirooka.chukasa.domain.service.common;

import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.common.type.HardwareAccelerationType;

public interface ISystemService {
    boolean isFFmpeg();
    boolean isWebCamera();
    String getWebCameraDeviceName();
    boolean isTuner();
    boolean isRecxxx();
    boolean isEpgdump();
    boolean isMongoDB();
    boolean canWebCameraStreaming();
    boolean canFileStreaming();
    boolean canPTxStreaming();
    boolean canRecording();
    @Deprecated
    HardwareAccelerationType getHardwareAccelerationType();
    FfmpegVcodecType getFfmpegVcodecType();
    FfmpegVcodecType getFfmpegVcodecType(String userAgent);
}
