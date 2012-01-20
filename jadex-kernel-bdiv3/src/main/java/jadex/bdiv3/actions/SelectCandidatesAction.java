package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 */
public class SelectCandidatesAction implements IAction<Void>
{
	/** The element. */
	protected Object element;
	
	/** The processable element. */
	protected List<Method> candidates;
	
	/**
	 *  Create a new action.
	 */
	public SelectCandidatesAction(Object element, List<Method> candidates)
	{
		this.element = element;
		this.candidates = candidates;
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

		if(candidates!=null && candidates.size()>0)
		{
//			IAction<Void> action = new ExecutePlanStepAction(ia instanceof IPojoMicroAgent? 
//				((IPojoMicroAgent)ia).getPojoAgent(): ia, candidates.get(0));
			IAction<Void> action = new ExecutePlanStepAction(element, candidates.get(0));
			ia.getExternalAccess().scheduleStep(action);
			ret.setResult(null);
		}
		else
		{
			System.out.println("No applicable plan found.");
		}
		
		return ret;
	}
}
