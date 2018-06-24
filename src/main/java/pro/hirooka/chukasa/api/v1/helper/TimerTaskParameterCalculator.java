package pro.hirooka.chukasa.api.v1.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;

@Deprecated
@Component
public class TimerTaskParameterCalculator implements ITimerTaskParameterCalculator {

    private static final Logger log = LoggerFactory.getLogger(TimerTaskParameterCalculator.class);

    @Autowired
    IChukasaModelManagementComponent chukasaModelManagementComponent;

    @Override
    public void calculate(int adaptiveBitrateStreaming) {

        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);

        // segmenter timer parameters [ms]
        // TODO optimize
        int duration = chukasaModel.getHlsConfiguration().getDuration();
        int uriInPlaylist = chukasaModel.getHlsConfiguration().getUriInPlaylist();
        long timerSegmenterDelay = (long) (duration * 1000 * (uriInPlaylist - 1));

//        if (chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.WEB_CAMERA) {
//            timerSegmenterDelay = (long) (duration * 1000 * uriInPlaylist);
//        }

        if (chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.FILE) {
            //timerSegmenterDelay = (long)(DURATION * 1000 * (URI_IN_PLAYLIST));
            //timerSegmenterDelay = 0;
            timerSegmenterDelay = (long) (duration * 1000);
        }

        if (chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.TUNER)
                || chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.WEBCAM)) {
            timerSegmenterDelay = (long) (duration * 1000 * (uriInPlaylist)) + 1000;
            timerSegmenterDelay = (long) (duration * 1000);
            // timerSegmenterDelay = 0;
        }

        if(chukasaModel.getFfmpegVcodecType().equals(FfmpegVcodecType.H264_OMX)){
            timerSegmenterDelay = timerSegmenterDelay + 3000; // todo
        }
        long timerSegmenterPeriod = (long) (duration * 1000);

        // playlister timer parameters [ms]
        long timerPlaylisterDelay = timerSegmenterDelay + (Math.round(duration) * 1000 * uriInPlaylist + 1000);
        timerPlaylisterDelay = 0;

        long timerPlaylisterPeriod = (long) (duration * 1000);

        log.info("timerSegmenterDelay = {}, timerSegmenterPeriod = {}, timerPlaylisterDelay = {}, timerPlaylisterPeriod = {}", timerSegmenterDelay, timerSegmenterPeriod, timerPlaylisterDelay, timerPlaylisterPeriod);

        chukasaModel.setTimerSegmenterDelay(timerSegmenterDelay);
        chukasaModel.setTimerSegmenterPeriod(timerSegmenterPeriod);
        chukasaModel.setTimerPlaylisterDelay(timerPlaylisterDelay);
        chukasaModel.setTimerPlaylisterPeriod(timerPlaylisterPeriod);

        chukasaModelManagementComponent.update(0, chukasaModel);
    }
}

