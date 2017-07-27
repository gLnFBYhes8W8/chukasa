package pro.hirooka.chukasa.chukasa_hls.domain.service;

public interface ICoordinatorService {
    void execute();
    void cancel();
    void stop();
    void remove();
}
