package pro.hirooka.chukasa.domain.repository.epg;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.model.epg.LastEpgdumpExecuted;

@Repository
public interface IEpgLastEpgdumpExecutedRepository extends MongoRepository<LastEpgdumpExecuted, Integer> {
}
