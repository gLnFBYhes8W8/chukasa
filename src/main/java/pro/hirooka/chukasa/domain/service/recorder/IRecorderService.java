package pro.hirooka.chukasa.domain.service.recorder;

import pro.hirooka.chukasa.domain.model.recorder.ReservedProgram;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public interface IRecorderService {
    ReservedProgram create(ReservedProgram reservedProgram);
    List<ReservedProgram> read();
    ReservedProgram read(int id);
    ReservedProgram update(ReservedProgram reservedProgram);
    void delete(int id);
    void deleteAll();
    void recordDirectly(ReservedProgram reservedProgram, String hyarukaUri);
    void reserve(RecorderRunnable recorderRunnable);
    void cancel(int id);
    void cancelAll();
    Map<Integer, ScheduledFuture> getScheduledFutureMap();
}
