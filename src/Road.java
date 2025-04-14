import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Road {
    private final Vehicle[] buffer;
    private final int capacity;
    private int currentVehicles;
    private int start = 0;
    private int end = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final String name;

    public Road(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;
        buffer = new Vehicle[capacity];
    }

    public void vehicleRecorder(boolean condition){

        if(condition){
            currentVehicles+=1;
            Statistics.incrementQueued();
        }else {
            currentVehicles-=1;
            Statistics.decrementQueued();
        }

    }

    public boolean addVehicle(Vehicle vehicle) {
        lock.lock();
        try {
            if ((end + 1) % capacity == start) {
                return false; // Road is full
            }
            buffer[end] = vehicle;
            end = (end + 1) % capacity;
            vehicleRecorder(true); // Increment after successfully adding the vehicle
            notEmpty.signal(); // Signal that there is now a car on the road
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Vehicle removeVehicle() {
        lock.lock();
        try {
            if (start == end) {
                return null; // No vehicles to remove
            }
            Vehicle vehicle = buffer[start];
            start = (start + 1) % capacity;
            vehicleRecorder(false); // Decrement after successfully removing the vehicle
            notFull.signal(); // Signal that there is now space on the road
            return vehicle;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        return (end + 1) % capacity == start;
    }

    public boolean isEmpty() {
        return start == end;
    }

    public String getName() {
        return name;
    }

    public int getQueueLength() {
        return capacity;
    }
    public int getVehiclesWaiting() {
        return currentVehicles;
    }
    public String reportToString() {
        lock.lock();
        try {
            return "In " + name + " - Number Vehicles are: " + currentVehicles;
        } finally {
            lock.unlock();
        }
    }
}
