package simulation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/*
Autur: Jakub Frydrych
Lab: Wielowątkość
Data: 20.12.2022
Indeks: 263991
 */

class NarrowBridge implements Runnable {

    private int maxOnBridge=1;
    private boolean start;
    private int driveOneDirection=0;
    private final int DONT_STARVE=5;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    List<Bus> allBuses = new LinkedList<Bus>();

    List<Bus> busesWaiting = new LinkedList<>();

    List<Bus> busesOnTheBridge = new LinkedList<>();

    private JTextArea logs;

    private SimulationTypes simulationTypes= SimulationTypes.ONLY_ONE;

    private Direction direction;
    private int delay;
    private int westProba = 50;

    public NarrowBridge(JTextArea logs) {
        this.logs = logs;
        allBuses = Collections.synchronizedList(new ArrayList<>());
        this.start=true;
    }

    synchronized void getOnTheBridge(Bus bus) throws InterruptedException {
        busesWaiting.add(bus);
        support.firePropertyChange("Changed sizes", busesWaiting.size() - 1, busesWaiting.size());
        while(!canGetOnBridge(bus)){
            bus.setWaitingOnBridge(true);
            writeToLog(String.format("[%d -> %s]: czeka przed mostem", bus.getId(), bus.getDrivingDirection()));
            wait();

        }
        busesWaiting.remove(bus);
        bus.setWaitingOnBridge(false);
        busesOnTheBridge.add(bus);
        if (simulationTypes == SimulationTypes.ONE_FOR_SITE) {
            direction = bus.getDrivingDirection();
        }
        support.firePropertyChange("Changed sizes", busesWaiting.size() - 1, busesWaiting.size());

    }

    private boolean canGetOnBridge(Bus bus) {
        if (simulationTypes == SimulationTypes.ONLY_ONE || simulationTypes == SimulationTypes.HIGHWAY) {
            return busesOnTheBridge.size() < maxOnBridge;
        } else if (simulationTypes == SimulationTypes.ONE_FOR_SITE) {
            long count = busesOnTheBridge.stream()
                    .filter(b -> b.getDrivingDirection() == bus.getDrivingDirection())
                    .count();
            return count < maxOnBridge / 2;
        } else if (simulationTypes == SimulationTypes.ONLY_TWO) {
            if (busesOnTheBridge.size() < maxOnBridge && driveOneDirection <= DONT_STARVE) {
                return direction == null || bus.getDrivingDirection() == direction;
            }
        }
        return false;
    }

    synchronized void getOffTheBridge(Bus bus) {
        busesOnTheBridge.remove(bus);
        writeToLog(String.format("[%d -> %s]: Opuszcza most", bus.getId(), bus.getDrivingDirection()));
        if (simulationTypes == SimulationTypes.ONE_FOR_SITE) {
            driveOneDirection++;
            if (busesOnTheBridge.isEmpty()) {
                if (driveOneDirection >= DONT_STARVE) {
                    driveOneDirection = 0;
                    direction = direction == Direction.EAST ? Direction.WEST : Direction.EAST;
                } else {
                    direction = null;
                    driveOneDirection = 0;
                }
            }
            support.firePropertyChange("Changed sizes", busesOnTheBridge.size() - 1, busesOnTheBridge.size());
            notifyAll();
            notify();
        }
    }

    synchronized void writeToLog(String message) {
        SwingUtilities.invokeLater(() -> logs.append(message + "\n"));
    }



    public synchronized String getWaitingBusesMessage() {
        return busesWaiting.stream()
                .map(Bus::toString)
                .reduce("", (start, current) -> start + " " + current);
    }

    public synchronized String getBussesOnBridgeMessage() {
        return busesOnTheBridge.stream()
                .map(Bus::toString)
                .reduce("", (start, current) -> start + " " + current);
    }


    public synchronized void changeRule(SimulationTypes simulationType) throws InterruptedException {
        while (!busesOnTheBridge.isEmpty()) {
            wait();
        }
        wait(2000);
        switch (simulationType) {
            case ONLY_ONE:
                maxOnBridge = 1;
                break;
            case HIGHWAY:
                maxOnBridge = 1000;
                break;
            case ONLY_TWO:
                maxOnBridge = 4;
                break;
            case ONE_FOR_SITE:
                maxOnBridge = 2;
                break;
        }
        this.simulationTypes = simulationType;
        notifyAll();
    }


    @Override
    public void run() {
        this.start = true;
        while (start) {
            new Thread(() -> {
                Direction direction = chooseRandomDirection();
                Bus bus = new Bus(this, direction);
                allBuses.add(bus);
                new Thread(bus).start();
            }).start();
            try {
                Thread.sleep(5000 -delay);
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null,
                        "Wystąpił błąd",
                        "BŁĄD",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        }
        writeToLog("Koniec symulacji");
        this.logs = new JTextArea();
    }

    private Direction chooseRandomDirection() {
        int random = ThreadLocalRandom.current().nextInt(1,101);
        return random<=westProba ? Direction.WEST : Direction.EAST;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }


    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setWestProba(int westProba) {
        this.westProba = westProba;
    }
}
