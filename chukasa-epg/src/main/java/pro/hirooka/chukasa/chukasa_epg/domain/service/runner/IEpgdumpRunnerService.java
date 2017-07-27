package pro.hirooka.chukasa.chukasa_epg.domain.service.runner;

import pro.hirooka.chukasa.chukasa_common.domain.model.ChannelConfiguration;

import java.util.List;
import java.util.concurrent.Future;

public interface IEpgdumpRunnerService {
    Future<Integer> submit(List<ChannelConfiguration> channelConfigurationList);
    void cancel();
}
