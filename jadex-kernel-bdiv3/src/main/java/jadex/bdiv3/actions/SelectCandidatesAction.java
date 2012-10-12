package jadex.bdiv3.actions;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class SelectCandidatesAction implements IAction<Void>
{
	/** The element. */
	protected RProcessableElement element;
	
	/**
	 *  Create a new action.
	 */
	public SelectCandidatesAction(RProcessableElement element)
	{
		this.element = element;
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public IFuture<Boolean> isValid()
	{
		return new Future<Boolean>(true);
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
		Future<Void> ret = new Future<Void>();

		Object cand = element.getApplicablePlanList().getNextCandidate();
		if(cand!=null)
		{
			IAction<Void> action = new ExecutePlanStepAction(element, ((MPlan)cand).getTarget());
			ia.getExternalAccess().scheduleStep(action);
			ret.setResult(null);
		}
		else
		{
			// todo: throw goal failed exception for goal listeners
			
			System.out.println("No applicable plan found.");
		}
		
		return ret;
	}
}
