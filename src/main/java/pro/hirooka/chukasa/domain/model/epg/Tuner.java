package pro.hirooka.chukasa.domain.model.epg;

public class Tuner {

    private String displayName;
    private TunerType type;
    private String deviceName;
    private int index;
    private String command;

    private int channelRecording;
    private long pid;
    private TunerStatus status = TunerStatus.AVAILABLE;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TunerType getType() {
        return type;
    }

    public void setType(TunerType type) {
        this.type = type;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getChannelRecording() {
        return channelRecording;
    }

    public void setChannelRecording(int channelRecording) {
        this.channelRecording = channelRecording;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public TunerStatus getStatus() {
        return status;
    }

    public void setStatus(TunerStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tuner{" +
                "displayName='" + displayName + '\'' +
                ", type=" + type +
                ", deviceName='" + deviceName + '\'' +
                ", index=" + index +
                ", command='" + command + '\'' +
                ", channelRecording=" + channelRecording +
                ", pid=" + pid +
                ", status=" + status +
                '}';
    }
}

