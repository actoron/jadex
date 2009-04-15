package jadex.commons.xml;

import jadex.commons.SReflect;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *  Handler for reading XML into Java beans.
 */
public class BeanObjectHandler implements IObjectHandler
{
	//-------- methods --------

	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, Object context, ClassLoader classloader) throws Exception
	{
		Object ret = null;
		Class clazz = (Class)type;
		if(!BasicTypeConverter.isBuiltInType(clazz))
		{
			// Must have empty constructor.
			ret = clazz.newInstance();
		}
		return ret;
	}
	
	/**
	 *  Handle the attribute of an object.
	 *  @param object The object.
	 *  @param attrname The attribute name.
	 *  @param attrval The attribute value.
	 *  @param attrinfo The attribute info.
	 *  @param context The context.
	 */
	public void handleAttributeValue(Object object, String attrname, List attrpath, Object attrval, 
		Object attrinfo, Object context, ClassLoader classloader) throws Exception
	{
		String mname = attrinfo!=null? "set"+((String)attrinfo).substring(0,1).toUpperCase()+((String)attrinfo).substring(1)
			: "set"+attrname.substring(0,1).toUpperCase()+attrname.substring(1);
		
		Method[] ms = SReflect.getMethods(object.getClass(), mname);
		boolean set = false;
		for(int j=0; j<ms.length && !set; j++)
		{
			Class[] ps = ms[j].getParameterTypes();
			if(ps.length==1)
			{
				Object arg;
				if(attrval instanceof String && !String.class.isAssignableFrom(ps[0]))
					arg = BasicTypeConverter.convertType(ps[0], (String)attrval);
				else
					arg = attrval;
				
				try
				{
					ms[j].invoke(object, new Object[]{arg});
					set = true;
				}
				catch(Exception e)
				{
				}
			}
		}
		
		if(!set)
			throw new RuntimeException("Failure in setting attribute: "+attrname+" on object: "+object);
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, 
		String tagname, Object context, ClassLoader classloader) throws Exception
	{
		// Add object to its parent.
		boolean	linked	= false;
		List classes	= new LinkedList();
		classes.add(object.getClass());
		
		if(linkinfo!=null)
		{
			String setm = "set"+((String)linkinfo).substring(0,1).toUpperCase()+((String)linkinfo).substring(1);
			linked = internalLinkObjects(object.getClass(), setm, object, parent);
			if(!linked)
			{
				String addm = "add"+((String)linkinfo).substring(0,1).toUpperCase()+((String)linkinfo).substring(1);
				linked = internalLinkObjects(object.getClass(), addm, object, parent);
			}
			if(!linked)
				throw new RuntimeException("Failure in link info: "+linkinfo);
		}
		
		while(!linked && !classes.isEmpty())
		{
			Class clazz = (Class)classes.remove(0);
			
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				String name = SReflect.getInnerClassName(clazz);
				linked = internalLinkObjects(clazz, "set"+name, object, parent);
				if(!linked)
					linked = internalLinkObjects(clazz, "add"+name, object, parent);
			}
			
			if(!linked)
			{
				String name = tagname.substring(0, 1).toUpperCase()+tagname.substring(1);
				linked = internalLinkObjects(clazz, "set"+name, object, parent);
				if(!linked)
					linked = internalLinkObjects(clazz, "add"+name, object, parent);
			}
			
			if(!linked)
			{
				if(clazz.getSuperclass()!=null)
					classes.add(clazz.getSuperclass());
				Class[]	ifs	= clazz.getInterfaces();
				for(int i=0; i<ifs.length; i++)
				{
					classes.add(ifs[i]);
				}
			}
		}
		
		if(!linked)
			throw new RuntimeException("Could not link: "+object+" "+parent);
	}
		
	/**
	 * Internal link objects method.
	 */
	protected boolean internalLinkObjects(Class clazz, String name, Object object, Object parent) throws Exception
	{
		boolean ret = false;
		
		Method[] ms = SReflect.getMethods(parent.getClass(), name);
		for(int i=0; !ret && i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1)
			{
				if(ps[0].isAssignableFrom(clazz))
				{
					try
					{
						ms[i].invoke(parent, new Object[]{object});
						ret	= true;
					}
					catch(Exception e)
					{
					}
				}
				else if(BasicTypeConverter.isBuiltInType(ps[0]) && object instanceof String)
				{
					try
					{
						object = BasicTypeConverter.convertType(ps[0], (String)object);
						ms[i].invoke(parent, new Object[]{object});
						ret	= true;
					}
					catch(Exception e)
					{
					}
				}
			}
		}
		
		return ret;
	}
}
