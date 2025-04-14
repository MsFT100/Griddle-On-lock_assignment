import java.util.concurrent.locks.ReentrantLock;

public class CarPark extends Thread {
    private final Road road;
    private final Vehicle[] buffer;
    private final int capacity;
    private int currentCount = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final String carParkName;
    private final Clock clock; // Reference to the simulation clock
    private final long[] journeyTimes; // Array to store journey times
    private int journeyCount = 0; // Track how many journey times are recorded

    public CarPark(Road road, int capacity, String carParkName, Clock clock) {
        this.road = road;
        this.capacity = capacity;
        this.carParkName = carParkName;
        this.clock = clock;
        buffer = new Vehicle[capacity];
        journeyTimes = new long[capacity];
    }

    public void run() {
        while (clock.isRunning()) {
            try {
                Vehicle vehicle = road.removeVehicle();
                if (vehicle != null) {
                    parkVehicle(vehicle);
                    System.out.println("[" + Thread.currentThread().getName() + "] Car parked in " + carParkName + ": " + vehicle.getDestination());
                }
                Thread.sleep(12000); // Simulate the time it takes to park
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Car park " + carParkName + " interrupted and stopping.");
                break;
            }
        }
        //System.out.println(carParkName + " has stopped.");
    }

    public void parkVehicle(Vehicle vehicle) {
        lock.lock();
        try {
            if (currentCount < capacity) {
                buffer[currentCount++] = vehicle;
                Statistics.incrementParked();

                // Calculate journey time
                int journeyTime = clock.getCurrentTime() - vehicle.getEntryTime();
                journeyTimes[journeyCount++] = journeyTime;
            }
        } finally {
            lock.unlock();
        }
    }

    // Generate the final report
    public void generateReport() {
        lock.lock();
        try {
            long totalJourneyTime = 0;

            for (int i = 0; i < journeyCount; i++) {
                totalJourneyTime += journeyTimes[i];
            }

            long averageJourneyTime = journeyCount > 0 ? totalJourneyTime / journeyCount : 0;

            long minutes = averageJourneyTime / 60;
            long seconds = averageJourneyTime % 60;

            System.out.printf("%s: %d Cars parked, average journey time %dm%ds%n",
                    carParkName, currentCount, minutes, seconds);
        } finally {
            lock.unlock();
        }
    }

    // Generate the report as a string
    public String reportToString() {
        lock.lock();
        try {
            StringBuilder report = new StringBuilder();
            report.append("Car Park: ").append(carParkName).append("\n");
            report.append("Capacity: ").append(capacity).append("\n");
            report.append("Occupied Spaces: ").append(currentCount).append("\n");
            report.append("Available Spaces: ").append(capacity - currentCount).append("\n");
            return report.toString();
        } finally {
            lock.unlock();
        }
    }

    // Get available spaces
    public int getAvailableSpaces() {
        lock.lock();
        try {
            return capacity - currentCount;
        } finally {
            lock.unlock();
        }
    }
}
