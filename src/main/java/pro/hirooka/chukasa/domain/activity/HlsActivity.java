package pro.hirooka.chukasa.domain.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.hirooka.chukasa.domain.model.hls.ChukasaModel;
import pro.hirooka.chukasa.domain.model.hls.ChukasaSettings;
import pro.hirooka.chukasa.domain.operator.IHlsOperator;

import static java.util.Objects.requireNonNull;

@Service
public class HlsActivity implements IHlsActivity {

    private static final Logger log = LoggerFactory.getLogger(HlsActivity.class);

    private final IHlsOperator hlsOperator;

    @Autowired
    public HlsActivity(
            IHlsOperator hlsOperator
    ) {
        this.hlsOperator = requireNonNull(hlsOperator);
    }

    @Override
    public ChukasaModel startPlayback(ChukasaSettings chukasaSettings, String userAgent, String servletRealPath) {
        return hlsOperator.startPlayback(chukasaSettings, userAgent, servletRealPath);
    }

    @Override
    public void stopPlayback() {
        hlsOperator.stopPlayback();
    }

    @Override
    public void removeStream() {
        hlsOperator.removeStream();
    }
}
