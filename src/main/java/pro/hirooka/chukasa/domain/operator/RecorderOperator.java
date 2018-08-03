package pro.hirooka.chukasa.domain.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.config.common.MongoDBConfiguration;
import pro.hirooka.chukasa.domain.config.common.SystemConfiguration;
import pro.hirooka.chukasa.domain.config.recorder.RecorderConfiguration;
import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;
import pro.hirooka.chukasa.domain.service.common.ISystemService;
import pro.hirooka.chukasa.domain.service.epg.IEpgService;
import pro.hirooka.chukasa.domain.service.recorder.IRecorderService;
import pro.hirooka.chukasa.domain.service.recorder.IRecordingProgramManagementComponent;
import pro.hirooka.chukasa.domain.service.recorder.RecorderRunnable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.FILE_SEPARATOR;

@Service
public class RecorderOperator implements IRecorderOperator {

    private static final Logger log = LoggerFactory.getLogger(RecorderOperator.class);

    private final RecorderConfiguration recorderConfiguration;
    private final HyarukaConfiguration hyarukaConfiguration;
    private final SystemConfiguration systemConfiguration;
    private final MongoDBConfiguration mongoDBConfiguration;
    private final IEpgService epgService;
    private final IRecorderService recorderService;
    private final ISystemService systemService;
    private final IRecordingProgramManagementComponent recordingProgramManagementComponent;

    @Autowired
    public RecorderOperator(
            RecorderConfiguration recorderConfiguration,
            HyarukaConfiguration hyarukaConfiguration,
            SystemConfiguration systemConfiguration,
            MongoDBConfiguration mongoDBConfiguration,
            IEpgService epgService,
            IRecorderService recorderService,
            ISystemService systemService,
            IRecordingProgramManagementComponent recordingProgramManagementComponent
    ) {
        this.recorderConfiguration = requireNonNull(recorderConfiguration);
        this.hyarukaConfiguration = requireNonNull(hyarukaConfiguration);
        this.systemConfiguration = requireNonNull(systemConfiguration);
        this.mongoDBConfiguration = requireNonNull(mongoDBConfiguration);
        this.epgService = requireNonNull(epgService);
        this.recorderService = requireNonNull(recorderService);
        this.systemService = requireNonNull(systemService);
        this.recordingProgramManagementComponent = requireNonNull(recordingProgramManagementComponent);
    }

    @Async
    @Override
    public void onBoot(){
        if(systemService.isMongoDB()){
            log.info("recorder: on boot...");
            final List<ReservedProgram> reservedProgramList = recorderService.read();
            for(ReservedProgram reservedProgram : reservedProgramList){
                if(true){ // TODO:check
                    final long startRecording = reservedProgram.getStartRecording();
                    final long stopRecording = reservedProgram.getStopRecording();
                    final long now = new Date().getTime();
                    if (startRecording > now && stopRecording > now){
                        // reserve
                        log.info("reservation: {}", reservedProgram.toString());
                        final RecorderRunnable recorderRunnable = new RecorderRunnable();
                        recorderRunnable.setReservedProgram(reservedProgram);
                        final String path = "/streams/"
                                + epgService.getTunerType(reservedProgram.getChannelRecording())
                                + "/"
                                + reservedProgram.getChannelRecording()
                                + "/"
                                + reservedProgram.getRecordingDuration();
                        final String hyarukaUri = getHyarukaUri(path);
                        recorderRunnable.setHyarukaUri(hyarukaUri);
                        recorderService.reserve(recorderRunnable);
                    }else if(now > startRecording && stopRecording > now){
                        // start recording immediately
                        log.info("no reservation, direct recording");
                        long recordingDuration = (stopRecording - now) / 1000;
                        reservedProgram.setRecordingDuration(recordingDuration);
                        final String path = "/streams/"
                                + epgService.getTunerType(reservedProgram.getChannelRecording())
                                + "/"
                                + reservedProgram.getChannelRecording()
                                + "/"
                                + reservedProgram.getRecordingDuration();
                        final String hyarukaUri = getHyarukaUri(path);
                        recorderService.recordDirectly(reservedProgram, hyarukaUri);

                    }else if(now > startRecording && now > stopRecording){
                        //  nothing to do... (as error)
                        log.error("no reservation, no recording");
                    }else{
                        log.error("");
                    }
                }else{
                    log.info("skip (in recording...) {}", reservedProgram.toString());
                }
            }
        }
    }

    @Override
    public ReservedProgram create(ReservedProgram reservedProgram) {

        final List<ReservedProgram> reservedProgramList = recorderService.read();
        if(reservedProgramList.size() > 0) {
            int n = Collections.max(reservedProgramList.stream().map(ReservedProgram::getId).collect(Collectors.toList()));
            n++;
            reservedProgram.setId(n);
        }else{
            reservedProgram.setId(0);
        }

        final long startRecording = reservedProgram.getBegin() - recorderConfiguration.getStartMargin() * 1000;
        final long stopRecording = reservedProgram.getEnd() + recorderConfiguration.getStopMargin() * 1000;
        long recordingDuration = (stopRecording - startRecording) / 1000;
        reservedProgram.setStartRecording(startRecording);
        reservedProgram.setStopRecording(stopRecording);
        reservedProgram.setRecordingDuration(recordingDuration);

        String fileName = systemConfiguration.getFilePath() + FILE_SEPARATOR + reservedProgram.getChannelRemoteControl() + "_" + reservedProgram.getBegin() + "_" + reservedProgram.getTitle()  + ".ts";
        reservedProgram.setFileName(fileName);

        final long now = new Date().getTime();

        if(startRecording > now && stopRecording > now){
            // reserve
            log.info("reservation: {}", reservedProgram.toString());
            final RecorderRunnable recorderRunnable = new RecorderRunnable();
            recorderRunnable.setReservedProgram(reservedProgram);
            final String path = "/streams"
                    + "/"
                    + reservedProgram.getChannelRemoteControl()
                    + "/"
                    + reservedProgram.getRecordingDuration();
            final String hyarukaUri = getHyarukaUri(path);
            recorderRunnable.setHyarukaUri(hyarukaUri);
            recorderService.reserve(recorderRunnable);
        }else if(now > startRecording && stopRecording > now){
            // start recording immediately
            log.info("no reservation, direct recording");
            recordingDuration = (stopRecording - now) / 1000;
            reservedProgram.setRecordingDuration(recordingDuration);
            final String path = "/streams"
                    + "/"
                    + reservedProgram.getChannelRemoteControl()
                    + "/"
                    + reservedProgram.getRecordingDuration();
            final String hyarukaUri = getHyarukaUri(path);
            recorderService.recordDirectly(reservedProgram, hyarukaUri);

        }else if(now > startRecording && now > stopRecording){
            //  nothing to do... (as error)
            log.info("no reservation, no recording");
        }else{
            //
        }

        return null;
    }

    private String getHyarukaUri(String path){
        final String HYARUKA_USERNAME = hyarukaConfiguration.getUsername();
        final String HYARUKA_PASSWORD = hyarukaConfiguration.getPassword();
        final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name().toLowerCase();
        final String HYARUKA_HOST = hyarukaConfiguration.getHost();
        final int HYARUKA_PORT = hyarukaConfiguration.getPort();
        final String HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://"
                + HYARUKA_USERNAME + ":" + HYARUKA_PASSWORD + "@"
                + HYARUKA_HOST + ":" + HYARUKA_PORT
                + "/api" + path;
        return HYARUKA_URI;
    }
}
