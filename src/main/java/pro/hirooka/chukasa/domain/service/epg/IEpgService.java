package pro.hirooka.chukasa.domain.service.epg;

import pro.hirooka.chukasa.domain.model.epg.*;

import java.util.List;

public interface IEpgService {
    List<ChannelConfiguration> getChannelConfigurationList();
    List<Tuner> getTunerList();
    TunerType getTunerType(int channelRemoteControl);
    Program create(Program program);

    LatestEpgAcquisition createLatestEpgAcquisition(LatestEpgAcquisition latestEpgAcquisition);
    LatestEpgAcquisition readLatestEpgAcquisition(int unique);
    LatestEpgAcquisition updateLatestEpgAcquisition(LatestEpgAcquisition latestEpgAcquisition);
    void deleteLatestEpgAcquisition(int unique);
}
