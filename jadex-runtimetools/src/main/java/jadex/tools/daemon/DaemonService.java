package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.annotation.XMLClassname;

/**
 *  The daemon service.
 */
public class DaemonService extends BasicService implements IDaemonService
{
	//-------- attributes --------
	
	/** The agent. */
	protected IMicroExternalAccess agent;
		
	//-------- constructors --------
	
	/**
	 *  Create a new helpline service.
	 */
	public DaemonService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IDaemonService.class, null);
		this.agent = (IMicroExternalAccess)agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(final StartOptions opt)
	{
		final Future ret = new Future();
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("startPlatform")
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.startPlatform(opt).addResultListener(new DelegationResultListener(ret));
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("shutdownPlatform")
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.shutdownPlatform(cid).addResultListener(new DelegationResultListener(ret));
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	public IFuture getPlatforms()
	{
		final Future ret = new Future();
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("getPlatforms")
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.getPlatforms().addResultListener(new DelegationResultListener(ret));
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(final IRemoteChangeListener listener)
	{
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("addChangeListener")
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.addChangeListener(listener);
				return null;
			}
		});
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(final IRemoteChangeListener listener)
	{
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("removeChangeListener")
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.removeChangeListener(listener);
				return null;
			}
		});
	}
}
