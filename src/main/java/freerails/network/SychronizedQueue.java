package freerails.network;

import freerails.world.FreerailsSerializable;

import java.util.LinkedList;

/**
 * Intended to let objects be safely passed between threads. 666 perhaps an
 * arrayList is better (-> profile it)
 *
 */
public class SychronizedQueue {
    private final LinkedList<FreerailsSerializable> queue = new LinkedList<>();

    /**
     *
     * @param f
     */
    public synchronized void write(FreerailsSerializable f) {
        queue.add(f);
    }

    /**
     *
     * @return
     */
    public synchronized FreerailsSerializable[] read() {
        int length = queue.size();
        FreerailsSerializable[] read = new FreerailsSerializable[length];

        for (int i = 0; i < length; i++) {
            read[i] = queue.removeFirst();
        }

        return read;
    }

    /**
     *
     * @return
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     *
     * @return
     */
    public synchronized FreerailsSerializable getFirst() {
        return queue.removeFirst();
    }
}