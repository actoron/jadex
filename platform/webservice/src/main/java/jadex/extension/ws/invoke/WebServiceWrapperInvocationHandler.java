package jadex.extension.ws.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Create a new web service wrapper invocation handler.
 *  
 *  Creates an 'web service invocation agent' for each method invocation.
 *  Lets this invocation agent call the web service by using the mapping
 *  data to determine details about the service call.
 *  The invocation agent returns the result and terminates itself after the call.
 */
@Service
class WebServiceWrapperInvocationHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The web service. */
	protected WebServiceMappingInfo mapping;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 *  @mapping The mapping info about the web service to Java.
	 */
	public WebServiceWrapperInvocationHandler(IInternalAccess agent, WebServiceMappingInfo mapping)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(mapping==null)
			throw new IllegalArgumentException("Web service mapping must not null.");
		this.agent = agent;
		this.mapping = mapping;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a wrapper method is invoked.
	 *  Uses the cms to create a new invocation agent and lets this
	 *  agent call the web service. The result is transferred back
	 *  into the result future of the caller.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
			
		CreationInfo ci = new CreationInfo(agent.getId());
		ci.setFilename("jadex/extension/ws/invoke/WebServiceInvocationAgent.class");
		agent.createComponent(null, ci, null)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
		{
			public void customResultAvailable(IExternalAccess exta) 
			{
				exta.scheduleStep(new IComponentStep<Object>()
				{
					public IFuture<Object> execute(IInternalAccess ia)
					{
						Future<Object> re = new Future<Object>();
						
						try
						{
							Class<?> sclass = mapping.getService();
							Object service = sclass.newInstance();
							Method ptm = sclass.getMethod(mapping.getPortType(), new Class[0]);
							Object porttype = ptm.invoke(service, new Object[0]);
							Method m = porttype.getClass().getMethod(method.getName(), method.getParameterTypes());
							Object res = m.invoke(porttype, args);
//											System.out.println("result is: "+res);
							re.setResult(res);
							ia.killComponent();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							re.setException(e);
						}
						return re;
					}
				}).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Object>(ret)));
			}
		}));
			
		return ret;
	}
}