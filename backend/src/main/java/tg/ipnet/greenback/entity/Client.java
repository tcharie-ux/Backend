package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity
@Table(name="client")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Client {
    @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private String adresse;
private String document;
private String localiter;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public String getAdresse() {
    return adresse;
}
public void setAdresse(String adresse) {
    this.adresse = adresse;
}
public String getDocument() {
    return document;
}
public void setDocument(String document) {
    this.document = document;
}
public String getLocaliter() {
    return localiter;
}
public void setLocaliter(String localiter) {
    this.localiter = localiter;
}
}
