package jadex.bridge.service;

import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  Basic service provide a simple default isValid() implementation
 *  that returns true after start service and false afterwards.
 */
public class BasicService implements IInternalService
{	
	//-------- attributes --------

	/** The id counter. */
	protected static long idcnt;
	
	/** The started state. */
	protected boolean started;
	
	/** The shutdowned state. */
	protected boolean shutdowned;
	
	/** The service id. */
	protected IServiceIdentifier sid;
	
	/** The service properties. */
	protected Map properties;
	
//	/** The list of start futures. */
//	protected List startfutures;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 * /
	public BasicService()
	{
		this(null);
	}*/
	
	/**
	 *  Create a new service.
	 * /
	public BasicService(IServiceIdentifier sid)
	{
		this.sid = sid;
	}*/
	
	/**
	 *  Create a new service.
	 */
	public BasicService(Object providerid, Class type, Map properties)
	{
//		if(!SReflect.isSupertype(type, getClass()))
//			throw new RuntimeException("Service must implement provided interface: "+getClass().getName()+", "+type.getName());
		this.sid = createServiceIdentifier(providerid, type, getClass());
		this.properties	= properties;
		
		// todo: move to be able to use the constant
		// jadex.base.gui.componentviewer.IAbstractViewerPanel.PROPERTY_VIEWERCLASS
		Object guiclazz = properties!=null? properties.get("componentviewer.viewerclass"): null;
		
		if(guiclazz==null && type.isAnnotationPresent(GuiClass.class))
		{
			GuiClass gui = (GuiClass)type.getAnnotation(GuiClass.class);
			guiclazz = gui.value();
			if(this.properties==null)
				this.properties = new HashMap();
			this.properties.put("componentviewer.viewerclass", guiclazz);
		}
		else if(guiclazz==null && type.isAnnotationPresent(GuiClassName.class))
		{
			GuiClassName gui = (GuiClassName)type.getAnnotation(GuiClassName.class);
			guiclazz = gui.value();
			if(this.properties==null)
				this.properties = new HashMap();
			this.properties.put("componentviewer.viewerclass", guiclazz);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 *  
	 *  todo: why is method synchronized?
	 */
	public synchronized IFuture isValid()
	{
		return new Future(started && !shutdowned? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the service id.
	 *  @return The service id.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 * /
	public Object getProperty(String name)
	{
		return properties!=null ? properties.get(name) : null; 
	}*/
	
	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 */
	public Map getPropertyMap()
	{
		return properties!=null? properties: Collections.EMPTY_MAP; 
	}
	
	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setPropertyMap(Map properties)
	{
		this.properties = properties;
	}

	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
//		System.out.println("start: "+this);
		Future ret = new Future();
		
		Future[] tosignal = null;
		boolean ex = false;
		synchronized(this)
		{
			if(started)
			{
				ex = true;
			}
			else
			{
				started = true;
//				if(startfutures!=null)
//				{
//					tosignal = (Future[])startfutures.toArray(new Future[startfutures.size()]);
//					startfutures = null;
//				}
			}
		}
		
		if(ex)
		{
			ret.setException(new RuntimeException("Already running."));
		}
		else 
		{
			if(tosignal!=null)
			{
				for(int i=0; i<tosignal.length; i++)
				{
					tosignal[i].setResult(null);
				}
			}
			
			ret.setResult(getServiceIdentifier());
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService()
	{
		final Future ret = new Future();
		isValid().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(!((Boolean)result).booleanValue())
				{
					ret.setException(new RuntimeException("Not running."));
				}
				else
				{
					shutdowned = true;
					ret.setResult(getServiceIdentifier());
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Generate a unique name.
	 *  @param The calling service class.
	 */
	protected static String generateServiceName(Class service)
	{
		synchronized(BasicService.class)
		{
			return SReflect.getInnerClassName(service)+"_#"+idcnt++;
		}
	}
	
	/**
	 *  Create a new service identifier.
	 *  @param providerid The provider id.
	 *  @param servicename The service name.
	 *  @return A service identifier.
	 */
	public static IServiceIdentifier createServiceIdentifier(Object providerid, Class servicetype, Class serviceimpl)
	{
		return new ServiceIdentifier(providerid, servicetype, generateServiceName(serviceimpl));
	}
	
//	/**
//	 *  Get a future that signals when the service is started.
//	 *  @return A future that signals when the service has been started.
//	 */
//	public IFuture signalStarted()
//	{
//		final Future ret = new Future();
//		
//		int alt = 2;
//		synchronized(this)
//		{
//			if(shutdowned)
//				alt = 0;
//			else if(started)
//				alt = 1;
//			else
//			{
//				if(startfutures==null)
//					startfutures = new ArrayList();
//				startfutures.add(ret);
//			}
//		}
//		
//		if(alt==0)
//		{
//			ret.setException(new RuntimeException("Service already shutdowned: "+getServiceIdentifier()));
//		}
//		else if(alt==1)
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Check if the service is valid.
	 * /
	public IFuture checkValid()
	{
		Future ret = new Future();
		if(!isValid())
			ret.setException(new RuntimeException("Service invalid: "+getServiceIdentifier()));
		else
			ret.setResult(null);
		return ret;
	}*/
}
