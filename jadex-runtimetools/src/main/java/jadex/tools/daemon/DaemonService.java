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
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.IMicroExternalAccess;
import jadex.tools.daemon.gui.DaemonAgent;

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
	public IFuture<Void> startPlatform(final StartOptions opt)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("startPlatform")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.startPlatform(opt).addResultListener(new DelegationResultListener(ret));
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture<Void> shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("shutdownPlatform")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.shutdownPlatform(cid).addResultListener(new DelegationResultListener(ret));
				return IFuture.DONE;
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
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("getPlatforms")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.getPlatforms().addResultListener(new DelegationResultListener(ret));
				return IFuture.DONE;
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
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addChangeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.addChangeListener(listener);
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(final IRemoteChangeListener listener)
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeChangeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.removeChangeListener(listener);
				return IFuture.DONE;
			}
		});
	}
}
