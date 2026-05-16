package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="elementPlan")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class ElementPlan {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private float longeur;
private float epaisseur;
private String type;
private float hauteur;
private float position_X;
private float position_Y;
private float rotation;
private float scaleX;
private float scaleY;
@ManyToOne
private Modelisation_2D modelisation2D;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public float getLongeur() {
    return longeur;
}
public void setLongeur(float longeur) {
    this.longeur = longeur;
}
public float getEpaisseur() {
    return epaisseur;
}
public void setEpaisseur(float epaisseur) {
    this.epaisseur = epaisseur;
}
public String getType() {
    return type;
}
public void setType(String type) {
    this.type = type;
}
public float getHauteur() {
    return hauteur;
}
public void setHauteur(float hauteur) {
    this.hauteur = hauteur;
}
public float getPosition_X() {
    return position_X;
}
public void setPosition_X(float position_X) {
    this.position_X = position_X;
}
public float getPosition_Y() {
    return position_Y;
}
public void setPosition_Y(float position_Y) {
    this.position_Y = position_Y;
}
public Modelisation_2D getModelisation2D() {
    return modelisation2D;
}
public void setModelisation2D(Modelisation_2D modelisation2d) {
    modelisation2D = modelisation2d;
}
public float getRotation() {
    return rotation;
}
public void setRotation(float rotation) {
    this.rotation = rotation;
}
public float getScaleX() {
    return scaleX;
}
public void setScaleX(float scaleX) {
    this.scaleX = scaleX;
}
public float getScaleY() {
    return scaleY;
}
public void setScaleY(float scaleY) {
    this.scaleY = scaleY;
}
}
