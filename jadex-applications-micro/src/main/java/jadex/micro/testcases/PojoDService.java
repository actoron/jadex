package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
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
					cms.createComponent(null, "jadex.micro.testcases.ServiceParameterAgent.class", new CreationInfo("second", null, agent.getComponentIdentifier()), null)
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
		
		ret.addResultListener(agent.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				TestReport tr = new TestReport("#1", "Test if pojo service can be passed as parameter value.");
				
				if(result==null || (result instanceof Boolean && ((Boolean)result).booleanValue()))
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setReason("Wrong parameter value received.");
				}
				
				agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				
				if(result!=null)
					agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.setResultValue("testresults", new Testcase(0, new TestReport[]{}));
				agent.killComponent();
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture testServiceArgument(IDService service) 
	{
//		System.out.println("service: "+service.getClass());
		return new Future(Proxy.isProxyClass(service.getClass()));
	};
}
