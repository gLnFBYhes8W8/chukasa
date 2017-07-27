package pro.hirooka.chukasa.chukasa_hls.domain.service.transcoder;

import java.util.concurrent.Future;

public interface IFfmpegStopperService {
    Future<Integer> stop();
}
