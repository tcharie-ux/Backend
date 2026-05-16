package tg.ipnet.greenback.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tg.ipnet.greenback.security.model.User;


@Entity
@Table(name="projet")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Projet {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
private String description;
private String nomProjet;
private boolean statut;
private LocalDateTime dateCreation;
@OneToMany(mappedBy = "projet")
private List<Validation> validations;
@OneToMany(mappedBy = "projet")
private List<Modelisation_2D> modeles2D;
@ManyToOne
@JoinColumn(name = "client_id", nullable = false)
private User client;
@ManyToOne
@JoinColumn(name = "architecte_id")
private User architecte;
@Column(name = "email_architecte_invite")
private String emailArchitecteInvite;
@OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Architecture> architectures;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public String getDescription() {
    return description;
}
public void setDescription(String description) {
    this.description = description;
}
public String getNomProjet() {
    return nomProjet;
}
public void setNomProjet(String nomProjet) {
    this.nomProjet = nomProjet;
}
public boolean isStatut() {
    return statut;
}
public void setStatut(boolean statut) {
    this.statut = statut;
}
public LocalDateTime getDateCreation() {
    return dateCreation;
}
public void setDateCreation(LocalDateTime dateCreation) {
    this.dateCreation = dateCreation;
}
public List<Validation> getValidations() {
    return validations;
}
public void setValidations(List<Validation> validations) {
    this.validations = validations;
}
public List<Modelisation_2D> getModeles2D() {
    return modeles2D;
}
public void setModeles2D(List<Modelisation_2D> modeles2d) {
    modeles2D = modeles2d;
}
public User getClient() {
    return client;
}
public void setClient(User client) {
    this.client = client;
}
public User getArchitecte() {
    return architecte;
}
public void setArchitecte(User architecte) {
    this.architecte = architecte;
}
public String getEmailArchitecteInvite() {
    return emailArchitecteInvite;
}
public void setEmailArchitecteInvite(String emailArchitecteInvite) {
    this.emailArchitecteInvite = emailArchitecteInvite;
}
public List<Architecture> getArchitectures() {
    return architectures;
}
public void setArchitectures(List<Architecture> architectures) {
    this.architectures = architectures;
}
}
