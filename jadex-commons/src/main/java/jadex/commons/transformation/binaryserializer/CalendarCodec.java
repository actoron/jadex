package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding Date objects.
 *
 */
public class CalendarCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Calendar.class, clazz);
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
		Calendar ret = null;
		
		byte[] abuf = new byte[8];
		context.read(abuf);
		ByteBuffer buf = ByteBuffer.wrap(abuf);
		buf.order(ByteOrder.BIG_ENDIAN);
		
		try
		{
			ret = (Calendar)clazz.newInstance();
			ret.setTime(new Date(buf.getLong()));
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
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
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		long time = ((Calendar)object).getTime().getTime();
		byte[] abuf = new byte[8];
		ByteBuffer buf = ByteBuffer.wrap(abuf);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(time);
		ec.write(abuf);
		
		return object;
	}
}
