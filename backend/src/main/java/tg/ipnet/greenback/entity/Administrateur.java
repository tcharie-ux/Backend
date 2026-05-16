package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="administrateur")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Administrateur {
        @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

}
