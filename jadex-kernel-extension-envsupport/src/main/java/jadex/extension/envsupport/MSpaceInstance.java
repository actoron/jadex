package jadex.extension.envsupport;

import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.collection.MultiCollection;
import jadex.extension.envsupport.environment.SynchronizedPropertyObject;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public abstract class MSpaceInstance extends SynchronizedPropertyObject implements IExtensionInstance
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The space type name. */
	protected String type;
	
	/** The space type (resolved during loading). */
	protected MSpaceType spacetype;
	
	/** The properties. */
	protected Map initproperties;
	
	/**
	 *  Create a new space instance.
	 */
	public MSpaceInstance()
	{
		super(null, new Object());
	}
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addInitProperty(String key, Object value)
	{
//		System.out.println("addP: "+key+" "+value);
		if(initproperties==null)
			initproperties = new MultiCollection();
		initproperties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getInitPropertyList(String key)
	{
		return initproperties!=null? (List)initproperties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getInitProperties()
	{
		return initproperties;
	}
	
	/**
	 *  Get a property from a (multi)map.
	 *  @param map The map.
	 *  @param name The name.
	 *  @return The property.
	 */
	public Object getInitProperty(String name)
	{
		return initproperties!=null? MEnvSpaceType.getProperty(initproperties, name): null;
	}
	
	/**
	 *  Get the type name.
	 *  @return The type name. 
	 */
	public String getTypeName()
	{
		return this.type;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the type of this element.
	 *  @return The structure type.
	 */
	public MSpaceType getType()
	{
		return spacetype;
	}

	/**
	 *  Set the type of this element.
	 *  @return The structure type.
	 */
	public void	setType(MSpaceType spacetype)
	{
		this.spacetype	= spacetype;
	}

//	public Class getClazz()
//	{		
//		return (Class)getProperty(((MEnvSpaceType)getType()).getProperties(), "clazz");
//	}
	
	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

//	/**
//	 *  Initialize the extension.
//	 *  Called once, when the extension is created.
//	 */
//	public IFuture init(IExternalAccess exta, IValueFetcher fetcher)
//	{
//		final Future ret = new Future();
//		
////		System.out.println("init space: "+ia);
//		
//		try
//		{
////			space = (ISpace)getClazz().newInstance();
//			initSpace(exta, fetcher);
//			
//			exta.scheduleStep(new IComponentStep()
//			{
//				public Object execute(IInternalAccess ia)
//				{
//					ia.addComponentListener(new IComponentListener()
//					{
//						IFilter filter = new IFilter()
//						{
//							public boolean filter(Object obj)
//							{
//								IComponentChangeEvent event = (IComponentChangeEvent)obj;
//								return event.getSourceCategory().equals(StatelessAbstractInterpreter.TYPE_COMPONENT);
//							}
//						};
//						public IFilter getFilter()
//						{
//							return filter;
//						}
//						
//						public IFuture eventOccured(IComponentChangeEvent cce)
//						{
//							if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_CREATION))
//							{
////								System.out.println("add: "+cce.getDetails());
//								componentAdded((IComponentDescription)cce.getDetails());
//							}
//							else if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_DISPOSAL))
//							{
////								System.out.println("rem: "+cce.getComponent());
//								componentRemoved((IComponentDescription)cce.getDetails());
//							}
//							return IFuture.DONE;
//						}
//					});
//					return null;
//				}
//			}).addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					ret.setResult(MSpaceInstance.this);
//				}
//			});
//		}
//		catch(Exception e)
//		{
//			System.out.println("Exception while creating space: "+getName());
//			e.printStackTrace();
//			ret.setException(e);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Initialize the extension.
//	 *  Called once, when the extension is terminate.
//	 */
//	public IFuture terminate()
//	{
//		return IFuture.DONE;
//	}
}
