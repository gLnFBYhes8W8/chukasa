package pro.hirooka.chukasa.domain.service.hls.detector.event;

import org.springframework.context.ApplicationEvent;

public class LastMediaSegmentSequenceEvent extends ApplicationEvent {

    private int adaptiveBitrateStreaming;

    public LastMediaSegmentSequenceEvent(Object source, int adaptiveBitrateStreaming) {
        super(source);
        this.adaptiveBitrateStreaming = adaptiveBitrateStreaming;
    }

    public int getAdaptiveBitrateStreaming() {
        return adaptiveBitrateStreaming;
    }
}
