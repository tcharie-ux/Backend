package tg.ipnet.greenback.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tg.ipnet.greenback.security.model.User;
@Entity
@Table(name="architecture")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Architecture {
    @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private String fileName;
private String fileType;
private long taille;
private LocalDateTime dateDepot;
@Lob
private byte[] data;
@ManyToOne
@JoinColumn(name = "projet_id", nullable = false)
private Projet projet;
@ManyToOne
@JoinColumn(name = "auteur_id", nullable = false)
private User auteur;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public String getFileName() {
    return fileName;
}
public void setFileName(String fileName) {
    this.fileName = fileName;
}
public String getFileType() {
    return fileType;
}
public void setFileType(String fileType) {
    this.fileType = fileType;
}
public long getTaille() {
    return taille;
}
public void setTaille(long taille) {
    this.taille = taille;
}
public LocalDateTime getDateDepot() {
    return dateDepot;
}
public void setDateDepot(LocalDateTime dateDepot) {
    this.dateDepot = dateDepot;
}
public byte[] getData() {
    return data;
}
public void setData(byte[] data) {
    this.data = data;
}
public Projet getProjet() {
    return projet;
}
public void setProjet(Projet projet) {
    this.projet = projet;
}
public User getAuteur() {
    return auteur;
}
public void setAuteur(User auteur) {
    this.auteur = auteur;
}
}
