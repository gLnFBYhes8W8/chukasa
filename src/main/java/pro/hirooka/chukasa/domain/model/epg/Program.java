package pro.hirooka.chukasa.domain.model.epg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Program {
    private UUID uuid = UUID.randomUUID();
    @Id
    private String id;
    private String channel;
    private String title;
    private String detail;
    //    private List<Item> extdetail;
    private long start;
    private long end;
    private long duration;
//    private List<Category> category;
//    private List<?> attachinfo;
//    private Video video;
//    private List<Audio> audio;
//    private boolean freeCA;
//    private int eventID;

    private long begin;
    //private int physicalChannel;
    private String channelName;
    private String beginDate;
    private String endDate;
    //    private int physicalLogicalChannel;
//    private int remoteControllerChannel;
    private int channelRecording;
    private int channelRemoteControl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

//    public int getPhysicalLogicalChannel() {
//        return physicalLogicalChannel;
//    }
//
//    public void setPhysicalLogicalChannel(int physicalLogicalChannel) {
//        this.physicalLogicalChannel = physicalLogicalChannel;
//    }
//
//    public int getRemoteControllerChannel() {
//        return remoteControllerChannel;
//    }
//
//    public void setRemoteControllerChannel(int remoteControllerChannel) {
//        this.remoteControllerChannel = remoteControllerChannel;
//    }

    public int getChannelRecording() {
        return channelRecording;
    }

    public void setChannelRecording(int channelRecording) {
        this.channelRecording = channelRecording;
    }

    public int getChannelRemoteControl() {
        return channelRemoteControl;
    }

    public void setChannelRemoteControl(int channelRemoteControl) {
        this.channelRemoteControl = channelRemoteControl;
    }
}

