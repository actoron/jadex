package jadex.bdiv3.actions;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.PlanCandidate;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.List;

/**
 * 
 */
public class SelectCandidatesAction implements IConditionalComponentStep<Void>
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
	public boolean isValid()
	{
		boolean ret = true;
		
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			ret = RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(rgoal.getLifecycleState())
				&& RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(rgoal.getProcessingState());
		}
			
//		if(!ret)
//			System.out.println("not valid: "+this+" "+element);
		
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
//		System.out.println("select candidates: "+element);
		
		Future<Void> ret = new Future<Void>();

		List<Object> cands = element.getApplicablePlanList().selectCandidates();
		if(cands!=null && !cands.isEmpty())
		{
			element.setState(RProcessableElement.PROCESSABLEELEMENT_CANDIDATESSELECTED);
			for(Object cand: cands)
			{
				if(cand instanceof MPlan)
				{
					MPlan mplan = (MPlan)cand;
					RPlan rplan = RPlan.createRPlan(mplan, cand, element, ia);
					RPlan.adoptPlan(rplan, ia);
					ret.setResult(null);
				}
				if(cand instanceof PlanCandidate)
				{
					MPlan mplan = ((PlanCandidate)cand).getMPlan();
					RPlan rplan = RPlan.createRPlan(mplan, cand, element, ia);
					RPlan.adoptPlan(rplan, ia);
					ret.setResult(null);
				}
				else if(cand instanceof RPlan)
				{
					// dispatch to running plan
					RPlan rplan = (RPlan)cand;
					rplan.setDispatchedElement(element);
					RPlan.adoptPlan(rplan, ia);
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
			element.planFinished(ia, null);
//			System.out.println("No applicable plan found.");
		}
		
		return ret;
	}
}
