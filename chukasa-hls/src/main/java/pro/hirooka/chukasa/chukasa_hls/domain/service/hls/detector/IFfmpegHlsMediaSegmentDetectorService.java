package pro.hirooka.chukasa.chukasa_hls.domain.service.hls.detector;

import java.util.Date;

public interface IFfmpegHlsMediaSegmentDetectorService {
    void schedule(int adaptiveBitrateStreaming, Date startTime, long period);
    void cancel(int adaptiveBitrateStreaming);
}
