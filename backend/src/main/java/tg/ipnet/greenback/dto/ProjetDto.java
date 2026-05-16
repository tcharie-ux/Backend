package tg.ipnet.greenback.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProjetDto {
private Integer id;
private String description;
private String nomProjet;
private boolean statut;
private LocalDateTime dateCreation;
private List<ValidationDto> validations;
private List<Modelisation_2DDto> modeles2D;
private UUID idClient;
private String nomClient;
private UUID idArchitecte;
private String nomArchitecte;
private String emailArchitecteInvite;
private Long idNotificationInvitationArchitecte;
private String tokenInvitationArchitecte;
private List<ArchitectureDto> architectures;
public Integer getId() {
    return id;
}
public void setId(Integer id) {
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
public List<ValidationDto> getValidations() {
    return validations;
}
public void setValidations(List<ValidationDto> validations) {
    this.validations = validations;
}
public List<Modelisation_2DDto> getModeles2D() {
    return modeles2D;
}
public void setModeles2D(List<Modelisation_2DDto> modeles2d) {
    modeles2D = modeles2d;
}
public UUID getIdClient() {
    return idClient;
}
public void setIdClient(UUID idClient) {
    this.idClient = idClient;
}
public String getNomClient() {
    return nomClient;
}
public void setNomClient(String nomClient) {
    this.nomClient = nomClient;
}
public UUID getIdArchitecte() {
    return idArchitecte;
}
public void setIdArchitecte(UUID idArchitecte) {
    this.idArchitecte = idArchitecte;
}
public String getNomArchitecte() {
    return nomArchitecte;
}
public void setNomArchitecte(String nomArchitecte) {
    this.nomArchitecte = nomArchitecte;
}
public String getEmailArchitecteInvite() {
    return emailArchitecteInvite;
}
public void setEmailArchitecteInvite(String emailArchitecteInvite) {
    this.emailArchitecteInvite = emailArchitecteInvite;
}
public Long getIdNotificationInvitationArchitecte() {
    return idNotificationInvitationArchitecte;
}
public void setIdNotificationInvitationArchitecte(Long idNotificationInvitationArchitecte) {
    this.idNotificationInvitationArchitecte = idNotificationInvitationArchitecte;
}
public String getTokenInvitationArchitecte() {
    return tokenInvitationArchitecte;
}
public void setTokenInvitationArchitecte(String tokenInvitationArchitecte) {
    this.tokenInvitationArchitecte = tokenInvitationArchitecte;
}
public List<ArchitectureDto> getArchitectures() {
    return architectures;
}
public void setArchitectures(List<ArchitectureDto> architectures) {
    this.architectures = architectures;
}
}
