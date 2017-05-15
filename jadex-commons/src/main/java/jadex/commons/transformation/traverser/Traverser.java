package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;

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
	/**
	 *  Available modes:
	 *  
	 *  PREPROCESS	- Preprocess objects using the conversion processors
	 *  POSTPROCESS	- Postprocess objects using the conversion processors
	 *  PLAIN		- Ignore conversion processors
	 *
	 */
	public enum MODE
	{
		PREPROCESS,
		POSTPROCESS,
		PLAIN
	}
	
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
//	public static Object traverseObject(Object object, List<ITraverseProcessor> processors,  boolean clone, Object context)
//	{
//		return traverseObject(object, null, processors, null, clone, context);
//	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, Object context)
	{
		return traverseObject(object, conversionprocessors, processors, mode, null, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		return traverseObject(object, null, conversionprocessors, processors, mode, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, Class<?> clazz, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, Object context)
	{
		return traverseObject(object, clazz, conversionprocessors, processors, mode, null, context);
	}
	
	/**
	 *  Traverse an object.
	 *  @param object The object to traverse.
	 *  @param processors The lists of processors.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The traversed (or modified) object.
	 */
	public static Object traverseObject(Object object, Class<?> clazz, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors,  MODE mode, ClassLoader targetcl, Object context)
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
			ret = getInstance().traverse(object, clazz, conversionprocessors, processors, mode, targetcl, context);
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
	public Object traverse(Object object, Type clazz, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		if(processors == null)
		{
			processors = getDefaultProcessors();
		}
		
		Object obj = doTraverse(object, clazz, conversionprocessors, processors, mode, targetcl, context);
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
	public Object doTraverse(Object object, Type type, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		if (mode == null)
			throw new IllegalArgumentException("MODE IS NULL");
		Object ret = preemptProcessing(object, type, context);
		if (ret == null)
		{
			Object orig = object;
			ITraverseProcessor usedconvproc = null;
			ITraverseProcessor usedproc = null;
			if (mode == MODE.PREPROCESS && conversionprocessors != null)
			{
				for (ITraverseProcessor preprocessor : conversionprocessors)
				{
					if (preprocessor.isApplicable(object, type, targetcl, context))
					{
						usedconvproc = preprocessor;
						object = preprocessor.process(object, type, this, conversionprocessors, processors, mode, targetcl, context);
						break;
					}
				}
			}
			
			ret = object;
			Class<?> clazz = SReflect.getClass(type);
			
			if(object!=null)
			{
	//			if(object.getClass().getName().indexOf("ProgressData")!=-1)
	//				System.out.println("oooo");
				
//				boolean fin = false;
//				Object match = traversed.get(orig);
//				if(match != null)
//				{
//					ret = traversed.get(orig);
//					fin = true;
//					handleDuplicate(orig, clazz, match, processors, clone, context);
//				}
				Class<?> oclazz = findClazz(object, targetcl);
				if(clazz==null || SReflect.isSupertype(clazz, oclazz))
					clazz = oclazz;
					
				// Todo: apply all or only first matching processor!?
				Object	processed	= object;
				
				for(int i=0; i<processors.size(); i++)
				{
					ITraverseProcessor proc = processors.get(i);
					if(proc.isApplicable(processed, clazz, targetcl, context))
					{
	//					if(object.getClass().getName().indexOf("awt")!=-1)
	//						System.out.println("traverse: "+object+" "+proc.getClass());
						usedproc = proc;
						processed = proc.process(processed, clazz, this, conversionprocessors, processors, mode, targetcl, context);
						ret	= processed;
						break;
						//processorcache.put(clazz, proc);
					}
				}
			}
//			else
//			{
//				ret = handleNull(clazz, processors, context);
//			}
				
	//		System.out.println("traversed: "+traversed);
			
			if (mode == MODE.POSTPROCESS && conversionprocessors != null)
			{
				for (ITraverseProcessor postprocessor : conversionprocessors)
				{
					if (postprocessor.isApplicable(ret, ret!=null?ret.getClass():clazz, targetcl, context))
					{
						usedconvproc = postprocessor;
						ret = postprocessor.process(ret,  ret!=null?ret.getClass():clazz, this, conversionprocessors, processors, mode, targetcl, context);
						break;
					}
				}
			}
			finalizeProcessing(orig, ret, usedconvproc, usedproc, context);
//			postHandle(orig, ret, context);
		}
		
		return ret;
	}
	
	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @param cl The classloader.
	 *  @return The objects class.
	 */
	public Class<?> findClazz(Object object, ClassLoader cl)
	{
		return object.getClass();
	}
	
	/**
	 *  Allows preemption of processing, if the return value is not null,
	 *  the returned object is used and processing is skipped.
	 *  
	 *  @param inputobject The input object
	 *  @param inputtype The input class.
	 *  @param context The context.
	 *  @return Null to process as normal, any other object skips normal processing.
	 */
	public Object preemptProcessing(Object inputobject, Type inputtype, Object context)
	{
		Object ret = null;
		if (context instanceof TraversedObjectsContext)
			ret = ((TraversedObjectsContext) context).get(inputobject);
		return ret;
	}
	
	/**
	 *  Handle objects after all processing steps have been done before object is returned.
	 *  Not called for objects returned by preHandle().
	 *  
	 *  @param inputobject The input object
	 *  @param outputobject The object after processing.
	 *  @param context The context.
	 */
	public void finalizeProcessing(Object inputobject, Object outputobject, ITraverseProcessor convproc, ITraverseProcessor proc, Object context)
	{
	}
	
	/**
	 *  Special handling for duplicate objects.
	 */
//	public void handleDuplicate(Object object, Class<?> clazz, Object match, 
//		List<ITraverseProcessor> processors, boolean clone, Object context)
//	{
//	}
	
	/**
	 *  Special handling for null objects.
	 */
//	public Object handleNull(Class<?> clazz, List<ITraverseProcessor> processors, Object context)
//	{
//		return null;
//	}
}


