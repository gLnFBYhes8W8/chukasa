package pro.hirooka.chukasa.domain.repository.aaa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.entity.aaa.UserDetailsEntity;

import java.util.UUID;

@Repository
public interface UserDetailsEntityRepository extends JpaRepository<UserDetailsEntity, UUID> {
    UserDetailsEntity findOneByUsername(String username);
}
