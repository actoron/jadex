package jadex.micro.testcases;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Proxy;

/**
 * 
 */
@Service
public class PojoDService implements IDService
{
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 *  Init the agent.
	 */
	@ServiceStart
	public IFuture serviceStarted()
	{
		final Future ret = new Future();
		
		if("first".equals(agent.getConfiguration()))
		{
			agent.getServiceContainer().getRequiredService("cms")
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{	
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.createComponent(null, "jadex.micro.testcases.ServiceParameterAgent.class", new CreationInfo("second", null), null)
						.addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IComponentIdentifier cid = (IComponentIdentifier)result;
							agent.getServiceContainer().getService(IDService.class, cid)
								.addResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									IDService otherser = (IDService)result;
									otherser.testServiceArgument(PojoDService.this).addResultListener(new DelegationResultListener(ret));
								}
							});
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture testServiceArgument(IDService service) 
	{
		return new Future(Proxy.isProxyClass(service.getClass()));
	};
}
