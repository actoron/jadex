package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
	//@ServiceStart
	@OnStart
	public IFuture<Void> serviceStarted()
	{
		final Future<Void> ret = new Future<Void>();
		final Future<Boolean> res = new Future<Boolean>();
		
		if("first".equals(agent.getConfiguration()))
		{
			agent.createComponent(
				new CreationInfo("second", null).setFilename( "jadex.micro.testcases.ServiceParameterAgent.class"))
				.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess exta)
				{
//							IComponentIdentifier cid = (IComponentIdentifier)result;
					IFuture<IDService> serfut = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IDService.class).setProvider(exta.getId()));
					serfut.addResultListener(new ExceptionDelegationResultListener<IDService, Void>(ret)
					{
						public void customResultAvailable(IDService otherser)
						{
							otherser.testServiceArgument(PojoDService.this)
								.addResultListener(new DelegationResultListener<Boolean>(res));
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		res.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				TestReport tr = new TestReport("#1", "Test if pojo service can be passed as parameter value.");
				
				if(result instanceof Boolean && ((Boolean)result).booleanValue())
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setReason("Wrong parameter value received.");
				}
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				
				ret.setResult(null);
//				if(result!=null)
					agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(0, new TestReport[]{}));
				ret.setResult(null);
				agent.killComponent();
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Boolean> testServiceArgument(IDService service) 
	{
//		System.out.println("service: "+service.getClass());
		return ProxyFactory.isProxyClass(service.getClass()) ? IFuture.TRUE: IFuture.FALSE;
	};
}
