package jadex.bdiv3.actions;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;

import java.lang.reflect.Method;

// todo: use IPlan (and plan executor abstract to be able to execute plans as subcomponents)
// todo: allow using multi-step plans

/**
 * 
 */
public class ExecutePlanStepAction implements IAction<Void>
{
//	/** The object. */
//	protected Object object;
	
	/** The element. */
	protected Object element;
	
	/** The method. */
	protected Method method;
	
	/**
	 *  Create a new action.
	 */
	public ExecutePlanStepAction(Object element, Method method)
	{
		this.element = element;
		this.method = method;
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
		// problem plan context for steps needed that allows to know
		// when a plan has completed 
		
		Future<Void> ret = new Future<Void>();
		try
		{
			method.setAccessible(true);
			method.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, 
				new Object[]{element});
			ret.setResult(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		return ret;
	}
}
