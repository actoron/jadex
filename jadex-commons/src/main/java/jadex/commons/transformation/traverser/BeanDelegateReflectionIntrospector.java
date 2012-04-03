package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.DuplicateMemberException;


/**
 * Introspector for Java beans. It uses the reflection to build up a map with
 * property infos (name, read/write method, etc.)
 */
public class BeanDelegateReflectionIntrospector implements IBeanIntrospector, IBeanDelegateProvider
{
	// -------- attributes --------

	/** The cache for saving time for multiple lookups. */
	protected LRU	beaninfos;
	
	/** Accessor delegate cache. */
	protected Map<Class, IBeanAccessorDelegate> delegates;
	//protected LRU	delegates;

	// -------- constructors --------

	/**
	 * Create a new introspector.
	 */
	public BeanDelegateReflectionIntrospector()
	{
		this(200);
	}

	/**
	 * Create a new introspector.
	 */
	public BeanDelegateReflectionIntrospector(int lrusize)
	{
		this.beaninfos = new LRU(lrusize);
		this.delegates = new WeakHashMap<Class, IBeanAccessorDelegate>();
	}

	// -------- methods --------

	/**
	 * Get the bean properties for a specific clazz.
	 */
	public Map getBeanProperties(Class clazz, boolean includefields)
	{
		// includefields component of key is call based to avoid reflection calls during cache hits.
		Tuple2<Class, Boolean> beaninfokey = new Tuple2<Class, Boolean>(clazz, includefields);
		Map ret = (Map)beaninfos.get(beaninfokey);
		
		if(ret == null)
		{
			if (clazz.getAnnotation(IncludeFields.class) != null)
				includefields = true;
			try
			{
				Field incfield = clazz.getField("INCLUDE_FIELDS");
				if (incfield.getBoolean(null))
					includefields = true;
			}
			catch (Exception e)
			{
			}
			
			Method[] ms = clazz.getMethods();
			HashMap getters = new HashMap();
			ArrayList setters = new ArrayList();
			for(int i = 0; i < ms.length; i++)
			{
				String method_name = ms[i].getName();
				if((method_name.startsWith("is") || method_name.startsWith("get"))
					&& ms[i].getParameterTypes().length == 0)
				{
					getters.put(method_name, ms[i]);
				}
				else if(method_name.startsWith("set")
					&& ms[i].getParameterTypes().length == 1)
				{
					setters.add(ms[i]);
				}
			}

			ret = new HashMap();
			Iterator it = setters.iterator();

			while(it.hasNext())
			{
				Method setter = (Method)it.next();
				String setter_name = setter.getName();
				String property_name = setter_name.substring(3);
				Method getter = (Method)getters.get("get" + property_name);
				if(getter == null)
					getter = (Method)getters.get("is" + property_name);

				if(getter != null)
				{
					Class[] setter_param_type = setter.getParameterTypes();
					String property_java_name = Character.toLowerCase(property_name.charAt(0))
						+ property_name.substring(1);
					ret.put(property_java_name, new BeanProperty(property_java_name, 
						getter.getReturnType(), getter, setter, setter_param_type[0], this));
				}
			}

			// Get all public fields.
			Field[] fields = clazz.getFields();
			for(int i = 0; i < fields.length; i++)
			{
				String property_java_name = fields[i].getName();
				if((includefields || fields[i].getAnnotation(Include.class) != null) && !ret.containsKey(property_java_name))
				{
					ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
				}
			}
			
			/*if(includefields)
			{
				Field[] fields = clazz.getFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(!ret.containsKey(property_java_name))
					{
						ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
					}
				}
			}*/
//			else
//			{
//				Field[] fields = clazz.getFields();
//				for(int i = 0; i < fields.length; i++)
//				{
//					String property_java_name = fields[i].getName();
//					if(!ret.containsKey(property_java_name))
//					{
//						ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
//					}
//				}
//			}

			// Get final values (val$xyz fields) for anonymous classes.
			if(clazz.isAnonymousClass())
			{
				fields = clazz.getDeclaredFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(property_java_name.startsWith("val$"))
					{
						property_java_name = property_java_name.substring(4);
						if(!ret.containsKey(property_java_name))
						{
							ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
						}
					}
				}
			}

			beaninfos.put(beaninfokey, ret);
		}

		return ret;
	}
	
	/*public IBeanAccessorDelegate getDelegate(Class clazz)
	{
		MappedBeanAccessorDelegate ret = (MappedBeanAccessorDelegate) delegates.get(clazz);
		
		if (ret == null)
		{
			ret = new MappedBeanAccessorDelegate();
			Map properties = getBeanProperties(clazz, true);
			ClassLoader cl = clazz.getClassLoader();
			ClassPool pool = new ClassPool(null);
			pool.appendSystemPath();
			pool.insertClassPath(new ClassClassPath(clazz));
			for (Object entry : properties.entrySet())
			{
				try
				{
					String propname = (String) ((Map.Entry) entry).getKey();
					BeanProperty prop = (BeanProperty) ((Map.Entry) entry).getValue();
					
					CtClass gcclazz = pool.makeClass(this.getClass().getPackage().getName() + "." +
							clazz + "AccessorDelegate" + propname + "Getter");
					CtClass gcinterface = pool.get(IResultCommand.class.getName());
					gcclazz.addInterface(gcinterface);
					
					StringBuilder methodsrc = new StringBuilder();
					methodsrc.append("public Object execute(Object args) { return ");
					if (prop.getGetter() != null)
					{
						if (prop.getType().isPrimitive())
						{
							methodsrc.append("new ");
							methodsrc.append(SReflect.getWrappedType(prop.getType()).getCanonicalName());
							methodsrc.append("(");
						}
						methodsrc.append("((");
						methodsrc.append(clazz.getCanonicalName());
						methodsrc.append(") args).");
						methodsrc.append(prop.getGetter().getName());
						methodsrc.append("()");
						if (prop.getType().isPrimitive())
						{
							methodsrc.append(")");
						}
						methodsrc.append("; } ");
					}
					else
					{
						if (prop.getType().isPrimitive())
						{
							methodsrc.append("new ");
							methodsrc.append(SReflect.getWrappedType(prop.getType()).getCanonicalName());
							methodsrc.append("(");
						}
						methodsrc.append("((");
						methodsrc.append(clazz.getCanonicalName());
						methodsrc.append(") args).");
						methodsrc.append(prop.getField().getName());
						if (prop.getType().isPrimitive())
							methodsrc.append(")");
						methodsrc.append("; } ");
					}
					CtMethod gcmethod = CtMethod.make(methodsrc.toString(), gcclazz);
					gcclazz.addMethod(gcmethod);
					Class gcommandclass = gcclazz.toClass(cl, clazz.getProtectionDomain());
					ret.addGetCommand(propname, (IResultCommand) gcommandclass.newInstance());
					
					CtClass scclazz = pool.makeClass(this.getClass().getPackage().getName() + "." +
							clazz + "AccessorDelegate" + propname + "Setter");
					CtClass scinterface = pool.get(ICommand.class.getName());
					scclazz.addInterface(scinterface);
					
					methodsrc = new StringBuilder();
					methodsrc.append("public void execute(Object args) { Object object = ((Object[]) args)[0]; Object value = ((Object[]) args)[1]; ");
					if (prop.getSetter() != null)
					{
						// Method access
						methodsrc.append("((");
						methodsrc.append(clazz.getCanonicalName());
						methodsrc.append(") object).");
						methodsrc.append(prop.getSetter().getName());
						methodsrc.append("(");
						if (boolean.class.equals(prop.getType()))
						{
							methodsrc.append("((Boolean) value).booleanValue()");
						}
						else if (char.class.equals(prop.getType()))
						{
							methodsrc.append("((Character) value).charValue()");
						}
						else if (prop.getType().isPrimitive())
						{
							methodsrc.append("((java.lang.Number) value).");
							methodsrc.append(prop.getType().getSimpleName());
							methodsrc.append("Value()");
						}
						else
						{
							methodsrc.append("(");
							methodsrc.append(prop.getSetterType().getCanonicalName());
							methodsrc.append(") value");
						}
						methodsrc.append("); }");
					}
					else
					{
						// Field access
						methodsrc.append("((");
						methodsrc.append(clazz.getCanonicalName());
						methodsrc.append(") object).");
						methodsrc.append(prop.getField().getName());
						methodsrc.append("=");
						if (boolean.class.equals(prop.getType()))
						{
							methodsrc.append("((Boolean) value).booleanValue()");
						}
						else if (char.class.equals(prop.getType()))
						{
							methodsrc.append("((Character) value).charValue()");
						}
						else if (prop.getType().isPrimitive())
						{
							methodsrc.append("((java.lang.Number) value).");
							methodsrc.append(prop.getType().getSimpleName());
							methodsrc.append("Value()");
						}
						else
						{
							methodsrc.append("((");
							methodsrc.append(prop.getSetterType().getCanonicalName());
							methodsrc.append(") value);");
						}
						methodsrc.append("}");
					}
					
					CtMethod scmethod = CtMethod.make(methodsrc.toString(), scclazz);
					scclazz.addMethod(scmethod);
					Class scommandclass = scclazz.toClass(cl, clazz.getProtectionDomain());
					ret.addSetCommand(propname, (ICommand) scommandclass.newInstance());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			delegates.put(clazz, ret);
		}
		
		return ret;
	}*/
	
	/**
	 * 
	 */
	public IBeanAccessorDelegate getDelegate(Class clazz)
	{
		IBeanAccessorDelegate ret = delegates.get(clazz);
		if (ret == null)
		{
			Map properties = getBeanProperties(clazz, true);
			ClassLoader cl = clazz.getClassLoader();
			ClassPool pool = new ClassPool(null);
			pool.appendSystemPath();
			pool.insertClassPath(new ClassClassPath(clazz));
			try
			{
				String accname = clazz.getPackage().getName() + "." + clazz + "AccessorDelegate";
				CtClass dclazz = pool.makeClass(accname);
				CtClass dinterface = pool.get(IBeanAccessorDelegate.class.getName());
				dclazz.addInterface(dinterface);
				StringBuilder getmethodsrc = new StringBuilder();
				StringBuilder setmethodsrc = new StringBuilder();
				getmethodsrc.append("public final java.lang.Object getPropertyValue(Object object, String property) { ");
				setmethodsrc.append("public final void setPropertyValue(Object object, String property, Object value) { ");
				for (Object entry : properties.entrySet())
				{
					String propname = (String) ((Map.Entry) entry).getKey();
					BeanProperty prop = (BeanProperty) ((Map.Entry) entry).getValue();
					
					//Get
					getmethodsrc.append("if (\"");
					getmethodsrc.append(propname);
					getmethodsrc.append("\".equals(property)) { ");
					if (prop.getGetter() != null)
					{
						// Method access
						getmethodsrc.append("return ");
						if (prop.getType().isPrimitive())
						{
							//getmethodsrc.append("jadex.commons.SReflect.wrapValue(");
							getmethodsrc.append("new ");
							getmethodsrc.append(SReflect.getWrappedType(prop.getType()).getCanonicalName());
							getmethodsrc.append("(");
						}
						getmethodsrc.append("((");
						getmethodsrc.append(clazz.getCanonicalName());
						getmethodsrc.append(") object).");
						getmethodsrc.append(prop.getGetter().getName());
						getmethodsrc.append("()");
						if (prop.getType().isPrimitive())
						{
							getmethodsrc.append(")");
						}
						getmethodsrc.append("; } ");
					}
					else
					{
						// Field access
						getmethodsrc.append("return ");
						if (prop.getType().isPrimitive())
						{
							getmethodsrc.append("new ");
							getmethodsrc.append(SReflect.getWrappedType(prop.getType()).getCanonicalName());
							getmethodsrc.append("(");
						}
						getmethodsrc.append("((");
						getmethodsrc.append(clazz.getCanonicalName());
						getmethodsrc.append(") object).");
						getmethodsrc.append(prop.getField().getName());
						if (prop.getType().isPrimitive())
							getmethodsrc.append(")");
						getmethodsrc.append("; } ");
					}
					
					//Set
					setmethodsrc.append("if (\"");
					setmethodsrc.append(propname);
					setmethodsrc.append("\".equals(property)) { ");
					if (prop.getSetter() != null)
					{
						// Method access
						setmethodsrc.append("((");
						setmethodsrc.append(clazz.getCanonicalName());
						setmethodsrc.append(") object).");
						setmethodsrc.append(prop.getSetter().getName());
						setmethodsrc.append("(");
						if (boolean.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Boolean) value).booleanValue()");
						}
						else if (char.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Character) value).charValue()");
						}
						else if (prop.getType().isPrimitive())
						{
							setmethodsrc.append("((java.lang.Number) value).");
							setmethodsrc.append(prop.getType().getSimpleName());
							setmethodsrc.append("Value()");
						}
						else
						{
							setmethodsrc.append("(");
							setmethodsrc.append(prop.getSetterType().getCanonicalName());
							setmethodsrc.append(") value");
						}
						setmethodsrc.append("); return;");
					}
					else
					{
						// Field access
						setmethodsrc.append("((");
						setmethodsrc.append(clazz.getCanonicalName());
						setmethodsrc.append(") object).");
						setmethodsrc.append(prop.getField().getName());
						setmethodsrc.append(" = ");
						if (boolean.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Boolean) value).booleanValue()");
						}
						else if (char.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Character) value).charValue()");
						}
						else if (prop.getType().isPrimitive())
						{
							setmethodsrc.append("((java.lang.Number) value).");
							setmethodsrc.append(prop.getType().getSimpleName());
							setmethodsrc.append("Value()");
						}
						else
						{
							setmethodsrc.append("((");
							setmethodsrc.append(prop.getSetterType().getCanonicalName());
							setmethodsrc.append(") value); return;");
						}
					}
					setmethodsrc.append(" } ");
				}
				getmethodsrc.append("throw new RuntimeException(\"Bean property not found: \" + property); }");
				setmethodsrc.append("throw new RuntimeException(\"Bean property not found: \" + property); }");
				
				CtMethod getmethod = CtMethod.make(getmethodsrc.toString(), dclazz);
				dclazz.addMethod(getmethod);
				CtMethod setmethod = CtMethod.make(setmethodsrc.toString(), dclazz);
				dclazz.addMethod(setmethod);
				Class delegateclazz = null;
				try
				{
					 delegateclazz = dclazz.toClass(cl, clazz.getProtectionDomain());
				}
				catch (CannotCompileException e)
				{
					//e.printStackTrace();
					try
					{
						delegateclazz = SReflect.classForName(accname, clazz.getClassLoader());
					}
					catch(Exception e2)
					{
						throw new RuntimeException(e);
					}
				}
				ret = (IBeanAccessorDelegate) delegateclazz.newInstance();
				delegates.put(clazz, ret);
			}
			catch(Exception e)
			{
				System.out.println(delegates.toString());
				System.out.println(clazz);
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}