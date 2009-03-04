package jadex.commons.xml;

import jadex.commons.SReflect;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

/**
 *  Handler for reading XML into Java beans.
 */
public class BeanObjectHandler implements IObjectHandler
{
	/** The debug flag. */
	public static boolean DEBUG = false;
	
	//-------- attributes --------
	
	/** The type mappings. */
	protected Map types;
	
	/** The comment method name used for setting comments. */
	protected String comname;
	
	//-------- constructors --------

	/**
	 *  Create a new bean object handler.
	 */
	public BeanObjectHandler(Map types, String comname)
	{
		this.types = types;
		this.comname = comname;
	}
	
	//-------- methods --------
	
	/**
	 *  Create an object for the current tag.
	 *  @param parser The parser.
	 *  @param comment The preceding xml comment.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(XMLStreamReader parser, String comment, Object context, List stack) throws Exception
	{
		Object ret = null;
		
		Class clazz = (Class)types.get(parser.getLocalName());
		if(clazz!=null)
		{
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				// Must have empty constructor.
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
							Object arg = BasicTypeConverter.convertBuiltInTypes(ps[0], attrval);
							ms[j].invoke(ret, new Object[]{arg});
						}
					}
				}
				
				// If comment method name is set, set the comment.
				if(comname!=null)
				{
					Method m = SReflect.getMethod(clazz, comname, new Class[]{String.class});
					if(m!=null)
						m.invoke(ret, new Object[]{comment});
				}
			}
		}
		else
		{
			if(DEBUG)
				System.out.println("No mapping found: "+parser.getLocalName());
		}
		
		return ret;
	}
	
	/**
	 *  Handle content for an object.
	 *  @param parser The parser.
	 *  @param comment The comment.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public void handleContent(XMLStreamReader parser, Object elem, String content, Object context, List stack) throws Exception
	{
		throw new UnsupportedOperationException("Content not yet supported.");
	}

	/**
	 *  Link an object to its parent.
	 *  @param parser The parser.
	 *  @param elem The element.
	 *  @param paranet The parent element.
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent, Object context, List stack) throws Exception
	{
		// Add object to its parent.
		boolean	linked	= false;
		List classes	= new LinkedList();
		classes.add(elem.getClass());
		
		while(!linked && !classes.isEmpty())
		{
			Class clazz = (Class)classes.remove(0);
			
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				String name = SReflect.getInnerClassName(clazz);
				linked = internalLinkObjects(clazz, name, elem, parent);
			}
			
			if(!linked)
			{
				String name = parser.getLocalName().substring(0, 1).toUpperCase()+parser.getLocalName().substring(1);
				linked = internalLinkObjects(clazz, name, elem, parent);
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
	}
	
	/**
	 * Internal link objects method.
	 */
	protected boolean internalLinkObjects(Class clazz, String name, Object elem, Object parent) throws Exception
	{
		boolean ret = false;
		
		String mname = "set"+name;
		Method[] ms = SReflect.getMethods(parent.getClass(), mname);
		for(int i=0; !ret && i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1 && ps[0].isAssignableFrom(clazz))
			{
				ms[i].invoke(parent, new Object[]{elem});
				ret	= true;
			}
		}
		
		if(!ret)
		{
			mname = "add"+name;
			ms = SReflect.getMethods(parent.getClass(), mname);
			for(int i=0; !ret && i<ms.length; i++)
			{
				Class[] ps = ms[i].getParameterTypes();
				if(ps.length==1 && ps[0].isAssignableFrom(clazz))
				{
					ms[i].invoke(parent, new Object[]{elem});
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
}
