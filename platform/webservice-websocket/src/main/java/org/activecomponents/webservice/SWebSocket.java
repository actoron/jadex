package org.activecomponents.webservice;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Static helper methods for Jadex websocket integration.
 */
public class SWebSocket
{
	//-------- constants --------
	
	/** Attribute name for platform in context. */
	public static final String	ATTR_PLATFORM	= "com.actoron.webservice.platform";
	
	//-------- methods --------
	
	/**
	 *  Initialize a context. Should be called only once per context at startup.
	 *  Creates platform and initial agents, if necessary.
	 *  @param context	The servlet context.
	 */
	public static IFuture<Void>	initContext(ServletContext context)
	{
		FutureBarrier<IComponentIdentifier>	agents	= new FutureBarrier<IComponentIdentifier>();

		// Create components specified in web.xml
		Enumeration<String> pnames = context.getInitParameterNames();
		if(pnames!=null)
		{
			while(pnames.hasMoreElements())
			{
				String pname = pnames.nextElement();
				if(pname!=null && pname.startsWith("ws_component"))
				{
					final String model = context.getInitParameter(pname);
					IFuture<IComponentIdentifier> fut = createAgent(context, model);
					agents.addFuture(fut);
					fut.addResultListener(new IResultListener<IComponentIdentifier>()
					{
						public void resultAvailable(IComponentIdentifier result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Exception in component start: "+model+" "+exception);
						}
					});
				}
			}
		}
		
		return agents.waitForIgnoreFailures(null);
	}
	
	/**
	 *  Cleanup a context. Should be called only once per context at shutdown.
	 *  Removes platform with all agents, if necessary.
	 *  @param context	The servlet context.
	 */
	public static IFuture<Void>	cleanupContext(ServletContext context)
	{
		// Terminates the platform of the application/context
		
		IFuture<IExternalAccess> fut = (IFuture<IExternalAccess>)context.getAttribute(ATTR_PLATFORM);
		if(fut!=null)
		{
			final Future<Void>	ret	= new Future<Void>();
			fut.addResultListener(new IResultListener<IExternalAccess>()
			{
				@Override
				public void resultAvailable(IExternalAccess platform)
				{
					platform.killComponent().addResultListener(new IResultListener<Map<String,Object>>()
					{
						@Override
						public void resultAvailable(Map<String,Object> result)
						{
							ret.setResult(null);
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							// Platform shutdown failed -> no more cleanup possible.
							ret.setResult(null);
						}
					});
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// Platform startup failed -> no cleanup necessary/possible.
					ret.setResult(null);
				}
			});
			
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Get or create a Jadex platform for the given context.
	 *  @param context	The servlet context.
	 */
	public static IFuture<IExternalAccess> getPlatform(ServletContext context)
	{
		IFuture<IExternalAccess> ret = (IFuture<IExternalAccess>)context.getAttribute(ATTR_PLATFORM);
	    if(ret==null)
	    {
	    	IPlatformConfiguration pc = PlatformConfigurationHandler.getDefault();
		    pc.setGui(false);
//		    pc.getRootConfig().setGui(true);
//		    pc.getRootConfig().setLogging(true);
//		    pc.getRootConfig().setRsPublish(true);	
		    ret = Starter.createPlatform(pc);
			context.setAttribute(ATTR_PLATFORM, ret);		    
	    }
	    return ret;
	}
	
	/**
	 *  Create a agent.
	 *  @param context	The servlet context.
	 *  @param model	The agent model.
	 *  @return Future for the component identifier of the created agent. 
	 */
	public static IFuture<IComponentIdentifier>	createAgent(ServletContext context, final String model)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		getPlatform(context).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			@Override
			public void customResultAvailable(IExternalAccess platform) throws Exception
			{				
				platform.createComponent(null, new CreationInfo().setFilename(model)).addTuple2ResultListener(new IFunctionalResultListener<IComponentIdentifier>()
				{
					@Override
					public void resultAvailable(IComponentIdentifier cid)
					{
						ret.setResult(cid);
						System.out.println("Created component: "+cid);
					}
				}, null, new IFunctionalExceptionListener()
				{
					@Override
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				});
			}
		});
		return ret;
	}
}
