package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;

import java.util.Set;

/**
 *  The daemon service.
 */
@Service
public class DaemonService implements IDaemonService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
		
	/** The daemon agent. */
	protected DaemonAgent da;
	
	//-------- methods --------
	
	/**
	 *  Get the daemon agent.
	 */
	protected DaemonAgent getDaemonAgent()
	{
		if(da==null)
		{
			da = (DaemonAgent)((IPojoMicroAgent)agent).getPojoAgent();
		}
		return da;
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture<IComponentIdentifier> startPlatform(final StartOptions opt)
	{
		return getDaemonAgent().startPlatform(opt);
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture<Void> shutdownPlatform(final IComponentIdentifier cid)
	{
		return getDaemonAgent().shutdownPlatform(cid);
	}
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	public IFuture<Set<IComponentIdentifier>>  getPlatforms()
	{
		return getDaemonAgent().getPlatforms();
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public IFuture<Void> addChangeListener(final IRemoteChangeListener<IComponentIdentifier> listener)
	{
		return getDaemonAgent().addChangeListener(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public IFuture<Void> removeChangeListener(final IRemoteChangeListener<IComponentIdentifier> listener)
	{
		return getDaemonAgent().removeChangeListener(listener);
	}
}
