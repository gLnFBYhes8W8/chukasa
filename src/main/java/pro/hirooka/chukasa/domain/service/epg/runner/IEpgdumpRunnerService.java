package pro.hirooka.chukasa.domain.service.epg.runner;

import pro.hirooka.chukasa.domain.model.common.ChannelConfiguration;

import java.util.List;
import java.util.concurrent.Future;

public interface IEpgdumpRunnerService {
    Future<Integer> submit(List<ChannelConfiguration> channelConfigurationList);
    void cancel();
}
