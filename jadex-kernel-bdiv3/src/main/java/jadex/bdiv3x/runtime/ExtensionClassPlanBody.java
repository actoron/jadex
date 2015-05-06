package jadex.bdiv3x.runtime;

import java.lang.reflect.Constructor;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.runtime.impl.AbstractPlanBody;
import jadex.bdiv3.runtime.impl.BodyAborted;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;

/**
 * 
 */
public class ExtensionClassPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The body class. */
	protected Class<?> body;
	
	/** The body instance. */
	protected Object plan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan body.
	 */
	public ExtensionClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body)
	{
		super(ia, rplan);
		this.body = body;
	}
	
	/**
	 *  Get the body impl (object that is actually invoked).
	 *  @return The object representing the body. 
	 */
	public Object getBody()
	{
		if(plan==null)
		{
			try
			{
				// create plan  
				Constructor<?>[] cons = body.getDeclaredConstructors();
				for(Constructor<?> c: cons)
				{
					Object[] params = BDIAgentFeature
						.getInjectionValues(c.getParameterTypes(), c.getParameterAnnotations(), rplan.getModelElement(), null, rplan, null, ia);
					if(params!=null)
					{
						try
						{
							c.setAccessible(true);
							plan = c.newInstance(params);
							break;
						}
						catch(Exception e)
						{
						}							
					}						
				}
				if(plan==null)
					throw new RuntimeException("Plan body has no accessible constructor (maybe wrong args?): "+body);
				
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
	//			e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		return plan;
	}
	
	/**
	 *  Invoke the plan body.
	 */
	public Object invokeBody(Object[] params) throws BodyAborted
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Invoke the plan passed method.
	 */
	public Object invokePassed(Object[] params)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Invoke the plan failed method.
	 */
	public Object invokeFailed(Object[] params)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Invoke the plan aborted method.
	 */
	public Object invokeAborted(Object[] params)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the body parameters.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the passed parameters.
	 */
	public Class<?>[] getPassedParameterTypes()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the failed parameters.
	 */
	public Class<?>[] getFailedParameterTypes()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the aborted parameters.
	 */
	public Class<?>[] getAbortedParameterTypes()
	{
		throw new UnsupportedOperationException();
	}
}
