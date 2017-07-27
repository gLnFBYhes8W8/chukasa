package pro.hirooka.chukasa.chukasa_hls.domain.service.transcoder;

import java.util.concurrent.Future;

public interface IFfmpegService {
    Future<Integer> submit(int adaptiveBitrateStreaming);
    void cancel(int adaptiveBitrateStreaming);
}
