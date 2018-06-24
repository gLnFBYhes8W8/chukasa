package pro.hirooka.chukasa.domain.model.recorder;

public class RecordingProgramModel {
    private String fileName;
    private long startRecording;
    private long stopRecording;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
}
