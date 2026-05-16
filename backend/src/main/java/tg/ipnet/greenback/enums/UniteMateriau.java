package tg.ipnet.greenback.enums;

public enum UniteMateriau {
KG("Kilogramme"),
    M3("Mètre cube"),
    M2("Mètre carré"),
    UNITE("Unité");

    private String label;

    UniteMateriau(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}