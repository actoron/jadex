package jadex.commons.transformation.traverser;


import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.List;
import java.util.logging.Level;

import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * 
 */
public class ImmutableProcessor implements ITraverseProcessor
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
		return object instanceof Enum || object instanceof URL || object instanceof URI 
			|| object instanceof Level || object instanceof InetAddress || object instanceof Exception
			|| object instanceof Certificate || object instanceof String;
	}
//	SReflect.isSupertype(Enum.class, object.getClass())
	
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
