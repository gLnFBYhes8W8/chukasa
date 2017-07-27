package pro.hirooka.chukasa.chukasa_recorder.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.chukasa_recorder.domain.model.ReservedProgram;

@Repository
public interface IReservedProgramRepository extends MongoRepository<ReservedProgram, Integer> {
}
