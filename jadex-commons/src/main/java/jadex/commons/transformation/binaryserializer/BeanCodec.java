package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.BeanReflectionIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Codec for encoding and decoding Java Beans.
 *
 */
public class BeanCodec implements ITraverseProcessor, IDecoderHandler
{
	/** Bean introspector for inspecting beans. */
	protected BeanReflectionIntrospector intro = new BeanReflectionIntrospector();
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return true;
	}
	
	/**
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
	{
		Object bean = null;
		boolean isanonclass = context.readBool();
		if (isanonclass)
		{
			/*Class[] dclasses = clazz.getClasses();
			clazz = null;
			int i = 0;
			
			try{
			while (clazz == null)
			{
				Classname cn = getAnonClassName(dclasses[i]);
				if (cn != null)
					clazz = dclasses[i];
				++i;
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}*/
			context.readString();
			
			Constructor	c	= clazz.getDeclaredConstructors()[0];
			c.setAccessible(true);
			Class[] paramtypes = c.getParameterTypes();
			Object[] paramvalues = new Object[paramtypes.length];
			for(int i=0; i<paramtypes.length; i++)
			{
				if(paramtypes[i].equals(boolean.class))
				{
					paramvalues[i] = Boolean.FALSE;
				}
				else if(SReflect.isBasicType(paramtypes[i]))
				{
					paramvalues[i] = 0;
				}
			}
			
			try
			{
				bean = c.newInstance(paramvalues);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}
		else
		{
			try
			{
				bean = clazz.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		Map props = intro.getBeanProperties(clazz, false);
		
		int size = (int) context.readVarInt();
		for (int i = 0; i < size; ++i)
		{
			String name = context.readString();
			Method setter = ((BeanProperty) props.get(name)).getSetter();
			Object val = null;
			try
			{
				val = BinarySerializer.decodeObject(context);
				setter.invoke(bean, val);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		/*for(Iterator it=props.values().iterator(); it.hasNext(); )
		{
			BeanProperty prop = (BeanProperty) it.next();
			Method setter = prop.getSetter();
			if (prop.getGetter() != null && setter != null)
			{
				Object fieldval = null;
				try
				{
					fieldval = BinarySerializer.decodeObject(context);
					if (fieldval != null)
					{
						try
						{
							setter.invoke(bean, fieldval);
						}
						catch(NullPointerException e)
						{
							e.printStackTrace();
							throw new RuntimeException(e);
						}
						catch(IllegalArgumentException e)
						{
							System.out.println(fieldval);
							System.out.println(prop.getName());
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{*/
				/*Field field = prop.getField();
				if (!field.isAccessible())
					field.setAccessible(true);
				try
				{
					field.set(bean, BinarySerializer.decodeObject(context));
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}*/
			//}
		//}
		
		return bean;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object != null;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		EncodingContext ec = (EncodingContext) context;
		
		object = ec.runPreProcessors(object, clazz, processors, traverser, traversed, clone, context);
		clazz = object == null? null : object.getClass();
		
		if (clazz.isAnonymousClass())
		{
			Class eclazz = clazz;
			while (eclazz.getEnclosingClass() != null)
				eclazz = eclazz.getEnclosingClass();
			ec.writeClass(clazz);
			
			// Flag class as enclosing the actual inner class.
			ec.writeBoolean(true);
			
			Classname cn = getAnonClassName(clazz);
			
			if (cn == null)
				throw new RuntimeException("Anonymous Class without Classname identifier not supported: " + String.valueOf(clazz));
			
			ec.writeString(cn.value());
			
			
		}
		else
		{
			ec.writeClass(clazz);
			ec.writeBoolean(false);
		}
		
		Map props = intro.getBeanProperties(clazz, false);
		
		List<String> names = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		for(Iterator it=props.keySet().iterator(); it.hasNext(); )
		{
			BeanProperty prop = (BeanProperty)props.get(it.next());
			if (prop.getGetter() != null && prop.getSetter() != null)
			{
				Object val;
				try
				{
					val = prop.getGetter().invoke(object, (Object[]) null);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
				if (val != null)
				{
					names.add(prop.getName());
					values.add(val);
				}
			}
		}
		ec.write(VarInt.encode(names.size()));
		
		for (int i = 0; i < names.size(); ++i)
		{
			ec.writeString(names.get(i));
			Object val = values.get(i);
			traverser.traverse(val, val.getClass(), traversed, processors, clone, context);
		}
		
		/*for(Iterator it=props.keySet().iterator(); it.hasNext(); )
		{
			try
			{
				String name = (String)it.next();
				BeanProperty prop = (BeanProperty)props.get(name);
				if (prop.getGetter() != null && prop.getSetter() != null)
				{
					Object val = prop.getGetter().invoke(object, new Object[0]);
					//System.out.println(val);
					//System.out.println(prop.getName());
					//if (val == null)
						//BinarySerializer.NULL_HANDLER.process(val, prop.getType(), null, null, null, false, context);
					//else
						//traverser.traverse(val, prop.getType(), traversed, processors, clone, context);
					if (val != null)
					{
						ec.writeString(name);
						traverser.traverse(val, prop.getType(), traversed, processors, clone, context);
					}
				}
			}
			catch(Exception e)
			{
				throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
			}
		}*/
		
		return object;
	}
	
	private Classname getAnonClassName(Class clazz)
	{
		Field[] fields = clazz.getFields();
		Classname cn = null;
		for (int i = 0; i < fields.length && cn == null; ++i)
		{
			if (fields[i].isAnnotationPresent(Classname.class))
			{
				cn = fields[i].getAnnotation(Classname.class);
			}
		}
		
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length && cn == null; ++i)
		{
			if (methods[i].isAnnotationPresent(Classname.class))
			{
				cn = methods[i].getAnnotation(Classname.class);
			}
		}
		
		return cn;
	}
}
