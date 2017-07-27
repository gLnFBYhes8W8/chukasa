package pro.hirooka.chukasa.chukasa_recorder.domain.service;

import pro.hirooka.chukasa.chukasa_recorder.domain.model.ReservedProgram;

import java.util.concurrent.Future;

public interface IRecorderRunnerService {
    Future<Integer> submit(ReservedProgram reservedProgram);
    void cancel();
}
