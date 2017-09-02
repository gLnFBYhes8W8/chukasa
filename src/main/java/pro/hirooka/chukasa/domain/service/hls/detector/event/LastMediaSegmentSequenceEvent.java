package pro.hirooka.chukasa.domain.service.hls.detector.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class LastMediaSegmentSequenceEvent extends ApplicationEvent {

    @Getter
    private int adaptiveBitrateStreaming;

    public LastMediaSegmentSequenceEvent(Object source, int adaptiveBitrateStreaming) {
        super(source);
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
    }
}
