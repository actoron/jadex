package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *
 */
public class JsonSimpleDateFormatProcessor extends JsonBeanProcessor
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
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(SimpleDateFormat.class, clazz);
	}
	 
	/**
	 *  Add pattern property from toPattern method
	 */
	@Override
	protected void traverseProperties(Object object, List<ITraverseProcessor> conversionprocessors,
			List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, Object context,
			IBeanIntrospector intro, boolean first)
	{
		Class<?> clazz = object.getClass();
		JsonWriteContext wr = (JsonWriteContext)context;
		if(!wr.isPropertyExcluded(clazz, "pattern"))
		{	
			Object val = ((SimpleDateFormat)object).toPattern();
			if(val!=null) 
			{
				if(!first)
					wr.write(",");
				first = false;
				wr.writeString("pattern");
				wr.write(":");
				
				traverser.doTraverse(val, String.class, conversionprocessors, processors, mode, targetcl, context);
			}
		}

		super.traverseProperties(object, conversionprocessors, processors, mode, traverser, targetcl, context, intro, first);
	}
}
