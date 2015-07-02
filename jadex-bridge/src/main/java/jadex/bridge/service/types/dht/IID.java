package jadex.bridge.service.types.dht;


/**
 * Identifier interface.
 */
public interface IID extends Comparable<IID>
{

	/**
	 * Check whether this ID is in the given closed interval.
	 * 
	 * @param start Begin of interval.
	 * @param end End of interval.
	 * @return true, if this Id is inside the specified interval, else false.
	 */
	public boolean isInInterval(IID start, IID end);

	/**
	 * Check whether this ID is in the given interval.
	 * 
	 * @param start Begin of interval.
	 * @param end End of interval.
	 * @param leftOpen If true, the start value is included in the interval.
	 * @param rightOpen If true, the end value is included in the interval.
	 * @return true, if this Id is inside the specified interval, else false.
	 */
	public boolean isInInterval(IID start, IID end, boolean leftOpen, boolean rightOpen);

	/**
	 * Check whether this ID is in the given closed interval.
	 * 
	 * @param start Begin of interval.
	 * @param end End of interval.
	 * @return true, if this Id is inside the specified interval, else false.
	 */
	public int getLength();


	/**
	 * Returns the bytes.
	 */
	public byte[] getBytes();

	/**
	 * Adds 2^powerOfTwo to the value of this ID and returns the result as new
	 * ID. Does not modify this instance.
	 * 
	 * @param powerOfTwo exponent to be used for subtraction.
	 * @return the new ID which is 2^powerOfTwo higher than the current ID
	 *         modulo the maximum ID.
	 */
	public IID addPowerOfTwo(int i);

}
