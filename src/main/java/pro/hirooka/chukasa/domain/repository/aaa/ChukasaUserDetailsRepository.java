package pro.hirooka.chukasa.domain.repository.aaa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pro.hirooka.chukasa.domain.model.aaa.ChukasaUserDetails;

import java.util.UUID;

public interface ChukasaUserDetailsRepository extends MongoRepository<ChukasaUserDetails, UUID> {
    ChukasaUserDetails findOneByUsername(String username);
}
