package pro.hirooka.chukasa.chukasa_hls.domain.service.hls.playlist;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.hirooka.chukasa.chukasa_common.domain.constants.ChukasaConstants;
import pro.hirooka.chukasa.chukasa_common.domain.enums.FfmpegVcodecType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.HardwareAccelerationType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.PlaylistType;
import pro.hirooka.chukasa.chukasa_hls.domain.model.ChukasaModel;
import pro.hirooka.chukasa.chukasa_hls.domain.service.IChukasaModelManagementComponent;

import java.io.*;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class PlaylistCreator implements IPlaylistCreator {

    final String FILE_SEPARATOR = ChukasaConstants.FILE_SEPARATOR;
    final String initialStreamPath = ChukasaConstants.INITIAL_STREAM_PATH;
    final String STREAM_FILE_NAME_PREFIX = ChukasaConstants.STREAM_FILE_NAME_PREFIX;
    final String M3U8_FILE_NAME = ChukasaConstants.M3U8_FILE_NAME;
    final String M3U8_FILE_EXTENSION = ChukasaConstants.M3U8_FILE_EXTENSION;

    @Setter
    private int adaptiveBitrateStreaming;

    private final IChukasaModelManagementComponent chukasaModelManagementComponent;

    @Autowired
    public PlaylistCreator(IChukasaModelManagementComponent chukasaModelManagementComponent) {
        this.chukasaModelManagementComponent = requireNonNull(chukasaModelManagementComponent, "chukasaModelManagementComponent");
    }

    public void create() {

        try {

            final ChukasaModel chukasaModel = chukasaModelManagementComponent.get(adaptiveBitrateStreaming);
            final String STREAM_FILE_EXTENSION = chukasaModel.getStreamFileExtension();

            final int URI_IN_PLAYLIST = chukasaModel.getHlsConfiguration().getUriInPlaylist();
            final int TARGET_DURATION = chukasaModel.getHlsConfiguration().getDuration() + 1;
            final double DURATION = (double) chukasaModel.getHlsConfiguration().getDuration();
            final PlaylistType playlistType = chukasaModel.getChukasaSettings().getPlaylistType();
            final String playlistPath = chukasaModel.getStreamPath() + FILE_SEPARATOR + M3U8_FILE_NAME + M3U8_FILE_EXTENSION;
            final boolean canEncrypt = chukasaModel.getChukasaSettings().isCanEncrypt();

            final FfmpegVcodecType ffmpegVcodecType = chukasaModel.getFfmpegVcodecType();

            final int sequenceMediaSegment = chukasaModel.getSequenceMediaSegment();
            final int sequencePlaylist = chukasaModel.getSequencePlaylist();
            log.info("sequenceMediaSegment = {}, sequencePlaylist = {}", sequenceMediaSegment, sequencePlaylist);

            // イニシャルストリームのみか否か。
            // sequenceMediaSegment が 0 以上にならない限りイニシャルストリームを流し続ける。
            if(sequenceMediaSegment >= 0){

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(playlistPath)))) {

                    bufferedWriter.write("#EXTM3U");
                    bufferedWriter.newLine();
                    bufferedWriter.write("#EXT-X-VERSION:7");
                    bufferedWriter.newLine();
                    bufferedWriter.write("#EXT-X-TARGETDURATION:" + TARGET_DURATION);
                    bufferedWriter.newLine();

                    // MIX STREAM or ONLY LIVE STREAM
                    if (URI_IN_PLAYLIST - 1 > sequenceMediaSegment) {
                        // MIX STREAM
                        log.info("MIX STREAM");

                        final int initialSequenceInPlaylist = chukasaModel.getSequenceInitialPlaylist() + 1;
                        chukasaModel.setSequenceInitialPlaylist(initialSequenceInPlaylist);

                        if (playlistType == PlaylistType.LIVE) {
                            bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:" + initialSequenceInPlaylist);
                            bufferedWriter.newLine();
                            bufferedWriter.write("#EXT-X-DISCONTINUITY-SEQUENCE:" + 1);
                        } else if (playlistType == PlaylistType.EVENT) {
                            bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:0");
                        }
                        bufferedWriter.newLine();

                        // TODO: HardwareAccelerationType -> hls_segment_type
                        // /usr/local/bin/ffmpeg -i now_transcoding.ts -acodec aac -ab 160k -ar 48000 -ac 2 -s 1280x720 -vcodec hevc_nvenc -tag:v hvc1 -g 60 -b:v 2560k -threads 1 -f hls -hls_segment_type fmp4 -segment_time 2 i.m3u8
                        if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC){
                            bufferedWriter.write("#EXT-X-MAP:URI=\"" + initialStreamPath + "/init.mp4\"");
                            bufferedWriter.newLine();
                        }

                        if (playlistType == PlaylistType.LIVE) {
                            for (int i = initialSequenceInPlaylist; i < initialSequenceInPlaylist + URI_IN_PLAYLIST - (sequenceMediaSegment + 1); i++) {
                                bufferedWriter.write("#EXTINF:" + DURATION + ",");
                                bufferedWriter.newLine();
                                bufferedWriter.write(initialStreamPath + "/" + "i" + i + STREAM_FILE_EXTENSION);
                                bufferedWriter.newLine();
                            }
                        } else if (playlistType == PlaylistType.EVENT) {
                            for (int i = 0; i < initialSequenceInPlaylist + URI_IN_PLAYLIST - (sequenceMediaSegment + 1); i++) {
                                bufferedWriter.write("#EXTINF:" + DURATION + ",");
                                bufferedWriter.newLine();
                                bufferedWriter.write(initialStreamPath + "/" + "i" + i + STREAM_FILE_EXTENSION);
                                bufferedWriter.newLine();
                            }
                        }

                        bufferedWriter.write("#EXT-X-DISCONTINUITY");
                        bufferedWriter.newLine();

                        if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC){
                            bufferedWriter.write("#EXT-X-MAP:URI=\"" + STREAM_FILE_NAME_PREFIX + ".mp4\"");
                            bufferedWriter.newLine();
                        }

                        for (int i = 0; i < sequenceMediaSegment + 1; i++) {
                            if(canEncrypt) {
                                bufferedWriter.write("#EXT-X-KEY:METHOD=AES-128,URI=");
                                bufferedWriter.write("\"" + "" + chukasaModel.getKeyArrayList().get(i) + i + ".key\"" + ",IV=0x");
                                bufferedWriter.write(chukasaModel.getIvArrayList().get(i));
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.write("#EXTINF:" + chukasaModel.getExtinfList().get(i) + ",");
                            bufferedWriter.newLine();
                            bufferedWriter.write(STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                            bufferedWriter.newLine();
                        }

                    } else {
                        // ONLY LIVE STREAM
                        log.info("ONLY LIVE STREAM");

                        if (playlistType == PlaylistType.LIVE) {
                            final int extXMmediaSequence = sequenceMediaSegment - (URI_IN_PLAYLIST - 1) + chukasaModel.getSequenceInitialPlaylist() + 1;
                            bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:" + extXMmediaSequence);
                            bufferedWriter.newLine();
                            bufferedWriter.write("#EXT-X-DISCONTINUITY-SEQUENCE:" + 1);
                        } else if (playlistType == PlaylistType.EVENT) {
                            bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:0");
                        }
                        bufferedWriter.newLine();

                        if (playlistType == PlaylistType.EVENT) {
                            final int sequenceInitialPlaylist = chukasaModel.getSequenceInitialPlaylist();
                            for (int i = 0; i < sequenceInitialPlaylist + 1; i++) {
                                bufferedWriter.write("#EXTINF:" + DURATION + ",");
                                bufferedWriter.newLine();
                                bufferedWriter.write(initialStreamPath + "/" + "i" + i + STREAM_FILE_EXTENSION);
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.write("#EXT-X-DISCONTINUITY");
                            bufferedWriter.newLine();
                        }

                        if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC){
                            bufferedWriter.write("#EXT-X-MAP:URI=\"" + STREAM_FILE_NAME_PREFIX + ".mp4\"");
                            bufferedWriter.newLine();
                        }

                        if (playlistType == PlaylistType.LIVE) {
                            for (int i = sequenceMediaSegment - (URI_IN_PLAYLIST - 1); i < sequenceMediaSegment + URI_IN_PLAYLIST - (URI_IN_PLAYLIST - 1); i++) {
                                if(canEncrypt) {
                                    bufferedWriter.write("#EXT-X-KEY:METHOD=AES-128,URI=");
                                    bufferedWriter.write("\"" + "" + chukasaModel.getKeyArrayList().get(i) + i + ".key\"" + ",IV=0x");
                                    bufferedWriter.write(chukasaModel.getIvArrayList().get(i));
                                    bufferedWriter.newLine();
                                }
                                bufferedWriter.write("#EXTINF:" + chukasaModel.getExtinfList().get(i) + ",");
                                bufferedWriter.newLine();
                                bufferedWriter.write(STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                                bufferedWriter.newLine();
                            }
                        } else if (playlistType == PlaylistType.EVENT) {
                            for (int i = 0; i < sequenceMediaSegment + URI_IN_PLAYLIST; i++) {
                                if(canEncrypt) {
                                    bufferedWriter.write("#EXT-X-KEY:METHOD=AES-128,URI=");
                                    bufferedWriter.write("\"" + "" + chukasaModel.getKeyArrayList().get(i) + i + ".key\"" + ",IV=0x");
                                    bufferedWriter.write(chukasaModel.getIvArrayList().get(i));
                                    bufferedWriter.newLine();
                                }
                                bufferedWriter.write("#EXTINF:" + chukasaModel.getExtinfList().get(i) + ",");
                                bufferedWriter.newLine();
                                bufferedWriter.write(STREAM_FILE_NAME_PREFIX + i + STREAM_FILE_EXTENSION);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    final int lastSequenceMediaSegment = chukasaModel.getSequenceLastMediaSegment();
                    if(lastSequenceMediaSegment > -1){
                        if(sequenceMediaSegment >= lastSequenceMediaSegment - (URI_IN_PLAYLIST - 1)){
                            bufferedWriter.write("#EXT-X-ENDLIST");
                            log.info("end of playlist: {}", lastSequenceMediaSegment);
                        }
                    }
                }
                chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);

            } else {

                // FFmpeg で生成されるストリームを検出できないため、イニシャルストリームのみを流すプレイリスト

                // ONLY INITIAL STREAM //
                log.info("ONLY INITIAL STREAM");

                final int sequenceInitialPlaylist;
                if (playlistType == PlaylistType.LIVE) {
                    sequenceInitialPlaylist = chukasaModel.getSequenceInitialPlaylist() + 1;
                } else if(playlistType == PlaylistType.EVENT) {
                    sequenceInitialPlaylist = 0;
                } else {
                    sequenceInitialPlaylist = 0;
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(playlistPath)))) {

                    bufferedWriter.write("#EXTM3U");
                    bufferedWriter.newLine();
                    bufferedWriter.write("#EXT-X-VERSION:7");
                    bufferedWriter.newLine();
                    bufferedWriter.write("#EXT-X-TARGETDURATION:" + TARGET_DURATION);
                    bufferedWriter.newLine();
                    bufferedWriter.write("#EXT-X-MEDIA-SEQUENCE:" + sequenceInitialPlaylist);
                    bufferedWriter.newLine();

                    if(ffmpegVcodecType == FfmpegVcodecType.HEVC_NVENC){
                        bufferedWriter.write("#EXT-X-MAP:URI=\"" + initialStreamPath + "/init.mp4\"");
                        bufferedWriter.newLine();
                    }

                    if(playlistType == PlaylistType.LIVE) {
                        for (int i = sequenceInitialPlaylist; i < sequenceInitialPlaylist + URI_IN_PLAYLIST; i++) {
                            bufferedWriter.write("#EXTINF:" + Double.toString(DURATION) + ",");
                            bufferedWriter.newLine();
                            bufferedWriter.write(initialStreamPath + "/" + "i" + i + STREAM_FILE_EXTENSION);
                            bufferedWriter.newLine();
                        }
                    }else if(playlistType == PlaylistType.EVENT){
                        for (int i = 0; i < sequenceInitialPlaylist + URI_IN_PLAYLIST; i++) {
                            bufferedWriter.write("#EXTINF:" + Double.toString(DURATION) + ",");
                            bufferedWriter.newLine();
                            bufferedWriter.write(initialStreamPath + "/" + "i" + i + STREAM_FILE_EXTENSION);
                            bufferedWriter.newLine();
                        }
                    }else{

                    }
                }

                chukasaModel.setSequenceInitialPlaylist(sequenceInitialPlaylist);
                chukasaModelManagementComponent.update(adaptiveBitrateStreaming, chukasaModel);
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(playlistPath)));
            String s;
            while((s = bufferedReader.readLine()) != null){
                System.out.println(s);
            }

        } catch (IOException e) {
            log.error("{} {}", e.getMessage(), e);
        }
    }
}

