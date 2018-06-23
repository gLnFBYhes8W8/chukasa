package pro.hirooka.chukasa.domain.model.hls;

import lombok.Data;
import pro.hirooka.chukasa.domain.config.common.type.PlaylistType;
import pro.hirooka.chukasa.domain.config.common.type.StreamingType;
import pro.hirooka.chukasa.domain.config.common.type.TranscodingSettings;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;

@Data
public class ChukasaSettings {

    //@Id
    private int adaptiveBitrateStreaming;
    private StreamingType streamingType;
    private PlaylistType playlistType;
    private TranscodingSettings transcodingSettings;
    private boolean canEncrypt;
    private int physicalLogicalChannel;
    private TunerType tunerType;
    private String fileName;

    private String videoResolution;
    private int videoBitrate;
    private int audioBitrate;
}
