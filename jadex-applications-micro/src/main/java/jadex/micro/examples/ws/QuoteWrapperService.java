package jadex.micro.examples.ws;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Wrapper service implementation.
 *  Implements the asynchronous service version of the web service
 *  and redirects calls to the web service using an invocation agent.
 *  The invocation agent is blocked during the call is running.
 */
@Service
public class QuoteWrapperService implements IQuoteService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	//-------- methods --------

	/**
	 *  Get a quote.
	 */
	public IFuture<String> getQuote(String stock)
	{
		final Future<String> ret = new Future<String>();
//		ret.addResultListener(new IResultListener<String>()
//		{
//			public void resultAvailable(String result)
//			{
//				System.out.println("res: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//				exception.printStackTrace();
//			}
//		});
		
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, String>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				cms.createComponent(null, "invocation", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, String>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
						{
							public void customResultAvailable(IExternalAccess exta) 
							{
								exta.scheduleStep(new IComponentStep<String>()
								{
									public IFuture<String> execute(IInternalAccess ia)
									{
										Future<String> re = new Future<String>();
										try
										{
//											System.out.println("1");
//											StockQuotes sq = new StockQuotes();
//											System.out.println("2");
//											StockQuotesSoap sqs = sq.getStockQuotesSoap();
//											System.out.println("3");
//											BigDecimal res = sqs.getStockValue("Frankfurt", "F", null);
//											System.out.println("4");
											String res = "33";
//											System.out.println("quote is: "+res);
											re.setResult(res);
											ia.killComponent();
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										return re;
									}
								}).addResultListener(new DelegationResultListener<String>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
}
