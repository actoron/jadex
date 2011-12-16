package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *  An enumeration processor allows for traversing enumerations.
 */
public class EnumerationProcessor implements ITraverseProcessor
{
	/** The clone falg. */
	protected boolean clone;
	
	/**
	 *  Create a new enumeration processor.
	 */
	public EnumerationProcessor()
	{
		this(false);
	}
	
	/**
	 *  Create a new enumeration processor.
	 */
	public EnumerationProcessor(boolean clone)
	{
		this.clone = clone;
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class clazz)
	{
		return SReflect.isSupertype(Enumeration.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		Enumeration en = (Enumeration)object;
		Enumeration ret = en;
		Vector copy = new Vector();
		
		boolean changed = false;
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors);
			copy.add(newval);
			
			if(newval!=val)
				changed = true;
		}
		
		if(clone || changed)
			ret = copy.elements();

		traversed.put(object, ret);

		return ret;
	}
}
