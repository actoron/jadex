package jadex.binary;

import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

public class LocalDateTimeCodec extends AbstractCodec
{
	/** LocalDate super interface */
	public static final Class<?> CHRONOLOCALDATECLASS;
	
	/** Local time class */
	public static final Class<?> LOCALTIMECLASS;
	
	/** LocalDateTime super interface */
	public static final Class<?> CHRONOLOCALDATETIMECLASS;
	
	static
	{
		Class<?> localdateclass = null;
		Class<?> localtimeclass = null;
		Class<?> localdatetimeclass = null;
		
		try
		{
			localdateclass = Class.forName("java.time.chrono.ChronoLocalDate");
			localtimeclass = Class.forName("java.time.LocalTime");
			localdatetimeclass = Class.forName("java.time.chrono.ChronoLocalDateTime");
		}
		catch (Exception e)
		{
		}
		
		CHRONOLOCALDATECLASS = localdateclass;
		LOCALTIMECLASS = localtimeclass;
		CHRONOLOCALDATETIMECLASS = localdatetimeclass;
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		ec.writeString(object.toString());
		return object;
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
		try
		{
			ret = clazz.getMethod("parse", new Class<?>[] { CharSequence.class }).invoke(null, context.readString());		
		}
		catch (Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
		return ret;
	}
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		boolean ret = (CHRONOLOCALDATECLASS != null && SReflect.isSupertype(CHRONOLOCALDATECLASS, clazz)) ||
					  (LOCALTIMECLASS != null && SReflect.isSupertype(LOCALTIMECLASS, clazz)) ||
					  (CHRONOLOCALDATETIMECLASS != null && SReflect.isSupertype(CHRONOLOCALDATETIMECLASS, clazz));
		
		return ret;
	}
	
	/**
	 *  Test if the codec allows referencing.
	 *  
	 *  @param object The object.
	 *  @param clazz The class.
	 *  @param ec The encoding context.
	 *  @return True, if the codec allows referencing.
	 */
	public boolean canReference(Object object, Class<?> clazz, IEncodingContext ec)
	{
		return false;
	}
}
