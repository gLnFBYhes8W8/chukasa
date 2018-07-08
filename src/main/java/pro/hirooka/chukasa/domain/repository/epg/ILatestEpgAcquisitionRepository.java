package pro.hirooka.chukasa.domain.repository.epg;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.model.epg.LatestEpgAcquisition;

@Repository
public interface ILatestEpgAcquisitionRepository extends MongoRepository<LatestEpgAcquisition, Integer> {
}
