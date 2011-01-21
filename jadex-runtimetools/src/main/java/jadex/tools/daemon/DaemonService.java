package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.micro.IMicroExternalAccess;

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
			public static final String XML_CLASSNAME = "startPlatform"; 
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
			public static final String XML_CLASSNAME = "shutdownPlatform"; 
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
			public static final String XML_CLASSNAME = "getPlatforms"; 
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
			public static final String XML_CLASSNAME = "addChangeListener"; 
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
			public static final String XML_CLASSNAME = "removeChangeListener"; 
			public Object execute(IInternalAccess ia)
			{
				DaemonAgent agent = (DaemonAgent)ia;
				agent.removeChangeListener(listener);
				return null;
			}
		});
	}
}
