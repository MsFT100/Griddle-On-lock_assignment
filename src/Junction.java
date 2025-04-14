import java.util.concurrent.locks.ReentrantLock;
import java.io.FileWriter;
import java.io.IOException;

public class Junction extends Thread {
    private final FixedSizeArray<Road> entryRoads; // Custom array for entry roads
    private final FixedSizeArray<Road> exitRoads; // Custom array for exit roads
    private final int greenLightDuration;
    private final ReentrantLock lock = new ReentrantLock();
    private final Clock clock;
    private boolean simulationEnded = false;
    private final String name;
    private final String logFile;

    public Junction(FixedSizeArray<Road> entryRoads, FixedSizeArray<Road> exitRoads, int greenLightDuration, Clock clock, String name) {
        this.entryRoads = entryRoads;
        this.exitRoads = exitRoads;
        this.greenLightDuration = greenLightDuration;
        this.clock = clock;
        this.name = name;
        this.logFile = "logs/" + name + "_log.txt"; // Each junction has its own log file
    }

    private void logActivity(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) { // Append mode
            writer.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to log file for " + name + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (clock.isRunning()) {
            for (int i = 0; i < entryRoads.getSize(); i++) {
                Road entryRoad = entryRoads.get(i); // Access road via index
                if (entryRoad == null) continue;

                try {
                    // Simulate green light for each entry road
                    Thread.sleep(greenLightDuration * 1000);
                    moveCars(entryRoad);
                } catch (InterruptedException e) {
                    simulationEnded = true;
                    break;
                }

                if (clock.getCurrentTime() >= 3600) { // Check simulation time
                    simulationEnded = true;
                    break;
                }
            }
        }

        //System.out.println("Junction stopped.");
    }

    private void moveCars(Road entryRoad) {
        lock.lock();
        try {
            int carsThrough = 0;
            int carsWaiting = entryRoad.getVehiclesWaiting(); // Number of cars waiting before processing
            boolean isGridlocked = false;

            for (int i = 0; i < 12; i++) { // Move up to 12 vehicles per cycle
                Vehicle vehicle = entryRoad.removeVehicle();
                if (vehicle != null) {

                    Road destinationRoad = findRoadByName(exitRoads, vehicle.getDestination());
                    if (destinationRoad != null && destinationRoad.addVehicle(vehicle)) {
                        carsThrough++;
                        System.out.println("In " + name + " Vehicle moved to destination: " + vehicle.getDestination());
                    } else {
                        entryRoad.addVehicle(vehicle);
                        isGridlocked = true; // Exit road cannot accept more vehicles
                        break;
                    }
                } else {
                    break; // No more vehicles on the entry road
                }
            }

            int remainingQueue = entryRoad.getVehiclesWaiting();
            String logMessage = String.format(
                    "Time: %dm%ds - Junction %s: %d cars through from %s, %d cars waiting.%s",
                    clock.getCurrentTime() / 60,
                    clock.getCurrentTime() % 60,
                    name,
                    carsThrough,
                    entryRoad.getName(),
                    remainingQueue,
                    isGridlocked ? " GRIDLOCK" : ""
            );

            logActivity(logMessage);
            //System.out.println(logMessage); // Optional: log to console
        } finally {
            lock.unlock();
        }
    }

    private Road findRoadByName(FixedSizeArray<Road> roads, String name) {
        for (int i = 0; i < roads.getSize(); i++) {
            Road road = roads.get(i);
            if (road != null && road.getName().equals(name)) {
                return road;
            }
        }
        return null;
    }

    public void stopSimulation() {
        simulationEnded = true;
        interrupt();
    }




}
