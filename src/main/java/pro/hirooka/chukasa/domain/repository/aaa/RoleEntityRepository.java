package pro.hirooka.chukasa.domain.repository.aaa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.entity.aaa.RoleEntity;

import java.util.UUID;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {
    RoleEntity findOneByName(String name);
}