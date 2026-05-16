package tg.ipnet.greenback.security.service;

import java.util.List;
import java.util.UUID;

public class AuthenticationResponse {
    private String token;
    private UUID id;
    private String fullName;
    private String username;
    private Long ministere;
    private String direction;
    private List<String> roles;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String token, UUID id, String fullName, String username, Long ministere, String direction, List<String> roles) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.ministere = ministere;
        this.direction = direction;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
