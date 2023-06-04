package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */


class Bus implements Runnable {

    public static final int MIN_BOARDING_TIME = 1000;
    public static final int MAX_BOARDING_TIME = 10000;

    public static final int GETTING_TO_BRIDGE_TIME = 500;

    public static final int CROSSING_BRIDGE_TIME = 3000;

    public static final int GETTING_PARKING_TIME = 500;

    public static final int UNLOADING_TIME = 500;
    private static int BUS_ID;


    private static int numberOfBuses = 0;

    private int x;
    private int y;
    private static final List<Integer> AVAILABLE_Y = new ArrayList<>();
    private static final List<Integer> RESERVE_Y = new ArrayList<>();


    private final int id;
    private final NarrowBridge simulation;
    private final Direction drivingDirection;
    private boolean waitingOnBridge;

    public Bus(NarrowBridge simulation, Direction drivingDirection) {
        this.simulation = simulation;
        this.drivingDirection = drivingDirection;
        this.id = (++BUS_ID) % 100;
    }


    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }


    public void boarding() throws InterruptedException {
        simulation.writeToLog(String.format("[%d -> %s]: Zbieram pasażerów", id, drivingDirection));
        Thread.sleep((ThreadLocalRandom.current().nextInt(MIN_BOARDING_TIME, MAX_BOARDING_TIME)));
    }

    void goToTheBridge() {
        simulation.writeToLog(String.format("[%d -> %s]: Jedzie w strone mosty", id, drivingDirection));
        sleep(GETTING_TO_BRIDGE_TIME);
    }

    void rideTheBridge() {
        simulation.writeToLog(String.format("[%d -> %s]: Przejezdza przez most", id, drivingDirection));
        sleep(CROSSING_BRIDGE_TIME);
    }

    void goToTheParking() {
        simulation.writeToLog(String.format("[%d -> %s]: Jedzie w strone parkingu", id, drivingDirection));
        sleep(GETTING_PARKING_TIME);
    }

    void unloading() {
        simulation.writeToLog(String.format("[%d -> %s]: Rozładunek pasazerow", id, drivingDirection));
        sleep(UNLOADING_TIME);
    }


    public void run() {

        try {
            boarding();
            goToTheBridge();
            simulation.getOnTheBridge(this);
            rideTheBridge();
            simulation.getOffTheBridge(this);
            goToTheParking();
            unloading();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public int getId() {
        return id;
    }


    public Direction getDrivingDirection() {
        return drivingDirection;
    }


    public void setWaitingOnBridge(boolean waitingOnBridge) {
        this.waitingOnBridge = waitingOnBridge;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}