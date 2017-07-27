package pro.hirooka.chukasa.chukasa_recorder.domain.service;

import pro.hirooka.chukasa.chukasa_recorder.domain.model.ReservedProgram;

import java.util.List;

public interface IRecorderService {
    ReservedProgram create(ReservedProgram reservedProgram);
    List<ReservedProgram> read();
    ReservedProgram read(int id);
    ReservedProgram update(ReservedProgram reservedProgram);
    void delete(int id);
    void deleteAll();
}
