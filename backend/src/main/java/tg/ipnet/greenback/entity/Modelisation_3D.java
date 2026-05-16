
package tg.ipnet.greenback.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tg.ipnet.greenback.enums.Format3D;

@Entity
@Table(name="modelisation_3D")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Modelisation_3D {
     @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private String nomModel;
private Format3D format ;
private LocalDateTime dateCreation;
@Column(columnDefinition = "TEXT")
private String url_model;
@OneToOne
@JoinColumn(name = "modelisation_2D_id")
private Modelisation_2D modelisation2D;
public int getId() {
     return id;
}
public void setId(int id) {
     this.id = id;
}
public String getNomModel() {
     return nomModel;
}
public void setNomModel(String nomModel) {
     this.nomModel = nomModel;
}
public Format3D getFormat() {
     return format;
}
public void setFormat(Format3D format) {
     this.format = format;
}
public LocalDateTime getDateCreation() {
     return dateCreation;
}
public void setDateCreation(LocalDateTime dateCreation) {
     this.dateCreation = dateCreation;
}
public String getUrl_model() {
     return url_model;
}
public void setUrl_model(String url_model) {
     this.url_model = url_model;
}
public Modelisation_2D getModelisation2D() {
     return modelisation2D;
}
public void setModelisation2D(Modelisation_2D modelisation2d) {
     modelisation2D = modelisation2d;
}
}
