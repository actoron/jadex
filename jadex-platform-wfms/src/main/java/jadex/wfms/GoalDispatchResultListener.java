package jadex.wfms;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAParameter;
import jadex.bdi.runtime.IParameter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentAdapter;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

public class GoalDispatchResultListener implements IResultListener
{
	private IBDIExternalAccess access;
	IComponentAdapter adapter;
	
	public GoalDispatchResultListener(IBDIExternalAccess access)
	{
		this.access = access;
	}
	
	/**
	 *  Called when the result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void resultAvailable(final Object source, final Object result)
	{
		final IEAGoal goal = (IEAGoal) result;
		configureGoal(goal);
		access.dispatchTopLevelGoalAndWait(goal).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				goal.getParameters().addResultListener(new IResultListener()
				{
					public void resultAvailable(final Object source, final Object result)
					{
						IEAParameter[] params = (IEAParameter[]) result;
						final Map parameters = new HashMap();
						for (int i = 0; i < params.length - 1; ++i)
						{
							final String name = params[i].getName();						
							params[i].getValue().addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									parameters.put(name, result);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
								}
							});
						}
						
						if (params.length > 0)
						{
							final String name = params[params.length - 1].getName();
							params[params.length - 1].getValue().addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									parameters.put(name, result);
									goalResultsAvailable(parameters);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									goalResultsAvailable(parameters);
								}
							});
						}
						else
							goalResultsAvailable(parameters);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						goalExceptionOccurred(exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				goalExceptionOccurred(exception);
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(final Object source, final Exception exception)
	{
		goalExceptionOccurred(exception);
	}
	
	public void configureGoal(IEAGoal goal)
	{
	}
	
	public void goalResultsAvailable(Map parameters)
	{
	}
	
	public void goalExceptionOccurred(Exception exception)
	{
	}
}
