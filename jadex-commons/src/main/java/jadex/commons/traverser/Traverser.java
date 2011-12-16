package jadex.commons.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The traverser allows to traverse an object graph deeply.
 *  Traversal processors can be used to perform specific actions
 *  on traversed object, e.g. object modifications. Does currently
 *  not allow transforming objects to other objects of another type
 *  (replacements), because objects have to be saved in the graph itself
 *  (no extra table for exchanged objects).
 *  
 *  The traverser can be used as a cloner when processors always return
 *  copies of the original objects.
 *  
 *  todo: introduce classloaders to allows for creating objects with other
 *  classloaders (e.g. convert a parameter from a sender to a receiver resource id).
 */
public class Traverser
{
	/** The default cloner. */
	protected static Traverser instance;
	
	/** The default traversal processors with no special actions. */
	protected static List<ITraverseProcessor> traversalprocessors;
	
	/** The default cloner processors with clone actions. */
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
	 *  Get the default traversal processors.
	 *  @return The traversal processors.
	 */
	public static List<ITraverseProcessor> getDefaultTraversalProcessors()
	{
		List ret = new ArrayList();
		ret.addAll(traversalprocessors);
		return ret;
	}
	
	/**
	 *  Get the default clone processors.
	 *  @return The clone processors.
	 */
	public static List<ITraverseProcessor> getDefaultCloneProcessors()
	{
		List ret = new ArrayList();
		ret.addAll(cloneprocessors);
		return ret;
	}
	
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
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The processors to apply.
	 *  @return The traversed (or modified) object.
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
	 *  Traverse an object.
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


