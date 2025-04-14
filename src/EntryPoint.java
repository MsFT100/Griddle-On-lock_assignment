import java.util.Random;

public class EntryPoint extends Thread {
    private final Road road;
    private final int carsPerHour;
    private final String name;
    private final Random random = new Random();
    private final Clock clock;
    private boolean simulationEnded = false;

    public EntryPoint(String name, Road road, int carsPerHour, Clock clock) {
        this.name = name;
        this.road = road;
        this.carsPerHour = carsPerHour;
        this.clock = clock;
    }

    @Override
    public void run() {
        int carsGenerated = 0;

        while (clock.isRunning()) {
            try {
                // Sleep to simulate car creation rate (cars per hour converted to milliseconds)
                Thread.sleep(360000 / carsPerHour);

                // Generate a random destination
                String[] destinations = {"University", "Station", "Shopping Centre", "Industrial Park"};
                String destination = destinations[random.nextInt(4)];

                // Create a vehicle with destination and timestamp
                Vehicle vehicle = new Vehicle(destination, clock.getCurrentTime());

                // Add the vehicle to the road
                if (road.addVehicle(vehicle)) {
                    carsGenerated++;
                    Statistics.incrementCreated();// update stats
                    System.out.println(name + " added a vehicle to the road. The destination is: " + destination);
                }

                // Stop the loop if the clock has run out (i.e., if simulation time exceeds 3600 seconds)
                if (clock.getCurrentTime() >= 3600) {
                    simulationEnded = true;
                }

            } catch (InterruptedException e) {
                // Handle the interruption gracefully
                System.out.println(name + " thread interrupted.");
                simulationEnded = true;
                break; // Exit the loop if interrupted
            }
        }

        System.out.println(name + " stopped generating vehicles.");
        System.out.println(carsGenerated + "  generated vehicles.");
    }

    public void stopSimulation() {
        simulationEnded = true;
        interrupt(); // Ensure thread stops if it's sleeping
    }
}
