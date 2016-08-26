package jadex.commons.collection;

import java.util.BitSet;

/**
 *  A bloom filter is a probabilistic data structure for
 *  checking if a value is contained in a set.
 *  
 *  It has a false positive rate, i.e. it can tell that
 *  a value is contained in the filter although it is not.
 *  
 *  This implementation does not support remove() because
 *  clearing a bit that is used by more than one element
 *  might lead to false negatives. (If remove is required
 *  a couting filter needs to be used).
 */
public class BloomFilter
{
	/** The bit set. */
	protected BitSet bs;

	/** The number of hash (functions). */
	protected int k;
	
	/** The number of bits in the bit set */
	protected int m;
	
	/** The number of expected entries. */
	protected int n;
	
	/**
	 *  Create a new BloomFilter with default
	 *  p = 0.01 and n = 1000.
	 */
	public BloomFilter() 
	{
		this(0.01, 1000);
	}
	
	/**
	 *  Create a new BloomFilter.
	 *  @param p The acceptable false positive rate.
	 *  @param n The expected number of entries.
	 */
	public BloomFilter(double p, int n) 
	{
		this(computeOptimalM(n, p), computeOptimalK(n, computeOptimalM(n, p)), n);
	}
	
	/**
	 *  Create a new BloomFilter.
	 *  @param m The number of bits in the filter.
	 *  @param k The number of hashes used.
	 *  @param n The expected number of values in the filter.
	 */
	public BloomFilter(int m, int k, int n) 
	{
		this.m = m;
        this.k = k;
        this.n = n;
        this.bs = new BitSet(m);
    }

	/**
	 *  Add a value to the filter.
	 *  @param value The value.
	 */
	public boolean add(byte[] value)
	{
		boolean ret = false;
		int[] hashes = hashK(value, m, k);
		for(int i=0; i<k; ++i)
		{
//			System.out.println("hash is: "+hashes[i]);
			if(!bs.get(hashes[i]))
			{
				bs.set(hashes[i]);
				ret = true;
			}
		}
		return ret;
	}
	
	/**
	 *  Clear the filter content.
	 */
	public void clear()
	{
		bs.clear();
	}

	/**
	 *  Test if a value is contained in the filter.
	 *  @param value The value.
	 *  @return True, if value might be contained.
	 */
	public boolean mightContain(byte[] value) 
	{
		boolean ret = true;
		
		int[] hashes = hashK(value, m, k);
		for(int i=0; i<k && ret; ++i)
		{
//			System.out.println("hash is: "+hashes[i]);
			ret = bs.get(hashes[i]); 
		}
		
        return ret;
    }
	
    /**
     *  Compute optimal size of bloom filter in bits by expected 
     *  number of elements and acceptable false positive rate.
     *  @param n Expected number of elements.
     *  @param p Acceptable false positive rate.
     * 	@return The optimal size of the bloom filter in bits.
     */
    public static int computeOptimalM(long n, double p) 
    {
        return (int)Math.ceil(-1*(n*Math.log(p))/Math.pow(Math.log(2), 2));
    }

    /**
     *  Compute optimal number of hash functions using expected number
     *  of elements and size of filter in bits.
     *  @param n Expected number of elements inserted in the bloom filter
     *  @param m The size of the bloom filter in bits.
     *  @return The optimal number of hash functions.
     */
    public static int computeOptimalK(long n, long m) 
    {
    	return (int)Math.ceil((Math.log(2)*m)/n);
    }

    /**
     *  Compute acceptable amount of elements with given
     *  number of hashes and size of filter in bits.  
     *  @param k Number of hashes.
     *  @param m The size of the bloom filter in bits.
     *  @return Acceptable number elements that can be inserted.
     */
    public static int computeAcceptableN(long k, long m) 
    {
        return (int)Math.ceil((Math.log(2)*m)/k);
    }

    /**
     *  Compute best-case (uniform hash function) false positive probability.
     *  @param k Number of hashes.
     *  @param m The size of the bloom filter in bits.
     *  @param n Number of elements inserted in the filter.
     *  @return The expected false positive probability.
     */
    public static double computeP(long k, long m, double insertedElements) 
    {
    	return Math.pow((1-Math.exp(-k*insertedElements/(double) m)),k);
    }

    /**
     *  Compute k hashes based on two hashes using
     *  (hash1+i*hash2)%m;
     *  @param value The byte array.
     *  @param m The maximum number of values.
     *  @param k The number of hash values to compute.
     *  @return K hashes.
     */
    public static int[] hashK(byte[] value, int m, int k) 
    {
        int[] ret = new int[k];
        long hash1 = Math.abs(murmur3(value, 0))%m;
        long hash2 = Math.abs(murmur3(value, (int)hash1))%m;
        for(int i = 0; i < k; i++) 
        {
            ret[i] = (int)((hash1+i*hash2)%m);
        }
        return ret;
    }
    
    /**
     *  Compute the murmur hash for a byte array.
     */
    public static int murmur3(byte[] data, int seed) 
    {
    	final int c1 = 0xcc9e2d51;
    	final int c2 = 0x1b873593;
    	int h1 = seed;
    	int len = data.length;
    	int roundedEnd = len & 0xfffffffc;  

    	for(int i=0; i<roundedEnd; i+=4) 
    	{
    		int k1 = (data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | (data[i+3] << 24);
    		k1 *= c1;
    		k1 *= c2;

    		h1 ^= k1;
    		h1 = (h1 << 13) | (h1 >>> 19);  
    		h1 = h1*5+0xe6546b64;
    	}

    	int k1 = 0;

    	switch(len & 0x03) 
    	{
    		case 3:
    			k1 = (data[roundedEnd + 2] & 0xff) << 16;
    		case 2:
    			k1 |= (data[roundedEnd + 1] & 0xff) << 8;
    		case 1:
    			k1 |= (data[roundedEnd] & 0xff);
    			k1 *= c1;
    			k1 = (k1 << 15) | (k1 >>> 17); 
    			k1 *= c2;
    			h1 ^= k1;
    	}

    	h1 ^= len;
    	h1 ^= h1 >>> 16;
    	h1 *= 0x85ebca6b;
    	h1 ^= h1 >>> 13;
    	h1 *= 0xc2b2ae35;
    	h1 ^= h1 >>> 16;

    	return h1;
    }
    
    /**
     *  Get the string representation.
     */
	public String toString()
	{
		return "BloomFilter [bs=" + bs + ", k=" + k + ", m=" + m + "]";
	}

	/**
     *  Main for testing.
     */
	public static void main(String[] args)
	{
		byte[] value = new byte[]{9};
//		BloomFilter bf = new BloomFilter(10, 2);
		
		BloomFilter bf = new BloomFilter(0.05, 1000);
		System.out.println(bf);
		
		System.out.println("Query for 9: " + bf.mightContain(value));
		System.out.println("Adding 9");
		bf.add(value);
		System.out.println("Query for 9: " + bf.mightContain(value));
	}
}