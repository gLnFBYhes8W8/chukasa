package pro.hirooka.chukasa.domain.model.hls;

import pro.hirooka.chukasa.domain.config.common.type.PlaylistType;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.config.common.type.TranscodingSettings;
import pro.hirooka.chukasa.domain.model.epg.TunerType;

public class ChukasaSettings {

    //@Id
    private int adaptiveBitrateStreaming;
    private StreamingType streamingType;
    private PlaylistType playlistType;
    private TranscodingSettings transcodingSettings;
    private boolean canEncrypt;
    private int channelRecording;
    private TunerType tunerType;
    private String fileName;

    private String videoResolution;
    private int videoBitrate;
    private int audioBitrate;

    public int getAdaptiveBitrateStreaming() {
        return adaptiveBitrateStreaming;
    }

    public void setAdaptiveBitrateStreaming(int adaptiveBitrateStreaming) {
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
    }

    public StreamingType getStreamingType() {
        return streamingType;
    }

    public void setStreamingType(StreamingType streamingType) {
        this.streamingType = streamingType;
    }

    public PlaylistType getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(PlaylistType playlistType) {
        this.playlistType = playlistType;
    }

    public TranscodingSettings getTranscodingSettings() {
        return transcodingSettings;
    }

    public void setTranscodingSettings(TranscodingSettings transcodingSettings) {
        this.transcodingSettings = transcodingSettings;
    }

    public boolean isCanEncrypt() {
        return canEncrypt;
    }

    public void setCanEncrypt(boolean canEncrypt) {
        this.canEncrypt = canEncrypt;
    }

    public int getChannelRecording() {
        return channelRecording;
    }

    public void setChannelRecording(int channelRecording) {
        this.channelRecording = channelRecording;
    }

    public TunerType getTunerType() {
        return tunerType;
    }

    public void setTunerType(TunerType tunerType) {
        this.tunerType = tunerType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    @Override
    public String toString() {
        return "ChukasaSettings{" +
                "adaptiveBitrateStreaming=" + adaptiveBitrateStreaming +
                ", streamingType=" + streamingType +
                ", playlistType=" + playlistType +
                ", transcodingSettings=" + transcodingSettings +
                ", canEncrypt=" + canEncrypt +
                ", channelRecording=" + channelRecording +
                ", tunerType=" + tunerType +
                ", fileName='" + fileName + '\'' +
                ", videoResolution='" + videoResolution + '\'' +
                ", videoBitrate=" + videoBitrate +
                ", audioBitrate=" + audioBitrate +
                '}';
    }
}
