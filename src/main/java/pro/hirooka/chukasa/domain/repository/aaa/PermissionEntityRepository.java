package pro.hirooka.chukasa.domain.repository.aaa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.entity.aaa.PermissionEntity;

import java.util.UUID;

@Repository
public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, UUID> {
    PermissionEntity findOneByName(String name);
}
