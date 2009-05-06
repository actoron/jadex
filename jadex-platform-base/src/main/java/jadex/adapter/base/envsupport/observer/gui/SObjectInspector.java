package jadex.adapter.base.envsupport.observer.gui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.IPropertyObject;

/**
 * A convenience class for retrieving properties from objects.
 */
public class SObjectInspector
{
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
	 * Retrieves all properties of an Object.
	 * @param obj the object being inspected
	 * @return map the properties
	 */
	public static Map getProperties(Object obj)
	{
		if (obj instanceof IPropertyObject)
		{
			return ((IPropertyObject) obj).getProperties(); 
		}
		
		HashMap ret = new HashMap();
		try
		{
			BeanInfo info = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] descs = info.getPropertyDescriptors();
			for (int i = 0; i < descs.length; ++i)
			{
				String name = descs[i].getName();
				Method getter = descs[i].getReadMethod();
				Object val = getter.invoke(obj, (Object[]) null);
				ret.put(name, val);
			}
		}
		catch (IntrospectionException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (InvocationTargetException e)
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
		if (obj instanceof IPropertyObject)
		{
			return ((IPropertyObject) obj).getProperty(name); 
		}
		
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
		catch (IntrospectionException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (InvocationTargetException e)
		{
		}
		return null;
	}
	
	/**
	 * Retrieves a 1-vector given an object and either a string-based binding or
	 * the vector itself.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 1-vector
	 */
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
	}
	
	/**
	 * Retrieves a 1-vector given an object and either a string-based binding or
	 * the vector itself. If the vector is a 2-vector, it is converted to a 1-vector
	 * by retrieving its direction.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 1-vector
	 */
	public static IVector1 getVector1asDirection(Object obj, Object vecId)
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
	}
	
	/**
	 * Retrieves a 2-vector given an object and either a string-based binding or
	 * the vector itself.
	 * 
	 * @param obj the object
	 * @param vecId either the vector or a property name
	 * @return retrieved 2-vector
	 */
	public static IVector2 getVector2(Object obj, Object vecId)
	{
		IVector2 vector2;
		if (vecId instanceof IVector2)
		{
			vector2 = (IVector2) vecId;
		}
		else
		{
			vector2 = (IVector2) SObjectInspector.getProperty(obj, (String) vecId);
		}
		return vector2;
	}

}
