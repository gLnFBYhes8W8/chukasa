package pro.hirooka.chukasa.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChannelConfigurationWrapper {

    @JsonProperty("channelConfiguration")
    private List<ChannelConfiguration> channelConfigurationList;

    public List<ChannelConfiguration> getChannelConfigurationList() {
        return channelConfigurationList;
    }

    public void setChannelConfigurationList(List<ChannelConfiguration> channelConfigurationList) {
        this.channelConfigurationList = channelConfigurationList;
    }
}
