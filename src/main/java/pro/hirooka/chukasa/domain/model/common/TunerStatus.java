package pro.hirooka.chukasa.domain.model.common;

import lombok.Data;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.config.common.type.RecxxxDriverType;
import pro.hirooka.chukasa.domain.config.common.type.TunerUseType;

@Data
public class TunerStatus {
    private ChannelType channelType;
    private String deviceName;
    private int index;
    boolean canUse;
    private RecxxxDriverType recxxxDriverType;
    private TunerUseType tunerUseType;
}
