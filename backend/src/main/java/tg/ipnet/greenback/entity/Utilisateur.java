package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tg.ipnet.greenback.enums.Role;

@Entity
@Table(name="utilisateur")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Utilisateur {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private String nom;
private String prenom;
private String password;
private String email;
private int telephone;
@Enumerated(EnumType.STRING)
private Role role;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
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
public Role getRole() {
    return role;
}
public void setRole(Role role) {
    this.role = role;
}
}
