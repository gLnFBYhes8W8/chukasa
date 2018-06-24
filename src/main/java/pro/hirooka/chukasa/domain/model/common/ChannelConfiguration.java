package pro.hirooka.chukasa.domain.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pro.hirooka.chukasa.domain.config.common.type.ChannelType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelConfiguration {
    private ChannelType channelType;
    private int remoteControllerChannel;
    private int physicalLogicalChannel;
    private int frequency;

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public int getRemoteControllerChannel() {
        return remoteControllerChannel;
    }

    public void setRemoteControllerChannel(int remoteControllerChannel) {
        this.remoteControllerChannel = remoteControllerChannel;
    }

    public int getPhysicalLogicalChannel() {
        return physicalLogicalChannel;
    }

    public void setPhysicalLogicalChannel(int physicalLogicalChannel) {
        this.physicalLogicalChannel = physicalLogicalChannel;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
