package pro.hirooka.chukasa.chukasa_epg.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pro.hirooka.chukasa.chukasa_epg.domain.model.LastEpgdumpExecuted;

public interface ILastEpgdumpExecutedRepository extends MongoRepository<LastEpgdumpExecuted, Integer> {
}
