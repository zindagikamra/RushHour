/*************************************************************************
 *  Generic updateable min priority queue implementation with a binary heap.
 *  Can be used with a comparator instead of the natural order.
 *
 *  % java MinPQ < tinyPQ.txt
 *  E A E (6 left on pq)
 *
 *  We use a one-based array to simplify parent and child calculations.
 *
 *************************************************************************/

import java.util.Comparator;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 *  The <tt>UpdateableMinPQ</tt> class represents a priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with methods for peeking at the minimum key,
 *  testing if the priority queue is empty, and updating keys.
 *  <p>
 *  This implementation uses a binary heap.
 *  The <em>insert</em> and <em>delete-the-minimum</em> operations take
 *  logarithmic amortized time.
 *  The <em>min</em>, <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction takes time proportional to the specified capacity or the number of
 *  items used to initialize the data structure.
 *  <p>
 *  Based on <tt>MinPQ</tt> from <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class UpdateableMinPQ<Key> {
	private static PQHook hook;					// For testing purposes
	private Key[] pq;                    		// store items at indices 1 to N
    private HashMap<Key, Integer> keyToIndex;	// Maps a Key to its index in pq
    private int N;                       		// number of items on priority queue
    private Comparator<Key> comparator;  		// optional comparator


    public static void registerHook(PQHook hookP)
    {
    	if (hook != null)
    	{
    		throw new UnsupportedOperationException("registerHook has been called outside of the tests.  Student code should not call registerHook");
    	}
    	hook = hookP;
    }
    
    public static void unregisterHook()
    {
    	hook = null;
    }
    
    /**
     * Initializes an empty priority queue with the given initial capacity.
     * @param initCapacity the initial capacity of the priority queue
     */
    public UpdateableMinPQ(int initCapacity) {
        pq = (Key[]) new Object[initCapacity + 1];
        keyToIndex = new HashMap<Key, Integer>();
        N = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public UpdateableMinPQ() {
        this(1);
    }

    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     * @param initCapacity the initial capacity of the priority queue
     * @param comparator the order to use when comparing keys
     */
    public UpdateableMinPQ(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        keyToIndex = new HashMap<Key, Integer>(initCapacity + 1);
        N = 0;
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     * @param comparator the order to use when comparing keys
     */
    public UpdateableMinPQ(Comparator<Key> comparator) { this(1, comparator); }

    /**
     * Is the priority queue empty?
     * @return true if the priority queue is empty; false otherwise
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Returns the number of keys on the priority queue.
     * @return the number of keys on the priority queue
     */
    public int size() {
        return N;
    }

    /**
     * Returns a smallest key on the priority queue.
     * @return a smallest key on the priority queue
     * @throws java.util.NoSuchElementException if priority queue is empty
     */
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > N;
        Key[] temp = (Key[]) new Object[capacity];
        for (int i = 1; i <= N; i++) 
        {
        	temp[i] = pq[i];
        }
        pq = temp;
        
        // keyToIndex remains unchanged since it is inherently resizeable, and
        // none of the keys' indices were updated during the resize
    }

    /**
     * Adds a new key to the priority queue.
     * @param x the key to add to the priority queue
     */
    public void insert(Key x) {
    	if (hook != null)
    	{
    		hook.onEnqueue(x);
    	}
    	
        // double size of array if necessary
    	if (N == pq.length - 1) 
    	{
    		resize(2 * pq.length);
    	}

        // add x, and percolate it up to maintain heap invariant
        N++;
        pq[N] = x;
        keyToIndex.put(x, N);
        swim(N);
        assert isMinHeap();
    }

    /**
     * Removes and returns a smallest key on the priority queue.
     * @return a smallest key on the priority queue
     * @throws java.util.NoSuchElementException if the priority queue is empty
     */
    public Key delMin() {
    	if (isEmpty())
    	{
    		throw new NoSuchElementException("Priority queue underflow");
    	}
    	exch(1, N);
        Key min = pq[N--];
    	if (hook != null)
    	{
    		hook.onDequeue(min);
    	}
    	sink(1);
        
        // avoid loitering and help with garbage collection
        keyToIndex.remove(pq[N+1]);
        pq[N+1] = null;
        
        if ((N > 0) && (N == (pq.length - 1) / 4)) 
        {
        	resize(pq.length  / 2);
        }
        assert isMinHeap();
        return min;
    }
    
    /**
     * Finds oldKey in the queue and overwrites it with newKey.
     * All elements of the queue are shifted appropriately so that
     * the order remains correct 
     * @param oldKey Existing key in the queue to update
     * @param newKey New key to add to the queue in place of oldKey
     */
    public void updateKey(Key oldKey, Key newKey)
    {
    	if (hook != null)
    	{
    		hook.onUpdate(oldKey, newKey);
    	}
    	Integer iBoxed = keyToIndex.get(oldKey);
    	if (iBoxed == null)
    	{
    		throw new NoSuchElementException("updateKey called with nonexistent key");
    	}
    	
    	int i = iBoxed;
    	pq[i] = newKey;
    	keyToIndex.remove(oldKey);
    	keyToIndex.put(newKey, i);
    	swim(i);
    	int iAfterSwim = keyToIndex.get(newKey); 
    	sink(iAfterSwim);
    	
        assert isMinHeap();
    }


   /***********************************************************************
    * Helper functions to restore the heap invariant.
    **********************************************************************/

    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
    	while (2*k <= N) {
    		int j = 2*k;
    		if (j < N && greater(j, j+1))
    		{
    			j++;
    		}
    		if (!greater(k, j)) 
    		{
    			break;
    		}
    		exch(k, j);
    		k = j;
    	}
    }

   /***********************************************************************
    * Helper functions for compares and swaps.
    **********************************************************************/
    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        keyToIndex.put(pq[j], i);
        pq[j] = swap;
        keyToIndex.put(swap, j);
    }

    private boolean isMinHeap() {
        // is pq[1..N] a min heap?
        if (!isMinHeap(1))
        {
        	return false;
        }

        // Is keyToIndex consistent with pq?
        
        for (int i=1; i <= N; i++)
        {
        	Key key = pq[i];
        	if (key != null && keyToIndex.get(key) != i)
        	{
        		return false;
        	}
        }
        
        for (HashMap.Entry entry : keyToIndex.entrySet())
        {
        	Key key = pq[(int) entry.getValue()];
        	if (!key.equals(entry.getKey()))
        	{
        		return false;
        	}
        }
        
        return true;
    }

    // is subtree of pq[1..N] rooted at k a min heap?
    private boolean isMinHeap(int k) {
        if (k > N) return true;
        int left = 2*k, right = 2*k + 1;
        if (left  <= N && greater(k, left))  return false;
        if (right <= N && greater(k, right)) return false;
        return isMinHeap(left) && isMinHeap(right);
    }
}