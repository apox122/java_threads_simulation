package simulation;/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */


enum Direction {

    EAST("W"),
    WEST("Z");

    private final String directionSymbol;

    Direction(String directionSymbol) {
        this.directionSymbol = directionSymbol;
    }

    @Override
    public String toString() {
        return directionSymbol;
    }
}