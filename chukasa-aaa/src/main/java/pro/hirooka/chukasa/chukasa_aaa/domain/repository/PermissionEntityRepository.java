package pro.hirooka.chukasa.chukasa_aaa.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.chukasa_aaa.domain.entity.PermissionEntity;

import java.util.UUID;

@Repository
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, UUID> {
    PermissionEntity findOneByName(String name);
}
