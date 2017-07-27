package pro.hirooka.chukasa.chukasa_hls.domain.model;

import lombok.Data;
import pro.hirooka.chukasa.chukasa_common.domain.enums.PlaylistType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.StreamingType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.TranscodingSettings;

@Data
public class ChukasaSettings {

    //@Id
    private int adaptiveBitrateStreaming;
    private StreamingType streamingType;
    private PlaylistType playlistType;
    private TranscodingSettings transcodingSettings;
    private boolean canEncrypt;
    private int physicalLogicalChannel;
    private String fileName;

    private String videoResolution;
    private int videoBitrate;
    private int audioBitrate;
}
