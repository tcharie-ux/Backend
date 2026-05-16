package tg.ipnet.greenback.dto;

import java.time.LocalDateTime;

import tg.ipnet.greenback.entity.Projet;

public class ValidationDto {
private String commentaire;
private Boolean statut;
private LocalDateTime dateValidation;
private ProjetDto projet;

public String getCommentaire() {
    return commentaire;
}
public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
}
public Boolean getStatut() {
    return statut;
}
public void setStatut(Boolean statut) {
    this.statut = statut;
}
public LocalDateTime getDateValidation() {
    return dateValidation;
}
public void setDateValidation(LocalDateTime dateValidation) {
    this.dateValidation = dateValidation;
}
public ProjetDto getProjet() {
    return projet;
}
public void setProjet(ProjetDto projet) {
    this.projet = projet;
}

}
