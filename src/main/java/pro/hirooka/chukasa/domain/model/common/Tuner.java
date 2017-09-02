package pro.hirooka.chukasa.domain.model.common;

import lombok.Data;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;

@Data
public class Tuner {
    private ChannelType channelType;
    private String deviceName;
}
