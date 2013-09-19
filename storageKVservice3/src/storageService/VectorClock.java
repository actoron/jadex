/**
 * 
 */
package storageService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author frederik
 * 
 */
public class VectorClock implements Version, Comparable<VectorClock> {

	private Map<String, Long> map = new TreeMap<String, Long>();

	/**
	 * Create an empty VectorClock
	 */
	public VectorClock() {
	}

	/**
	 * Create a VectorClock with one entry
	 */
	public VectorClock(String node, long time) {
		map.put(node, time);
	}

	/**
	 * Increment the timestamp for a node or initialize time with 1 if node is
	 * not in VectorClock yet
	 * 
	 * @param node
	 */
	public void increment(String node) {
		Long time = map.get(node);
		if (time == null) {
			map.put(node, new Long(1));
		} else {
			map.put(node, new Long(time.longValue() + 1));
		}
	}

	/**
	 * 
	 */
	public void setTime(String nodeId, long time) {
		map.put(nodeId, time);
	}

	/**
	 * Compare this VectorClock to another version. Show if this VectorClock is
	 * from before / after / concurrently / equal to / incomparable to
	 * (different type) other Version
	 * 
	 * @return Occurred
	 */
	@Override
	public Occurred compare(Version v) {
		if (!(v instanceof VectorClock)) {
			return Occurred.INCOMPARABLE;
		}
		Map<String, Long> v2 = ((VectorClock) v).getMap();
		boolean v1Larger = false;
		boolean v2Larger = false;
		String key;
		Set<String> v1Keys = map.keySet();
		Set<String> v2KeySet = v2.keySet();
		Set<String> v2Keys = new HashSet<String>();
		Iterator<String> it = v2KeySet.iterator();
		while (it.hasNext()) {
			String s = it.next();
			v2Keys.add(s);
		}
		Iterator<String> iter = v1Keys.iterator();
		while (iter.hasNext()) {
			key = iter.next();
			Long v1Value = map.get(key);
			Long v2Value = v2.get(key);
			if (v2Value == null) {
				v1Larger = true;
			} else if (v1Value.longValue() > v2Value.longValue()) {
				v1Larger = true;
				v2Keys.remove(key);
			} else if (v1Value.longValue() < v2Value.longValue()) {
				v2Larger = true;
				v2Keys.remove(key);
			} else {
				v2Keys.remove(key);
			}
		}
		if (v2Keys.size() != 0) {
			v2Larger = true;
		}

		// return Occurred depending on v1Larger and v2Larger
		if (v1Larger && v2Larger) {
			return Occurred.CONCURRENTLY;
		}
		if (v1Larger && !v2Larger) {
			return Occurred.AFTER;
		}
		if (!v1Larger && v2Larger) {
			return Occurred.BEFORE;
		} else {
			return Occurred.EQUAL;
		}
	}

	/**
	 * Return VectorClock that has the maximum value for all entries in any of
	 * the VectorClocks given in parameter list.
	 * 
	 * @param versions
	 * @return
	 */
	public VectorClock getMax(List<Version> versions) {
		VectorClock ret = new VectorClock();
		Map<String, Long> retMap = ret.getMap();
		Iterator<Version> it = versions.iterator();
		while (it.hasNext()) {
			Version version = it.next();
			if (!(version instanceof VectorClock)) {
				throw new RuntimeException("Wrong type of version!");
			}
			VectorClock vc = (VectorClock) version;
			Set<Entry<String, Long>> entries = vc.getMap().entrySet();
			Iterator<Entry<String, Long>> it2 = entries.iterator();
			while (it2.hasNext()) {
				Entry<String, Long> entry = it2.next();
				String nodeId = entry.getKey();
				Long time = entry.getValue();
				if (!retMap.containsKey(nodeId)) {
					ret.setTime(nodeId, time);
				} else if (retMap.get(nodeId) < time) {
					ret.setTime(nodeId, time);
				}
			}

		}
		return ret;
	}

	@Override
	public String toString() {
		Set<String> set = map.keySet();
		Iterator<String> iter = set.iterator();
		String s = "[";
		while (iter.hasNext()) {
			String key = iter.next();
			Long value = map.get(key);
			if (iter.hasNext()) {
				s += key + ": " + value + ", ";
			} else {
				s += key + ": " + value;
			}
		}
		s += "]";
		return s;
	}

	public Map<String, Long> getMap() {
		return map;
	}

	public void setMap(Map<String, Long> map) {
		this.map = map;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorClock v = (VectorClock) obj;
		Occurred occ = this.compare(v);
		if (occ.equals(Occurred.EQUAL)) {
			return true;
		}
		return false;
	}

	/**
	 * Compares this object with the specified object for order. Returns -1, 0,
	 * or +1 as this object is less than, equal to, or greater than the
	 * specified object. Use only if a linear order must be enforced. Is
	 * consistent with the compare-method in returning -1, 0, or +1 if
	 * VectorClocks are BEFORE, EQUAL, or AFTER.
	 */
	@Override
	public int compareTo(VectorClock other) {
		Occurred occurred = this.compare(other);
		switch (occurred) {
		case BEFORE:
			return -1;
		case AFTER:
			return +1;
		case EQUAL:
			return 0;
		case CONCURRENTLY: {
			// highest sum of versions is greater
			Collection<Long> values = map.values();
			long valuesSum = 0;
			Iterator<Long> it = values.iterator();
			while (it.hasNext()) {
				valuesSum += it.next();
			}
			Collection<Long> otherValues = other.getMap().values();
			long otherValuesSum = 0;
			Iterator<Long> it2 = otherValues.iterator();
			while (it2.hasNext()) {
				otherValuesSum += it2.next();
			}
			if (valuesSum > otherValuesSum) {
				return +1;
			} else if (valuesSum < otherValuesSum) {
				return -1;
			}
			// highest nodeID in different entries
			// TODO
			return 0;

		}
		default:
			throw new RuntimeException("Compared wrong objects");
		}
	}

	@Override
	public VectorClock clone() {
		VectorClock vc = new VectorClock();
		HashMap<String, Long> newMap = new HashMap<String, Long>(map);
		vc.setMap(newMap);
		return vc;
	}

	/**
	 * return true, iff this is concurrent or newer compared to all given
	 * versions
	 */
	@Override
	public boolean concurrentOrNewer(List<Version> versions) {
		Iterator<Version> it = versions.iterator();
		while (it.hasNext()) {
			Version other = it.next();
			Occurred occurred = this.compare(other);
			if (!(occurred.equals(Occurred.CONCURRENTLY) || occurred
					.equals(Occurred.AFTER))) {
				return false;
			}
		}
		return true;
	}

}
