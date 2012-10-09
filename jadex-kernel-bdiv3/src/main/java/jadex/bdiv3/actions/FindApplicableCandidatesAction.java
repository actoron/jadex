package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.APL;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class FindApplicableCandidatesAction implements IAction<Void>
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

		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		BDIModel bdimodel = ip.getBDIModel();
		
		APL apl = element.getApplicablePlanList();
		apl.build(bdimodel);
		IAction<Void> action = new SelectCandidatesAction(element);
		ia.getExternalAccess().scheduleStep(action);
		ret.setResult(null);
		
		return ret;
	}
	
	
}
