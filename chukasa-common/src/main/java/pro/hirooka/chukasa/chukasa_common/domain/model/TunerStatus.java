package pro.hirooka.chukasa.chukasa_common.domain.model;

import lombok.Data;
import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.RecxxxDriverType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.TunerUseType;

@Data
public class TunerStatus {
    private ChannelType channelType;
    private String deviceName;
    private int index;
    boolean canUse;
    private RecxxxDriverType recxxxDriverType;
    private TunerUseType tunerUseType;
}
