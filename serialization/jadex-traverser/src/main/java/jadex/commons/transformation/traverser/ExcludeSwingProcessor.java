package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Excludes swing elements from being processed by traverser in !clone mode.
 *  Otherwise the processor is not applicable.
 */
public class ExcludeSwingProcessor implements ITraverseProcessor
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
		return !(SCloner.isCloneContext(context)) && object.getClass().getName().startsWith("javax.swing");
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
		return object;
	}
}

