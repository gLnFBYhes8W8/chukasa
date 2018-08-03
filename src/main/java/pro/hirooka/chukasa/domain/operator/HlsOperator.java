package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pro.hirooka.chukasa.api.v1.helper.ChukasaUtility;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.config.hls.HlsConfiguration;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;
import pro.hirooka.chukasa.domain.service.common.IHyarukaClientService;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;
import pro.hirooka.chukasa.domain.service.hls.detector.IFfmpegHlsMediaSegmentDetectorService;
import pro.hirooka.chukasa.domain.service.hls.remover.IChukasaHlsFileRemoverService;
import pro.hirooka.chukasa.domain.service.hls.segmenter.IIntermediateChukasaHlsSegmenterService;
import pro.hirooka.chukasa.domain.service.transcoder.IFfmpegAndRecxxxService;
import pro.hirooka.chukasa.domain.service.transcoder.IFfmpegService;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

@Service
public class HlsOperator implements IHlsOperator {

    private static final Logger log = LoggerFactory.getLogger(HlsOperator.class);

    private Future<Integer> future;

    private final IChukasaModelManagementComponent chukasaModelManagementComponent;
    private final HyarukaConfiguration hyarukaConfiguration;
    private final IFfmpegService ffmpegService;
    private final IFfmpegAndRecxxxService ffmpegAndRecxxxService;
    private final IFfmpegHlsMediaSegmentDetectorService ffmpegHlsMediaSegmentDetectorService;
    private final IChukasaHlsFileRemoverService chukasaHlsFileRemoverService;
    private final IIntermediateChukasaHlsSegmenterService intermediateChukasaHlsSegmenterService;
    private final ISystemService systemService;
    private final IHyarukaClientService hyarukaClientService;
    private final IEpgService epgService;
    private final SystemConfiguration systemConfiguration;
    private final HlsConfiguration hlsConfiguration;

    @Autowired
    public HlsOperator(
            IChukasaModelManagementComponent chukasaModelManagementComponent,
            HyarukaConfiguration hyarukaConfiguration,
            IFfmpegService ffmpegService,
            IFfmpegAndRecxxxService ffmpegAndRecxxxService,
            IFfmpegHlsMediaSegmentDetectorService ffmpegHlsMediaSegmentDetectorService,
            IChukasaHlsFileRemoverService chukasaHlsFileRemoverService,
            IIntermediateChukasaHlsSegmenterService intermediateChukasaHlsSegmenterService,
            ISystemService systemService,
            IHyarukaClientService hyarukaClientService,
            IEpgService epgService,
            SystemConfiguration systemConfiguration,
            HlsConfiguration hlsConfiguration
    ) {
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent);
        this.hyarukaConfiguration = requireNonNull(hyarukaConfiguration);
        this.ffmpegService = requireNonNull(ffmpegService);
        this.ffmpegAndRecxxxService = requireNonNull(ffmpegAndRecxxxService);
        this.ffmpegHlsMediaSegmentDetectorService = requireNonNull(ffmpegHlsMediaSegmentDetectorService);
        this.chukasaHlsFileRemoverService = requireNonNull(chukasaHlsFileRemoverService);
        this.intermediateChukasaHlsSegmenterService = requireNonNull(intermediateChukasaHlsSegmenterService);
        this.systemService = requireNonNull(systemService);
        this.hyarukaClientService = requireNonNull(hyarukaClientService);
        this.epgService = requireNonNull(epgService);
        this.systemConfiguration = requireNonNull(systemConfiguration);
        this.hlsConfiguration = requireNonNull(hlsConfiguration);
    }

    @Override
    public ChukasaModel startPlayback(ChukasaSettings chukasaSettings, String userAgent, String servletRealPath) {
        cancel();
        stop();
        remove();
        final ChukasaModel chukasaModel = setup(chukasaSettings, userAgent, servletRealPath);
        execute();
        return chukasaModel;
    }

    private ChukasaModel setup(ChukasaSettings chukasaSettings, String userAgent, String servletRealPath) {
        final FfmpegVcodecType ffmpegVcodecType = systemService.getFfmpegVcodecType(userAgent);
        if(ffmpegVcodecType == FfmpegVcodecType.UNKNOWN){
            //
        }
        chukasaModelManagementComponent.deleteAll();

        String unixDomainSocketPath = "";
        if(hyarukaConfiguration.isEnabled() && hyarukaConfiguration.isUnixDomainSocketEnabled()){
            unixDomainSocketPath = hyarukaClientService.getUnixDomainSocketPath(chukasaSettings.getChannelRemoteControl());
        }

        ChukasaModel chukasaModel = new ChukasaModel();
        chukasaModel.setSystemConfiguration(systemConfiguration);
        chukasaModel.setHlsConfiguration(hlsConfiguration);
        chukasaModel.setUnixDomainSocketPath(unixDomainSocketPath);
        chukasaSettings.setTunerType(epgService.getTunerType(chukasaSettings.getChannelRemoteControl()));
        log.info("ChukasaSettings -> {}", chukasaSettings.toString());
        chukasaModel.setChukasaSettings(chukasaSettings);

        chukasaModel.setUuid(UUID.randomUUID());
        chukasaModel.setAdaptiveBitrateStreaming(0);
        chukasaModel.setFfmpegVcodecType(ffmpegVcodecType);
        // TODO: -> system service
        if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC || ffmpegVcodecType == FfmpegVcodecType.HEVC_QSV || ffmpegVcodecType == FfmpegVcodecType.HEVC_VIDEOTOOLBOX){
            chukasaModel.setStreamFileExtension(".m4s");
        }

        chukasaModel = ChukasaUtility.operateEncodingSettings(chukasaModel);
        if(chukasaModel == null){
            //
        }

        String streamRootPath = systemService.getStreamRootPath(servletRealPath);
        chukasaModel.setStreamRootPath(streamRootPath);
        chukasaModel = ChukasaUtility.createChukasaDerectory(chukasaModel);
        chukasaModel = ChukasaUtility.calculateTimerTaskParameter(chukasaModel);

        return chukasaModelManagementComponent.create(0, chukasaModel);
    }

    private void execute() {
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

    private  void cancel() {
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

    private void stop() {
//        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
//            final int adaptiveBitrateStreaming = chukasaModel.getAdaptiveBitrateStreaming();
//            Future<Integer> future = ffmpegStopperService.stop();
//        });
        chukasaModelManagementComponent.deleteAll();
    }

    private void remove() {
        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            final String streamPath = chukasaModel.getStreamPath();
            chukasaHlsFileRemoverService.remove(streamPath);
        });
    }

    @Override
    public void stopPlayback() {
        chukasaModelManagementComponent.get().forEach(chukasaModel -> {
            if(hyarukaConfiguration.isEnabled() && chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.TUNER) {
                final String HYARUKA_USERNAME = hyarukaConfiguration.getUsername();
                final String HYARUKA_PASSWORD = hyarukaConfiguration.getPassword();
                final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name();
                final String HYARUKA_HOST = hyarukaConfiguration.getHost();
                final int HYARUKA_PORT = hyarukaConfiguration.getPort();
                final String HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://"
                        //+ HYARUKA_USERNAME + ":" + HYARUKA_PASSWORD + "@"
                        + HYARUKA_HOST + ":" + HYARUKA_PORT
                        + "/api" + "/streams"
                        + "/" + chukasaModel.getChukasaSettings().getChannelRemoteControl();
                log.info("{}", HYARUKA_URI);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getInterceptors().add(
                        new BasicAuthorizationInterceptor(HYARUKA_USERNAME, HYARUKA_PASSWORD));
                //restTemplate.delete(HYARUKA_URI);
                restTemplate.exchange(HYARUKA_URI, HttpMethod.DELETE, null, String.class);
            }
        });
        cancel();
        stop();
    }

    @Override
    public void removeStream() {
        remove();
    }
}
