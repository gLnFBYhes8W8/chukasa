package pro.hirooka.chukasa.domain.model.recorder;

import org.springframework.data.annotation.Id;

public class ReservedProgram {
    @Id
    private int id;
    private String channel;
    private String title;
    private String detail;
    private long start;
    private long end;
    private long duration;
//    private boolean freeCA;
//    private int eventID;

    private long begin;
    private String channelName;
    private String beginDate;
    private String endDate;
    private int physicalLogicalChannel;
    private int remoteControllerChannel;

    private long startRecording;
    private long stopRecording;
    //private long durationRecording;
    private long recordingDuration;

    private String fileName;
    RecordingStatus recordingStatus;
    M4vTranscodingStatus m4vTranscodingStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPhysicalLogicalChannel() {
        return physicalLogicalChannel;
    }

    public void setPhysicalLogicalChannel(int physicalLogicalChannel) {
        this.physicalLogicalChannel = physicalLogicalChannel;
    }

    public int getRemoteControllerChannel() {
        return remoteControllerChannel;
    }

    public void setRemoteControllerChannel(int remoteControllerChannel) {
        this.remoteControllerChannel = remoteControllerChannel;
    }

    public long getStartRecording() {
        return startRecording;
    }

    public void setStartRecording(long startRecording) {
        this.startRecording = startRecording;
    }

    public long getStopRecording() {
        return stopRecording;
    }

    public void setStopRecording(long stopRecording) {
        this.stopRecording = stopRecording;
    }

    public long getRecordingDuration() {
        return recordingDuration;
    }

    public void setRecordingDuration(long recordingDuration) {
        this.recordingDuration = recordingDuration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RecordingStatus getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(RecordingStatus recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    public M4vTranscodingStatus getM4vTranscodingStatus() {
        return m4vTranscodingStatus;
    }

    public void setM4vTranscodingStatus(M4vTranscodingStatus m4vTranscodingStatus) {
        this.m4vTranscodingStatus = m4vTranscodingStatus;
    }
}
