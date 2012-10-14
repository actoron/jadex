package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.IPlanBody;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.bdiv3.runtime.RPlan;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;

import java.lang.reflect.Method;

// todo: use IPlan (and plan executor abstract to be able to execute plans as subcomponents)
// todo: allow using multi-step plans

/**
 * 
 */
public class ExecutePlanStepAction implements IAction<Void>
{
	/** The element. */
	protected RProcessableElement element;
	
	/** The plan. */
	protected RPlan rplan;
	
	/**
	 *  Create a new action.
	 */
	public ExecutePlanStepAction(RProcessableElement element, RPlan rplan)
	{
		this.element = element;
		this.rplan = rplan;
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
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		// problem plan context for steps needed that allows to know
		// when a plan has completed 
		
		IPlanBody body = rplan.getBody();
		return body.executePlanStep();
	}
}
