package tg.ipnet.greenback.dto;

import jakarta.validation.constraints.NotBlank;
import tg.ipnet.greenback.enums.Format3D;

public class Modelisation3DCreationDto {
    @NotBlank
    private String nomModel;

    private Format3D format = Format3D.GLTF;

    @NotBlank
    private String url_model;

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

    public String getUrl_model() {
        return url_model;
    }

    public void setUrl_model(String url_model) {
        this.url_model = url_model;
    }
}
