package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

public class JsonLocalDateTimeProcessor implements ITraverseProcessor
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
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		boolean ret = (CHRONOLOCALDATECLASS != null && SReflect.isSupertype(CHRONOLOCALDATECLASS, clazz)) ||
					  (LOCALTIMECLASS != null && SReflect.isSupertype(LOCALTIMECLASS, clazz)) ||
					  (CHRONOLOCALDATETIMECLASS != null && SReflect.isSupertype(CHRONOLOCALDATETIMECLASS, clazz));
		
		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		JsonObject obj = (JsonObject)object;
		Object ret = null;
		
		try
		{
			ret = clazz.getMethod("parse", new Class<?>[] { CharSequence.class }).invoke(null, obj.getString("value", null));
		}
		catch (Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
		
		return ret;
	}
}
