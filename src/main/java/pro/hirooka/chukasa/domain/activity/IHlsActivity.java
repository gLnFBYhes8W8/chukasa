package pro.hirooka.chukasa.domain.activity;

import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;

public interface IHlsActivity {
    ChukasaModel startPlayback(ChukasaSettings chukasaSettings, String userAgent, String servletRealPath);
    void stopPlayback();
    void removeStream();
}
