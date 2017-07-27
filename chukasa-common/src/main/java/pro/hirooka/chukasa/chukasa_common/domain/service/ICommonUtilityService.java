package pro.hirooka.chukasa.chukasa_common.domain.service;

import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;
import pro.hirooka.chukasa.chukasa_common.domain.model.ChannelConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.model.Tuner;

import java.util.List;

public interface ICommonUtilityService {
    List<ChannelConfiguration> getChannelConfigurationList();
    String getStreamRootPath(String servletRealPath);
    List<Tuner> getTunerList();
    ChannelType getChannelType(int physicalLogicalChannel);
}
