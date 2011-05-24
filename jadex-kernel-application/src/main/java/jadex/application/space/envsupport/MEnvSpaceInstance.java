package jadex.application.space.envsupport;

import jadex.application.space.ISpace;
import jadex.application.space.MSpaceInstance;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;
import jadex.commons.IPropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Java representation of environment space instance for xml description.
 */
public class MEnvSpaceInstance extends MSpaceInstance
{
	//-------- attributes --------
	
	/** The properties. */
	protected Map properties;
	
	
	public MEnvSpaceInstance()
	{
		// TODO Auto-generated constructor stub
	}
	//-------- methods --------
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addProperty(String key, Object value)
	{
//		System.out.println("addP: "+key+" "+value);
		if(properties==null)
			properties = new MultiCollection();
		properties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getPropertyList(String key)
	{
		return properties!=null? (List)properties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}

	/**
	 *  Get a property from a (multi)map.
	 *  @param map The map.
	 *  @param name The name.
	 *  @return The property.
	 */
	public static Object getProperty(Map map, String name)
	{
		try
		{
		Object tmp = map.get(name);
		return (tmp instanceof List)? ((List)tmp).get(0): tmp; 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get a property from a (multi)map.
	 *  @param map The map.
	 *  @param name The name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		return properties!=null? getProperty(properties, name): null;
	}
	
	/**
	 *  Set properties on a IPropertyObject.
	 *  @param object The IPropertyObject.
	 *  @param properties A list properties (containing maps with "name", "value" keys).
	 *  @param fetcher The fetcher for parsing the Java expression (can provide
	 *  predefined values to the expression)
	 */
	public static void setProperties(IPropertyObject object, List properties, IValueFetcher fetcher)
	{
		if(properties!=null)
		{
			for(int i=0; i<properties.size(); i++)
			{
				Map prop = (Map)properties.get(i);
				IParsedExpression exp = (IParsedExpression)prop.get("value");
				boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
				if(dyn)
					object.setProperty((String)prop.get("name"), exp);
				else
					object.setProperty((String)prop.get("name"), exp==null? null: exp.getValue(fetcher));
			}
		}
	}
	
	/**
	 *  Set properties on a map.
	 *  @param properties A list properties (containing maps with "name", "value" keys).
	 *  @param fetcher The fetcher for parsing the Java expression (can provide
	 *  predefined values to the expression)
	 */
	public static Map convertProperties(List properties, IValueFetcher fetcher)
	{
		HashMap ret = null;
		if(properties!=null)
		{
			ret = new HashMap();
			for(int i=0; i<properties.size(); i++)
			{
				Map prop = (Map)properties.get(i);
				IParsedExpression exp = (IParsedExpression)prop.get("value");
				boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
				if(dyn)
					ret.put((String)prop.get("name"), exp);
				else
					ret.put((String)prop.get("name"), exp==null? null: exp.getValue(fetcher));
			}
		}
		return ret;
	}

	public Class getClazz()
	{		
		return (Class)MEnvSpaceInstance.getProperty(((MEnvSpaceType)getType()).getProperties(), "clazz");
	}
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture init(IInternalAccess ia, IValueFetcher fetcher)
	{
		Future ret = new Future();
		
//		System.out.println("init space: "+ia);
		
		try
		{
			final ISpace space = (ISpace)getClazz().newInstance();
			space.initSpace(ia, this, fetcher);
			
			ia.addComponentListener(new IComponentListener()
			{
				IFilter filter = new IFilter()
				{
					public boolean filter(Object obj)
					{
						IComponentChangeEvent event = (IComponentChangeEvent)obj;
						return event.getSourceCategory().equals(StatelessAbstractInterpreter.TYPE_COMPONENT);
					}
				};
				public IFilter getFilter()
				{
					return filter;
				}
				
				public IFuture eventOccured(IComponentChangeEvent cce)
				{
					if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_CREATION))
					{
//						System.out.println("add: "+cce.getDetails());
						space.componentAdded((IComponentDescription)cce.getDetails());
					}
					else if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_DISPOSAL))
					{
//						System.out.println("rem: "+cce.getComponent());
						space.componentRemoved((IComponentDescription)cce.getDetails());
					}
					return IFuture.DONE;
				}
			});
			
			ret.setResult(new Object[]{getName(), space});
		}
		catch(Exception e)
		{
			System.out.println("Exception while creating space: "+getName());
			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Get a string representation of this AGR space instance.
	 *  @return A string representation of this AGR space instance.
	 * /
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(type=");
		sbuf.append(getType());
		if(objects!=null)
		{
			sbuf.append(", objects=");
			sbuf.append(objects);
		}
		sbuf.append(")");
		return sbuf.toString();
	}*/
}
