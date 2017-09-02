package pro.hirooka.chukasa.domain.service.hls;

public interface ICoordinatorService {
    void execute();
    void cancel();
    void stop();
    void remove();
}
