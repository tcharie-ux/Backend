package tg.ipnet.greenback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class NotificationCreationDTO {

    private UUID idDestinataire;

    @Email
    private String emailDestinataire;

    private Integer idProjet;

    private String typeNotification;

    @NotBlank
    private String message;

    public UUID getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(UUID idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    public String getEmailDestinataire() {
        return emailDestinataire;
    }

    public void setEmailDestinataire(String emailDestinataire) {
        this.emailDestinataire = emailDestinataire;
    }

    public Integer getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Integer idProjet) {
        this.idProjet = idProjet;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
