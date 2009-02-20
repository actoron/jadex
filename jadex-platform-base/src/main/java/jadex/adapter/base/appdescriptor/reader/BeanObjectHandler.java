package jadex.adapter.base.appdescriptor.reader;

import jadex.commons.SReflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamReader;

/**
 * 
 */
public class BeanObjectHandler implements IObjectHandler
{
	/** The type mappings. */
	protected Map types;
	
	/** The built-in types. */
	protected static Set builtintypes;
	
	static
	{
		builtintypes = new HashSet();
		builtintypes.add(String.class);
		builtintypes.add(int.class);
		builtintypes.add(Integer.class);
		builtintypes.add(long.class);
		builtintypes.add(Long.class);
		builtintypes.add(float.class);
		builtintypes.add(Float.class);
		builtintypes.add(double.class);
		builtintypes.add(Double.class);
		builtintypes.add(boolean.class);
		builtintypes.add(Boolean.class);
		builtintypes.add(short.class);
		builtintypes.add(Short.class);
		builtintypes.add(byte.class);
		builtintypes.add(Byte.class);
		builtintypes.add(char.class);
		builtintypes.add(Character.class);
	}

	/**
	 * 
	 */
	public BeanObjectHandler(Map types)
	{
		this.types = types;
	}
	
	/**
	 * 
	 */
	public Object createObject(XMLStreamReader parser) throws Exception
	{
		Object ret = null;
		
		Class clazz = (Class)types.get(parser.getLocalName());
		if(clazz!=null)
		{
			if(isBuiltInType(clazz))
			{
				ret = convertBuiltInTypes(clazz, parser.getElementText());
			}
			else
			{
				ret = clazz.newInstance();
				
				// Handle attributes
				for(int i=0; i<parser.getAttributeCount(); i++)
				{
					String attrname = parser.getAttributeLocalName(i);
					String attrval = parser.getAttributeValue(i);
					
					String mname = "set"+attrname.substring(0,1).toUpperCase()+attrname.substring(1);
					Method[] ms = SReflect.getMethods(clazz, mname);
					for(int j=0; j<ms.length; j++)
					{
						Class[] ps = ms[j].getParameterTypes();
						if(ps.length==1)
						{
							Object arg = convertBuiltInTypes(ps[0], attrval);
							ms[j].invoke(ret, new Object[]{arg});
						}
					}
				}
			}
		}
		else
		{
			System.out.println("No mapping found: "+parser.getLocalName());
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent) throws Exception
	{
		// Add object to its parent.
		String clname = isBuiltInType(elem.getClass())?
			parser.getLocalName().substring(0, 1).toUpperCase()+parser.getLocalName().substring(1)
			:SReflect.getInnerClassName(elem.getClass());
		
		String mname = "set"+clname;
		Method[] ms = SReflect.getMethods(parent.getClass(), mname);
		for(int i=0; i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1 && ps[0].isAssignableFrom(elem.getClass()))
			{
				ms[i].invoke(parent, new Object[]{elem});
			}
		}
		
		mname = "add"+clname;
		ms = SReflect.getMethods(parent.getClass(), mname);
		for(int i=0; i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1 && ps[0].isAssignableFrom(elem.getClass()))
			{
				ms[i].invoke(parent, new Object[]{elem});
			}
		}
	}
	
	/**
	 * 
	 */
	protected Object convertBuiltInTypes(Class clazz, String val)
	{
		Object ret;
		
		if(clazz.equals(String.class))
		{
			ret = val;
		}
		else if(clazz.equals(int.class) || clazz.equals(Integer.class))
		{
			ret = new Integer(val);
		}
		else if(clazz.equals(long.class) || clazz.equals(Long.class))
		{
			ret = new Long(val);
		}
		else if(clazz.equals(float.class) || clazz.equals(Float.class))
		{
			ret = new Float(val);
		}
		else if(clazz.equals(double.class) || clazz.equals(Double.class))
		{
			ret = new Double(val);
		}
		else if(clazz.equals(boolean.class) || clazz.equals(Boolean.class))
		{
			ret = new Boolean(val);
		}
		else if(clazz.equals(short.class) || clazz.equals(Short.class))
		{
			ret = new Short(val);
		}
		else if(clazz.equals(byte.class) || clazz.equals(Byte.class))
		{
			ret = new Byte(val);
		}
		else if(clazz.equals(char.class) || clazz.equals(Character.class))
		{
			ret = new Character(val.charAt(0)); // ?
		}
		else
		{
			throw new RuntimeException("Unknown argument type: "+clazz);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected boolean isBuiltInType(Class clazz)
	{
		return builtintypes.contains(clazz);
	}
}
