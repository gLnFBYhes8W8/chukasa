package pro.hirooka.chukasa.domain.repository.aaa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.hirooka.chukasa.domain.entity.aaa.OperationEntity;

import java.util.UUID;

@Repository
public interface OperationEntityRepository extends JpaRepository<OperationEntity, UUID> {
    OperationEntity findOneByName(String name);
}
