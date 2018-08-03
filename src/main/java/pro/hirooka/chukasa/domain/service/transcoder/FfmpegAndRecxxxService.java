package pro.hirooka.chukasa.domain.service.transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.config.common.HyarukaConfiguration;
import pro.hirooka.chukasa.domain.config.common.type.FfmpegVcodecType;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.service.hls.IChukasaModelManagementComponent;

import java.io.*;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;
import static pro.hirooka.chukasa.domain.config.ChukasaConstants.*;

@Service
public class FfmpegAndRecxxxService implements IFfmpegAndRecxxxService {

    private static final Logger log = LoggerFactory.getLogger(FfmpegAndRecxxxService.class);

    private final HyarukaConfiguration hyarukaConfiguration;
    private final IChukasaModelManagementComponent chukasaModelManagementComponent;

    @Autowired
    public FfmpegAndRecxxxService(
            IChukasaModelManagementComponent chukasaModelManagementComponent,
            HyarukaConfiguration hyarukaConfiguration
    ){
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent);
        this.hyarukaConfiguration = requireNonNull(hyarukaConfiguration);
    }

    @Async
    @Override
    public Future<Integer> submit(int adaptiveBitrateStreaming) {

        // TODO: final
        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();
        log.debug("StreamPath: {}", chukasaModel.getStreamPath());

        final FfmpegVcodecType ffmpegVcodecType = chukasaModel.getFfmpegVcodecType();

        final boolean canEncrypt = chukasaModel.getChukasaSettings().isCanEncrypt();
        final String ffmpegOutputPath;
        final String fmp4InitFileOutputPath;
        if(canEncrypt){
            ffmpegOutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
            fmp4InitFileOutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4";
        } else {
            ffmpegOutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
            fmp4InitFileOutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + ".mp4";
        }
        final String ffmpegM3U8OutputPath;
        if(canEncrypt){
            ffmpegM3U8OutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        } else {
            ffmpegM3U8OutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        }

        chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

        final String HYARUKA_USERNAME = hyarukaConfiguration.getUsername();
        final String HYARUKA_PASSWORD = hyarukaConfiguration.getPassword();
        final String HYARUKA_SCHEME = hyarukaConfiguration.getScheme().name();
        final String HYARUKA_HOST = hyarukaConfiguration.getHost();
        final int HYARUKA_PORT = hyarukaConfiguration.getPort();

        final String HYARUKA_URI;
        if(hyarukaConfiguration.isEnabled() && hyarukaConfiguration.isUnixDomainSocketEnabled()){
            HYARUKA_URI = "unix:" + chukasaModel.getUnixDomainSocketPath();
        }else{
            HYARUKA_URI = HYARUKA_SCHEME.toLowerCase() + "://" + HYARUKA_USERNAME + ":" + HYARUKA_PASSWORD + "@" + HYARUKA_HOST + ":" + HYARUKA_PORT
                    + "/api" + "/streams"
                    + "/" + chukasaModel.getChukasaSettings().getChannelRemoteControl();
        }

        final String[] commandArray;

        if(hyarukaConfiguration.isEnabled()){
            if(ffmpegVcodecType == FfmpegVcodecType.H264_OMX){
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "copy",
                        //"-acodec", "aac",
                        //"-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        //"-ar", "44100",
                        //"-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-c:v", "h264_omx",
                        "-vf", "yadif",
                        //"-vcodec", "h264_qsv",
                        //"-g", "60",
                        //"-profile:v", "high",
                        //"-level", "4.2",
                        //"-b:v", chukasaModel.getChukasaSettings().getVideoBitrate()+"k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                  "-segment_list", m3u8OutputPath,
                        ffmpegOutputPath
                };
            } else if(ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_qsv",
                        "-init_hw_device", "qsv:hw",
                        "-vf", "yadif",
                        "-g", "60",
                        "-profile:v", "high",
                        "-level", "4.2",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                    "-segment_list", m3u8OutputPath,
                        ffmpegOutputPath
                };
            }else if(ffmpegVcodecType == FfmpegVcodecType.HEVC_QSV){
                // TODO: not working
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "hevc_qsv",
                        "-init_hw_device", "qsv:hw",
                        "-load_plugin", "hevc_hw",
                        "-maxrate", "50000k",
                        "-tag:v", "hvc1",
                        "-vf", "yadif",
                        "-g", "60",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "hls",
                        "-hls_segment_type", "fmp4",
                        "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        "-hls_fmp4_init_filename", fmp4InitFileOutputPath,
                        //"-hls_segment_filename", ffmpegOutputPath,
                        ffmpegM3U8OutputPath
                };
            }else if(ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "libx264",
                        "-vf", "yadif",
                        "-profile:v", "high",
                        "-level", "4.1",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-preset:v", "superfast",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                    "-segment_list", m3u8OutputPath,
                        "-x264opts", "keyint=10:min-keyint=10",
                        ffmpegOutputPath
                };
            } else if(ffmpegVcodecType == FfmpegVcodecType.H264_NVENC){
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_nvenc",
                        "-vf", "yadif",
                        "-g", "10",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "hls",
                        "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        "-hls_segment_filename", ffmpegOutputPath,
                        ffmpegM3U8OutputPath
                };
            } else if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC) {
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "hevc_nvenc",
                        "-tag:v", "hvc1",
                        "-vf", "yadif",
                        "-g", "10",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "hls",
                        "-hls_segment_type", "fmp4",
                        "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        "-hls_fmp4_init_filename", FMP4_INIT_FILE_NAME + FMP4_INIT_FILE_EXTENSION,
                        ffmpegM3U8OutputPath
                };
            } else if (ffmpegVcodecType == FfmpegVcodecType.H264_VIDEOTOOLBOX) {
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_videotoolbox",
                        "-vf", "yadif",
                        "-g", "10",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "hls",
                        "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        "-hls_segment_filename", ffmpegOutputPath,
                        ffmpegM3U8OutputPath
                };
            } else if (ffmpegVcodecType == FfmpegVcodecType.HEVC_VIDEOTOOLBOX) {
                commandArray = new String[]{
                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", HYARUKA_URI,
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "48000",
                        "-ac", "2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "hevc_videotoolbox",
                        "-tag:v", "hvc1",
                        "-vf", "yadif",
                        "-g", "10",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "hls",
                        "-hls_segment_type", "fmp4",
                        "-hls_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        "-hls_fmp4_init_filename", FMP4_INIT_FILE_NAME + FMP4_INIT_FILE_EXTENSION,
                        ffmpegM3U8OutputPath
                };
            } else {
                commandArray = new String[]{};
                log.error("");
            }
        }else{
            commandArray = new String[]{};
            log.error("");
        }

        String command = "";
        for(int i = 0; i < commandArray.length; i++){
            command += commandArray[i] + " ";
        }
        log.info("{}", command);

        final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        final Process process;
        try {
            process = processBuilder.start();
            final long pid = process.pid();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String str = "";
            boolean isTranscoding = false;
            while((str = bufferedReader.readLine()) != null){
                log.debug("{}", str);
                if(str.startsWith("frame=")){
                    if(!isTranscoding){
                        isTranscoding = true;
                        chukasaModel.setTrascoding(isTranscoding);
                        chukasaModel.setFfmpegPID(pid);
                        chukasaModel.setFfmpegProcess(process);
                        chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
                    }
                }
            }
            isTranscoding = false;
            chukasaModel.setTrascoding(isTranscoding);
            chukasaModel.setFfmpegPID(-1);
            chukasaModel.setFfmpegProcess(null);
            chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
            bufferedReader.close();
            process.destroy();
        } catch (IOException e) {
            log.warn("{}", e.getMessage());
        } finally {
            log.info("stream is closed.");
        }
        return new AsyncResult<>(0);
    }

    @Override
    public void execute(int adaptiveBitrateStreaming) {

    }

    @Override
    public void cancel(int adaptiveBitrateStreaming) {
        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        chukasaModel.setTunerDeviceName("");
        chukasaModel.setFfmpegPID(-1);
        if(chukasaModel.getFfmpegProcess() != null){
            chukasaModel.getFfmpegProcess().destroy();
            chukasaModel.setFfmpegProcess(null);
        }
        chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
    }
}
