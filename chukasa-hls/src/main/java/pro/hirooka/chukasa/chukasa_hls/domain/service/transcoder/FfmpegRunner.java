package pro.hirooka.chukasa.chukasa_hls.domain.service.transcoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants;
import pro.hirooka.chukasa.chukasa_common.domain.enums.FfmpegVcodecType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.HardwareAccelerationType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.StreamingType;
import pro.hirooka.chukasa.chukasa_hls.domain.model.ChukasaModel;
import pro.hirooka.chukasa.chukasa_hls.domain.service.IChukasaModelManagementComponent;
import pro.hirooka.chukasa.chukasa_hls.domain.service.hls.encrypter.Encrypter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import static java.util.Objects.requireNonNull;

@Deprecated
@Slf4j
public class FfmpegRunner implements Runnable {

    static final String FILE_SEPARATOR = System.getProperty("file.separator");

    final String STREAM_FILE_NAME_PREFIX = ChukasaConstants.STREAM_FILE_NAME_PREFIX;
    final String FFMPEG_HLS_M3U8_FILE_NAME = ChukasaConstants.FFMPEG_HLS_M3U8_FILE_NAME;
    final String M3U8_FILE_EXTENSION = ChukasaConstants.M3U8_FILE_EXTENSION;

    private int adaptiveBitrateStreaming;

    private IChukasaModelManagementComponent chukasaModelManagementComponent;

    public FfmpegRunner(int adaptiveBitrateStreaming, IChukasaModelManagementComponent chukasaModelManagementComponent) {
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent, "chukasaModelManagementComponent");
    }

    @Override
    public void run() {

        ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
        final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();

//        boolean isQSV = chukasaModel.getSystemConfiguration().isQuickSyncVideoEnabled();
//        boolean isOpenMAX = chukasaModel.getSystemConfiguration().isOpenmaxEnabled();
        FfmpegVcodecType ffmpegVcodecType = chukasaModel.getFfmpegVcodecType();

        boolean isEncrypted = chukasaModel.getChukasaSettings().isCanEncrypt();
        String ffmpegOutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
        String m3u8OutputPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + FFMPEG_HLS_M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        if (isEncrypted) {
            ffmpegOutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + "%d" + STREAM_FILE_EXTENSION;
            m3u8OutputPath = chukasaModel.getTempEncPath() + FILE_SEPARATOR + FFMPEG_HLS_M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
        }

        int seqCapturedTimeShifted = chukasaModel.getSeqTsOkkake();

        String[] cmdArray = null;

        if (chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.WEBCAM)) {

            if (ffmpegVcodecType == FfmpegVcodecType.H264_OMX) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-f", "video4linux2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-i", chukasaModel.getSystemConfiguration().getWebcamDeviceName(),
                        "-f", "alsa",
                        "-ac", Integer.toString(chukasaModel.getSystemConfiguration().getWebcamAudioChannel()),
                        "-i", "hw:0,0",
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "44100",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_omx",
                        //"-g", "60",
                        //"-profile:v", "high",
                        //"-level", "4.2",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-pix_fmt", "yuv420p",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                        "-segment_list", m3u8OutputPath,
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            } else if (ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-f", "video4linux2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-i", chukasaModel.getSystemConfiguration().getWebcamDeviceName(),
                        "-f", "alsa",
                        "-ac", Integer.toString(chukasaModel.getSystemConfiguration().getWebcamAudioChannel()),
                        "-i", "hw:0,0",
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "44100",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_qsv",
                        "-g", "60",
                        "-profile:v", "high",
                        "-level", "4.2",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-pix_fmt", "yuv420p",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
//                        "-segment_list", m3u8OutputPath,
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            } else if (ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-f", "video4linux2",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        //"-r", "30",
                        "-i", chukasaModel.getSystemConfiguration().getWebcamDeviceName(),
                        "-f", "alsa",
                        "-ac", Integer.toString(chukasaModel.getSystemConfiguration().getWebcamAudioChannel()),
                        "-i", "hw:0,0",
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ar", "44100",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "libx264",
                        "-profile:v", "high",
                        "-level", "4.1",
                        "-preset:v", "superfast",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-pix_fmt", "yuv420p",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-x264opts", "keyint=10:min-keyint=10",
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            }

        } else if (chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.FILE)) {

            if (ffmpegVcodecType == FfmpegVcodecType.H264_OMX) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", chukasaModel.getSystemConfiguration().getFilePath() + FILE_SEPARATOR + chukasaModel.getChukasaSettings().getFileName(),
                        "-acodec", "aac",
                        //"-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        //"-ac", "2",
                        //"-ar", "44100",
                        //"-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_omx",
                        //"-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            } else if (ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", chukasaModel.getSystemConfiguration().getFilePath() + FILE_SEPARATOR + chukasaModel.getChukasaSettings().getFileName(),
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ac", "2",
                        "-ar", "44100",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "h264_qsv",
                        "-profile:v", "high",
                        "-level", "4.1",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            } else if (ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
                String[] cmdArrayTemporary = {

                        chukasaModel.getSystemConfiguration().getFfmpegPath(),
                        "-i", chukasaModel.getSystemConfiguration().getFilePath() + FILE_SEPARATOR + chukasaModel.getChukasaSettings().getFileName(),
                        "-acodec", "aac",
                        "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                        "-ac", "2",
                        "-ar", "44100",
                        "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                        "-vcodec", "libx264",
                        "-profile:v", "high",
                        "-level", "4.1",
                        "-preset:v", "superfast",
                        "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                        "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                        "-x264opts", "keyint=10:min-keyint=10",
                        "-f", "segment",
                        "-segment_format", "mpegts",
                        "-segment_time", Integer.toString(chukasaModel.getHlsConfiguration().getDuration()),
                        ffmpegOutputPath
                };
                cmdArray = cmdArrayTemporary;
            }

        } else if (chukasaModel.getChukasaSettings().getStreamingType() == StreamingType.OKKAKE) {

            if (chukasaModel.getChukasaSettings().isCanEncrypt()) {

                if (ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
                    String[] cmdArrayTemporary = {

                            chukasaModel.getSystemConfiguration().getFfmpegPath(),
                            "-i", chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqCapturedTimeShifted + STREAM_FILE_EXTENSION,
                            "-acodec", "aac",
                            "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                            "-ac", "2",
                            "-ar", "44100",
                            "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                            "-vcodec", "h264_qsv",
                            "-profile:v", "high",
                            "-level", "4.1",
                            "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                            "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                            "-f", "mpegts",
                            "-y", chukasaModel.getTempEncPath() + FILE_SEPARATOR + "fileSequenceEncoded" + seqCapturedTimeShifted + STREAM_FILE_EXTENSION // TODO
                    };
                    cmdArray = cmdArrayTemporary;
                } else if (ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
                    String[] cmdArrayTemporary = {

                            chukasaModel.getSystemConfiguration().getFfmpegPath(),
                            "-i", chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqCapturedTimeShifted + STREAM_FILE_EXTENSION,
                            "-acodec", "aac",
                            "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                            "-ac", "2",
                            "-ar", "44100",
                            "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                            "-vcodec", "libx264",
                            "-profile:v", "high",
                            "-level", "4.1",
                            "-preset:v", "superfast",
                            "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                            "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                            "-f", "mpegts",
                            "-x264opts", "keyint=10:min-keyint=10",
                            "-y", chukasaModel.getTempEncPath() + FILE_SEPARATOR + "fileSequenceEncoded" + seqCapturedTimeShifted + STREAM_FILE_EXTENSION // TODO
                    };
                    cmdArray = cmdArrayTemporary;
                }

            } else {

                if (ffmpegVcodecType == FfmpegVcodecType.H264_QSV) {
                    String[] cmdArrayTemporary = {

                            chukasaModel.getSystemConfiguration().getFfmpegPath(),
                            "-i", chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqCapturedTimeShifted + STREAM_FILE_EXTENSION,
                            "-acodec", "aac",
                            "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                            "-ac", "2",
                            "-ar", "44100",
                            "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                            "-vcodec", "h264_qsv",
                            "-profile:v", "high",
                            "-level", "4.1",
                            "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                            "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                            "-f", "mpegts",
                            "-y", chukasaModel.getStreamPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqCapturedTimeShifted + STREAM_FILE_EXTENSION
                    };
                    cmdArray = cmdArrayTemporary;
                } else if (ffmpegVcodecType == FfmpegVcodecType.H264_X264) {
                    String[] cmdArrayTemporary = {

                            chukasaModel.getSystemConfiguration().getFfmpegPath(),
                            "-i", chukasaModel.getTempEncPath() + FILE_SEPARATOR + STREAM_FILE_NAME_PREFIX + seqCapturedTimeShifted + STREAM_FILE_EXTENSION,
                            "-acodec", "aac",
                            "-ab", chukasaModel.getChukasaSettings().getAudioBitrate() + "k",
                            "-ac", "2",
                            "-ar", "44100",
                            "-s", chukasaModel.getChukasaSettings().getVideoResolution(),
                            "-vcodec", "libx264",
                            "-profile:v", "high",
                            "-level", "4.1",
                            "-preset:v", "superfast",
                            "-b:v", chukasaModel.getChukasaSettings().getVideoBitrate() + "k",
                            "-threads", Integer.toString(chukasaModel.getSystemConfiguration().getFfmpegThreads()),
                            "-f", "mpegts",
                            "-x264opts", "keyint=10:min-keyint=10",
                            "-y", chukasaModel.getTempEncPath() + FILE_SEPARATOR + "fileSequenceEncoded" + seqCapturedTimeShifted + STREAM_FILE_EXTENSION // TODO
                    };
                    cmdArray = cmdArrayTemporary;
                }

            }

        }

        String command = "";
        for (int i = 0; i < cmdArray.length; i++) {
            command += cmdArray[i] + " ";
        }
        log.info("{}", command);

        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        try {

            log.info("Begin FFmpeg");
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            long pid = -1;
            try {
                if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                    Field field = process.getClass().getDeclaredField("pid");
                    field.setAccessible(true);
                    pid = field.getLong(process);
                    chukasaModel.setFfmpegPID(pid);
                    chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
                    field.setAccessible(false);
                }
            } catch (Exception e) {
                log.error("{} {}", e.getMessage(), e);
            }

            String str = "";
            boolean isTranscoding = false;
            boolean isSegmenterStarted = false;
            while ((str = bufferedReader.readLine()) != null) {
                log.debug(str);
                // TODO Input/output error (in use...)
                if (chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.WEBCAM) || chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.FILE)) {
                    if (str.startsWith("frame=")) {
                        if (!isTranscoding) {
                            isTranscoding = true;
                            chukasaModel.setTrascoding(isTranscoding);
                            chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
//                            if(!isSegmenterStarted) {
//                                isSegmenterStarted = true;
//                                SegmenterRunner segmenterRunner = new SegmenterRunner(adaptiveBitrateStreaming, chukasaModelManagementComponent);
//                                Thread sThread = new Thread(segmenterRunner, "__SegmenterRunner__");
//                                sThread.start();
//                                chukasaModel.setSegmenterRunner(segmenterRunner);
//                                chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
//                            }
                        }
                    }
                }
            }
            isTranscoding = false;
            chukasaModel.setTrascoding(isTranscoding);
            chukasaModel = chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
            isSegmenterStarted = false;
            bufferedReader.close();
            process.destroy();
            log.info("End FFmpeg");
            log.info("{} is completed.", this.getClass().getName());

            if (chukasaModel != null && chukasaModel.getChukasaSettings().getStreamingType().equals(StreamingType.OKKAKE)) {
                if (chukasaModel.getChukasaSettings().isCanEncrypt()) {
                    seqCapturedTimeShifted = seqCapturedTimeShifted + 1;
                    chukasaModel.setSeqTsOkkake(seqCapturedTimeShifted);
                    chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

                    SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
                    Encrypter encrypter = new Encrypter(adaptiveBitrateStreaming, chukasaModelManagementComponent);
                    taskExecutor.execute(encrypter);
                }
            }

        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
    }
}

