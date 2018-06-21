package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Tuple is itself immutable, but acts as a container
 *  for arbitrary objects -> must be cloned.
 */
public class TupleProcessor implements ITraverseProcessor
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
		return object instanceof Tuple;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		if(SCloner.isCloneContext(context))
		{
			Tuple t = (Tuple)object;
			Object[] vals = t.getEntities();
			Object[] dest = new Object[vals.length];
			
			// does only work as tuple does currently not copy
			ret = createTuple(t.getClass());
			TraversedObjectsContext.put(context, object, ret);
			
			for(int i=0; i<vals.length; i++) 
			{
				Object newval = traverser.doTraverse(vals[i], null, conversionprocessors, processors, mode, targetcl, context);
				if (newval != Traverser.IGNORE_RESULT)
				{
					dest[i] = newval;
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public Object createTuple(Class clazz)
	{
		Tuple ret = null;
		if(clazz.equals(Tuple3.class))
		{
			ret = new Tuple3(null, null, null);
		}
		else if (clazz.equals(Tuple2.class))
		{
			ret = new Tuple2(null, null);
		}
		else
		{
			ret =  new Tuple(null);
		}
		return ret;
	}
}

