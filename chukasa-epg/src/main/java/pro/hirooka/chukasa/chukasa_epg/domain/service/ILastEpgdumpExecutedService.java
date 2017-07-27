package pro.hirooka.chukasa.chukasa_epg.domain.service;

import pro.hirooka.chukasa.chukasa_epg.domain.model.LastEpgdumpExecuted;

public interface ILastEpgdumpExecutedService {
    LastEpgdumpExecuted create(LastEpgdumpExecuted lastEPGDumpExecuted);
    LastEpgdumpExecuted read(int unique);
    LastEpgdumpExecuted update(LastEpgdumpExecuted lastEPGDumpExecuted);
    void delete(int unique);
}
