package pro.hirooka.chukasa.domain.activity;

import pro.hirooka.chukasa.domain.model.epg.Program;

import java.util.List;

public interface IProgramActivity {
    List<Program> getProgramListNow();
}
