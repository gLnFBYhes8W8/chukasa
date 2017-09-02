package pro.hirooka.chukasa.domain.service.epg;

import pro.hirooka.chukasa.domain.model.epg.type.EpgdumpStatus;

public interface IEpgdumpService {
    EpgdumpStatus getStatus();
    boolean isEpgdump();
}
