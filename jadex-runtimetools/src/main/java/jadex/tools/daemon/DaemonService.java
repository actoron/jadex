package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IService;
import jadex.micro.IMicroExternalAccess;

/**
 * 
 */
public class DaemonService extends BasicService implements IDaemonService, IService
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
		
		agent.scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				DaemonAgent agent = (DaemonAgent)args;
				agent.startPlatform(opt).addResultListener(new DelegationResultListener(ret));
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
		
		agent.scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				DaemonAgent agent = (DaemonAgent)args;
				agent.shutdownPlatform(cid).addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
}
