package pro.hirooka.chukasa.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pro.hirooka.chukasa.domain.model.epg.TunerType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelConfiguration {

    private TunerType type;
    private int channelRemoteControl;
    private int channelRecording;

    public TunerType getType() {
        return type;
    }

    public void setType(TunerType type) {
        this.type = type;
    }

    public int getChannelRemoteControl() {
        return channelRemoteControl;
    }

    public void setChannelRemoteControl(int channelRemoteControl) {
        this.channelRemoteControl = channelRemoteControl;
    }

    public int getChannelRecording() {
        return channelRecording;
    }

    public void setChannelRecording(int channelRecording) {
        this.channelRecording = channelRecording;
    }
}
