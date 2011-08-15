package jadex.micro.testcases;


import jadex.base.test.Testcase;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if pojo services can be passed as local parameters.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IDService.class, implementation=@Implementation(PojoDService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
@Configurations({@Configuration(name="first"), @Configuration(name="second")})
public class ServiceParameterAgent
{
//	/**
//	 *  Init the agent.
//	 */
//	@ServiceStart
//	public IFuture agentCreated()
//	{
//		final Future ret = new Future();
//		
//		if("first".equals(agent.getConfiguration()))
//		{
//			cms.createComponent(null, "jadex.micro.testcases.ServiceParameterAgent", null, null)
//				.addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					IExternalAccess other = (IExternalAccess)result;
//					agent.getServiceContainer().getService(IDService.class, other.getComponentIdentifier())
//						.addResultListener(new DelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							IDService otherser = (IDService)result;
//							otherser.testServiceArgument(service)
//						}
//					});
//				}
//			});
//		}
//		else
//		{
//			
//		}
//		
//		return ret;
//	}
}
