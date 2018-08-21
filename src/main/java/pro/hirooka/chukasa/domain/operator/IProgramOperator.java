package pro.hirooka.chukasa.domain.operator;

import pro.hirooka.chukasa.domain.model.epg.Program;

import java.util.List;

public interface IProgramOperator {
    void deleteOldProgramList();
    List<Program> getProgramListNow();
    List<List<Program>> getOneDayFromNow();
}
