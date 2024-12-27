package knure.ua.model.components.arrows;

import lombok.Getter;

public enum Cardinality {
    None(""),
    One("1"),
    NulOrOne("0..1"),
    Many("*"),
    NulToMany("0..*"),
    OneToMany("1..*");

    @Getter
    private String cardinality;

    Cardinality(String cardinality) {
        this.cardinality = cardinality;
    }
}
