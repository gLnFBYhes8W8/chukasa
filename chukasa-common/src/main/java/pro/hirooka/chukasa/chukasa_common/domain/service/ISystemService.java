package pro.hirooka.chukasa.chukasa_common.domain.service;

import pro.hirooka.chukasa.chukasa_common.domain.enums.FfmpegVcodecType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.HardwareAccelerationType;

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
