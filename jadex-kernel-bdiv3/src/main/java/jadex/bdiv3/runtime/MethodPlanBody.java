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
public class MethodPlanBody implements IPlanBody
{
	/** The bdi interpreter. */
	protected IInternalAccess ia;
	
	/** The rplan. */
	protected RPlan rplan;
	
	/** The method. */
	protected Method body;
	
	
	/**
	 * 
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
//			Object res = body.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, 
//				new Object[]{rplan.getReason().getPojoElement()});
			
			final Object reason = rplan.getReason();
			Object pojope = null;
			if(reason instanceof RProcessableElement)
				pojope = ((RProcessableElement)reason).getPojoElement();
			
			// Guess parameters
			Class<?>[] ptypes = body.getParameterTypes();
			Object[] params = new Object[ptypes.length];
			
			for(int i=0; i<ptypes.length; i++)
			{
				if(reason!=null && SReflect.isSupertype(reason.getClass(), ptypes[i]))
				{
					params[i] = reason;
				}
				else if(pojope!=null && SReflect.isSupertype(pojope.getClass(), ptypes[i]))
				{
					params[i] = pojope;
				}
				else if(SReflect.isSupertype(RPlan.class, ptypes[i]))
				{
					params[i] = rplan;
				}
			}
			
			body.setAccessible(true);
			Object res = body.invoke(ia instanceof IPojoMicroAgent? ((IPojoMicroAgent)ia).getPojoAgent(): ia, params);
			if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						if(reason instanceof RProcessableElement)
							((RProcessableElement)reason).planFinished(ia, rplan);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						rplan.setException(exception);
						if(reason instanceof RProcessableElement)
							((RProcessableElement)reason).planFinished(ia, rplan);
					}
				});
			}
		}
		catch(Exception e)
		{
			rplan.setException(e);
			if(rplan.getReason() instanceof RProcessableElement)
				((RProcessableElement)rplan.getReason()).planFinished(ia, rplan);
		}
		
		return IFuture.DONE;
	}
}
