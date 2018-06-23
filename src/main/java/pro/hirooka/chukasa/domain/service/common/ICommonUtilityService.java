package pro.hirooka.chukasa.domain.service.common;

import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.Tuner;
import pro.hirooka.chukasa.domain.model.common.type.TunerType;

import java.util.List;

public interface ICommonUtilityService {
    List<ChannelConfiguration> getChannelConfigurationList();
    String getStreamRootPath(String servletRealPath);
    List<Tuner> getTunerList();
    ChannelType getChannelType(int physicalLogicalChannel);
    TunerType getTunerType(int channel);
}
