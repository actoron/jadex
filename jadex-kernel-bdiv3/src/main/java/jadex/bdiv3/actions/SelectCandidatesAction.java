package jadex.bdiv3.actions;

import java.lang.reflect.Method;
import java.util.List;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IPlanBody;
import jadex.bdiv3.runtime.MethodPlanBody;
import jadex.bdiv3.runtime.RPlan;
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

		List<Object> cands = element.getApplicablePlanList().selectCandidates();
		if(cands!=null)
		{
			for(Object cand: cands)
			{
				if(cand instanceof MPlan)
				{
					MPlan mplan = (MPlan)cand;
					RPlan rplan = new RPlan((MPlan)cand, cand);
					IPlanBody body = new MethodPlanBody(ia, rplan, (Method)mplan.getBody());
					rplan.setBody(body);
					rplan.setReason(element);
					rplan.setDispatchedElement(element);
					IAction<Void> action = new ExecutePlanStepAction(element, rplan);
					ia.getExternalAccess().scheduleStep(action);
					ret.setResult(null);
				}
				else if(cand instanceof RPlan)
				{
					// dispatch to running plan
					RPlan rplan = (RPlan)cand;
					rplan.setDispatchedElement(element);
					IAction<Void> action = new ExecutePlanStepAction(element, rplan);
					ia.getExternalAccess().scheduleStep(action);
					ret.setResult(null);
				}
				else
				{
					// todo: dispatch to waitqueue
				}
			}
		}
		else
		{
			// todo: throw goal failed exception for goal listeners
			
			System.out.println("No applicable plan found.");
		}
		
		return ret;
	}
}
