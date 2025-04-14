import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {

        // Ensure logs directory exists
        new File("logs").mkdirs();

        // Load configuration
        Properties config = loadConfig();

        // Read traffic values from config
        int carsPerHourNorth = Integer.parseInt(config.getProperty("North", "0"));
        int carsPerHourEast = Integer.parseInt(config.getProperty("East", "0"));
        int carsPerHourSouth = Integer.parseInt(config.getProperty("South", "0"));

        int junctionALightsDuration = Integer.parseInt(config.getProperty("A", "0"));
        int junctionBLightsDuration = Integer.parseInt(config.getProperty("B", "0"));
        int junctionCLightsDuration = Integer.parseInt(config.getProperty("C", "0"));
        int junctionDLightsDuration = Integer.parseInt(config.getProperty("D", "0"));

        // Access the parsed data
        System.out.println("junctionALightsDuration: " + junctionALightsDuration);
        System.out.println("junctionBLightsDuration: " + junctionBLightsDuration);
        System.out.println("junctionCLightsDuration: " + junctionCLightsDuration);
        System.out.println("junctionDLightsDuration: " + junctionDLightsDuration);


        // Initialize roads with names
        Road northRoad = new Road(50, "North Road");
        Road southRoad = new Road(60, "South Road");
        Road eastRoad = new Road(30, "East Road");
        Road roadToShoppingCentreAB = new Road(10, "Shopping Centre");
        Road roadToUniversityAB = new Road(10, "University");
        Road roadToStationAB = new Road(10, "Station");

        Road roadToShoppingCentreBC = new Road(10, "Shopping Centre");
        Road roadToUniversityBC = new Road(10, "University");
        Road roadToStationBC = new Road(10, "Station");

        Road roadToIndustrialCB = new Road(10, "Industrial Park");
        Road roadToIndustrialBA = new Road(10, "Industrial Park");
        Road industrial = new Road(15, "Industrial Park");

        Road roadToUniversityCD = new Road(10, "University");
        Road roadToStationCD= new Road(10, "Station");

        Road Shopping = new Road(7, "Shopping Centre");

        Road roadDUniversity = new Road(15, "University");
        Road roadDStation = new Road(15, "Station");


        Clock clock = new Clock();

        // Initialize entry points
        EntryPoint northEntry = new EntryPoint("North Entry", northRoad, carsPerHourNorth, clock);
        EntryPoint eastEntry = new EntryPoint("East Entry", eastRoad, carsPerHourEast, clock);
        EntryPoint southEntry = new EntryPoint("South Entry", southRoad, carsPerHourSouth, clock);

        // Initialize car parks
        CarPark universityCarPark = new CarPark(roadDUniversity, 100, "University CarPark", clock);
        CarPark stationCarPark = new CarPark(roadDStation, 150, "Station CarPark", clock);
        CarPark shoppingCentreCarPark = new CarPark(Shopping, 400, "Shopping Centre CarPark", clock);
        CarPark industrialCarPark = new CarPark(industrial, 1000, "Industrial CarPark", clock);

        // Initialize junctions
        FixedSizeArray<Road> entryRoadsA = new FixedSizeArray<>(4);
        entryRoadsA.add(southRoad);
        entryRoadsA.add(roadToIndustrialBA);

        FixedSizeArray<Road> exitRoadsA = new FixedSizeArray<>(6);
        exitRoadsA.add(roadToShoppingCentreAB);
        exitRoadsA.add(roadToUniversityAB);
        exitRoadsA.add(roadToStationAB);
        exitRoadsA.add(industrial);

        Junction junctionA = new Junction(
                entryRoadsA,
                exitRoadsA,
                junctionALightsDuration,
                clock, "JunctionA"
        );

        FixedSizeArray<Road> entryRoadsB = new FixedSizeArray<>(5);
        entryRoadsB.add(roadToIndustrialCB);
        entryRoadsB.add(eastRoad);

        FixedSizeArray<Road> exitRoadsB = new FixedSizeArray<>(5);
        exitRoadsB.add(roadToShoppingCentreBC);
        exitRoadsB.add(roadToUniversityBC);
        exitRoadsB.add(roadToStationBC);
        exitRoadsB.add(roadToIndustrialBA);


        Junction junctionB = new Junction(
                entryRoadsB,
                exitRoadsB,
                junctionBLightsDuration,
                clock, "JunctionB"
        );

        FixedSizeArray<Road> entryRoadsC = new FixedSizeArray<>(5);
        entryRoadsC.add(Shopping);
        entryRoadsC.add(roadToUniversityBC);
        entryRoadsC.add(roadToStationBC);
        entryRoadsC.add(northRoad);

        FixedSizeArray<Road> exitRoadsC = new FixedSizeArray<>(5);
        exitRoadsC.add(Shopping);
        exitRoadsC.add(roadToStationCD);
        exitRoadsC.add(roadToUniversityCD);
        exitRoadsC.add(roadToIndustrialCB);


        Junction junctionC = new Junction(
                entryRoadsC,
                exitRoadsC,
                junctionCLightsDuration,
                clock, "JunctionC"
        );

        FixedSizeArray<Road> entryRoadsD = new FixedSizeArray<>(2);
        entryRoadsD.add(roadToStationCD);
        entryRoadsD.add(roadToUniversityCD);

        FixedSizeArray<Road> exitRoadsD = new FixedSizeArray<>(2);
        exitRoadsD.add(roadDUniversity);
        exitRoadsD.add(roadDStation);

        Junction junctionD = new Junction(
                entryRoadsD,
                exitRoadsD,
                junctionDLightsDuration,
                clock, "JunctionD"
        );

        // Start simulation
        clock.start();
        northEntry.start();
        eastEntry.start();
        southEntry.start();
        universityCarPark.start();
        stationCarPark.start();
        shoppingCentreCarPark.start();
        industrialCarPark.start();
        junctionA.start();
        junctionB.start();
        junctionC.start();
        junctionD.start();


        // Schedule task to report car park status every 10 simulated minutes
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Time: " + clock.getCurrentTime() + "m");
            System.out.println("University: " + universityCarPark.getAvailableSpaces() + " Spaces");
            System.out.println("Station: " + stationCarPark.getAvailableSpaces() + " Spaces");
            System.out.println("Shopping Centre: " + shoppingCentreCarPark.getAvailableSpaces() + " Spaces");
            System.out.println("Industrial Park: " + industrialCarPark.getAvailableSpaces() + " Spaces");
        }, 1, 1, TimeUnit.MINUTES);

        // Wait for all threads to complete
        try {
            clock.join();
            northEntry.join();
            eastEntry.join();
            southEntry.join();
            universityCarPark.join();
            stationCarPark.join();
            shoppingCentreCarPark.join();
            industrialCarPark.join();
            junctionA.join();
            junctionB.join();
            junctionC.join();
            junctionD.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down the scheduler
            scheduler.shutdownNow();

            // Stop all threads
            stopThreads(northEntry, eastEntry, southEntry, universityCarPark, stationCarPark, shoppingCentreCarPark,
                    industrialCarPark, junctionA, junctionB, junctionC, junctionD);

            universityCarPark.generateReport();
            stationCarPark.generateReport();
            shoppingCentreCarPark.generateReport();
            industrialCarPark.generateReport();
            Statistics.generateFinalReport();
        }

        // Log the end of the simulation
        System.out.println("Simulation complete at: ");
        System.out.println("Generating final reports...");



        // Final reports
        try {
            saveReportToFile("logs/final_report_carparks.txt", universityCarPark, stationCarPark, shoppingCentreCarPark, industrialCarPark);
            saveReportToFile("logs/final_report_roads.txt", roadDStation,roadDUniversity,roadToStationAB,
                    roadToIndustrialCB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("src/config.properties")) {
            System.out.println("Using configuration file: src/config.properties");
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
        return properties;
    }

    // Method to save report to a file
    public static void saveReportToFile(String fileName, CarPark... carParks) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (CarPark carPark : carParks) {
                writer.write(carPark.reportToString());
                writer.write("\n");
            }
        }
    }


    public static void saveReportToFile(String fileName, Road... roads) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Road road: roads) {
                writer.write(road.reportToString());
                writer.write("\n");
            }
        }
    }

    private static void stopThreads(Thread... threads) {
        for (Thread thread : threads) {
            if (thread instanceof EntryPoint) {
                ((EntryPoint) thread).stopSimulation(); // Stop entry point threads
            }
            if (thread instanceof Junction) {
                ((Junction) thread).stopSimulation(); // Stop junction threads
            }
            thread.interrupt(); // Interrupt each thread to stop it
        }
    }
}
