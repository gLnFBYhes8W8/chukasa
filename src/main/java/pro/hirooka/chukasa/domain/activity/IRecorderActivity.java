package pro.hirooka.chukasa.domain.activity;

import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

public interface IRecorderActivity {
    ReservedProgram create(ReservedProgram reservedProgram);
}
