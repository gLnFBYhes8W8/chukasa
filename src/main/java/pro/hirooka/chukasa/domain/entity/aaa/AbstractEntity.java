package pro.hirooka.chukasa.domain.entity.aaa;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
abstract class AbstractEntity implements Serializable {

    @Id
    private UUID uuid = UUID.randomUUID();

    @CreationTimestamp
    private Date createdDate;

    @UpdateTimestamp
    private Date updatedDate;

    public UUID getUuid() {
        return uuid;
    }
}
