package jadex.platform.service.dht;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.dht.IID;

/**
 * This class is an identifier. It is used to represent hash values that are too
 * big for primitive data types.
 */
public class ID implements IID {
	/**
	 * Debug field. Setting this to true enables 8-bit hashes (0-255) for better
	 * readability.
	 */
	public static boolean DEBUG = true;

	/**
	 * The byte array containing the id information.
	 */
	protected byte[] id;
	
	/**
	 * Cached message digest.
	 */
	private static MessageDigest messageDigest = null;
	
	private static MessageDigest getMessageDigest() {
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return messageDigest;
	}

	/**
	 * Constructor.
	 */
	public ID() {
	}

	/**
	 * Constructor.
	 */
	public ID(byte[] id) {
		this.id = id;
	}

	/**
	 * Returns the internal byte array.
	 */
	public byte[] getId() {
		return id;
	}

	/**
	 * Sets the internal byte array.
	 * 
	 * @param id
	 */
	public void setId(byte[] id) {
		this.id = id;
	}

	/**
	 * Returns the bytes.
	 */
	public byte[] getBytes() {
		return id;
	}

	/**
	 * Returns the length of this ID in bits.
	 * 
	 * @return
	 */
	public int getLength() {
		return id.length * 8;
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
	 * Subtracts 2^powerOfTwo from the value of this ID and returns the result
	 * as new ID. Does not modify this instance.
	 * 
	 * @param powerOfTwo
	 *            exponent to be used for subtraction.
	 * @return the new ID which is 2^powerOfTwo lower than the current ID modulo
	 *         the maximum ID.
	 */
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
		} while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ID(copy);
	}

	/**
	 * Adds 2^powerOfTwo to the value of this ID and returns the result as new
	 * ID. Does not modify this instance.
	 * 
	 * @param powerOfTwo
	 *            exponent to be used for subtraction.
	 * @return the new ID which is 2^powerOfTwo higher than the current ID
	 *         modulo the maximum ID.
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
	 * Check whether this ID is in the given interval.
	 * 
	 * @param start
	 *            Begin of interval.
	 * @param end
	 *            End of interval.
	 * @param leftOpen
	 *            If true, the start value is included in the interval.
	 * @param rightOpen
	 *            If true, the end value is included in the interval.
	 * @return true, if this Id is inside the specified interval, else false.
	 */
	public boolean isInInterval(IID start, IID end, boolean leftOpen,
			boolean rightOpen) {
		return isInInterval(start, end) || (leftOpen && start.equals(this))
				|| (rightOpen && end.equals(this));
	}

	/**
	 * Check whether this ID is in the given closed interval.
	 * 
	 * @param start
	 *            Begin of interval.
	 * @param end
	 *            End of interval.
	 * @return true, if this Id is inside the specified interval, else false.
	 */
	public boolean isInInterval(IID start, IID end) {
		if (start.compareTo(end) == 0) {
			// from == to -> only bounds excluded
			return (this.compareTo(start) != 0);
		}

		if (start.compareTo(end) < 0) {
			// start < end, check if between start + end
			return (this.compareTo(start) > 0 && this.compareTo(end) < 0);
		} else {
			// end < start,
			return (this.compareTo(end) < 0) || this.compareTo(start) > 0;
		}
	}

	// public static IID get(final IRingNode node) {
	// IID result;
	// if (node instanceof IService) {
	// result = get(((IService) node).getServiceIdentifier().getProviderId());
	// } else {
	// // must be local
	// if (node != null) {
	// result = node.getId().get(2000);
	// } else {
	// throw new Error("node is null, cannot hash");
	// }
	// }
	// return result;
	// }

	/**
	 * Hashes the given CID and returns an ID containing the hash.
	 * 
	 * @param cid
	 *            CID to hash.
	 * @return ID Resulting Id.
	 */
	public static IID get(final IComponentIdentifier cid) {
		return get(cid.getName());
	}

	/**
	 * Hashes the given string and returns an ID containing the hash.
	 * 
	 * @param str
	 *            String to hash.
	 * @return ID Resulting Id.
	 */
	public static IID get(final String str) {
		if (DEBUG) {
//			return new ID(new byte[] { Hashing.md5()
//					.hashString(str, Charset.defaultCharset())
//					.asBytes()[15] });
			return new ID(new byte[] {hash(str)[15]});
		} else {
			return new ID(hash(str));
		}
	}

	/**
	 * 
	 * Compare this ID with another. If type or length do not match, throw
	 * ClassCastException.
	 * 
	 * @return -1 if this ID is smaller, 0 if equal, 1 if greater than the given
	 *         ID.
	 * 
	 */
	public final int compareTo(IID otherKey) throws ClassCastException {

		if (this.getLength() != otherKey.getLength()) {
			throw new ClassCastException("IDs with same length can be "
					+ "compared! This ID is " + this.getLength()
					+ " bits long while the other ID is "
					+ otherKey.getLength() + " bits long.");
		}

		// compare value byte by byte
		// byte[] otherBytes = new byte[this.id.length];
		// System.arraycopy(otherKey.id, 0, otherBytes, 0, this.id.length);

		byte[] otherBytes = otherKey.getBytes();

		for (int i = 0; i < this.id.length; i++) {
			if ((byte) (this.id[i] - 128) < (byte) (otherBytes[i] - 128)) {
				return -1; // this ID is smaller
			} else if ((byte) (this.id[i] - 128) > (byte) (otherBytes[i] - 128)) {
				return 1; // this ID is greater
			}
		}
		return 0;

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
	public boolean equals(Object obj) {
		if (obj instanceof ID) {
			return ((ID) obj).compareTo(this) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(id);
	}

	@Override
	public String toString() {
		if (DEBUG) {
			int i = (int) id[0] + 128;
			return i < 10 ? " " + i : i < 100 ? " " + i : "" + i;
		} else {
			return toHexString(Integer.MAX_VALUE);
		}
	}
	
	private static byte[] hash(String str) {
		return getMessageDigest().digest(str.getBytes());
	}

	// @Override
	// public IID createNew()
	// {
	// return new ID(new byte[id.length]);
	// }

}
