package pro.hirooka.chukasa.chukasa_common.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChannelConfiguration {
    private ChannelType channelType;
    private int remoteControllerChannel;
    private int physicalLogicalChannel;
    private int frequency;
}
