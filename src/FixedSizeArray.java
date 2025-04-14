import java.util.concurrent.locks.ReentrantLock;

public class FixedSizeArray<T> {
    private final Object[] array;
    private int size = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public FixedSizeArray(int capacity) {
        array = new Object[capacity];
    }

    public void add(T element) {
        lock.lock();
        try {
            if (size < array.length) {
                array[size++] = element;
            } else {
                System.out.println("Array is full, cannot add element.");
            }
        } finally {
            lock.unlock();
        }
    }

    public T remove() {
        lock.lock();
        try {
            if (size > 0) {
                @SuppressWarnings("unchecked")
                T element = (T) array[0];
                System.arraycopy(array, 1, array, 0, --size);
                array[size] = null;
                return element;
            } else {
                System.out.println("Array is empty, cannot remove element.");
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public T get(int index) {
        lock.lock();
        try {
            if (index < size && index >= 0) {
                return (T) array[index];
            } else {
                System.out.println("Index out of bounds.");
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }
}
