package jadex.binary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.SCloner;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * Codec for encoding and decoding Java Beans.
 *
 */
public class BeanCodec extends AbstractCodec
{
	protected static final int INTROSPECTOR_CACHE_SIZE = 5000;
	
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(INTROSPECTOR_CACHE_SIZE);
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return true;
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
		Object bean = null;
		boolean isanonclass = context.readBoolean();
		if(isanonclass)
		{
			String correctcl = context.readString();
			
			Classname cl = getAnonClassName(clazz);
			if (cl == null || (!correctcl.equals(cl.value())))
			{
				clazz = findCorrectInnerClass(0, SReflect.getClassName(clazz), correctcl, context.getClassloader());
			}
			
		}
		
		if (clazz != null)
		{
			try
			{
				bean = SCloner.createBeanObject(intro, clazz);
			}
			catch (Exception e)
			{
				context.getErrorReporter().exceptionOccurred(SUtil.convertToRuntimeException(e));
			}
		}
		else
		{
			context.getErrorReporter().exceptionOccurred(new ClassNotFoundException("Class not found: " + context.getCurrentClassName()));
		}
		
		return bean;
	}
	
	/**
	 *  Decodes and adds sub-objects during decoding.
	 *  
	 *  @param object The instantiated object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The finished object.
	 */
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		if(object != null)
		{
			readBeanProperties(object, clazz, context, intro);
//			Map props = intro.getBeanProperties(clazz, true, false);
//			int size = (int) context.readVarInt();
//			for (int i = 0; i < size; ++i)
//			{
//				String name = context.readString();
//				Object val = null;
//				val = BinarySerializer.decodeObject(context);
//				if (object != null)
//				{
//					try
//					{
//						((BeanProperty) props.get(name)).setPropertyValue(object, val);
//					}
//					catch (Exception e)
//					{
//						context.getErrorReporter().exceptionOccurred(e);
//					}
//				}
//			}
		}
		else
		{
			// Object failed to instantiate, skip sub-objects.
			int size = (int) context.readVarInt();
			for (int i = 0; i < size; ++i)
			{
				context.readString();
				SBinarySerializer.decodeObject(context);
			}
		}
		
		return object;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		return object != null;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		if (!ec.getNonInnerClassCache().contains(clazz))
		{
			if (clazz != null && clazz.isAnonymousClass())
			{
				// Flag class is inner class.
				ec.writeBoolean(true);
				
				Classname cn = getAnonClassName(clazz);
				
				if(cn == null)
				{
					String msg = String.valueOf(clazz);
					msg += " methods: ";
					for(Method m : clazz.getDeclaredMethods())
						msg += " "+m.getName();
					msg += " fields: ";
					for(Field f : clazz.getDeclaredFields())
						msg += " "+f.getName();
					throw new RuntimeException("Anonymous Class without Classname identifier not supported: " + msg);
				}
				
				ec.writeString(cn.value());
			}
			else
			{
				ec.writeBoolean(false);
				ec.getNonInnerClassCache().add(clazz);
			}
		}
		else
		{
			ec.writeBoolean(false);
		}
		
		writeBeanProperties(object, clazz, preprocessors, processors, traverser, mode, ec, intro);
		
		return object;
	}
	
	/**
	 * 
	 */
	public static void writeBeanProperties(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, Traverser traverser, MODE mode, IEncodingContext ec, IBeanIntrospector intro)
	{
		Map<String, BeanProperty> props = intro.getBeanProperties(clazz, true, false);
		
		List<String> names = new ArrayList<>();
		List<Object> values = new ArrayList<>();
		List<Class<?>> clazzes = new ArrayList<>();
		for(Map.Entry<String, BeanProperty> entry : props.entrySet())
		{
			BeanProperty prop = entry.getValue();
			if(prop!=null && prop.isReadable())
			{
				Object val = prop.getPropertyValue(object);
				if(val != null)
				{
					names.add(prop.getName());
					clazzes.add(prop.getType());
					values.add(val);
				}
			}
		}
//		for(Iterator it=props.keySet().iterator(); it.hasNext(); )
//		{
//			BeanProperty prop = (BeanProperty)props.get(it.next());
//			if(prop!=null && prop.isReadable())
//			{
//				Object val = prop.getPropertyValue(object);
//				if(val != null)
//				{
//					names.add(prop.getName());
//					clazzes.add(prop.getType());
//					values.add(val);
//				}
//			}
//		}
		ec.writeVarInt(names.size());
		
		for(int i = 0; i < names.size(); ++i)
		{
			ec.writeString(names.get(i));
			Object val = values.get(i);
			traverser.doTraverse(val, clazzes.get(i), preprocessors, processors, mode, ec.getClassLoader(), ec);
		}
	}
	
	/**
	 * 
	 */
	public static void readBeanProperties(Object object, Class clazz, IDecodingContext context, IBeanIntrospector intro)
	{
		Map props = intro.getBeanProperties(clazz, true, false);
		int size = (int) context.readVarInt();
		for (int i = 0; i < size; ++i)
		{
			String name = context.readString();
			Object val = null;
			val = SBinarySerializer.decodeObject(context);
			if(val!=null)
			{
				try
				{
					BeanProperty	prop	= (BeanProperty)props.get(name);
					if(prop!=null && prop.isWritable())
					{
						prop.setPropertyValue(object, val);
					}
					// else ignore
					
//					else if(prop!=null)
//					{
//						throw new RuntimeException("Property is write-protected: " + prop.getName());
//					}
				}
				catch (Exception e)
				{
					context.getErrorReporter().exceptionOccurred(e);
				}
			}
		}
	}
	
	/**
	 *  Bean object use fixed framing since they tend to be large.
	 */
//	protected boolean isFixedFrame()
//	{
//		return false;
//	}
	
	/**
	 *  Attempts to find the correct inner class (compilers have different ways enumerating anonymous inner classes).
	 *  
	 * 	@param level Enclosement level being searched, 0 being the level of the target class.
	 * 	@param startname The name as originally encoded.
	 * 	@param annotatedname Annotation marker for the correct class.
	 * 	@param classloader The classloader.
	 * 	@return The targeted inner class or null if not found.
	 */
	private static final Class findCorrectInnerClass(int level, String startname, String annotatedname, ClassLoader classloader)
	{
		int marker = 0;
		String basename = startname;
		
		for (int i = -1; i < level; ++i)
		{
			marker = basename.lastIndexOf('$');
			if (marker == -1)
				return null;
			basename = basename.substring(0, marker);
		}
		basename += "$";
		int exclude = Integer.parseInt(startname.substring(marker + 1, startname.length()));
		Class ret = null;
		
		int classindex = 0;
		boolean searching = true;
		while (searching)
		{
			if (classindex != exclude)
			{
				String candidatename = basename + classindex;
				
				try
				{
					Class candclass = SReflect.findClass(candidatename, null, classloader);
					
					if (level == 0)
					{
						Classname candclassname = getAnonClassName(candclass);
						if (candclassname != null && annotatedname.equals(candclassname.value()))
						{
							ret = candclass;
							searching = false;
						}
					}
					else
					{
						ret = findCorrectInnerClass(level - 1, startname, annotatedname, classloader);
						if (ret != null)
							searching = false;
					}
				}
				catch (ClassNotFoundException e)
				{
					if (classindex != 0)
					{
						searching = false;
					}
				}
			}
			++classindex;
		}
		
		if (ret == null)
		{
			ret = findCorrectInnerClass(level + 1, startname, annotatedname, classloader);
		}
		
		return ret;
	}
	
	/**
	 *  Attempts to find the "Classname" annotation for an anonymous inner class.
	 *  @param clazz The class.
	 *  @return The identifier or null if none was found.
	 */
	private static final Classname getAnonClassName(Class clazz)
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
