package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * 
 */
public class JsonMapProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Map.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(wr.getCurrentInputObject());
		
		Map map = (Map)object;
		
		wr.write("{");
		boolean first = true;
		
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}
		
		if(wr.isWriteId())
		{
			if(!first)
				wr.write(",");
			wr.writeId();
			first = false;
		}
		
		Set keyset = map.keySet();
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		
		if(keys.length>0)
		{
			if(!first)
			{
				wr.write(",");
				first = false;
			}
			
			boolean keystring = true;
			for(int i=0; i<keys.length && keystring; i++)
			{
				if (!(keys[i] instanceof String))
				{
					keystring = false;
					break;
				}
			}
			
			if(keystring)
			{
				for(int i=0; i<keys.length; i++)
				{
					Object val = map.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					Object key = keys[i];
					
					if(i>0)
						wr.write(",");
					wr.write("\"").write(key.toString()).write("\":");
					traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
				}
			}
			else
			{
				wr.write("\"__keys\":[");
				// just increase reference count because these helper objects do not count on read side
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object key = keys[i];
					Class<?> keyclazz = key != null? key.getClass() : null;
					traverser.doTraverse(key, keyclazz, conversionprocessors, processors, mode, targetcl, context);
				}
				wr.write("]");
				
				wr.write(",\"__values\":[");
				// just increase reference count because these helper objects do not count on read side
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object val = map.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
				}
				wr.write("]");
			}
		}
		
		wr.write("}");
		
		return object;
	}
}
