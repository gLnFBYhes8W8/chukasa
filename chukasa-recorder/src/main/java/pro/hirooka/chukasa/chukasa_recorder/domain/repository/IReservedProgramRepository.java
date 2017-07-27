package pro.hirooka.chukasa.chukasa_recorder.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pro.hirooka.chukasa.chukasa_recorder.domain.model.ReservedProgram;

public interface IReservedProgramRepository extends MongoRepository<ReservedProgram, Integer> {
}
