package jadex.bdiv3.actions;

import java.lang.reflect.Method;
import java.util.List;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IPlanBody;
import jadex.bdiv3.runtime.MethodPlanBody;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
			
		if(!ret)
			System.out.println("not valid: "+this+" "+element);
		
		return ret;
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
		if(cands!=null && !cands.isEmpty())
		{
			element.setState(RProcessableElement.PROCESSABLEELEMENT_CANDIDATESSELECTED);
			for(Object cand: cands)
			{
				if(cand instanceof MPlan)
				{
					MPlan mplan = (MPlan)cand;
					RPlan rplan = new RPlan((MPlan)cand, cand);
					
					// todo: move this code somehow
					String mname = (String)mplan.getBody();
					Class<?> cl = SReflect.findClass0(mplan.getName(), null, ia.getClassLoader());
					Method[] ms = cl.getDeclaredMethods();
					Method mbody = null;
					for(Method m: ms)
					{
						if(m.getName().equals(mname))
						{
							mbody = m;
							break;
						}
					}
					
					IPlanBody body = new MethodPlanBody(ia, rplan, mbody);
					rplan.setBody(body);
					rplan.setReason(element);
					rplan.setDispatchedElement(element);
					IConditionalComponentStep<Void> action = new ExecutePlanStepAction(element, rplan);
					ia.getExternalAccess().scheduleStep(action);
					ret.setResult(null);
				}
				else if(cand instanceof RPlan)
				{
					// dispatch to running plan
					RPlan rplan = (RPlan)cand;
					rplan.setDispatchedElement(element);
					IConditionalComponentStep<Void> action = new ExecutePlanStepAction(element, rplan);
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
//			element.planFinished(ia, rplan)
			System.out.println("No applicable plan found.");
		}
		
		return ret;
	}
}
