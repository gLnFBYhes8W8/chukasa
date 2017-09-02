package pro.hirooka.chukasa.domain.service.hls.segmenter;

import java.util.Date;

public interface IIntermediateChukasaHlsSegmenterService {
    void schedule(int adaptiveBitrateStreaming, Date startTime, long period);
    void cancel(int adaptiveBitrateStreaming);
}
