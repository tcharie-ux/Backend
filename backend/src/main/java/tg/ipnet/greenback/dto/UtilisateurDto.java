package tg.ipnet.greenback.dto;

import java.util.List;

import tg.ipnet.greenback.entity.Projet;

public class UtilisateurDto {
private String nom;
private String prenom;
private String password;
private String email;
private int telephone;
private int role;
private List<ProjetDto> projets;
public String getNom() {
    return nom;
}
public void setNom(String nom) {
    this.nom = nom;
}
public String getPrenom() {
    return prenom;
}
public void setPrenom(String prenom) {
    this.prenom = prenom;
}
public String getPassword() {
    return password;
}
public void setPassword(String password) {
    this.password = password;
}
public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}
public int getTelephone() {
    return telephone;
}
public void setTelephone(int telephone) {
    this.telephone = telephone;
}
public int getRole() {
    return role;
}
public void setRole(int role) {
    this.role = role;
}
public List<ProjetDto> getProjets() {
    return projets;
}
public void setProjets(List<ProjetDto> projets) {
    this.projets = projets;
}
}
