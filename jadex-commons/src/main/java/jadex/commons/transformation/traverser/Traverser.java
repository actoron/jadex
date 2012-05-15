package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
	protected static List<ITraverseProcessor> processors;
	
	static
	{
		processors = new ArrayList<ITraverseProcessor>();
		processors.add(new ExcludeProcessor());
		processors.add(new ImmutableProcessor());
		processors.add(new CloneProcessor());
		processors.add(new ArrayProcessor());
		processors.add(new ListProcessor());
		processors.add(new SetProcessor());
		processors.add(new MapProcessor());
		processors.add(new CollectionProcessor());
		processors.add(new IteratorProcessor());
		processors.add(new EnumerationProcessor());
		/* if_not[android]*/
		processors.add(new ColorProcessor());
		processors.add(new ImageProcessor());
		/* end[android] */
		processors.add(new LogRecordProcessor());
		processors.add(new DateProcessor());
		processors.add(new UUIDProcessor());
		processors.add(new BeanProcessor());
//		processors.add(new FieldProcessor());
	}
	
	protected Map<Class, ITraverseProcessor> processorcache = new HashMap<Class, ITraverseProcessor>();
	
	/**
	 *  Get the default traversal processors.
	 *  @return The traversal processors.
	 */
	public static List<ITraverseProcessor> getDefaultProcessors()
	{
		List ret = new ArrayList();
		ret.addAll(processors);
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
	 *  @param processors The lists of processors.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, List<ITraverseProcessor> processors, boolean clone, Object context)
	{
		return traverseObject(object, processors, clone, null, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
//		if(clone && object!=null && object.getClass().getName().indexOf("Prop")!=-1)
//		System.out.println("Cloning: "+object);
//		if(!clone) 
//		System.out.println("Traversing: "+object+" "+object.getClass());
	
		Object ret = null;
		try
		{
			// Must be identity hash map because otherwise empty collections will equal
			ret = getInstance().traverse(object, null, new IdentityHashMap<Object, Object>(), processors, clone, targetcl, context);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		
//		if(clone && object!=null && object.getClass().getName().indexOf("Prop")!=-1)
//		System.out.println("Cloned: "+ret);
		
		return ret;
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object traverse(Object object, Class<?> clazz, Map<Object, Object> traversed, 
		List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		
		if(object!=null)
		{
//			if(object.getClass().getName().indexOf("SpaceObject")!=-1)
//				System.out.println("oooo");
			
			boolean fin = false;
			Object match = traversed.get(object);
			if(match != null)
			{
				ret = traversed.get(object);
				fin = true;
				handleDuplicate(object, clazz, match, processors, clone, context);
			}
			if(clazz==null || SReflect.isSupertype(clazz, object.getClass()))
				clazz = object.getClass();
				
			// Todo: apply all or only first matching processor!?
			Object	processed	= object;
			
			for(int i=0; i<processors.size() && !fin; i++)
			{
				ITraverseProcessor proc = processors.get(i);
				if(proc.isApplicable(processed, clazz, clone, targetcl))
				{
//						System.out.println("traverse: "+object+" "+proc.getClass());
					processed = proc.process(processed, clazz, processors, this, traversed, clone, targetcl, context);
					ret	= processed;
					fin = true;
					//processorcache.put(clazz, proc);
				}
			}
		}
			
		return ret;
	}
	
	/**
	 *  Special handling for duplicate objects.
	 */
	public void handleDuplicate(Object object, Class<?> clazz, Object match, 
			List<ITraverseProcessor> processors, boolean clone, Object context)
	{
	}
}


