package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonThrowableProcessor extends JsonBeanProcessor
{
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
		return object instanceof JsonObject && SReflect.isSupertype(Throwable.class, clazz);
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
		return SReflect.isSupertype(Throwable.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		Object ret = null;
		Class<?> clazz = SReflect.getClass(type);
		
		JsonObject obj = (JsonObject)object;
		String msg = obj.getString("msg", null);
		
		Throwable cause = null;
		JsonValue cau = obj.get("cause");
		if(cau!=null)
			cause = (Throwable)traverser.traverse(cau, Throwable.class, conversionprocessors, processors, mode, targetcl, context);
//			cause = (Throwable)traverser.traverse(cau, Throwable.class, preprocessors, processors, postprocessors, targetcl, context);
		
//		Class<?> cl = JsonPrimitiveObjectProcessor.getClazz(object, targetcl);
		
		try
		{
			Constructor<?> con = clazz.getConstructor(new Class<?>[]{String.class, Throwable.class});
			ret = con.newInstance(new Object[]{msg, cause});
		}
		catch(Exception e)
		{
		}
		
		if(ret==null)
		{
			try
			{
				// At least UndeclaredThrowableException stupidly has the constructor arguments reverse... good job.
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Throwable.class, String.class});
				ret = con.newInstance(new Object[]{cause, msg});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Throwable.class});
				ret = con.newInstance(new Object[]{cause});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				// Special case for ErrorException, that supports only Error as cause.
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Error.class});
				ret = con.newInstance(new Object[]{cause});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{String.class});
				ret = con.newInstance(new Object[]{msg});
				if(ret != null && cause != null)
				{
					((Throwable)ret).initCause(cause);
				}
			}
			catch(Exception e)
			{
			}
		}
		
		// Try find empty constructor
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[0]);
				ret = con.newInstance(new Object[0]);
				if(ret != null && cause != null)
				{
					((Throwable)ret).initCause(cause);
				}
			}
			catch(Exception e)
			{
				RuntimeException rte = new RuntimeException("No empty constructor found for class: " + clazz.getName(), e);
				throw rte;
			}
		}
		
		if(ret!=null)
		{
//			traversed.put(object, ret);
//			((JsonReadContext)context).addKnownObject(ret);
			
			JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			
			JsonBeanProcessor.readProperties(object, clazz, conversionprocessors, processors, mode, traverser, targetcl, ret, context, intro);
//			JsonBeanProcessor.traverseProperties(object, clazz, traversed, processors, postprocessors, traverser, clone, targetcl, ret, context, intro);
		}
		
		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(wr.getCurrentInputObject());
		
		Throwable t = (Throwable)object;
		
		wr.write("{");
		
		boolean first = true;
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}
		
		if(t.getMessage()!=null)
		{
			if(!first)
				wr.write(",");
			wr.write("\"msg\":");
			traverser.doTraverse(t.getMessage(), String.class, conversionprocessors, processors, mode, targetcl, context);
			first = false;
		}
		if(t.getCause()!=null)
		{
			if(!first)
				wr.write(",");
			wr.write("\"cause\":");
			Object val = t.getCause();
			traverser.doTraverse(val, val!=null? val.getClass(): Throwable.class, conversionprocessors, processors, mode, targetcl, context);
			first = false;
		}
		
		writeProperties(object, conversionprocessors, processors, mode, traverser, targetcl, context, intro, first);
		
		wr.write("}");
		
		return object;
	}
}





