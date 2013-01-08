package jadex.bdiv3.runtime;

import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;

import java.lang.reflect.Method;

/**
 * 
 */
public class MethodPlanBody extends AbstractPlanBody
{
	/** The method. */
	protected Method body;
	
	
	/**
	 * 
	 */
	public MethodPlanBody(IInternalAccess ia, RPlan rplan, Method body)
	{
		super(ia, rplan);
		this.body = body;
	}
	
	/**
	 * 
	 */
	public Object executeBody(Object agent, Object[] params)
	{
		try
		{
			body.setAccessible(true);
			return body.invoke(agent, params);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return body.getParameterTypes();
	}

//	/**
//	 * 
//	 */
//	public IFuture<Void> executePlanStep()
//	{
//		try
//		{
////			Object res = body.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, 
////				new Object[]{rplan.getReason().getPojoElement()});
//
//			// Guess parameters
//			Class<?>[] ptypes = body.getParameterTypes();
//			Object[] params = guessParameters(ptypes);
//			
//			body.setAccessible(true);
//			Object res = body.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, params);
//			if(res instanceof IFuture)
//			{
//				((IFuture)res).addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_PASSED);
//						if(reason instanceof RProcessableElement)
//							((RProcessableElement)reason).planFinished(ia, rplan);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_FAILED);
//						rplan.setException(exception);
//						if(reason instanceof RProcessableElement)
//							((RProcessableElement)reason).planFinished(ia, rplan);
//					}
//				});
//			}
//			else
//			{
//				rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_PASSED);
//				if(reason instanceof RProcessableElement)
//					((RProcessableElement)reason).planFinished(ia, rplan);
//			}
//		}
//		catch(Exception e)
//		{
//			rplan.setException(e);
//			rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_FAILED);
//			if(rplan.getReason() instanceof RProcessableElement)
//				((RProcessableElement)rplan.getReason()).planFinished(ia, rplan);
//		}
//		
//		return IFuture.DONE;
//	}
}
