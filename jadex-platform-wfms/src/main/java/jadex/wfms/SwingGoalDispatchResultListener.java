package jadex.wfms;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAParameter;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

public class SwingGoalDispatchResultListener extends SwingDefaultResultListener
{
	private IBDIExternalAccess access;
	
	public SwingGoalDispatchResultListener(IBDIExternalAccess access)
	{
		super((Component) null);
		this.access = access;
	}
	
	public void customResultAvailable(Object source, Object result)
	{
		final IEAGoal goal = (IEAGoal) result;
		configureGoal(goal);
		access.dispatchTopLevelGoalAndWait(goal).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				goal.getParameters().addResultListener(new SwingDefaultResultListener((Component) null)
				{
					public void customResultAvailable(Object source, Object result)
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
									invokeResults();
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									invokeResults();
								}
								
								private void invokeResults()
								{
									EventQueue.invokeLater(new Runnable()
									{
										public void run()
										{
											goalResultsAvailable(parameters);
										}
									});
								}
							});
						}
						else
							goalResultsAvailable(parameters);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				goalExceptionOccurred(exception);
			}
		});
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
