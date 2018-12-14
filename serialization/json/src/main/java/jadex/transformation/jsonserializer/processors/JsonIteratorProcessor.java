package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Processor for ignoring Itorators during write.
 */
public class JsonIteratorProcessor extends AbstractJsonProcessor
{
	/**
	 *  Test if the processor is applicable for reading.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		// No applicable for reads.
		return false;
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
		return SReflect.isSupertype(Iterator.class, clazz);
	}
	
	/**
	 *  Read an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		// No applicable for reads.
		return null;
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
//		JsonWriteContext wr = (JsonWriteContext)context;
//		
//		Iterator it = (Iterator)object;
//		List copy = new ArrayList();
//		Iterator ret = new UncheckedIterator(copy);
//
//		traversed.put(object, ret);
//
//		for(; it.hasNext(); )
//		{
//			Object val = it.next();
//			Class valclazz = val!=null? val.getClass(): null;
//			Object newval = traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
//			if (newval != Traverser.IGNORE_RESULT)
//				copy.add(newval);
//		}
//		
//		return ret;
//		
////		traversed.put(object, null);
//		
//		wr.write("{");
//		
//		Set keyset = map.keySet();
//		Object[] keys = keyset.toArray(new Object[keyset.size()]);
//		for(int i=0; i<keys.length; i++)
//		{
//			Object val = map.get(keys[i]);
//			Class<?> valclazz = val!=null? val.getClass(): null;
//			Object key = keys[i];
//			Class<?> keyclazz = key != null? key.getClass() : null;
//			
//			if(key!=null && val!=null)
//			{
//				// hmm key must be string :-(
//				wr.write("\"");
//				wr.write(key.toString());
//				wr.write("\"");
////				traverser.doTraverse(key, keyclazz, traversed, processors, clone, targetcl, context);
//				wr.write(":");
//				traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
//			}
//		}
//
//		if(wr.isWriteClass())
//			wr.write(",").writeClass(object.getClass());
//		
//		wr.write("}");
//		
//		return object;
		return null;
	}
}
