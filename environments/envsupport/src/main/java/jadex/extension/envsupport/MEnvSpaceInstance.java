package jadex.extension.envsupport;

import java.util.List;
import java.util.Map;

import jadex.application.IExtensionInfo;
import jadex.application.IExtensionInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

/**
 * 	Configuration of an Env space.
 */
public class MEnvSpaceInstance	implements IExtensionInfo
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The space type name. */
	protected String type;
	
	/** The space type (resolved during loading). */
	protected MEnvSpaceType spacetype;
	
	/** The properties. */
	protected MultiCollection<String, Object> properties;
	
	//-------- IExtensionInfo interface --------

	/**
	 *  Instantiate the extension for a specific component instance.
	 *  @param access	The external access of the component.
	 *  @param fetcher	The value fetcher of the component to be used for evaluating dynamic expressions. 
	 *  @return The extension instance object.
	 */
	public IFuture<IExtensionInstance> createInstance(final IExternalAccess access, final IValueFetcher fetcher)
	{
		return access.scheduleStep(new IComponentStep<IExtensionInstance>()
		{
			public IFuture<IExtensionInstance> execute(IInternalAccess ia)
			{
				IFuture<IExtensionInstance>	ret;
				try
				{
					Class<AbstractEnvironmentSpace>	clazz	= SReflect.findClass(spacetype.getClassName(), ia.getModel().getAllImports(), ia.getClassLoader());
					AbstractEnvironmentSpace	space	= clazz.newInstance();
					space.setInitData(ia, MEnvSpaceInstance.this, fetcher);
					ret	= new Future<IExtensionInstance>(space);					
				}
				catch(Exception e)
				{
					ret	= new Future<IExtensionInstance>(e);
				}
				return ret;
			}
		});
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
			properties = new MultiCollection<String, Object>();
		properties.add(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List<Object> getPropertyList(String key)
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
	public Object getProperty(String name)
	{
		return properties!=null? MEnvSpaceType.getProperty(properties, name): null;
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
	public MEnvSpaceType getType()
	{
		return spacetype;
	}

	/**
	 *  Set the type of this element.
	 */
	public void	setType(MEnvSpaceType spacetype)
	{
		this.spacetype	= spacetype;
	}

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
