package jadex.commons.transformation.traverser;


import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.GroupLayout.SequentialGroup;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Processor for immutable types. 
 */
public class ImmutableProcessor implements ITraverseProcessor
{
	/** The immutable types. */
	protected Set<Class<?>> immutables;
	
	/** Default immutables. */
	public final static Set<Class<?>> DEFAULT_IMMUTABLES;
	
	static
	{
		DEFAULT_IMMUTABLES = new HashSet<Class<?>>();
		DEFAULT_IMMUTABLES.add(Enum.class);
		DEFAULT_IMMUTABLES.add(URL.class);
		DEFAULT_IMMUTABLES.add(URI.class);
		DEFAULT_IMMUTABLES.add(Level.class);
		DEFAULT_IMMUTABLES.add(InetAddress.class);
		DEFAULT_IMMUTABLES.add(Exception.class);
		DEFAULT_IMMUTABLES.add(Certificate.class);
		DEFAULT_IMMUTABLES.add(String.class);
	}
	
	/**
	 *  Create a new processor.
	 */
	public ImmutableProcessor()
	{
		this(DEFAULT_IMMUTABLES);
	}
	
	/**
	 *  Create a new processor.
	 */
	public ImmutableProcessor(Set<Class<?>> immutables)
	{
		this.immutables = immutables;
	}
	
	/**
	 *  Create a new processor.
	 */
	public ImmutableProcessor(Class<?>[] immutables)
	{
		this((Set)SUtil.arrayToSet(immutables));
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		boolean ret = false;
		for(Class<?> im: immutables)
		{
			ret = SReflect.isSupertype(im, object.getClass());
			if(ret)
				break;
		}
		return ret;
//		return object instanceof Enum || object instanceof URL || object instanceof URI 
//			|| object instanceof Level || object instanceof InetAddress || object instanceof Exception
//			|| object instanceof Certificate || object instanceof String;
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
