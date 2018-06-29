package jadex.binary;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding UUID objects.
 */
public class UUIDCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(UUID.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		byte[] abuf = new byte[16];
		context.read(abuf);
		ByteBuffer buf = ByteBuffer.wrap(abuf);
		buf.order(ByteOrder.BIG_ENDIAN);
		long msb = buf.getLong();
		long lsb = buf.getLong();
		return new UUID(msb, lsb);
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		UUID uuid = (UUID)object;
		byte[] abuf = new byte[16];
		ByteBuffer buf = ByteBuffer.wrap(abuf);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(uuid.getMostSignificantBits());
//		buf = ec.getByteBuffer(8);
//		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(uuid.getLeastSignificantBits());
		ec.write(abuf);
		
		return object;
	}
}