package jadex.commons.security.random;

import java.security.SecureRandom;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.util.Pack;

import jadex.commons.security.IEntropySource;
import jadex.commons.security.SSecurity;

public class ChaCha20Random extends SecureRandom
{
	/** ID */
	private static final long serialVersionUID = 0xBD611DFD65C5ABB2L;
	
	/** The start value of the block count. */
	private static final long BLOCK_COUNT_START = Long.MIN_VALUE + 16;
	
	/** Entropy source for seeding, use SSecurity. */
	protected IEntropySource entropysource;
	
//	/** ChaCha state */
//	protected int[] state = new int[16];
	
	/** ChaCha base state */
	protected int[] basestate = new int[10];
	
	/** Current block count. */
	protected long blockcount = BLOCK_COUNT_START;
	
	/** The output block. */
	protected byte[] outputblock = new byte[64];
	
	/** Pointer to unused output. */
	protected int outptr = 64;
	
	/**
	 *  Initializes the PRNG.
	 */
	public ChaCha20Random()
	{
		this(null, null);
	}
	
	/**
	 *  Initializes the PRNG.
	 */
	public ChaCha20Random(IEntropySource seedrandom)
	{
		this(seedrandom, null);
	}
	
	/**
	 *  Initializes the PRNG.
	 */
	public ChaCha20Random(byte[] initialseed)
	{
		this(null, initialseed);
	}
	
	/**
	 *  Initializes the PRNG.
	 */
	public ChaCha20Random(IEntropySource entropysource, byte[] initialseed)
	{
		if (initialseed != null && initialseed.length != 40)
			throw new IllegalArgumentException("Initial seed length must be 40 bytes.");
		
		this.entropysource = entropysource == null ? SSecurity.getEntropySource() : entropysource;
		
		if (initialseed == null)
			reseed();
		else
			reseed(initialseed);
	}
	
	/** 
	 *  Gets the next long.
	 */
	public long nextLong()
	{
		byte[] bytes = new byte[8];
		nextBytes(bytes);
		
		return Pack.littleEndianToLong(bytes, 0);
	}
	
	/**
	 *  Gets the next int.
	 */
	public int nextInt()
	{
		byte[] bytes = new byte[4];
		nextBytes(bytes);
		
		return Pack.littleEndianToInt(bytes, 0);
	}
	
	
	/**
	 *  Gets the next bytes.
	 */
	public void nextBytes(byte[] bytes)
	{
		int filled = 0;
		while (filled < bytes.length)
		{
			if (outptr >= outputblock.length)
				nextBlock();
			int len = Math.min(bytes.length - filled, outputblock.length - outptr);
			System.arraycopy(outputblock, outptr, bytes, filled, len);
			filled += len;
			outptr += len;
		}
	}
	
	/**
	 *  Generates the next ChaCha block.
	 */
	protected void nextBlock()
	{
//		if (state[12] < 0)
//			reseed();
		
		if (blockcount < BLOCK_COUNT_START)
			reseed();
		
		nextBlock(outputblock);
		
		outptr = 0;
	}
	
	/**
	 *  Reseeds the PRNG.
	 */
	public void reseed()
	{
		byte[] seedstate = new byte[40];
		entropysource.getEntropy(seedstate);
		reseed(seedstate);
	}
	
	/**
	 *  Reseeds the PRNG.
	 */
	public void reseed(byte[] providedseed)
	{
		Pack.littleEndianToInt(providedseed, 0, basestate);
		blockcount = BLOCK_COUNT_START;
	}
	
	/**
	 *  Generate next block (64 bytes).
	 *  
	 *  @param outputblock The block.
	 */
	public void nextBlock(byte[] outputblock)
	{
		assert outputblock.length == 64;
		
		int[] state = new int[16];
		state[0]   = 0x61707865;
		state[1] = 0x3320646e;
		state[2] = 0x79622d32;
		state[3] = 0x6b206574;
		System.arraycopy(basestate, 0, state, 4, 8);
		state[12] = (int) blockcount;
		state[13] = (int) (blockcount >>> 32);
		state[14] = basestate[8];
		state[15] = basestate[9];
		++blockcount;
		
		ChaChaEngine.chachaCore(20, state, state);
		Pack.intToLittleEndian(state, outputblock, 0);
	}
	
	/**
	 *  Generates a seed value from OS source.
	 */
	public byte[] generateSeed(int numbytes)
	{
		byte[] ret = new byte[numbytes];
		SSecurity.getEntropySource().getEntropy(ret);
		return ret;
	}
}
