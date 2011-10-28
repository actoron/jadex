package jadex.micro.examples.ws;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.math.BigDecimal;

import com.gama_system.webservices.StockQuotes;
import com.gama_system.webservices.StockQuotesSoap;

/**
 * 
 */
@Service
public class QuoteWrapperService implements IQuoteService
{
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 *  Get a quote.
	 */
	public IFuture<BigDecimal> getQuote(String exchange, String stock, String time)
	{
		final Future<BigDecimal> ret = new Future<BigDecimal>();
		
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, BigDecimal>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.createComponent(null, "invocation", null, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, BigDecimal>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, BigDecimal>(ret)
						{
							public void customResultAvailable(IExternalAccess exta) 
							{
								exta.scheduleStep(new IComponentStep<BigDecimal>()
								{
									public IFuture<BigDecimal> execute(IInternalAccess ia)
									{
										Future<BigDecimal> re = new Future<BigDecimal>();
										try
										{
											System.out.println("1");
											StockQuotes sq = new StockQuotes();
											System.out.println("2");
											StockQuotesSoap sqs = sq.getStockQuotesSoap();
											System.out.println("3");
											BigDecimal res = sqs.getStockValue("Frankfurt", "F", null);
											System.out.println("4");
											System.out.println("quote is: "+res);
											re.setResult(res);
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										return re;
									}
								}).addResultListener(new DelegationResultListener<BigDecimal>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
}
