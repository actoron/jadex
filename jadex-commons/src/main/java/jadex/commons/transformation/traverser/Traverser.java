package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.OptionalCodec;

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
	public static final Object IGNORE_RESULT = new Object();
	
	/** The default cloner. */
	protected static volatile Traverser instance;
	
	/** The default traversal processors with no special actions. */
	protected static final List<ITraverseProcessor> processors;
	
	static
	{
		processors = new ArrayList<ITraverseProcessor>();
		processors.add(new ExcludeProcessor());
		processors.add(new ImmutableProcessor());
		processors.add(new ArrayProcessor());
		processors.add(new ListProcessor());
		processors.add(new SetProcessor());
		processors.add(new MultiCollectionProcessor());
		processors.add(new MapProcessor());
		processors.add(new CollectionProcessor());
		processors.add(new IteratorProcessor());
		processors.add(new EnumerationProcessor());
		if(!SReflect.isAndroid())
		{
			processors.add(new ColorProcessor());
			processors.add(new ExcludeSwingProcessor());
			processors.add(new ImageProcessor());
			processors.add(new RectangleProcessor());
		}
		processors.add(new TimestampProcessor());
		processors.add(new LogRecordProcessor());
		processors.add(new DateProcessor());
		processors.add(new UUIDProcessor());
		processors.add(new BigIntegerProcessor());
		processors.add(new CloneProcessor());
		processors.add(new OptionalProcessor());
		processors.add(new BeanProcessor());
//		processors.add(new FieldProcessor());
	}
	
	protected Map<Class<?>, ITraverseProcessor> processorcache = new HashMap<Class<?>, ITraverseProcessor>();
	
	/**
	 *  Get the default traversal processors.
	 *  @return The traversal processors.
	 */
	public static List<ITraverseProcessor> getDefaultProcessors()
	{
		List<ITraverseProcessor> ret = new ArrayList<ITraverseProcessor>();
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
		return traverseObject(object, null, processors, clone, null, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, Class<?> clazz, List<ITraverseProcessor> processors, boolean clone, Object context)
	{
		return traverseObject(object, clazz, processors, clone, null, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, Class<?> clazz, List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
//		if(clone && object!=null && object.getClass().getName().indexOf("Prop")!=-1)
//			System.out.println("Cloning: "+object);
//			if(!clone) 
			
//			if(object!=null && (object.getClass().getName().indexOf("Connection")!=-1 || 
//				(object.getClass().getName().indexOf("TerminableIntermediateFuture")!=-1)))
//				System.out.println("Traversing: "+object+" "+object.getClass());
		
		Object ret = null;
		try
		{
			// Must be identity hash map because otherwise empty collections will equal
			ret = getInstance().traverse(object, clazz, new IdentityHashMap<Object, Object>(), processors, clone, targetcl, context);
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
	public Object traverse(Object object, Class<?> clazz, 
		List<ITraverseProcessor> processors, ClassLoader targetcl, Object context)
	{
		return traverse(object, clazz, new IdentityHashMap<Object, Object>(), processors, false, targetcl, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object traverse(Object object, Class<?> clazz, 
		List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
		return traverse(object, clazz, new IdentityHashMap<Object, Object>(), processors, clone, targetcl, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object traverse(Object object, Type clazz, Map<Object, Object> traversed, 
		List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
		if(processors == null)
		{
			processors = getDefaultProcessors();
		}
		
		Object obj = doTraverse(object, clazz, traversed, processors, clone, targetcl, context);
		if(obj == IGNORE_RESULT)
		{
			obj = null;
		}
		return obj;
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object doTraverse(Object object, Type type, Map<Object, Object> traversed, 
		List<ITraverseProcessor> processors, boolean clone, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		Class<?> clazz = SReflect.getClass(type);
		
		if(object!=null)
		{
//			if(object.getClass().getName().indexOf("ProgressData")!=-1)
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
				clazz = findClazz(object, targetcl);
				
			// Todo: apply all or only first matching processor!?
			Object	processed	= object;
			
			for(int i=0; i<processors.size() && !fin; i++)
			{
				ITraverseProcessor proc = processors.get(i);
				if(proc.isApplicable(processed, clazz, clone, targetcl))
				{
//					if(object.getClass().getName().indexOf("awt")!=-1)
//						System.out.println("traverse: "+object+" "+proc.getClass());
					
					processed = proc.process(processed, clazz, processors, this, traversed, clone, targetcl, context);
					ret	= processed;
					fin = true;
					//processorcache.put(clazz, proc);
				}
			}
			
			if(!fin)
				throw new RuntimeException("Found no processor for: "+object+" "+type);
		}
		else
		{
			ret = handleNull(clazz, processors, clone, context);
		}
			
//		System.out.println("traversed: "+traversed);
		
		return ret;
	}
	
	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @param cl The classloader.
	 *  @return The objects class.
	 */
	protected Class<?> findClazz(Object object, ClassLoader cl)
	{
		return object.getClass();
	}
	
	/**
	 *  Special handling for duplicate objects.
	 */
	public void handleDuplicate(Object object, Class<?> clazz, Object match, 
		List<ITraverseProcessor> processors, boolean clone, Object context)
	{
	}
	
	/**
	 *  Special handling for null objects.
	 */
	public Object handleNull(Class<?> clazz,
		List<ITraverseProcessor> processors, boolean clone, Object context)
	{
		return null;
	}
}


