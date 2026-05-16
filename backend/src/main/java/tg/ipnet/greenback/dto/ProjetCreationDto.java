package tg.ipnet.greenback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ProjetCreationDto {

    @NotBlank
    private String nomProjet;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime dateCreation;

    @Email
    private String emailArchitecte;

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getEmailArchitecte() {
        return emailArchitecte;
    }

    public void setEmailArchitecte(String emailArchitecte) {
        this.emailArchitecte = emailArchitecte;
    }
}
