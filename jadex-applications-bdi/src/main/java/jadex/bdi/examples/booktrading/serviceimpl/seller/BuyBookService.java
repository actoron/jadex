package jadex.bdi.examples.booktrading.serviceimpl.seller;

import jadex.bdi.examples.booktrading.serviceimpl.IBuyBookService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
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
	IBDIInternalAccess	agent;
	
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
		goal.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(goal.isSucceeded())
				{
					ret.setResult((Integer)goal.getParameter("proposal").getValue());
				}
				else
				{
					ret.setException(goal.getException()!=null ? goal.getException() : new GoalFailureException());
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		agent.getGoalbase().dispatchTopLevelGoal(goal);
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
		goal.getParameter("proposal").setValue(new Integer(price));
		goal.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(goal.isSucceeded())
				{
					ret.setResult(null);
				}
				else
				{
					ret.setException(goal.getException()!=null ? goal.getException() : new GoalFailureException());
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		agent.getGoalbase().dispatchTopLevelGoal(goal);
		return ret;
	}

}
