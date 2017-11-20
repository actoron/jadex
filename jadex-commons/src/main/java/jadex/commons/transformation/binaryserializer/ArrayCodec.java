package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding arrays.
 *
 */
public class ArrayCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return clazz != null && clazz.isArray();
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
		int length = (int) context.readVarInt();
		
		Object ret = Array.newInstance(clazz.getComponentType(), length);
		
		return ret;
	}
	
	/**
	 *  Decodes and adds sub-objects during decoding.
	 *  
	 *  @param object The instantiated object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The finished object.
	 */
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		Class compclass = clazz.getComponentType();
		int length = getArrayLength(object, compclass);
		
		if (compclass.isPrimitive())
		{
			if (byte.class.equals(compclass))
			{
				byte[] array = (byte[]) object;
				context.read(array);
			}
			else if (int.class.equals(compclass))
			{
				int[] array = (int[]) object;
				for (int i = 0; i < array.length; ++i)
					array[i] = (int) context.readSignedVarInt();
			}
			else if (boolean.class.equals(compclass))
			{
				boolean[] array = (boolean[]) object;
				byte[] packed = new byte[(int) Math.ceil(array.length / 8.0)];
				context.read(packed);
				for (int i = 0; i < array.length; ++i)
				{
					byte mask = (byte) ((1 << (7 - (i % 8))) & 0xFF);
					array[i] = ((packed[i >> 3] & mask) & 0xFF) > 0;
				}
			}
			else if (float.class.equals(compclass))
			{
				float[] array = (float[]) object;
				byte[] abuf = new byte[array.length << 2];
				context.read(abuf);
				ByteBuffer buf = ByteBuffer.wrap(abuf);
				buf.order(ByteOrder.BIG_ENDIAN);
				for (int i = 0; i < array.length; ++i)
					array[i] = buf.getFloat();
			}
			else if (double.class.equals(compclass))
			{
				double[] array = (double[]) object;
				byte[] abuf = new byte[array.length << 3];
				context.read(abuf);
				ByteBuffer buf = ByteBuffer.wrap(abuf);
				buf.order(ByteOrder.BIG_ENDIAN);
				for (int i = 0; i < array.length; ++i)
					array[i] = buf.getDouble();
			}
			else if (long.class.equals(compclass))
			{
				long[] array = (long[]) object;
				byte[] abuf = new byte[array.length << 3];
				context.read(abuf);
				ByteBuffer buf = ByteBuffer.wrap(abuf);
				buf.order(ByteOrder.BIG_ENDIAN);
				for (int i = 0; i < array.length; ++i)
					array[i] = buf.getLong();
			}
			else if (short.class.equals(compclass))
			{
				short[] array = (short[]) object;
				byte[] abuf = new byte[array.length << 1];
				context.read(abuf);
				ByteBuffer buf = ByteBuffer.wrap(abuf);
				buf.order(ByteOrder.BIG_ENDIAN);
				for (int i = 0; i < array.length; ++i)
					array[i] = buf.getShort();
			}
			else
			{
				char[] array = (char[]) object;
				for (int i = 0; i < array.length; ++i)
					array[i] = (char) context.readVarInt();
			}
		}
		else
		{
			Object[] array = (Object[]) object;
			for (int i = 0; i < length; ++i)
			{
				boolean ignoreclass = context.readBoolean();
				
				Object sub = null;
				if (ignoreclass)
					sub = SBinarySerializer.decodeRawObject(compclass, context);
				else
					sub = SBinarySerializer.decodeObject(context);
				
				array[i] = sub;
			}
		}
		
		return object;
	}
	
//	/**
//	 *  Test if the processor is applicable.
//	 *  @param object The object.
//	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
//	 *    e.g. by cloning the object using the class loaded from the target class loader.
//	 *  @return True, if is applicable. 
//	 */
//	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
//	{
//		Class<?> clazz = SReflect.getClass(type);
//		return clazz.isArray();
//	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Class compclazz = clazz.getComponentType();
		
		if (compclazz.isPrimitive())
			processRawMode(object, compclazz, ec, processors, traverser, ec);
		else
		{
			Object[] array = (Object[]) object;
			ec.writeVarInt(array.length);
			
			for(int i=0; i<array.length; i++) 
			{
				Object val = array[i];
				if (val == null)
				{
					ec.writeBoolean(false);
					ec.writeClassname(SBinarySerializer.NULL_MARKER);
					//BinarySerializer.NULL_HANDLER.process(val, compclazz, null, null, null, false, ec);
				}
				else
				{
					// TODO: Known object check here, is there a smarter approach?
					boolean ignoreclass = val.getClass().equals(compclazz) && ec.getObjectId(val) == null;
					ec.writeBoolean(ignoreclass);
					
					if (ignoreclass)
						ec.setIgnoreNextClassWrite(true);
					
					traverser.doTraverse(val, val.getClass(), preprocessors, processors, mode, targetcl, ec);
				}
			}
		}
		
		return object;
	}
	
	protected void processRawMode(Object obj, Class compclass, IEncodingContext ec, List<ITraverseProcessor> processors, 
			Traverser traverser, IEncodingContext context)
	{
		if (byte.class.equals(compclass))
		{
			byte[] array = (byte[]) obj;
			ec.writeVarInt(array.length);
			ec.write(array);
		}
		else if (int.class.equals(compclass))
		{
			int[] array = (int[]) obj;
			ec.writeVarInt(array.length);
			for (int i = 0; i < array.length; ++i)
			{
				ec.writeSignedVarInt(array[i]);
			}
		}
		else if (boolean.class.equals(compclass))
		{
			boolean[] array = (boolean[]) obj;
			ec.writeVarInt(array.length);
			byte[] packed = new byte[(int) Math.ceil(array.length / 8.0)];
			for (int i = 0; i < array.length; ++i)
			{
				if (array[i])
				{
					byte mask = (byte) ((1 << (7 - (i % 8))) & 0xFF);
					packed[i >>> 3] |= mask;
				}
			}
			ec.write(packed);
		}
		else if (float.class.equals(compclass))
		{
			float[] array = (float[]) obj;
			ec.writeVarInt(array.length);
			byte[] abuf = new byte[array.length << 2];
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			for (int i = 0; i < array.length; ++i)
			{
				buf.putFloat(array[i]);
			}
			ec.write(abuf);
			
		}
		else if (double.class.equals(compclass))
		{
			double[] array = (double[]) obj;
			ec.writeVarInt(array.length);
			byte[] abuf = new byte[array.length << 3];
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			for (int i = 0; i < array.length; ++i)
			{
				buf.putDouble(array[i]);
			}
			ec.write(abuf);
		}
		else if (long.class.equals(compclass))
		{
			long[] array = (long[]) obj;
			ec.writeVarInt(array.length);
			byte[] abuf = new byte[array.length << 3];
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			for (int i = 0; i < array.length; ++i)
			{
				buf.putLong(array[i]);
			}
			ec.write(abuf);
		}
		else if (short.class.equals(compclass))
		{
			short[] array = (short[]) obj;
			ec.writeVarInt(array.length);
			byte[] abuf = new byte[array.length << 1];
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			for (int i = 0; i < array.length; ++i)
			{
				buf.putShort(array[i]);
			}
			ec.write(abuf);
		}
		else if (char.class.equals(compclass))
		{
			char[] array = (char[]) obj;
			ec.writeVarInt(array.length);
			for (int i = 0; i < array.length; ++i)
			{
				ec.writeVarInt(array[i]);
			}
		}
	}
	
	protected int getArrayLength(Object obj, Class compclass)
	{

		if (!compclass.isPrimitive())
		{
			Object[] array = (Object[]) obj;
			return array.length;
		}
		else if (byte.class.equals(compclass))
		{
			byte[] array = (byte[]) obj;
			return array.length;
		}
		else if (int.class.equals(compclass))
		{
			int[] array = (int[]) obj;
			return array.length;
		}
		else if (boolean.class.equals(compclass))
		{
			boolean[] array = (boolean[]) obj;
			return array.length;
		}
		else if (float.class.equals(compclass))
		{
			float[] array = (float[]) obj;
			return array.length;
		}
		else if (double.class.equals(compclass))
		{
			double[] array = (double[]) obj;
			return array.length;
		}
		else if (long.class.equals(compclass))
		{
			long[] array = (long[]) obj;
			return array.length;
		}
		else if (short.class.equals(compclass))
		{
			short[] array = (short[]) obj;
			return array.length;
		}
		else
		{
			char[] array = (char[]) obj;
			return array.length;
		}
	}
}
