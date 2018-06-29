package jadex.binary;

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
	
	/** Lookup table for constants to separate VarInts with different extensions.
	 */
	private static final long[] ADDITIVE_CONSTANTS_TABLE;
	static
	{
		ADDITIVE_CONSTANTS_TABLE = new long[] { 0L, 128L, 16512L, 2113664L, 270549120L, 34630287488L,
												4432676798592L, 567382630219904L, 72624976668147840L };
		
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
	 *  Returns the number of bytes used for this number past the first byte.
	 *  
	 *  @param firstbyte First byte of encoded VarInt.
	 *  @return Number of bytes belonging to the number after the first byte.
	 */
	public static final byte getExtensionSize(byte firstbyte)
	{
		return EXTENSION_COUNT_TABLE[firstbyte & 0xFF];
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
		ret += ADDITIVE_CONSTANTS_TABLE[extsize];
		return ret;
	}
	
	/**
	 *  Encodes a VarInt.
	 *  @param val The value being encoded.
	 *  @return Encoded VarInt.
	 */
	public static final byte[] encode(long val)
	{
		/*int size = 0;
		while (ADDITIVE_CONSTANTS_TABLE[size] <= val)
			++size;
		
		val -= ADDITIVE_CONSTANTS_TABLE[size - 1];
		
		byte[] ret = new byte[size];
		for (int i = size - 1; i >= 0; --i)
		{
			ret[i] = (byte)(val & 0xFF);
			val >>>= 8;
		}
		ret[0] |= 1 << (8 - size);
		
		return ret;*/
		
		int size = getEncodedSize(val);
		byte[] ret = new byte[size];
		encode(val, ret, 0, size);
		return ret;
	}
	
	/**
	 *  Determines the encoded size of a value.
	 *  
	 *  @param val The value.
	 *  @return The size of the encoded value.
	 */
	public static final int getEncodedSize(long val)
	{
		int ret = 0;
		while (ADDITIVE_CONSTANTS_TABLE[ret] <= val)
			++ret;
		return ret;
	}
	
	/**
	 *  Encodes a VarInt and saves it in a buffer at the given offset.
	 *  @param val The value being encoded.
	 *  @param buffer The target buffer.
	 *  @param offset The buffer offset.
	 *  @param size Size of the encoded VarInt.
	 */
	public static final void encode(long val, byte[] buffer, int offset, int size)
	{
		val -= ADDITIVE_CONSTANTS_TABLE[size - 1];
		
		//for (int i = size - 1; i >= 0; --i)
		for (int i = offset + size - 1; i >= offset; --i)
		{
			buffer[i] = (byte)(val & 0xFF);
			val >>>= 8;
		}
		
		buffer[offset] |= (1 << (8 - size));
	}
}
