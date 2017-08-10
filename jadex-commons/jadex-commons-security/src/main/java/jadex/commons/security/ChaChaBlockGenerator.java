package jadex.commons.security;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.spongycastle.crypto.engines.ChaChaEngine;
import org.spongycastle.util.Pack;

public class ChaChaBlockGenerator
{
	/** ChaCha rounds */
	protected int rounds;
	
	/** ChaCha state */
	protected int[] state = new int[16];
	
	public ChaChaBlockGenerator()
	{
		rounds = 20;
	}
	
	public ChaChaBlockGenerator(int rounds)
	{
		this.rounds = rounds;
	}
	
	/**
	 *  State initialization.
	 *  
	 *  @param rndstate The state, key followed by block count and nonce.
	 */
	public void initState(byte[] rndstate)
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
	}
	
	/**
	 *  Set state.
	 *  
	 *  @param state The state.
	 */
	public void setState(int[] state)
	{
		this.state = state;
	}
	
	/**
	 *  Get state.
	 *  
	 *  @return The state.
	 */
	public int[] getState()
	{
		return state;
	}
	
	/**
	 *  Generate next block (64 bytes).
	 *  
	 *  @param outputblock The block.
	 */
	public void nextBlock(byte[] outputblock)
	{
		assert outputblock.length == 64;
		
		++state[12];
		int[] output = new int[16];
		System.arraycopy(state, 0, output, 0, state.length);
		ChaChaEngine.chachaCore(20, output, output);
		Pack.intToLittleEndian(output, outputblock, 0);
	}
}
