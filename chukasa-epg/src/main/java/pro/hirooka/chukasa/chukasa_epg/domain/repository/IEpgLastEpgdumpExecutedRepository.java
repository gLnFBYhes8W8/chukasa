package pro.hirooka.chukasa.chukasa_epg.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.chukasa_epg.domain.model.LastEpgdumpExecuted;

@Repository
public interface IEpgLastEpgdumpExecutedRepository extends MongoRepository<LastEpgdumpExecuted, Integer> {
}
