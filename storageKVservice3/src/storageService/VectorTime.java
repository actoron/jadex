package storageService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VectorTime implements Version, Comparable<VectorTime> {

	private VectorClock vectorClock;
	private long time; // Real time in ms

	public VectorTime() {
		vectorClock = new VectorClock();
		time = 0;
	}
	
	/**
	 * Set vector to nodeId=1 and time to System.currentTimeMillis()
	 * @param nodeId
	 */
	public VectorTime(String nodeId) {
		vectorClock = new VectorClock(nodeId, 1);
		time = System.currentTimeMillis();
	}
	
	/**
	 * Increment the timestamp for a node or initialize time with 1 if node is
	 * not in VectorClock yet. Update current time.
	 * 
	 * @param node
	 */
	public void increment(String node) {
		vectorClock.increment(node);
		time = System.currentTimeMillis();
	}
	

	/**
	 * compare this VectorTime to another version. Show if this VectorTime is
	 * from before / after / concurrently / equal to / incomparable to
	 * (different type) other Version
	 * 
	 * @return Occurred
	 */
	@Override
	public Occurred compare(Version v) {
		if (!(v instanceof VectorTime)) {
			return Occurred.INCOMPARABLE;
		}
		// Compare VectorClocks. If they are concurrent, decide by real time.
		// This still allows concurrent versions, but only in rare cases.
		VectorTime v2 = (VectorTime) v;
		return vectorClock.compare(v2.getVectorClock());
	}
	
	/**
	 * Compares this object with the specified object for order. Returns -1, 0,
	 * or +1 as this object is less than, equal to, or greater than the
	 * specified object. Use only if a linear order must be enforced. Is
	 * consistent with the compare-method in returning -1, 0, or +1 if
	 * VectorClocks are BEFORE, EQUAL, or AFTER.
	 */
	@Override
	public int compareTo(VectorTime other) {
		Occurred occurred = this.compare(other);
		switch (occurred) {
		case BEFORE:
			return -1;
		case AFTER:
			return +1;
		case EQUAL:
			return 0;
		case CONCURRENTLY: {
			// If VectorClocks are concurrent, decide by real time.
			if (this.time > other.getTime()) {
				return 1;
			} else if (this.time < other.getTime()) {
				return -1;
			} else {
				// If real times are the same, use VectorClock compareTo
				return this.vectorClock.compareTo(other.getVectorClock());
			}
		}
		default:
			throw new RuntimeException("Compared wrong objects");
		}
	}
	
	/**
	 * Construct VectorTime that has the maximum value for all entries in any of
	 * the VectorClocks and maximum time.
	 * 
	 * @param versions
	 * @return
	 */
	public VectorTime getMax(List<Version> versions) {
		VectorTime retVectorTime = new VectorTime();
		if (versions.size() == 0) {
			return retVectorTime;
		}
		List<Version> clocks = new ArrayList<Version>();
		Iterator<Version> it = versions.iterator();
		while (it.hasNext()) {
			Version version = it.next();
			if (!(version instanceof VectorTime)) {
				throw new RuntimeException("Wrong type of version!");
			}
			VectorTime vt = (VectorTime) version;
			clocks.add(vt.getVectorClock());
			if (retVectorTime.getTime() < vt.getTime()) {
				retVectorTime.setTime(vt.getTime());
			}
		}
		VectorClock retVectorClock = new VectorClock();
		retVectorClock = retVectorClock.getMax(clocks);
		retVectorTime.setVectorClock(retVectorClock);
		return retVectorTime;
	}
	
	/**
	 * return true, iff this is concurrent or newer compared to all given versions
	 */
	@Override
	public boolean concurrentOrNewer(List<Version> versions) {
		Iterator<Version> it = versions.iterator();
		while(it.hasNext()) {
			Version other = it.next();
			Occurred occurred = this.compare(other);
			if(!(occurred.equals(Occurred.CONCURRENTLY) || occurred.equals(Occurred.AFTER))) {
				return false;
			}
		}
		return true;
	}

	public VectorClock getVectorClock() {
		return vectorClock;
	}

	public void setVectorClock(VectorClock vectorClock) {
		this.vectorClock = vectorClock;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		String s = vectorClock.toString();
		s = s + " " + time;
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
			VectorTime v = (VectorTime) obj;
			if (this.getTime() != v.getTime()) {
				return false;
			}
			if (vectorClock.equals(v.getVectorClock())) {
				return true;
			}
		return false;
	}

	@Override
	public VectorTime clone() {
		VectorTime vt = new VectorTime();
		vt.setVectorClock(vectorClock.clone());
		vt.setTime(time);
		return vt;
	}
	
}
