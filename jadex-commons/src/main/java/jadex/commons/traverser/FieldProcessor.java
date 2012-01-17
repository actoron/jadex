package jadex.commons.traverser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 *  Processor that traverses all fields of an object.
 */
class FieldProcessor implements ITraverseProcessor
{
	/** The clone flag. */
	protected boolean clone;
	
	/**
	 *  Create a new field processor.
	 */
	public FieldProcessor()
	{
		this(false);
	}
	
	/**
	 *  Create a new field processor.
	 */
	public FieldProcessor(boolean clone)
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
		return true;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
//		System.out.println("fp: "+object);
		Object ret = object;
		
		try
		{
//			System.out.println("cloned: "+object.getClass());
//			ret = object.getClass().newInstance();
			
			ret = traverseFields(object, traversed, processors, traverser);
			traversed.put(object, ret);
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Clone all fields of an object.
	 */
	protected Object traverseFields(Object object, Map<Object, Object> cloned, 
		List<ITraverseProcessor> processors, Traverser traverser)
	{
		Class clazz = object.getClass();
		Object ret = getReturnObject(object, clazz);
			
		boolean changed = false;
		while(clazz!=null && clazz!=Object.class) 
		{
			// Get all declared fields (public, protected and private)
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++) 
			{
				if((fields[i].getModifiers() & Modifier.STATIC) != Modifier.STATIC) 
				{
					fields[i].setAccessible(true);
					Object val = null;
					try
					{
						val = fields[i].get(object);
						if(val!=null) 
						{
							System.out.println("traversing "+fields[i]);
							Object newval = traverser.traverse(val, fields[i].getType(), cloned, processors);
							if(clone || val!=newval)
								fields[i].set(ret, newval);
						}
					}
					catch(Exception e)
					{
						throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
					}
				}
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return ret;
	}
	
	/**
	 *  Get the object that is returned.
	 */
	public Object getReturnObject(Object object, Class clazz)
	{
		Object ret = object;
		if(clone)
		{
			try
			{
				ret = object.getClass().newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}