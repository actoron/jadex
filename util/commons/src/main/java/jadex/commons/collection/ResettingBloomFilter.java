package jadex.commons.collection;

/**
 *  Bloom filter that resets itself once it is saturated,
 *  avoiding false positives for the price of false negatives.
 *
 */
public class ResettingBloomFilter extends BloomFilter
{
	/** Added items. */
	protected int added = 0;
	
	/**
	 *  Create a new ResettingBloomFilter with default
	 *  p = 0.000001 and n = 500.
	 */
	public ResettingBloomFilter() 
	{
		this(0.000001, 500);
	}
	
	/**
	 *  Create a new ResettingBloomFilter.
	 *  @param p The acceptable false positive rate.
	 *  @param n The expected number of entries before reset.
	 */
	public ResettingBloomFilter(double p, int n) 
	{
		super(p, n);
	}
	
	/**
	 *  Add a value to the filter.
	 *  @param value The value.
	 */
	public boolean add(byte[] value)
	{
		if (added >= n)
		{
			bs.clear();
			added = 0;
		}
		
		
		boolean ret = super.add(value);
		if (ret)
			++added;
		return ret;
	}
	
	public static void main(String[] args)
	{
		ResettingBloomFilter bf = new ResettingBloomFilter();
		System.out.println(bf.bs.size() / 8.0);
	}
}
