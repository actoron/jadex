package jadex.binary;

import java.lang.reflect.Constructor;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding exception objects.
 */
public class ThrowableCodec extends AbstractCodec
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(500);
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Throwable.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Object ret = null;
//		String msg = (String)SBinarySerializer.decodeObject(context);
		String msg = context.readString();
		Throwable cause = (Throwable)SBinarySerializer.decodeObject(context);

		try
		{
			Constructor<?> con = clazz.getConstructor(new Class<?>[]{String.class, Throwable.class});
			ret = con.newInstance(new Object[]{msg, cause});
		}
		catch(Exception e)
		{
		}
		
		if(ret==null)
		{
			try
			{
				// At least UndeclaredThrowableException stupidly has the constructor arguments reverse... good job.
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Throwable.class, String.class});
				ret = con.newInstance(new Object[]{cause, msg});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Throwable.class});
				ret = con.newInstance(new Object[]{cause});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				// Special case for ErrorException, that supports only Error as cause.
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{Error.class});
				ret = con.newInstance(new Object[]{cause});
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[]{String.class});
				ret = con.newInstance(new Object[]{msg});
				if (ret != null && cause != null)
				{
					((Throwable) ret).initCause(cause);
				}
			}
			catch(Exception e)
			{
			}
		}
		
		// Try find empty constructor
		if(ret==null)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class<?>[0]);
				ret = con.newInstance(new Object[0]);
				if (ret != null && cause != null)
				{
					((Throwable) ret).initCause(cause);
				}
			}
			catch(Exception e)
			{
				RuntimeException rte = new RuntimeException("No empty constructor found for class: " + clazz.getName(), e);
				throw rte;
			}
		}
		
		if(ret!=null)
		{
			BeanCodec.readBeanProperties(ret, clazz, context, intro);
		}
		
		return ret;
	}
	
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return isApplicable(clazz);
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Throwable t = (Throwable)object;
		
		ec.writeString(t.getMessage());
//		traverser.doTraverse(t.getMessage(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
	
		Object val = t.getCause();
		traverser.doTraverse(val, val!=null? val.getClass(): Throwable.class, preprocessors, processors, mode, targetcl, ec);

		BeanCodec.writeBeanProperties(object, clazz, preprocessors, processors, traverser, mode, ec, intro);
		
		return object;
	}
}

