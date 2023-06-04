package simulation;/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */


public enum SimulationTypes {
    ONLY_ONE("Tylko jeden bus na moście"),
    HIGHWAY("Autostrada"),
    ONE_FOR_SITE("Ruch w obie strony, po 1 bus"),
    ONLY_TWO("Ruch po 2 busy w jedną stronę ");

    private final String description;

    SimulationTypes(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return description;
    }
}