package jadex.extension.envsupport.observer.gui;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import jadex.commons.IPropertyObject;
import jadex.commons.IValueFetcher;
import jadex.commons.beans.BeanInfo;
import jadex.commons.beans.IntrospectionException;
import jadex.commons.beans.Introspector;
import jadex.commons.beans.PropertyDescriptor;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.javaparser.IParsedExpression;

/**
 * A convenience class for retrieving properties from objects.
 */
public class SObjectInspector
{
	/**
	 * Retrieves the id of an object.
	 * @param obj the object being inspected
	 * @return the id
	 */
	public static Object getId(Object obj)
	{
		if (obj instanceof ISpaceObject)
		{
			return ((ISpaceObject) obj).getId();
		}
		Object ret;
		ret = getProperty(obj, "id");
		if (ret == null)
		{
			ret = getProperty(obj, "name");
		}
		if (ret == null)
		{
			ret = obj.toString();
		}
		return ret;
	}
	
	/**
	 * Retrieves the type of an object.
	 * @param obj the object being inspected
	 * @return the type
	 */
	public static Object getType(Object obj)
	{
		if (obj instanceof ISpaceObject)
		{
			return ((ISpaceObject) obj).getType();
		}
		Object ret;
		ret = getProperty(obj, "type");
		if (ret == null)
		{
			ret = obj.getClass().getName();
		}
		return ret;
	}
	
	/**
	 * Retrieves the names of all properties of an Object.
	 * @param obj the object being inspected
	 * @return the property names
	 */
	public static Set getPropertyNames(Object obj)
	{
		if (obj instanceof IPropertyObject)
		{
			return ((IPropertyObject)obj).getPropertyNames(); 
		}
		
		HashSet ret = new HashSet();
		try
		{
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] descs = info.getPropertyDescriptors();
			for (int i = 0; i < descs.length; ++i)
			{
				ret.add(descs[i].getName());
			}
		}
		catch (IntrospectionException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		return ret;
	}
	
	/**
	 * Retrieves a property from an IPropertyObject.
	 * @param obj the object being inspected
	 * @param name name of the property
	 * @return the property
	 */
	public static Object getProperty(Object obj, String name)
	{
		return getProperty(obj, name, "$object");
	}
	
	/**
	 * Retrieves a property from an IPropertyObject.
	 * @param obj the object being inspected
	 * @param name name of the property
	 * @return the property
	 */
	public static Object getProperty(Object obj, String name, String objname)
	{
		return getProperty(obj, name, objname, null);
	}
	
	/**
	 * Retrieves a property from an IPropertyObject.
	 * @param obj the object being inspected
	 * @param name name of the property
	 * @return the property
	 */
	public static Object getProperty(Object obj, String name, String objname, IValueFetcher fetcher)
	{
		Object ret = null;
		if(obj instanceof IPropertyObject)
		{
			ret = ((IPropertyObject)obj).getProperty(name); 
		}
		
		if(ret == null)
		{
			try
			{
				BeanInfo info = Introspector.getBeanInfo(obj.getClass());
				PropertyDescriptor[] descs = info.getPropertyDescriptors();
				for (int i = 0; i < descs.length; ++i)
				{
					if (descs[i].getName().equals(name))
					{
						Method getter = descs[i].getReadMethod();
						return getter.invoke(obj, (Object[]) null);
					}
				}
			}
			catch(Exception e)
			{
			}
		}
		
		// Hack. 
//		if(ret==null)
//		{
//			try
//			{
//				ret = SJavaParser.parseExpression(name, null, null);
//			}
//			catch(Exception e)
//			{
//			}
//		}
		
		if(ret instanceof IParsedExpression)
		{
//			SimpleValueFetcher fetcher = new SimpleValueFetcher();
//			fetcher.setValue(objname, obj);
//			if(prevals!=null)
//			{
//				for(Iterator it=prevals.keySet().iterator(); it.hasNext(); )
//				{
//					String valname = (String)it.next();
//					fetcher.setValue(valname, prevals.get(valname));
//				}
//			}
			ret = ((IParsedExpression)ret).getValue(fetcher);
		}
		
		return ret;
		
	}
	
	/**
	 * Returns the given property if the target class is met or looks it up on the property object.
	 * 
	 * @param obj the object being inspected
	 * @param prop the property or the name of the property
	 * @param clazz target class
	 * @return the property
	 * /
	public static final Object getPropertyAsClass(Object obj, Object prop, Class clazz)
	{
		if (!clazz.isInstance(prop))
		{
			prop = getProperty(obj, (String) prop);
		}
		return prop;
	}*/
	
	/**
	 * Retrieves a 1-vector given an object and either a string-based binding or
	 * the vector itself.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 1-vector
	 * /
	public static IVector1 getVector1(Object obj, Object vecId)
	{
		IVector1 vector1;
		if (vecId instanceof IVector1)
		{
			vector1 = (IVector1) vecId;
		}
		else
		{
			vector1 = (IVector1) SObjectInspector.getProperty(obj, (String) vecId);
		}
		return vector1;
	}*/
	
	/**
	 * Retrieves a 1-vector given an object and either a string-based binding or
	 * the vector itself. If the vector is a 2-vector, it is converted to a 1-vector
	 * by retrieving its direction.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 1-vector
	 * /
	public static IVector1 getVector1AsDirection(Object obj, Object vecId)
	{
		Object vector1;
		if (vecId instanceof String)
		{
			vector1 = SObjectInspector.getProperty(obj, (String) vecId);
		}
		else
		{
			vector1 = vecId;
		}
		
		if (vector1 instanceof IVector2)
		{
			vector1 = ((IVector2) vector1).getDirection();
		}
		
		return (IVector1) vector1;
	}*/
	
	/**
	 * Retrieves a 1-vector given an object and either a string-based binding or
	 * the vector itself. If the vector is a 2-vector, it is converted to a 1-vector
	 * by retrieving the length of the vector.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 1-vector
	 * /
	public static IVector1 getVector1AsLength(Object obj, Object vecId)
	{
		Object vector1;
		if (vecId instanceof String)
		{
			vector1 = SObjectInspector.getProperty(obj, (String) vecId);
		}
		else
		{
			vector1 = vecId;
		}
		
		if (vector1 instanceof IVector2)
		{
			vector1 = ((IVector2) vector1).getLength();
		}
		
		return (IVector1) vector1;
	}*/
	
	/**
	 * Retrieves a 2-vector given an object and either a string-based binding or
	 * the vector itself.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 2-vector
	 * /
	public static IVector2 getVector2(Object obj, Object vecId)
	{
		IVector2 vector2;
		if(vecId instanceof IVector2)
		{
			vector2 = (IVector2)vecId;
		}
		else
		{
			vector2 = (IVector2)SObjectInspector.getProperty(obj, (String)vecId);
		}
		return vector2;
	}*/
	
	/**
	 * Retrieves a 3-vector given an object and either a string-based binding or
	 * the vector itself.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 3-vector
	 * /
	public static IVector3 getVector3(Object obj, Object vecId)
	{
		IVector3 vector3=null;
		if (vecId instanceof IVector3)
		{
			vector3 = (IVector3)vecId;
		}
		else
		{
			try
			{
				vector3 = (IVector3)SObjectInspector.getProperty(obj, (String) vecId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return vector3;
	}*/

}
