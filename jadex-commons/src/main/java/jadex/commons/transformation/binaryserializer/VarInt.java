package jadex.commons.transformation.binaryserializer;

/**
 *  Class providing methods for encoding integers (longs) as
 *  byte array in a variable-length format.
 *
 */
public class VarInt
{
	/** Lookup table for quantifying the extension length */
	private static final byte[] EXTENSION_COUNT_TABLE;
	
	/** Lookup table for decoding the remainder value beyond the extension
	 *  length in the first byte.
	 */
	private static final byte[] REMAINDER_TABLE;
	
	static
	{
		EXTENSION_COUNT_TABLE = new byte[256];
		REMAINDER_TABLE = new byte[256];
		byte num = 9;
		for (int i = 0; i < 256; ++i)
		{
			if ((i & (i - 1)) == 0)
				--num;
			EXTENSION_COUNT_TABLE[i] = num;
			REMAINDER_TABLE[i] =  (byte) (i & ~(1 << 7 - num));
		}
		num = -2;
	}
	
	/**
	 *  Returns the number of bytes used for this number past the first byte.
	 *  
	 *  @param varint Encoded VarInt.
	 *  @param offset The offset.
	 *  @return Number of bytes belonging to the number after the first byte.
	 */
	public static final byte getExtensionSize(byte[] varint, int offset)
	{
		return EXTENSION_COUNT_TABLE[varint[offset] & 0xFF];
	}
	
	/**
	 *  Decodes a VarInt.
	 *  
	 *  @param varint The encoded VarInt.
	 *  @return The decoded VarInt.
	 */
	public static final long decode(byte[] varint)
	{
		return decode(varint, 0);
	}
	
	/**
	 *  Decodes a VarInt.
	 *  
	 *  @param varint The encoded VarInt.
	 *  @offset The offset.
	 *  @return The decoded VarInt.
	 */
	public static final long decode(byte[] varint, int offset)
	{
		byte ext = getExtensionSize(varint, offset);
		return decodeWithKnownSize(varint, offset, ext);
	}
	
	/**
	 *  Decodes a VarInt when the extension size is known.
	 *  
	 *  @param varint The encoded VarInt.
	 *  @param offset The offset.
	 *  @param The number of bytes after the first byte.
	 *  @return The decoded VarInt.
	 */
	public static final long decodeWithKnownSize(byte[] varint, int offset, byte extsize)
	{
		long ret = 0;
		ret |= REMAINDER_TABLE[varint[offset] & 0xFF] & 0xFF;
		for (int i = 0; i < extsize; ++i)
		{
			ret <<= 8;
			ret |= (varint[offset + i + 1] & 0xFF);
		}
		return ret;
	}
	
	/**
	 *  Encodes a VarInt.
	 *  @param val The value being encoded.
	 *  @return Encoded VarInt.
	 */
	public static final byte[] encode(long val)
	{
		int size = 0;
		if ((val & 0x7FL) == val)
			size = 1;
		else if ((val & 0x3FFFL) == val)
			size = 2;
		else if ((val & 0x1FFFFFL) == val)
			size = 3;
		else if ((val & 0x0FFFFFFFL) == val)
			size = 4;
		else if ((val & 0x07FFFFFFFFL) == val)
			size = 5;
		else if ((val & 0x03FFFFFFFFFFL) == val)
			size = 6;
		else if ((val & 0x01FFFFFFFFFFFFL) == val)
			size = 7;
		else if ((val & 0x00FFFFFFFFFFFFFFL) == val)
			size = 8;
		else
			throw new RuntimeException("Argument too large " + val);
		
		byte[] ret = new byte[size];
		for (int i = size - 1; i >= 0; --i)
		{
			ret[i] = (byte)(val & 0xFF);
			val >>>= 8;
		}
		ret[0] |= 1 << (8 - size);
		
		return ret;
	}
	
	/**
	 * Self-Test.
	 * 
	 * @param args Arguments.
	 */
	public static final void main(String[] args)
	{
		long val = 0;
		int el = 1;
		test(val, el);
		
		val = 127;
		el = 1;
		test(val, el);
		
		val = 128;
		el = 2;
		test(val, el);
		
		val = 255;
		el = 2;
		test(val, el);
		
		val = 256;
		el = 2;
		test(val, el);
		
		val = 16383;
		el = 2;
		test(val, el);
		
		val = 16384;
		el = 3;
		test(val, el);
		
		val = 2097151;
		el = 3;
		test(val, el);
		
		val = 2097152;
		el = 4;
		test(val, el);
		
		val = 268435455;
		el = 4;
		test(val, el);
		
		val = 268435456;
		el = 5;
		test(val, el);
		
		val = 34359738367L;
		el = 5;
		test(val, el);
		
		val = 34359738368L;
		el = 6;
		test(val, el);
		
		val = 4398046511103L;
		el = 6;
		test(val, el);
		
		val = 4398046511104L;
		el = 7;
		test(val, el);
		
		val = 562949953421311L;
		el = 7;
		test(val, el);
		
		val = 562949953421312L;
		el = 8;
		test(val, el);
	}
	
	/**
	 *  Helper method for testing.
	 *  
	 *  @param val Value.
	 *  @param explngth Expected encoded length.
	 */
	private static final void test(long val, int explngth)
	{
		System.out.print("Encoding " + val + ", encoded length is ");
		byte[] enc = encode(val);
		System.out.println(enc.length + " bytes, should be " +
						 explngth + " bytes. ");
		long dec = decode(enc);
		System.out.println("Decodes to " + dec + ".");
		if ((enc.length == explngth) && (dec == val))
			System.out.println("PASS");
		else
			System.out.println("FAIL");
		System.out.println();
	}
}
