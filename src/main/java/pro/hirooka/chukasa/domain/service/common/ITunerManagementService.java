package pro.hirooka.chukasa.domain.service.common;

import pro.hirooka.chukasa.domain.config.common.type.ChannelType;
import pro.hirooka.chukasa.domain.config.common.type.RecxxxDriverType;
import pro.hirooka.chukasa.domain.config.common.type.TunerUseType;
import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;
import pro.hirooka.chukasa.domain.model.common.TunerStatus;

import java.util.List;

public interface ITunerManagementService {
    TunerStatus create(TunerStatus tunerStatus);
    List<TunerStatus> get();
    List<TunerStatus> get(ChannelType channelType);
    List<TunerStatus> available(ChannelType channelType);
    TunerStatus findOne(ChannelType channelType);
    TunerStatus get(String deviceName);
    TunerStatus update(TunerStatus tunerStatus);
    TunerStatus update(TunerStatus tunerStatus, boolean canUse);
    TunerStatus update(String deviceName, boolean canUse);
    RecxxxDriverType getRecxxxDriverType();
    String getDeviceOption();
    String getDeviceArgument(TunerStatus tunerStatus);
    String getDeviceArgument(TunerUseType tunerUseType, int physicalLogicalChannel, List<ChannelConfiguration> channelConfigurationList);
    void releaseAll();
}
