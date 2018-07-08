package pro.hirooka.chukasa.domain.operator;

import pro.hirooka.chukasa.domain.model.epg.LatestEpgAcquisition;

public interface IEpgOperator {
    void persist();
    LatestEpgAcquisition readLatestEpgAcquisition(int unique);
}
