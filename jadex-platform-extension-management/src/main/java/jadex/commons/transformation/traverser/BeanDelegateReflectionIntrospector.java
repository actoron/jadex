package jadex.commons.transformation.traverser;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

import jadex.commons.SReflect;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


/**
 * Introspector for Java beans. It uses the reflection to build up a map with
 * property infos (name, read/write method, etc.)
 */
public class BeanDelegateReflectionIntrospector extends BeanReflectionIntrospector implements IBeanDelegateProvider
{
	// -------- attributes --------
	
	/** Accessor delegate cache. */
	protected Map<Class, IBeanAccessorDelegate> delegates;

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
		super(lrusize);
		this.delegates = new WeakHashMap<Class, IBeanAccessorDelegate>();
	}
	
	/**
	 *  Retrieves a bean access delegate for fast access to bean properties.
	 *  
	 *  @param clazz The bean class.
	 *  @return The bean access delegate.
	 */
	public IBeanAccessorDelegate getDelegate(Class clazz)
	{
		if(!delegates.containsKey(clazz))
		{
			IBeanAccessorDelegate	ret	= null;
			Map properties = getBeanProperties(clazz, true, true);
			ClassLoader cl = clazz.getClassLoader();
			ClassPool pool = new ClassPool(null);
			pool.appendSystemPath();
			pool.insertClassPath(new ClassClassPath(clazz));
			try
			{
//				String accname = clazz.getPackage().getName() + "." + clazz.getSimpleName() + "AccessorDelegate";
				String accname = SReflect.getClassName(clazz)+"AccessorDelegate";
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
					if (prop.isReadable())
					{
						// Method access
						getmethodsrc.append("return ");
						if (prop.getType().isPrimitive())
						{
							//getmethodsrc.append("jadex.commons.SReflect.wrapValue(");
							getmethodsrc.append("new ");
							getmethodsrc.append(SReflect.getClassName(SReflect.getWrappedType(prop.getType())));
							getmethodsrc.append("(");
						}
						getmethodsrc.append("((");
						getmethodsrc.append(SReflect.getClassName(clazz));
						getmethodsrc.append(") object).");
						getmethodsrc.append(prop.getter.getName());
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
							getmethodsrc.append(SReflect.getClassName(SReflect.getWrappedType(prop.getType())));
							getmethodsrc.append("(");
						}
						getmethodsrc.append("((");
						getmethodsrc.append(SReflect.getClassName(clazz));
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
					if (prop.isWritable())
					{
						// Method access
						setmethodsrc.append("((");
						setmethodsrc.append(SReflect.getClassName(clazz));
						setmethodsrc.append(") object).");
						setmethodsrc.append(prop.setter.getName());
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
							setmethodsrc.append(SReflect.getClassName(prop.getSetterType()));
							setmethodsrc.append(") value");
						}
						setmethodsrc.append("); return;");
					}
					else
					{
						// Field access
						setmethodsrc.append("((");
						setmethodsrc.append(SReflect.getClassName(clazz));
						setmethodsrc.append(") object).");
						setmethodsrc.append(prop.getField().getName());
						setmethodsrc.append(" = ");
						if (boolean.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Boolean) value).booleanValue()");
							setmethodsrc.append("; return;");
						}
						else if (char.class.equals(prop.getType()))
						{
							setmethodsrc.append("((Character) value).charValue()");
							setmethodsrc.append("; return;");
						}
						else if (prop.getType().isPrimitive())
						{
							setmethodsrc.append("((java.lang.Number) value).");
							setmethodsrc.append(prop.getType().getSimpleName());
							setmethodsrc.append("Value()");
							setmethodsrc.append("; return;");
						}
						else
						{
							setmethodsrc.append("((");
							setmethodsrc.append(SReflect.getClassName(prop.getSetterType()));
							setmethodsrc.append(") value); return;");
						}
					}
					setmethodsrc.append(" } ");
				}
				getmethodsrc.append("throw new RuntimeException(\"Bean property not found: \" + property); }");
				setmethodsrc.append("throw new RuntimeException(\"Bean property not found: \" + property); }");
//				System.out.println(setmethodsrc.toString());
				
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
//					e.printStackTrace();
//					try
//					{
						delegateclazz = SReflect.classForName0(accname, clazz.getClassLoader());
//					}
//					catch(Exception e2)
//					{
//						throw new RuntimeException(e2);
//					}
				}
				ret = delegateclazz!=null ? (IBeanAccessorDelegate)delegateclazz.newInstance() : null;
			}
			catch(RuntimeException e)
			{
//				throw e;
			}
			catch(Exception e)
			{
//				throw new RuntimeException(e);				
			}
			delegates.put(clazz, ret);
		}
		
		return delegates.get(clazz);		
	}
	
	/**
	 *  Creates a bean property based on a field.
	 * 
	 *  @param name Property name
	 *  @param field The field.
	 *  @return The bean property.
	 */
	protected BeanProperty createBeanProperty(String name, Field field, boolean anonclass)
	{
		return anonclass? super.createBeanProperty(name, field, anonclass): new BeanProperty(name, field, this);
	}
}