package pro.hirooka.chukasa.chukasa_hls.domain.service.transcoder;

import java.util.concurrent.Future;

public interface IFfmpegAndRecxxxService {
    Future<Integer> submit(int adaptiveBitrateStreaming);
    void execute(int adaptiveBitrateStreaming);
    void cancel(int adaptiveBitrateStreaming);
}
