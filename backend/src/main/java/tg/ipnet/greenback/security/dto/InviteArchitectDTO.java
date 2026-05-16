package tg.ipnet.greenback.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InviteArchitectDTO {

    @Email
    @NotBlank
    private String emailArchitecte;

    @NotNull
    private Integer idProjet;

    private String message;

    public String getEmailArchitecte() {
        return emailArchitecte;
    }

    public void setEmailArchitecte(String emailArchitecte) {
        this.emailArchitecte = emailArchitecte;
    }

    public Integer getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Integer idProjet) {
        this.idProjet = idProjet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
