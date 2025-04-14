public class Statistics {
    public static int totalCarsCreated = 0;
    public static int totalCarsParked = 0;
    public static int totalCarsQueued = 0;
    private static int totalDiscarded = 0;

    public static synchronized void incrementCreated() {
        totalCarsCreated++;
    }

    public static synchronized void incrementParked() {
        totalCarsParked+=1;
    }

    public static synchronized void incrementQueued() {
        totalCarsQueued+=1;

    }

    public static synchronized void decrementQueued() {
        totalCarsQueued-=1;
    }


    public static void generateFinalReport() {
        System.out.println("Simulation complete.");
        System.out.println("Total Cars Created: " + Statistics.totalCarsCreated);
        System.out.println("Total Cars Parked: " + Statistics.totalCarsParked);
        System.out.println("Total Cars Queued: " + Statistics.totalCarsQueued);

        int sum = Statistics.totalCarsParked + Statistics.totalCarsQueued;
        System.out.println("Sum of Parked and Queued: " + sum);

        if (Statistics.totalCarsCreated == sum) {
            System.out.println("No data lost. All cars accounted for!");
        } else {
            System.out.println("Discrepancy detected! Some cars are missing.");
        }
    }

}

