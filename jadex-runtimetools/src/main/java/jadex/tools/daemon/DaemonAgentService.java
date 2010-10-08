package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IService;
import jadex.micro.IMicroExternalAccess;

/**
 * 
 */
public class DaemonAgentService extends BasicService implements IDaemonService, IService
{
	//-------- attributes --------
	
	/** The agent. */
	protected IMicroExternalAccess agent;
	
	/** The daemon service. */
	protected IDaemonService ds;
	
	//-------- constructors --------
	
	/**
	 *  Create a new helpline service.
	 */
	public DaemonAgentService(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IDaemonService.class, null);
		this.agent = (IMicroExternalAccess)agent;
		this.ds = new DaemonService();
	}
	
	//-------- methods --------
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(String[] args)
	{
		return ds.startPlatform(args);
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(IComponentIdentifier cid)
	{
		return ds.shutdownPlatform(cid);
	}
}
