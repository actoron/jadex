package jadex.commons.security.random;

import java.security.SecureRandom;

import jadex.commons.SUtil;
import jadex.commons.security.ChaChaBlockGenerator;
import jadex.commons.security.SSecurity;

public class ChaCha20Random extends SecureRandom
{
	/** ID */
	private static final long serialVersionUID = 0xBD611DFD65C5ABB2L;
	
	/** Seeding source, use SSecurity. */
	protected SecureRandom seedrandom;
	
	protected ChaChaBlockGenerator blockgen = new ChaChaBlockGenerator();
	
	/** The output block. */
	protected byte[] outputblock = new byte[64];
	
	/** Pointer to unused output. */
	protected int outptr = 64;
	
	public ChaCha20Random()
	{
		seedrandom = SSecurity.getSeedRandom();
		reseed();
	}
	
	public ChaCha20Random(SecureRandom seedrandom)
	{
		this.seedrandom = seedrandom;
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
	
	protected void nextBlock()
	{
		if (blockgen.getState()[12] < 0)
			reseed();
		
		blockgen.nextBlock(outputblock);
		
		outptr = 0;
	}
	
	public void reseed()
	{
		byte[] seedstate = new byte[48];
		seedrandom.nextBytes(seedstate);
		blockgen.initState(seedstate);
	}
	
//	public static void main(String[] args)
//	{
//		byte[] out = new byte[64];
//		ChaCha20Random r = new ChaCha20Random();
//		r.nextBytes(out);
//		
//		char[] hexArray = "0123456789ABCDEF".toCharArray();
//		char[] hexChars = new char[out.length * 2];
//	    for ( int j = 0; j < out.length; j++ ) {
//	        int v = out[j] & 0xFF;
//	        hexChars[j * 2] = hexArray[v >>> 4];
//	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//	    }
//		
//	    System.out.println(new String(hexChars));
//	}
}
