package pro.hirooka.chukasa.domain.service.hls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.service.hls.detector.IFfmpegHlsMediaSegmentDetectorService;
import pro.hirooka.chukasa.domain.service.hls.remover.IChukasaHlsFileRemoverService;
import pro.hirooka.chukasa.domain.service.hls.segmenter.IIntermediateChukasaHlsSegmenterService;
import pro.hirooka.chukasa.domain.service.transcoder.IFfmpegAndRecxxxService;
import pro.hirooka.chukasa.domain.service.transcoder.IFfmpegService;
import pro.hirooka.chukasa.domain.service.transcoder.IFfmpegStopperService;

import java.util.Date;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class CoordinatorService implements ICoordinatorService {

    private final IChukasaModelManagementComponent chukasaModelManagementComponent;
    private final IFfmpegService ffmpegService;
    private final IFfmpegAndRecxxxService ffmpegAndRecxxxService;
    private final IFfmpegHlsMediaSegmentDetectorService ffmpegHlsMediaSegmentDetectorService;
    private final IFfmpegStopperService ffmpegStopperService;
    private final IChukasaHlsFileRemoverService chukasaHlsFileRemoverService;
    private final IIntermediateChukasaHlsSegmenterService intermediateChukasaHlsSegmenterService;

    private Future<Integer> future;

    @Autowired
    public CoordinatorService(
            IChukasaModelManagementComponent chukasaModelManagementComponent,
            IFfmpegService ffmpegService,
            IFfmpegAndRecxxxService ffmpegAndRecxxxService,
            IFfmpegHlsMediaSegmentDetectorService ffmpegHlsMediaSegmentDetectorService,
            IFfmpegStopperService ffmpegStopperService,
            IChukasaHlsFileRemoverService chukasaHlsFileRemoverService,
            IIntermediateChukasaHlsSegmenterService intermediateChukasaHlsSegmenterService
    ) {
        this.chukasaModelManagementComponent = requireNonNull(
                chukasaModelManagementComponent, "chukasaModelManagementComponent");
        this.ffmpegService = requireNonNull(
                ffmpegService, "ffmpegService");
        this.ffmpegAndRecxxxService = requireNonNull(
                ffmpegAndRecxxxService, "ffmpegAndRecxxxService");
        this.ffmpegHlsMediaSegmentDetectorService = requireNonNull(
                ffmpegHlsMediaSegmentDetectorService, "ffmpegHLSMediaSegmentDetectorService");
        this.ffmpegStopperService = requireNonNull(
                ffmpegStopperService, "intermediateFFmpegStopperService");
        this.chukasaHlsFileRemoverService = requireNonNull(
                chukasaHlsFileRemoverService, "intermediateChukasaHLSFileRemoverService");
        this.intermediateChukasaHlsSegmenterService = requireNonNull(
                intermediateChukasaHlsSegmenterService, "intermediateChukasaHLSSegmenterService");
    }

    @Override
    public void execute() {

        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            final StreamingType streamingType = chukasaModel.getChukasaSettings().getStreamingType();
            final int adaptiveBitrateStreaming = chukasaModel.getAdaptiveBitrateStreaming();
            if(streamingType == StreamingType.WEBCAM || streamingType == StreamingType.FILE) {
                ffmpegHlsMediaSegmentDetectorService.schedule(adaptiveBitrateStreaming, new Date(), 2000);
                if(future != null){
                    future.cancel(true);
                }
                future = ffmpegService.submit(adaptiveBitrateStreaming);
            } else if(streamingType == StreamingType.TUNER) {
                ffmpegHlsMediaSegmentDetectorService.schedule(adaptiveBitrateStreaming, new Date(), 2000);
                if(future != null){
                    future.cancel(true);
                }
                future = ffmpegAndRecxxxService.submit(adaptiveBitrateStreaming);
            } else if(streamingType == StreamingType.OKKAKE) {
                intermediateChukasaHlsSegmenterService.schedule(adaptiveBitrateStreaming, new Date(), 2000);
            } else {
                //
            }
        });

    }

    @Override
    public void cancel() {

        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            final StreamingType streamingType = chukasaModel.getChukasaSettings().getStreamingType();
            final int adaptiveBitrateStreaming = chukasaModel.getAdaptiveBitrateStreaming();
            if(streamingType == StreamingType.WEBCAM
                    || chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.FILE) {
                ffmpegHlsMediaSegmentDetectorService.cancel(adaptiveBitrateStreaming);
                ffmpegService.cancel(adaptiveBitrateStreaming);
                if(future != null){
                    future.cancel(true);
                }
            } else if(streamingType == StreamingType.TUNER) {
                ffmpegHlsMediaSegmentDetectorService.cancel(adaptiveBitrateStreaming);
                ffmpegAndRecxxxService.cancel(adaptiveBitrateStreaming);
                if(future != null){
                    future.cancel(true);
                }
            } else if(streamingType == StreamingType.OKKAKE) {
                intermediateChukasaHlsSegmenterService.cancel(adaptiveBitrateStreaming);
            } else {
                //
            }
        });
    }

    @Override
    public void stop() {
        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            final int adaptiveBitrateStreaming = chukasaModel.getAdaptiveBitrateStreaming();
            ffmpegStopperService.stop();
        });
    }

    @Override
    public void remove() {
        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            final String streamPath = chukasaModel.getStreamPath();
            chukasaHlsFileRemoverService.remove(streamPath);
        });
    }
}

