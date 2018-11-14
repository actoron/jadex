package jadex.bdiv3.actions;

import jadex.bdiv3.runtime.impl.APL;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Find applicable candidates action - searches plans for a goal/event.
 */
public class FindApplicableCandidatesAction implements IConditionalComponentStep<Void>
{
	/** The processable element. */
	protected RProcessableElement element;
	
	/**
	 *  Create a new action.
	 */
	public FindApplicableCandidatesAction(RProcessableElement element)
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
			ret = RGoal.GoalLifecycleState.ACTIVE.equals(rgoal.getLifecycleState())
				&& RGoal.GoalProcessingState.INPROCESS.equals(rgoal.getProcessingState());
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
	public IFuture<Void> execute(final IInternalAccess ia)
	{
//		if(element.toString().indexOf("cnp_make_proposal")!=-1)
//			System.out.println("Select app cands for: "+element.getId());
		
//		if(element!=null && element.toString().indexOf("testgoal")!=-1)
//			System.out.println("find applicable candidates: "+element);
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("find applicable candidates 1: "+element);
		final APL apl = element.getApplicablePlanList();
		apl.build(ia).addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(apl.isEmpty())
				{
//					if(element.toString().indexOf("go_home")!=-1)
//						System.out.println("find applicable candidates 2a: "+element.getId()+" "+apl);
					element.setState(RProcessableElement.State.NOCANDIDATES);
					element.planFinished(ia, null);
//					element.reason(ia);
				}
				else
				{
//					if(element.toString().indexOf("go_home")!=-1)
//						System.out.println("find applicable candidates 2b: "+element.getId()+" "+apl);
					element.setState(RProcessableElement.State.APLAVAILABLE);
					ia.getFeature(IExecutionFeature.class).scheduleStep(new SelectCandidatesAction(element));
				}
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		}));
		
		return ret;
	}
}
