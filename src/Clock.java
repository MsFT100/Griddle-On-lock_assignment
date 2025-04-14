import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Clock extends Thread {
    private int currentTime = 0; // Time in seconds
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition timePassed = lock.newCondition();
    private boolean running = true;
    private int convertedTime;
    @Override
    public void run() {

        try {
            while (running && currentTime < 3600) { // Simulate 1 hour of rush hour (3600 seconds)
                Thread.sleep(1000); // Simulate real-time tick (1 real-time second)
                lock.lock();
                try {
                    currentTime += 10; // Each tick represents 10 seconds of simulation time
                    convertedTime = currentTime/60;
                    timePassed.signalAll();
                } finally {
                    lock.unlock();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stopClock();

        } finally {
            stopClock();

        }
    }

    public void stopClock() {
        lock.lock();
        try {
            running = false;
            System.out.println("Simulated Time: " + convertedTime + " minutes");
            timePassed.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public boolean isRunning() {
        lock.lock();
        try {
            return running;
        } finally {
            lock.unlock();
        }
    }
}
