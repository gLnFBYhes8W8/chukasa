package pro.hirooka.chukasa.chukasa_hls.domain.service;

import pro.hirooka.chukasa.chukasa_hls.domain.model.ChukasaModel;

import java.util.List;

public interface IChukasaModelManagementComponent {
    ChukasaModel create(int adaptiveBitrateStreaming, ChukasaModel chukasaModel);
    List<ChukasaModel> get();
    ChukasaModel get(int adaptiveBitrateStreaming);
    ChukasaModel update(int adaptiveBitrateStreaming, ChukasaModel chukasaModel);
    void delete(int adaptiveBitrateStreaming);
    void deleteAll();
}
