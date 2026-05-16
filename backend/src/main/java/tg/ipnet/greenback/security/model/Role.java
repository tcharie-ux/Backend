package tg.ipnet.greenback.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import tg.ipnet.greenback.utils.BaseAuditEntity;

import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role extends BaseAuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private tg.ipnet.greenback.enums.Role name;

    public Role() {
    }

    @PrePersist
    private void ensurePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public tg.ipnet.greenback.enums.Role getName() {
        return name;
    }

    public void setName(tg.ipnet.greenback.enums.Role name) {
        this.name = name;
    }
}
