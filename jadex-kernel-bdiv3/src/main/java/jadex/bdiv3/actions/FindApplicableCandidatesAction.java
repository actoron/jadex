package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.APL;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RCapability;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
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
//		System.out.println("find applicable candidates: "+element);
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		RCapability rcapa = ip.getCapability();
		
		APL apl = element.getApplicablePlanList();
		apl.build(rcapa);
		if(apl.isEmpty())
		{
			element.setState(RProcessableElement.PROCESSABLEELEMENT_NOCANDIDATES);
			element.planFinished(ia, null);
//			element.reason(ia);
		}
		else
		{
			element.setState(RProcessableElement.PROCESSABLEELEMENT_APLAVAILABLE);
			ia.getExternalAccess().scheduleStep(new SelectCandidatesAction(element));
		}
		
		return IFuture.DONE;
	}
}
