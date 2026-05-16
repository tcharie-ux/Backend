package tg.ipnet.greenback.security.dto;


import jakarta.validation.constraints.NotBlank;
import tg.ipnet.greenback.enums.Role;

import java.util.UUID;


public class UserDTO {

    @NotBlank
    private String fullName;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Role roles;
    private boolean enable;
    private Long ministere;
    private String direction;
    private UUID publicId;
    private String invitationToken;

    public UserDTO() {
    }

    public UserDTO(String fullName, String username, String password, Role roles, boolean enable, Long ministere, String direction, UUID publicId, String invitationToken) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enable = enable;
        this.ministere = ministere;
        this.direction = direction;
        this.publicId = publicId;
        this.invitationToken = invitationToken;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoles() {
        return roles;
    }

    public void setRoles(Role roles) {
        this.roles = roles;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Long getMinistere() {
        return ministere;
    }

    public void setMinistere(Long ministere) {
        this.ministere = ministere;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }
}
