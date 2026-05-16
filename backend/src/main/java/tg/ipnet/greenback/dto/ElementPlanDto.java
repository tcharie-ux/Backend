package tg.ipnet.greenback.dto;

public class ElementPlanDto {
private Integer id;
private float longeur;
private float epaisseur;
private String type;
private float hauteur;
private float position_X;
private float position_Y;
private float rotation;
private float scaleX;
private float scaleY;
private Integer idModelisation2D;
private Modelisation_2DDto modelisation2D;
public Integer getId() {
    return id;
}
public void setId(Integer id) {
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
public Integer getIdModelisation2D() {
    return idModelisation2D;
}
public void setIdModelisation2D(Integer idModelisation2D) {
    this.idModelisation2D = idModelisation2D;
}
public Modelisation_2DDto getModelisation2D() {
    return modelisation2D;
}
public void setModelisation2D(Modelisation_2DDto modelisation2d) {
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
