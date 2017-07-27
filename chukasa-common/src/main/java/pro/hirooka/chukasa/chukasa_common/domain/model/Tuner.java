package pro.hirooka.chukasa.chukasa_common.domain.model;

import lombok.Data;
import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;

@Data
public class Tuner {
    private ChannelType channelType;
    private String deviceName;
}
