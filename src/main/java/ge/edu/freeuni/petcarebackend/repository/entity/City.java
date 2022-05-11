package ge.edu.freeuni.petcarebackend.repository.entity;

public enum City {
    TBILISI("თბილისი"),
    BATUMI("ბათუმი"),
    RUSTAVI("რუსთავი"),
    KUTAISI("ქუთაისი");

    private String nameKa;

    City(String nameKa) {
        this.nameKa = nameKa;
    }

    public String getNameKa() {
        return nameKa;
    }
}
