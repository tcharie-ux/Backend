package tg.ipnet.greenback.dto;

import java.time.LocalDateTime;

import tg.ipnet.greenback.enums.Format3D;

public class Modelisation_3DDto {

    private int id;
private String nomModel;
private Format3D format ;
private LocalDateTime dateCreation;
private String url_model;
public int getId() {
     return id;
}
public void setId(int id) {
     this.id = id;
}
public String getNomModel() {
     return nomModel;
}
public void setNomModel(String nomModel) {
     this.nomModel = nomModel;
}
public Format3D getFormat() {
     return format;
}
public void setFormat(Format3D format) {
     this.format = format;
}
public LocalDateTime getDateCreation() {
     return dateCreation;
}
public void setDateCreation(LocalDateTime dateCreation) {
     this.dateCreation = dateCreation;
}
public String getUrl_model() {
     return url_model;
}
public void setUrl_model(String url_model) {
     this.url_model = url_model;
}
}