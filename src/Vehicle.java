public class Vehicle {
    private final String destination;
    private final int entryTime;

    public Vehicle(String destination, int entryTime) {
        this.destination = destination;
        this.entryTime = entryTime;
    }

    public String getDestination() {
        return destination;
    }

    public int getEntryTime() {
        return entryTime;
    }


}
