package pro.hirooka.chukasa.domain.model.common;

import pro.hirooka.chukasa.domain.config.common.type.ChannelType;

public class Tuner {
    private ChannelType channelType;
    private String deviceName;

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
}
