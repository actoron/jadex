package jadex.wfms.service.impl;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.service.IClientConnector;
import jadex.wfms.service.IWfmsClientService;

public class WfmsClientService extends BasicService implements IWfmsClientService
{
	private IServiceContainer provider;
	
	public WfmsClientService(IServiceContainer provider)
	{
		super(provider.getId(), IWfmsClientService.class, null);
		this.provider = provider;
	}
	
	/**
	 * Queues a new workitem.
	 * @param workitem new workitem
	 * @param listener listener used when the workitem has been performed
	 */
	public IFuture queueWorkitem(final IWorkitem workitem, final IResultListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IClientConnector) result).queueWorkitem(workitem, listener);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @return current activities for all users
	 */
	public IFuture getUserActivities()
	//public Map getUserActivities()
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(((IClientConnector) result).getUserActivities());
			}
		});
		return ret;
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 */
	public IFuture terminateActivity(final IClientActivity activity)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IClientConnector) result).terminateActivity(activity);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IActivityListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IClientConnector) result).addActivityListener(listener);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 */
	public IFuture removeActivityListener(final IActivityListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IClientConnector) result).removeActivityListener(listener);
				ret.setResult(null);
			}
		});
		return ret;
	}
}
