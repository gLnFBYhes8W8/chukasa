package pro.hirooka.chukasa.domain.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChannelConfiguration {
    private ChannelType channelType;
    private int remoteControllerChannel;
    private int physicalLogicalChannel;
    private int frequency;
}
