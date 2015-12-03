package jadex.commons.transformation.binaryserializer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Codec for encoding and decoding numbers (short, integer, float, etc.),
 *  boolean values and char values.
 *
 */
public class LegacyNumberCodec extends AbstractCodec
{
	/** Default Instance */
	public static final LegacyNumberCodec INSTANCE = new LegacyNumberCodec();
	
	/** The types this processor can handle. */
	protected static final Set<Class<?>> TYPES;
	static
	{
		TYPES = new HashSet<Class<?>>();
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
	public boolean isApplicable(Class<?> clazz)
	{
		return TYPES.contains(clazz);
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
		Object ret = null;
		
		if (Byte.class.equals(clazz) || byte.class.equals(clazz))
			ret = context.readByte();
		else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz))
			ret = context.readBoolean();
		else if (Character.class.equals(clazz) || char.class.equals(clazz))
			ret = Character.valueOf((char) context.readVarInt());
		else if (Short.class.equals(clazz) || short.class.equals(clazz))
		{
			byte[] in = new byte[2];
			context.read(in);
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
			byte[] in = new byte[8];
			context.read(in);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getLong();
		}
		else if (Float.class.equals(clazz) || float.class.equals(clazz))
		{
			byte[] in = new byte[4];
			context.read(in);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getFloat();
		}
		else // Double
		{
			byte[] in = new byte[8];
			context.read(in);
			ByteBuffer buff = ByteBuffer.wrap(in);
			buff.order(ByteOrder.BIG_ENDIAN);
			ret = buff.getDouble();
		}
		
		return ret;
	}
	
	/**
	 *  References handling not needed.
	 */
	public void recordKnownDecodedObject(Object object, IDecodingContext context)
	{
		//if (!(object instanceof Boolean || object instanceof Byte))
			//super.recordKnownDecodedObject(object, context);
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
		if (object instanceof Byte)
			ec.write(new byte[] {(Byte) object});
		else if (object instanceof Boolean)
			ec.writeBoolean(((Boolean) object).booleanValue());
		else if (object instanceof Character)
			ec.writeVarInt(((Character) object).charValue());
		else if (object instanceof Short)
		{
			ByteBuffer buff = ((EncodingContext) ec).getByteBuffer(2);
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
			ByteBuffer buff = ((EncodingContext) ec).getByteBuffer(8);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putLong((Long) object);
		}
		else if (object instanceof Float)
		{
			ByteBuffer buff = ((EncodingContext) ec).getByteBuffer(4);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putFloat((Float) object);
		}
		else
		{
			ByteBuffer buff = ((EncodingContext) ec).getByteBuffer(8);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.putDouble((Double) object);
		}
		
		return object;
	}
	
	/**
	 *  Prevent references for primitive-wrapped types.
	 *  
	 *  @param object The current object.
	 *  @param clazz The class.
	 *  @param ec The encoding context.
	 *  @return True, if a reference has been encoded, false otherwise.
	 */
	/*protected boolean encodeReference(Object object, Class clazz, IEncodingContext ec)
	{
		return false;
		//if (object instanceof Boolean || object instanceof Byte)
		//	return false;
		
		//return super.encodeReference(object, clazz, ec);
	}*/
	
	public boolean canReference(Object object, Class<?> clazz, IEncodingContext ec)
	{
		return false;
	}
}
