package pro.hirooka.chukasa.domain.operator;

import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;

public interface IHlsOperator {
    ChukasaModel startPlayback(ChukasaSettings chukasaSettings, String userAgent, String servletRealPath);
    void stopPlayback();
    void removeStream();
}
