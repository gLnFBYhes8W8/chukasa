package pro.hirooka.chukasa.domain.model.common;

import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.config.common.type.RecxxxDriverType;
import pro.hirooka.chukasa.domain.config.common.type.TunerUseType;

public class TunerStatus {
    private ChannelType channelType;
    private String deviceName;
    private int index;
    boolean canUse;
    private RecxxxDriverType recxxxDriverType;
    private TunerUseType tunerUseType;

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public RecxxxDriverType getRecxxxDriverType() {
        return recxxxDriverType;
    }

    public void setRecxxxDriverType(RecxxxDriverType recxxxDriverType) {
        this.recxxxDriverType = recxxxDriverType;
    }

    public TunerUseType getTunerUseType() {
        return tunerUseType;
    }

    public void setTunerUseType(TunerUseType tunerUseType) {
        this.tunerUseType = tunerUseType;
    }
}
