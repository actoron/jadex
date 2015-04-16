package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.hash.Hashing;

public class ID implements IID
{
	public static boolean	DEBUG	= true;
	private byte[] id;
	
	public ID() {
		
	}
	
	public ID(byte[] id) {
		this.id = id;
	}
	
	public byte[] getId()
	{
		return id;
	}

	public void setId(byte[] id)
	{
		this.id = id;
	}
	
	public ID subtractPowerOfTwo(int powerOfTwo) {
		if (powerOfTwo < 0 || powerOfTwo >= (this.id.length * 8)) {
			throw new IllegalArgumentException(
					"The power of two is out of range! It must be in the interval "
							+ "[0, length-1]");
		}
		
		byte[] copy = new byte[this.id.length];
		System.arraycopy(this.id, 0, copy, 0, this.id.length);
		
		// determine index of byte and the value to be added
		int indexOfByte = this.id.length - 1 - (powerOfTwo / 8);
		byte[] toSub = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToSub = toSub[powerOfTwo % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] -= valueToSub;

			// reset value to 1 for possible overflow situation
			valueToSub = 1;
		}
		// check for overflow - occurs if old value had a leading one, i.e. it
		// was negative, and new value has a leading zero, i.e. it is zero or
		// positive; indexOfByte >= 0 prevents running out of the array to the
		// left in case of going over the maximum of the ID space
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ID(copy);
	}
	
	/**
	 * Returns a string of the decimal representation of the first
	 * <code>n</code> bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * 
	 * @return Hex string of ID
	 */
	public final String toDecimalString(int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
		int displayBytes = Math.max(1, Math.min(numberOfBytes, this.id.length));

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			String block = Integer.toString(this.id[i] & 0xff);

			result.append(block + "");
		}
		return result.toString();
	}

	
	/**
	 * Calculates the ID which is 2^powerOfTwo bits greater than the current ID
	 * modulo the maximum ID and returns it.
	 * 
	 * @param powerOfTwo
	 *            Power of two which is added to the current ID. Must be a value
	 *            of the interval [0, length-1], including both extremes.
	 * @return ID which is 2^powerOfTwo bits greater than the current ID modulo
	 *         the maximum ID.
	 */
	public ID addPowerOfTwo(int powerOfTwo) {
		if (powerOfTwo < 0 || powerOfTwo >= (this.id.length * 8)) {
			throw new IllegalArgumentException(
					"The power of two is out of range! It must be in the interval "
							+ "[0, length-1]");
		}
		
		byte[] copy = new byte[this.id.length];
		System.arraycopy(this.id, 0, copy, 0, this.id.length);
		
		// determine index of byte and the value to be added
		int indexOfByte = this.id.length - 1 - (powerOfTwo / 8);
		byte[] toAdd = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToAdd = toAdd[powerOfTwo % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] += valueToAdd;

			// reset value to 1 for possible overflow situation
			valueToAdd = 1;
		}
		// check for overflow - occurs if old value had a leading one, i.e. it
		// was negative, and new value has a leading zero, i.e. it is zero or
		// positive; indexOfByte >= 0 prevents running out of the array to the
		// left in case of going over the maximum of the ID space
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ID(copy);
	}

	/**
	 * Interval.
	 */
	public boolean isInInterval(IID start, IID end, boolean leftOpen, boolean rightOpen) {
		return isInInterval(start, end) 
			|| (leftOpen && start.equals(this))
			|| (rightOpen && end.equals(this));
	}
	
	/**
	 * Closed interval (start, end)
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean isInInterval(IID start, IID end) {
		if (start.compareTo(end) == 0) {
			// from == to -> only bounds excluded
			return (this.compareTo(start) != 0);
		}
		
		if (start.compareTo(end) < 0 ) {
			// start < end, check if between start + end
			return (this.compareTo(start) > 0 && this.compareTo(end) < 0);
		} else {
			// end < start,
			return (this.compareTo(end) < 0) || this.compareTo(start) > 0;
		}
		
		
	}
	
//	public static boolean inInterval(long id, long intervalStart, long intervalEnd) {
//		if (intervalStart < intervalEnd) {
//			return (intervalStart <= id && intervalEnd >= id);
//		} else {
//			return !(intervalEnd < id && intervalStart > id);
//		}
//	}
	
	public static IID get(final IRingNode node) {
		IID result;
		if (node instanceof IService) {
//			HashCode l = Hashing.md5().hashString(((IService) node).getServiceIdentifier().getProviderId().getName(), Charset.defaultCharset());
//			result = new ID(l.asBytes());
			result = get(((IService) node).getServiceIdentifier().getProviderId());
		} else {
			// must be local
			if (node != null) {
				result = node.getId().get();
			} else {
				throw new Error("node is null, cannot hash");
			}
		}
		return result;
	}
	
	public static ID get(final IComponentIdentifier cid) {
		if (DEBUG) {
			return new ID(new byte[]{Hashing.md5().hashString(cid.getName(), Charset.defaultCharset()).asBytes()[15]});
		} else {
			return new ID(Hashing.md5().hashString(cid.getName(), Charset.defaultCharset()).asBytes());
		}
	}
	
	public static ID get(final String str) {
		if (DEBUG) {
			return new ID(new byte[]{Hashing.md5().hashString(str, Charset.defaultCharset()).asBytes()[15]});
		} else {
			return new ID(Hashing.md5().hashString(str, Charset.defaultCharset()).asBytes());
		}
	}
	
	/**
	 * 
	 * Compare this ID with another.
	 * If type or length do not match, throw ClassCastException.
	 * 
	 * @return -1 if this ID is smaller, 0 if equal, 1 if greater than the given ID. 
	 * 
	 */
	public final int compareTo(IID otherKey) throws ClassCastException {

		if (this.getLength() != otherKey.getLength()) {
			throw new ClassCastException(
					"IDs with same length can be "
							+ "compared! This ID is " + this.getLength()
							+ " bits long while the other ID is "
							+ otherKey.getLength() + " bits long.");
		}

		// compare value byte by byte
//		byte[] otherBytes = new byte[this.id.length];
//		System.arraycopy(otherKey.id, 0, otherBytes, 0, this.id.length);
		
		byte[] otherBytes = otherKey.getBytes();

		for (int i = 0; i < this.id.length; i++) {
			if ((byte) (this.id[i] - 128) < (byte) (otherBytes[i] - 128)) {
				return -1; // this ID is smaller
			} else if ((byte) (this.id[i] - 128) > (byte) (otherBytes[i] -128)) {
				return 1; // this ID is greater
			}
		}
		return 0;

	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ID) {
			return ((ID)obj).compareTo(this) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(id);
	}

	/**
	 * in bits
	 * @return
	 */
	public int getLength()
	{
		return id.length * 8;
	}
	
	
	@Override
	public byte[] getBytes()
	{
		return id;
	}

	/**
	 * Returns a string of the hexadecimal representation of the first
	 * <code>n</code> bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * 
	 * @return Hex string of ID
	 */
	public final String toHexString(int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
		int displayBytes = Math.max(1, Math.min(numberOfBytes, this.id.length));

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			String block = Integer.toHexString(this.id[i] & 0xff).toUpperCase();

			// add leading zero to block, if necessary
			if (block.length() < 2) {
				block = "0" + block;
			}

			result.append(block + " ");
		}
		return result.toString();
	}
	
	@Override
	public String toString()
	{
		if (DEBUG) {
			int i = (int) id[0]+128;
			return i < 10 ? " " + i : i < 100 ? " " + i : "" + i;
		} else {
			return toHexString(Integer.MAX_VALUE);
		}
	}

	@Override
	public IID createNew()
	{
		return new ID(new byte[id.length]);
	}
	
	
}
