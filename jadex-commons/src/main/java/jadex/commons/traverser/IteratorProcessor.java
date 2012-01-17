package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Processor for handling iterators.
 */
public class IteratorProcessor implements ITraverseProcessor
{
	/** The clone flag. */
	protected boolean clone;
	
	/**
	 *  Create a new iterator processor.
	 */
	public IteratorProcessor()
	{
		this(false);
	}
	
	/**
	 *  Create a new iterator processor.
	 */
	public IteratorProcessor(boolean clone)
	{
		this.clone = clone;
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz)
	{
		return SReflect.isSupertype(Iterator.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		Iterator it = (Iterator)object;
		Iterator ret = it;
		
		boolean changed = false;
		List copy = new ArrayList();
		for(; it.hasNext(); )
		{
			Object val = it.next();
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors);
			copy.add(newval);
			
			if(newval!=val)
				changed = true;
		}
		
		if(clone || changed)
			ret = copy.iterator();
		
		traversed.put(object, ret);
		
		return ret;
	}
}
