package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceContainer;
import jadex.commons.ICommand;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;

import java.security.AccessControlException;

public class AccessControlCheck
{
	private Integer[] actions;
	private IComponentIdentifier client;
	
	public AccessControlCheck(IComponentIdentifier client, Integer[] actions)
	{
		this.actions = actions;
		this.client = client;
	}
	
	public AccessControlCheck(IComponentIdentifier client, Integer action)
	{
		this(client, Integer.valueOf[] { action });
	}
	
	public void checkAccess(final Future targetFuture, IServiceContainer provider, final ICommand actionCommand)
	{
		provider.getRequiredService("aaa_service").addResultListener(new DelegationResultListener(targetFuture)
		{
			public void customResultAvailable(Object result)
			{
				final IResultListener actionCounter = new CounterResultListener(actions.length, false, new DelegationResultListener(targetFuture)
				{
					public void customResultAvailable(Object result)
					{
						actionCommand.execute(actions);
					}
				});
				for (int i = 0; i < actions.length; ++i)
				{
					final Integer action = actions[i];
					((IAAAService) result).accessAction(client, action).addResultListener(new IResultListener<Void>()
					{
						
						public void resultAvailable(Void result)
						{
							actionCounter.resultAvailable(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							actionCounter.exceptionOccurred(new AccessControlException("Not allowed: "+client + " " + exception));
						}
					});
				}
			}
		});
	}
}
