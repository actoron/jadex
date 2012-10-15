package jadex.bdiv3.runtime;

import java.lang.reflect.Method;

import jadex.bdiv3.actions.IAction;
import jadex.bdiv3.actions.SelectCandidatesAction;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;

/**
 * 
 */
public class MethodPlanBody implements IPlanBody
{
	/** The bdi interpreter. */
	protected IInternalAccess ia;
	
	/** The rplan. */
	protected RPlan rplan;
	
	/** The method. */
	protected Method body;
	
	
	/**
	 * @param body
	 */
	public MethodPlanBody(IInternalAccess ia, RPlan rplan, Method body)
	{
		this.ia = ia;
		this.rplan = rplan;
		this.body = body;
	}

	/**
	 * 
	 */
	public IFuture<Void> executePlanStep()
	{
		try
		{
			body.setAccessible(true);
			Object res = body.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, 
				new Object[]{rplan.getReason().getPojoElement()});
			if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						rplan.getReason().planFinished(ia, rplan);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						rplan.setException(exception);
						rplan.getReason().planFinished(ia, rplan);
					}
				});
			}
		}
		catch(Exception e)
		{
			rplan.setException(e);
			rplan.getReason().planFinished(ia, rplan);
		}
		
		return IFuture.DONE;
	}
}
