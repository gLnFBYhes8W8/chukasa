package pro.hirooka.chukasa.chukasa_recorder.domain.service;

import pro.hirooka.chukasa.chukasa_recorder.domain.model.RecordingProgramModel;

import java.util.List;

public interface IRecordingProgramManagementComponent {
    RecordingProgramModel create(int id, RecordingProgramModel recordingProgramModel);
    List<RecordingProgramModel> get();
    RecordingProgramModel get(int id);
    RecordingProgramModel update(int id, RecordingProgramModel recordingProgramModel);
    void delete(int id);
    void deleteAll();
}
