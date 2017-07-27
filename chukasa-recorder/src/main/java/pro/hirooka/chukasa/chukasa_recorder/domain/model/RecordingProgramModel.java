package pro.hirooka.chukasa.chukasa_recorder.domain.model;

import lombok.Data;

@Data
public class RecordingProgramModel {
    private String fileName;
    private long startRecording;
    private long stopRecording;
}
