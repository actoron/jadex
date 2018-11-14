package jadex.bridge.service.types.registryv2;

import jadex.commons.SUtil;
import net.cinnom.nanocuckoo.ConcurrentSwapSafety;
import net.cinnom.nanocuckoo.NanoCuckooFilter;

/**
 *  Filter class based on two cuckoo filter.
 *  Splits the capacity into two filters, if the capacity is
 *  exceeded, the older filter is dropped and a new filter is created.
 *  
 *
 */
public class SlidingCuckooFilter
{
	/** The currently expiring filter. */
	protected NanoCuckooFilter expiringfilter;
	
	/** The current filter. */
	protected NanoCuckooFilter currentfilter;
	
	/** The bucket size / entries per bucker. */
	protected int bucketsize;
	
	/** Size of fingerprints in bits. */
	protected byte fingerprintsize;
	
	/**
	 *  Creates the cuckoo filter with the given capacity and
	 *  a false positive probability of ~1:500000.
	 *   
	 *  @param capacity Capacity.
	 */
	public SlidingCuckooFilter()
	{
		this(256L, 2, (byte) 24);
	}
	
	/**
	 *  Creates the cuckoo filter with the given capacity and
	 *  false positive probability.
	 *   
	 *  @param capacity Filter capacity of a single filter.
	 *  @param bucketsize Size of buckets.
	 *  @param fingerprintsize Size of fingerprints.
	 */
	public SlidingCuckooFilter(long capacity, int bucketsize, byte fingerprintsize)
	{
		this.bucketsize = bucketsize;
		this.fingerprintsize = fingerprintsize;
		currentfilter = createFilter(capacity);
	}
	
	/**
	 * Insert a byte array into the filter.
	 *
	 * @param string The data being inserted.
	 */
	public void insert(String string)
	{
		if (!currentfilter.insert(string))
		{
			expiringfilter = currentfilter;
			currentfilter = createFilter(expiringfilter.getCapacity());
			currentfilter.insert(string);
		}
	}
	
	/**
	 *  Check if a given byte array is contained in the filter.
	 *
	 * @param data The data being checked.
	 * @return True if value is probably contained.
	 */
	public boolean contains(String string)
	{
		return currentfilter.contains(string) || (expiringfilter!=null ? expiringfilter.contains(string) : false);
	}
	
	/**
	 *  Creates a new filter.
	 *  
	 *  @param capacity Filter capacity.
	 *  @param bucketsize Size of buckets
	 *  @param fingerprintsize Size of fingerprints.
	 *  @return The filter.
	 */
	protected NanoCuckooFilter createFilter(long capacity)
	{
		NanoCuckooFilter.Builder builder = new NanoCuckooFilter.Builder(capacity).withConcurrency(1).withConcurrentSwapSafety(ConcurrentSwapSafety.FAST);
		builder = builder.withFingerprintBits(fingerprintsize).withEntriesPerBucket(bucketsize);
		builder = builder.withRandomSeed(SUtil.FAST_RANDOM.nextInt());
		return builder.build();
	}
}
