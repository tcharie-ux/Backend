package tg.ipnet.greenback.dto;

public class ClientDto  extends UtilisateurDto{
private int id;
private String adresse;
private String document;
private String localiter;
public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public String getAdresse() {
    return adresse;
}
public void setAdresse(String adresse) {
    this.adresse = adresse;
}
public String getDocument() {
    return document;
}
public void setDocument(String document) {
    this.document = document;
}
public String getLocaliter() {
    return localiter;
}
public void setLocaliter(String localiter) {
    this.localiter = localiter;
} 
}
