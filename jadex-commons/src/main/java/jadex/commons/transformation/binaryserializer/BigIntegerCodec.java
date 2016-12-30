package jadex.commons.transformation.binaryserializer;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Codec for big integers.
 */
public class BigIntegerCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return BigInteger.class.equals(clazz);
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
		int len = (int)context.readVarInt();
		byte[] abuf = new byte[len];
		context.read(abuf);
		ByteBuffer buf = ByteBuffer.wrap(abuf);
		buf.order(ByteOrder.BIG_ENDIAN);
		BigInteger ret = new BigInteger(buf.array());
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		byte[] ba = ((BigInteger)object).toByteArray();
		ec.writeVarInt(ba.length);
		ec.write(ba);
		return object;
	}
}
