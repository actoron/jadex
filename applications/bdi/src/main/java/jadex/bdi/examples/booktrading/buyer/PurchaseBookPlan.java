package jadex.bdi.examples.booktrading.buyer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import jadex.bdi.examples.booktrading.IBuyBookService;
import jadex.bdi.examples.booktrading.common.NegotiationReport;
import jadex.bdi.examples.booktrading.common.Order;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ServiceCall;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;

/**
 * The plan tries to purchase a book.
 */
public class PurchaseBookPlan extends Plan
{
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Get order properties and calculate acceptable price.
		final Order order = (Order)getParameter("order").getValue();
		double time_span = order.getDeadline().getTime() - order.getStartTime();
		double elapsed_time = getTime() - order.getStartTime();
		double price_span = order.getLimit() - order.getStartPrice();
		int acceptable_price = (int)(price_span * elapsed_time / time_span)
			+ order.getStartPrice();

//		System.out.println("PurchaseBookPlan: "+order);

		Future<Collection<Tuple2<IBuyBookService, Integer>>> cfp = new Future<Collection<Tuple2<IBuyBookService, Integer>>>();
		final CollectionResultListener<Tuple2<IBuyBookService, Integer>> crl = new CollectionResultListener<Tuple2<IBuyBookService, Integer>>(true,
			new DelegationResultListener<Collection<Tuple2<IBuyBookService, Integer>>>(cfp));
		
		// Find available seller agents.
		ServiceCall.getOrCreateNextInvocation().setTimeout(1000);
		IRequiredServicesFeature rsf = getAgent().getFeature(IRequiredServicesFeature.class);
		ITerminableIntermediateFuture<IBuyBookService>  fut = rsf.getServices("buyservice");
		fut.addResultListener(new TimeoutIntermediateResultListener<IBuyBookService>(5000, getExternalAccess(),
			new IntermediateEmptyResultListener<IBuyBookService>()
		{
			int cnt;
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
				if(exception instanceof TimeoutException)
					finished();
			}
			
			public void resultAvailable(Collection<IBuyBookService> result)
			{
//				System.out.println("ra: "+result);
				for(IBuyBookService ser: result)
				{
					intermediateResultAvailable(ser);
				}
				finished();
			}

			public void intermediateResultAvailable(final IBuyBookService seller)
			{
				cnt++;
				
//				System.out.println("ira: "+seller+" "+cnt);
				
				seller.callForProposal(order.getTitle()).addResultListener(new IResultListener<Integer>()
				{
					public void resultAvailable(Integer result)
					{
//						System.out.println("cfp end");
						crl.resultAvailable(new Tuple2<IBuyBookService, Integer>(seller, result));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						crl.exceptionOccurred(exception);
					}
				});
			}
			
			public void finished()
			{
//				System.out.println("fini");
				crl.setNumber(cnt);
			}
		}));
		
//		IBuyBookService[] services = getAgent().getComponentFeature(IRequiredServicesFeature.class).getServices("buyservice").get().toArray(new IBuyBookService[0]);
//		if(services.length == 0)
//		{
//			System.out.println("No seller found, purchase failed.");
//			generateNegotiationReport(order, null, acceptable_price);
//			fail();
//		}

//		// Initiate a call-for-proposal.
//		Future<Collection<Tuple2<IBuyBookService, Integer>>>	cfp	= new Future<Collection<Tuple2<IBuyBookService, Integer>>>();
//		final CollectionResultListener<Tuple2<IBuyBookService, Integer>>	crl	= new CollectionResultListener<Tuple2<IBuyBookService, Integer>>(services.length, true,
//			new DelegationResultListener<Collection<Tuple2<IBuyBookService, Integer>>>(cfp));
//		for(int i=0; i<services.length; i++)
//		{
//			final IBuyBookService	seller	= services[i];
//			seller.callForProposal(order.getTitle()).addResultListener(new IResultListener<Integer>()
//			{cfp end
//				public void resultAvailable(Integer result)
//				{
//					crl.resultAvailable(new Tuple2<IBuyBookService, Integer>(seller, result));
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					crl.exceptionOccurred(exception);
//				}
//			});
//		}
		
		// Sort results by price.
//		System.out.println("before props: "+this);
		Tuple2<IBuyBookService, Integer>[] proposals = cfp.get().toArray(new Tuple2[0]);
//		System.out.println("after props: "+this);
		
		// Does not work because no intermediate results arrive this way
//		Tuple2<IBuyBookService, Integer>[] proposals = null;
//		try
//		{
//			proposals = cfp.get(5000).toArray(new Tuple2[0]);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		Arrays.sort(proposals, new Comparator<Tuple2<IBuyBookService, Integer>>()
		{
			public int compare(Tuple2<IBuyBookService, Integer> o1, Tuple2<IBuyBookService, Integer> o2)
			{
				return o1.getSecondEntity().compareTo(o2.getSecondEntity());
			}
		});
		
		// Do we have a winner?
		if(proposals.length>0 && proposals[0].getSecondEntity().intValue()<=acceptable_price)
		{
			proposals[0].getFirstEntity().acceptProposal(order.getTitle(), proposals[0].getSecondEntity().intValue()).get();
			
			generateNegotiationReport(order, proposals, acceptable_price);
			
			// If contract-net succeeds, store result in order object.
			order.setState(Order.DONE);
			order.setExecutionPrice(proposals[0].getSecondEntity());
			order.setExecutionDate(new Date(getTime()));
		}
		else
		{
			generateNegotiationReport(order, proposals, acceptable_price);
			
			fail();
		}
//		System.out.println("result: "+cnp.getParameter("result").getValue());
	}
	
	/**
	 *  Generate and add a negotiation report.
	 */
	protected void generateNegotiationReport(Order order, Tuple2<IBuyBookService, Integer>[] proposals, double acceptable_price)
	{
		String report = "Accepable price: "+acceptable_price+", proposals: ";
		if(proposals!=null)
		{
			for(int i=0; i<proposals.length; i++)
			{
				report += proposals[i].getSecondEntity()+"-"+proposals[i].getFirstEntity().toString();
				if(i+1<proposals.length)
					report += ", ";
			}
		}
		else
		{
			report	+= "No seller found, purchase failed.";
		}
		NegotiationReport nr = new NegotiationReport(order, report, getScope().getTime());
		//System.out.println("REPORT of agent: "+getAgentName()+" "+report);
		getBeliefbase().getBeliefSet("negotiation_reports").addFact(nr);
	}
	
//	@Override
//	public void failed()
//	{
//		System.out.println("failed: "+this);
//	}
//	
//	@Override
//	public void aborted()
//	{
//		System.out.println("aborted: "+this);
//	}
}