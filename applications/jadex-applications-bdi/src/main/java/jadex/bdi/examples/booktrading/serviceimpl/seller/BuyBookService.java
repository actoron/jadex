package jadex.bdi.examples.booktrading.serviceimpl.seller;

import jadex.bdi.examples.booktrading.serviceimpl.IBuyBookService;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Implementation of the buy book service.
 */
@Service
public class BuyBookService implements IBuyBookService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IBDIXAgentFeature	agent;
	
	//-------- IBuyBookService interface --------
	
	/**
	 *  Ask the seller for a a quote on a book.
	 *  @param title	The book title.
	 *  @return The price.
	 */
	public IFuture<Integer> callForProposal(String title)
	{
		final Future<Integer>	ret	= new Future<Integer>();
		final IGoal	goal	= agent.getGoalbase().createGoal("cnp_make_proposal");
		goal.getParameter("cfp").setValue(title);
		agent.getGoalbase().dispatchTopLevelGoal(goal)
			.addResultListener(new ExceptionDelegationResultListener<Object, Integer>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult((Integer)goal.getParameter("proposal").getValue());
			}
		});
		return ret;
	}

	/**
	 *  Buy a book
	 *  @param title	The book title.
	 *  @param price	The price to pay.
	 *  @return A future indicating if the transaction was successful.
	 */
	public IFuture<Void> acceptProposal(String title, int price)
	{
		final Future<Void>	ret	= new Future<Void>();
		final IGoal	goal	= agent.getGoalbase().createGoal("cnp_execute_task");
		goal.getParameter("cfp").setValue(title);
		goal.getParameter("proposal").setValue(Integer.valueOf(price));
		agent.getGoalbase().dispatchTopLevelGoal(goal)
			.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(null);
			}
		});
		return ret;
	}

}
