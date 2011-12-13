package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Traverser
{
	protected static List<ITraverseProcessor> traversalprocessors;
	
	protected static List<ITraverseProcessor> cloneprocessors;
	
	static
	{
		traversalprocessors = new ArrayList<ITraverseProcessor>();
		traversalprocessors.add(new ExcludeProcessor());
		traversalprocessors.add(new ArrayProcessor());
		traversalprocessors.add(new MapProcessor());
		traversalprocessors.add(new CollectionProcessor());
		traversalprocessors.add(new IteratorProcessor());
		traversalprocessors.add(new EnumerationProcessor());
		traversalprocessors.add(new FieldProcessor());
		
		cloneprocessors = new ArrayList<ITraverseProcessor>();
		cloneprocessors.add(new ExcludeProcessor());
		cloneprocessors.add(new CloneProcessor());
		cloneprocessors.add(new ArrayProcessor(true));
		cloneprocessors.add(new MapProcessor(true));
		cloneprocessors.add(new CollectionProcessor(true));
		cloneprocessors.add(new IteratorProcessor(true));
		cloneprocessors.add(new EnumerationProcessor(true));
		cloneprocessors.add(new FieldProcessor(true));
	}
	
	/**
	 * 
	 */
	public static List<ITraverseProcessor> getDefaultProcessors(boolean clone)
	{
		List ret = new ArrayList();
		ret.addAll(clone? cloneprocessors: traversalprocessors);
		return ret;
	}
	
	/** The default cloner. */
	protected static Traverser instance;
	
	/**
	 *  Get the default cloner instance.
	 */
	public static Traverser getInstance()
	{
		if(instance==null)
		{
			synchronized(Traverser.class)
			{
				if(instance==null)
				{
					instance = new Traverser();
				}
			}
		}
		return instance;
	}
	
//	/**
//	 *  Traverse an object.
//	 */
//	public static Object traverseObject(Object object)
//	{
//		return getInstance().traverse(object, null, new HashMap<Object, Object>(), null);
//	}
	
	/**
	 *  Traverse an object.
	 */
	public static Object traverseObject(Object object, List<ITraverseProcessor> processors)
	{
		try
		{
			return getInstance().traverse(object, null, new HashMap<Object, Object>(), processors);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 *  Deep clone an object.
	 */
	public Object traverse(Object object, Class<?> clazz, Map<Object, Object> traversed, 
		List<ITraverseProcessor> processors)
	{
		Object ret = object;
		
		if(object!=null)
		{
			boolean fin = false;
			if(traversed.containsKey(object))
			{
				ret = traversed.get(object);
				fin = true;
			}

			if(!fin && processors!=null)
			{
				if(clazz==null || SReflect.isSupertype(clazz, object.getClass()))
					clazz = object.getClass();
				
				// Todo: apply all or only first matching processor!?
				Object	processed	= object;
				for(int i=0; i<processors.size() && !fin; i++)
				{
					ITraverseProcessor proc = processors.get(i);
					if(proc.isApplicable(processed, clazz))
					{
//						System.out.println("traverse: "+object+" "+proc.getClass());
						processed = proc.process(processed, clazz, processors, this, traversed);
						ret	= processed;
						fin = true;
					}
				}
			}
		}
			
		return ret;
	}
}


