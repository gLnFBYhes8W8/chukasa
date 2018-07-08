package pro.hirooka.chukasa.domain.operator;

import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

public interface IRecorderOperator {
    void onBoot();
    ReservedProgram create(ReservedProgram reservedProgram);
}
