package jadex.binary;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding Date and SimpleDateFormat objects.
 *
 */
public class DateCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return Date.class.equals(clazz) || SimpleDateFormat.class.equals(clazz);
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
		if (Date.class.equals(clazz))
		{
			byte[] abuf = new byte[8];
			context.read(abuf);
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			ret = new Date(buf.getLong());
		}
		else if (SimpleDateFormat.class.equals(clazz))
		{
			String pattern = context.readString();
			ret = new SimpleDateFormat(pattern);
		}
		return ret;
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
//		return isApplicable(clazz);
//	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		if (Date.class.equals(clazz))
		{
			long time = ((Date)object).getTime();
			byte[] abuf = new byte[8];
			ByteBuffer buf = ByteBuffer.wrap(abuf);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putLong(time);
			ec.write(abuf);
		}
		else if (SimpleDateFormat.class.equals(clazz))
		{
			String pattern = ((SimpleDateFormat) object).toPattern();
			ec.writeString(pattern);
		}
		
		return object;
	}
}
