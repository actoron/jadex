package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

public class JsonLocalDateTimeProcessor extends AbstractJsonProcessor
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
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		boolean ret = (CHRONOLOCALDATECLASS != null && SReflect.isSupertype(CHRONOLOCALDATECLASS, clazz)) ||
					  (LOCALTIMECLASS != null && SReflect.isSupertype(LOCALTIMECLASS, clazz)) ||
					  (CHRONOLOCALDATETIMECLASS != null && SReflect.isSupertype(CHRONOLOCALDATETIMECLASS, clazz));
		
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		boolean ret = (jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.CHRONOLOCALDATECLASS != null && SReflect.isSupertype(jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.CHRONOLOCALDATECLASS, clazz)) ||
					  (jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.LOCALTIMECLASS != null && SReflect.isSupertype(jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.LOCALTIMECLASS, clazz)) ||
					  (jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.CHRONOLOCALDATETIMECLASS != null && SReflect.isSupertype(jadex.transformation.jsonserializer.processors.read.JsonLocalDateTimeProcessor.CHRONOLOCALDATETIMECLASS, clazz));
		
		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
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
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
		wr.write("{");
		
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			wr.write(", ");
		}
		wr.writeNameString("value", object.toString());
		wr.write("}");
		
		return object;
	}
}
