package jadex.commons.security.random;

import java.security.SecureRandom;

import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.util.Pack;

import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

public class ChaCha20Random extends SecureRandom
{
	/** ID */
	private static final long serialVersionUID = 0xBD611DFD65C5ABB2L;
	
	/** The start value of the block count. */
	private static final long BLOCK_COUNT_START = Long.MIN_VALUE + 1024;
	
	/** Seeding source, use SSecurity. */
	protected SecureRandom seedrandom;
	
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
		seedrandom = SSecurity.getSeedRandom();
		reseed();
	}
	
	/**
	 *  Initializes the PRNG.
	 */
	public ChaCha20Random(SecureRandom seedrandom)
	{
		this.seedrandom = seedrandom;
		reseed();
	}
	
	/**
	 *  Initializes the PRNG in deterministic mode.
	 */
	public ChaCha20Random(byte[] seed)
	{
		Blake2bDigest dig = new Blake2bDigest(512);
		dig.update(seed, 0, seed.length);
		byte[] hash = new byte[64];
		dig.doFinal(hash, 0);
		byte[] trunc = new byte[24];
		System.arraycopy(hash, 40, trunc, 0, trunc.length);
		SSecurity.xor(hash, trunc);
		final byte[] seedbytes = new byte[40];
		System.arraycopy(hash, 0, seedbytes, 0, seedbytes.length);
		 
		this.seedrandom = new SecureRandom()
		{
			private boolean used = false;
			
			public void nextBytes(byte[] bytes)
			{
				if (used)
				{
					throw new SecurityException("Deterministic mode ChaCha20 Random out of seed.");
				}
				else
				{
					assert bytes.length == 40;
					System.arraycopy(seedbytes, 0, bytes, 0, bytes.length);
					used = true;
				}
			}
		};
		reseed();
	}
	
	public long nextLong()
	{
		byte[] bytes = new byte[8];
		nextBytes(bytes);
		
		return SUtil.bytesToLong(bytes);
	}
	
	public int nextInt()
	{
		byte[] bytes = new byte[4];
		nextBytes(bytes);
		
		return SUtil.bytesToInt(bytes);
	}
	
	
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
		seedrandom.nextBytes(seedstate);
		Pack.littleEndianToInt(seedstate, 0, basestate);
		blockcount = BLOCK_COUNT_START;
	}
	
	/**
	 *  State initialization.
	 *  
	 *  @param rndstate The state, key followed by block count and nonce, block count is zeroed before use.
	 */
	/*public void initState(byte[] rndstate)
	{
		int i = 0;
		state[i]   = 0x61707865;
		state[++i] = 0x3320646e;
		state[++i] = 0x79622d32;
		state[++i] = 0x6b206574;
		
		IntBuffer buf = (ByteBuffer.wrap(rndstate)).asIntBuffer();
		while (buf.hasRemaining())
			state[++i] = buf.get();
		
		state[12] = 0;
		
//		RFC 7539 Test Vector
//		state[++i] = 0x03020100;
//		state[++i] = 0x07060504;
//		state[++i] = 0x0b0a0908;
//		state[++i] = 0x0f0e0d0c;
//		state[++i] = 0x13121110;
//		state[++i] = 0x17161514;
//		state[++i] = 0x1b1a1918;
//		state[++i] = 0x1f1e1d1c;
//		state[++i] = 0x00000001;
//		state[++i] = 0x09000000;
//		state[++i] = 0x4a000000;
//		state[++i] = 0x00000000;
		
		// Vector 2
//		state[13] = 0x00000000;
	}*/
	
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
	
	public static void main(String[] args)
	{
		byte[] out = new byte[64];
		ChaCha20Random r = new ChaCha20Random();
		
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 30000000; ++i)
			r.nextBytes(out);
		System.out.println(System.currentTimeMillis() - ts);
		
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[out.length * 2];
	    for ( int j = 0; j < out.length; j++ ) {
	        int v = out[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
//		
	    System.out.println(new String(hexChars));
	}
}
