package com.airport.domain;

public enum TypeCategory {
    INTERNATIONAL("International"),
    DOMESTIC("Domestic"),
    CHARTER("Charter"),
    LOW_COST("Low Cost");

    private String description;

    TypeCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
