package jadex.commons.transformation.binaryserializer;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Codec for encoding and decoding numbers (short, integer, float, etc.),
 *  boolean values and char values.
 *
 */
public class NumberCodec implements ITraverseProcessor, IDecoderHandler
{
	/** The types this processor can handle. */
	protected static final Set TYPES;
	static
	{
		TYPES = new HashSet();
		TYPES.add(Boolean.class);
		TYPES.add(boolean.class);
		TYPES.add(Integer.class);
		TYPES.add(int.class);
		TYPES.add(Double.class);
		TYPES.add(double.class);
		TYPES.add(Float.class);
		TYPES.add(float.class);
		TYPES.add(Long.class);
		TYPES.add(long.class);
		TYPES.add(Short.class);
		TYPES.add(short.class);
		TYPES.add(Byte.class);
		TYPES.add(byte.class);
		TYPES.add(Character.class);
		TYPES.add(char.class);
	}
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return TYPES.contains(clazz);
	}
	
	/**
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
	{
		Object ret = null;
		
		if (Byte.class.equals(clazz) || byte.class.equals(clazz))
			ret = context.read(1)[0];
		else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz))
			ret = context.readBool();
		else if (Character.class.equals(clazz) || char.class.equals(clazz))
			ret = new Character((char) context.readVarInt());
		else if (Short.class.equals(clazz) || short.class.equals(clazz))
		{
			byte[] in = context.read(2);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getShort();
		}
		else if (Integer.class.equals(clazz) || int.class.equals(clazz))
		{
			/*byte[] in = context.read(4);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getInt();*/
			ret = (int) context.readSignedVarInt();
		}
		else if (Long.class.equals(clazz) || long.class.equals(clazz))
		{
			byte[] in = context.read(8);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getLong();
		}
		else if (Float.class.equals(clazz) || float.class.equals(clazz))
		{
			byte[] in = context.read(4);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getFloat();
		}
		else // Double
		{
			byte[] in = context.read(8);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getDouble();
		}
		
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		EncodingContext ec = (EncodingContext) context;
		
		ec.runPreProcessors(object, clazz, processors, traverser, traversed, clone, context);
		clazz = object == null? null : object.getClass();
		
		byte[] out = null;
		ec.writeClass(clazz);
		if (object instanceof Byte)
			out = new byte[] { (Byte) object };
		else if (object instanceof Boolean)
			ec.writeBoolean((Boolean) object);
		else if (object instanceof Character)
			out = VarInt.encode(((Character) object).charValue());
		else if (object instanceof Short)
		{
			out = new byte[2];
			ByteBuffer buff = ByteBuffer.wrap(out);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putShort((Short) object);
		}
		else if (object instanceof Integer)
		{
			/*out = new byte[4];
			ByteBuffer buff = ByteBuffer.wrap(out);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putInt((Integer) object);*/
			ec.writeSignedVarInt((Integer) object);
		}
		else if (object instanceof Long)
		{
			out = new byte[8];
			ByteBuffer buff = ByteBuffer.wrap(out);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putLong((Long) object);
		}
		else if (object instanceof Float)
		{
			out = new byte[4];
			ByteBuffer buff = ByteBuffer.wrap(out);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putFloat((Float) object);
		}
		else
		{
			out = new byte[8];
			ByteBuffer buff = ByteBuffer.wrap(out);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putDouble((Double) object);
		}
		
		if (out != null)
			ec.write(out);
		
		return object;
	}
}
