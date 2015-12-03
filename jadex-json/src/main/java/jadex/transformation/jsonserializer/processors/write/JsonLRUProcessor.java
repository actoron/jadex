package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonLRUProcessor extends JsonMapProcessor
{
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
		return SReflect.isSupertype(LRU.class, clazz);
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
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(traversed, object);
		
		LRU lru = (LRU)object;
		
		wr.write("{");
		
		wr.writeNameValue("max", lru.getMaxEntries());
		
		if(lru.getCleaner()!=null)
		{
			wr.write(",\"cleaner\":");
			traverser.doTraverse(lru.getCleaner(), lru.getCleaner().getClass(), traversed, processors, clone, targetcl, context);
		}
		
		if(wr.isWriteClass())
		{
			wr.write(",");
			wr.writeClass(object.getClass());
		}
		
		Set keyset = lru.keySet();
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		
		if(keys.length>0)
		{
			wr.write(",");
			
			boolean keystring = true;
			for(int i=0; i<keys.length && keystring; i++)
			{
				keystring = keys[i] instanceof String;
			}
			
			if(keystring)
			{
				for(int i=0; i<keys.length; i++)
				{
					Object val = lru.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					Object key = keys[i];
					
					wr.write("\"").write(key.toString()).write("\":");
					traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
				}
			}
			else
			{
				wr.write("\"__keys\":[");
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object key = keys[i];
					Class<?> keyclazz = key != null? key.getClass() : null;
					traverser.doTraverse(key, keyclazz, traversed, processors, clone, targetcl, context);
				}
				wr.write("]");
				
				wr.write(",\"__values\":[");
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object val = lru.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
				}
				wr.write("]");
			}
		}
		
		wr.write("}");
		
		return object;
	}
}
