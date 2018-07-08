package pro.hirooka.chukasa.domain.service.hls.segmenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.service.hls.playlist.IPlaylistCreator;

import java.util.Date;

@Service
public class IntermediateChukasaHlsSegmenterService implements IIntermediateChukasaHlsSegmenterService {

    private static final Logger log = LoggerFactory.getLogger(IntermediateChukasaHlsSegmenterService.class);

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    IPlaylistCreator playlistCreator;

    @Async
    @Override
    public void schedule(int adaptiveBitrateStreaming, Date startTime, long period) {
        playlistCreator.create();
        //chukasaHLSSegmenter.setAdaptiveBitrateStreaming(adaptiveBitrateStreaming);
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix(Integer.toString(adaptiveBitrateStreaming));
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.initialize();
        //treadPoolTaskScheduler.scheduleAtFixedRate(chukasaHLSSegmenter, startTime, period);
    }

    @Override
    public void cancel(int adaptiveBitrateStreaming) {
        if(threadPoolTaskScheduler != null){
            if(threadPoolTaskScheduler.getThreadNamePrefix().equals(Integer.toString(adaptiveBitrateStreaming))){
                log.info("shutdown - {}", adaptiveBitrateStreaming);
                threadPoolTaskScheduler.shutdown();
            }
        }
    }
}