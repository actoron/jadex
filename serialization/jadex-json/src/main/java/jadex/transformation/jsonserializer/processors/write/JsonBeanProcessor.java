package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.SUtil;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * 
 */
public class JsonBeanProcessor implements ITraverseProcessor
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(5000);
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		return true;
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
//		System.out.println("fp: "+object);
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(wr.getCurrentInputObject());
		
		wr.write("{");
		
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			if(wr.isWriteId())
			{
				wr.write(",").writeId();
			}
//			wr.write(",");
		}
		else if(wr.isWriteId())
		{
			wr.writeId();
//			wr.write(",");
		}
		
		try
		{
//			System.out.println("cloned: "+object.getClass());
//			ret = object.getClass().newInstance();
			
			traverseProperties(object, conversionprocessors, processors, mode, traverser, targetcl, context, intro, !wr.isWriteClass() && !wr.isWriteId());
		}
		catch(Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		
		wr.write("}");
		
		return object;
	}
	
	/**
	 *  Clone all properties of an object.
	 */
	protected static void traverseProperties(Object object, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, 
		ClassLoader targetcl, Object context, IBeanIntrospector intro, boolean first)
	{
		Class<?> clazz = object.getClass();
		JsonWriteContext wr = (JsonWriteContext)context;
		
		Map<String, BeanProperty> props = intro.getBeanProperties(clazz, true, false);

		for(Iterator<String> it=props.keySet().iterator(); it.hasNext(); )
		{
			try
			{
				String name = (String)it.next();
				
				if(!wr.isPropertyExcluded(clazz, name))
				{	
					BeanProperty prop = (BeanProperty)props.get(name);
					
					if(prop.isReadable() && prop.isWritable())
					{
						Object val = prop.getPropertyValue(object);
						
//						if (val != null && val.getClass().toString().contains("jadex.bridge.ComponentIdentifier"))
//							System.out.println("Contains addresses: " + object.getClass() + " " + object);
						
						if(val!=null) 
						{
							if(!first)
								wr.write(",");
							first = false;
							wr.writeString(name);
							wr.write(":");
							
							traverser.doTraverse(val, prop.getType(), conversionprocessors, processors, mode, targetcl, context);
						}
					}
				}
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
	}
}
