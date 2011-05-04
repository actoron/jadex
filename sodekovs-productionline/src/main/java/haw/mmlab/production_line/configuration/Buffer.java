/**
 * 
 */
package haw.mmlab.production_line.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A buffer to store {@link BufferElement}s in a ring.
 * 
 * @author thomas
 */
public class Buffer {

	/**
	 * Queue elements
	 */
	private BufferElement[] elements;

	/**
	 * Number of elements on queue
	 */
	private int n = 0;

	/**
	 * Index of first element on queue
	 */
	private int first = 0;

	/**
	 * Index of next available slot
	 */
	private int last = 0;

	/**
	 * Creates a ring buffer with the given capacity.
	 * 
	 * @param capacity
	 *            the given capacity
	 */
	public Buffer(int capacity) {
		this.elements = new BufferElement[capacity];
	}

	/**
	 * Enqueues the given {@link BufferElement}.
	 * 
	 * @param element
	 *            the given {@link BufferElement}
	 * @return <code>true</code> if the given {@link BufferElement} was
	 *         enqueued, else (if the buffer was full) <code>false</code>.
	 */
	public boolean enqueue(BufferElement element) {
		if (n == elements.length) {
			return false;
		} else {
			elements[last] = element;
			last = (last + 1) % elements.length; // wrap-around
			n++;
			return true;
		}
	}

	/**
	 * Removes and returns the least recently added {@link BufferElement}.
	 * 
	 * @return the least recently added {@link BufferElement} or
	 *         <code>null</code> if the buffer is empty.
	 */
	public BufferElement dequeue() {
		if (isEmpty()) {
			return null;
		} else {
			BufferElement element = elements[first];
			elements[first] = null; // to help with garbage collection
			n--;
			first = (first + 1) % elements.length; // wrap-around
			return element;
		}
	}

	/**
	 * Returns the least recently added {@link BufferElement} without removing
	 * it from the buffer.
	 * 
	 * @return the least recently added {@link BufferElement} or
	 *         <code>null</code> if the buffer is empty.
	 */
	public BufferElement peek() {
		if (isEmpty()) {
			return null;
		} else {
			BufferElement element = elements[first];
			return element;
		}
	}

	/**
	 * Returns the number of free slots in the buffer.
	 * 
	 * @return the number of free slots
	 */
	public int getFreeSlots() {
		return capacity() - size();
	}

	/**
	 * Returns the number of available slots in the buffer for the given
	 * {@link Role}.
	 * 
	 * @param role
	 *            - the given {@link Role}
	 * @param roles
	 *            - all the roles the agent currently has
	 * @return the number of available slots in the buffer for the given
	 *         {@link Role}
	 */
	public int getAvailableSlots(Role role, List<Role> roles) {
		Map<Role, Integer> occurrence = new HashMap<Role, Integer>();

		for (BufferElement element : this.elements) {
			if (element != null) {
				Role currRole = element.getRole();
				if (occurrence.containsKey(currRole)) {
					occurrence.put(currRole, occurrence.get(currRole) + 1);
				} else {
					occurrence.put(currRole, 1);
				}
			}
		}

		for (Role currRole : roles) {
			if (!occurrence.containsKey(currRole) && !currRole.equals(role)) {
				occurrence.put(currRole, 1);
			}
		}

		int usedSlots = 0;
		for (Role key : occurrence.keySet()) {
			usedSlots += occurrence.get(key);
		}

		return capacity() - usedSlots;
	}

	/**
	 * Returns the size, the number of elements in the buffer.
	 * 
	 * @return the size of the buffer
	 */
	public int size() {
		return n;
	}

	/**
	 * Checks whether the buffer is empty.
	 * 
	 * @return <code>true</code> if the buffer is empty, else false
	 */
	public boolean isEmpty() {
		return n == 0;
	}

	/**
	 * Checks whether the buffer is full.
	 * 
	 * @return <code>true</code> if the buffer is full, else false
	 */
	public boolean isFull() {
		return n == elements.length;
	}

	/**
	 * Returns the maximum capacity of the buffer.
	 * 
	 * @return the maximum capacity of the buffer.
	 */
	public int capacity() {
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + first;
		result = prime * result + last;
		result = prime * result + n;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buffer other = (Buffer) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		if (first != other.first)
			return false;
		if (last != other.last)
			return false;
		if (n != other.n)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Map<Role, Integer> occurrence = new HashMap<Role, Integer>();

		for (BufferElement element : this.elements) {
			if (element != null) {
				Role role = element.getRole();

				if (occurrence.containsKey(role)) {
					occurrence.put(role, occurrence.get(role) + 1);
				} else {
					occurrence.put(role, 1);
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Capacity: " + capacity());
		sb.append(" Size: " + size());
		for (Role role : occurrence.keySet()) {
			sb.append(" Role " + role.getCapabilityAsString() + " size: " + occurrence.get(role));
		}

		return sb.toString();
	}
}