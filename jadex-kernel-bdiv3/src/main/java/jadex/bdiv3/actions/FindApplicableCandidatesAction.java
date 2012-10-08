package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.APL;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
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
	protected Object element;
	
	/**
	 *  Create a new action.
	 */
	public FindApplicableCandidatesAction(Object element)
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
		
		// todo: support other elements than goals
		APL apl = getApplicableMPlansForGoal(element.getClass(), bdimodel);
		IAction<Void> action = new SelectCandidatesAction(element, apl);
		ia.getExternalAccess().scheduleStep(action);
		ret.setResult(null);
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static APL getApplicableMPlansForGoal(Class<?> element, BDIModel bdimodel)
	{
		List<MPlan> ret = new ArrayList<MPlan>();
		
		List<MPlan> mplans = bdimodel.getPlans();
		for(int i=0; i<mplans.size(); i++)
		{
			MPlan mplan = mplans.get(i);
			Plan aplan = mplan.getTarget().getAnnotation(Plan.class);
			Trigger atrigger = aplan.trigger();
			Class<?>[] mgoals = atrigger.goals();
			for(int j=0; j<mgoals.length; j++)
			{
//				Goal g = mgoals[j].getAnnotation(Goal.class);
				if(mgoals[j].equals(element))
				{
					ret.add(mplan);
				}
			}
		}
		
		return new APL(ret);
	}
}
