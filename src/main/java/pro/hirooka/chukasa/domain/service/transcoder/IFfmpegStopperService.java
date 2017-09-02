package pro.hirooka.chukasa.domain.service.transcoder;

import java.util.concurrent.Future;

public interface IFfmpegStopperService {
    Future<Integer> stop();
}
