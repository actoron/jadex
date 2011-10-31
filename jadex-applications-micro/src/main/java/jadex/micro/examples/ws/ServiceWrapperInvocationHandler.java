package jadex.micro.examples.ws;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 
 */
@Service
class ServiceWrapperInvocationHandler implements InvocationHandler
{
	/** The agent. */
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	public ServiceWrapperInvocationHandler(MicroAgent agent)
	{
		this.agent = agent;
	}
	
	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
			
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				cms.createComponent(null, "invocation", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
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
//												System.out.println("1");
//												StockQuotes sq = new StockQuotes();
//												System.out.println("2");
//												StockQuotesSoap sqs = sq.getStockQuotesSoap();
//												System.out.println("3");
//												BigDecimal res = sqs.getStockValue("Frankfurt", "F", null);
//												System.out.println("4");
											Object res = "33";
//												System.out.println("quote is: "+res);
											re.setResult(res);
											ia.killComponent();
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										return re;
									}
								}).addResultListener(new DelegationResultListener<Object>(ret));
							}
						});
					}
				});
			}
		});
			
		return ret;
	}
}