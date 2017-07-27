package pro.hirooka.chukasa.chukasa_common.domain.service;

import pro.hirooka.chukasa.chukasa_common.domain.enums.ChannelType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.RecxxxDriverType;
import pro.hirooka.chukasa.chukasa_common.domain.enums.TunerUseType;
import pro.hirooka.chukasa.chukasa_common.domain.model.ChannelConfiguration;
import pro.hirooka.chukasa.chukasa_common.domain.model.TunerStatus;

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
}
