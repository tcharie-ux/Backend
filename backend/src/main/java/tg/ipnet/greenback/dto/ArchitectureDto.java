package tg.ipnet.greenback.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ArchitectureDto {
    private Integer id;
    private String fileName;
    private String fileType;
    private long taille;
    private LocalDateTime dateDepot;
    private UUID idAuteur;
    private String nomAuteur;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille = taille;
    }

    public LocalDateTime getDateDepot() {
        return dateDepot;
    }

    public void setDateDepot(LocalDateTime dateDepot) {
        this.dateDepot = dateDepot;
    }

    public UUID getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(UUID idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }
}
