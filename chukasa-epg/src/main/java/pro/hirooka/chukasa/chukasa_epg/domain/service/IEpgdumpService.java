package pro.hirooka.chukasa.chukasa_epg.domain.service;

import pro.hirooka.chukasa.chukasa_epg.domain.enums.EpgdumpStatus;

public interface IEpgdumpService {
    EpgdumpStatus getStatus();
}
