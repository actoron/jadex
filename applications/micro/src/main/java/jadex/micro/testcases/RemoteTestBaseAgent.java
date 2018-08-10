package jadex.micro.testcases;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;

import jadex.base.Starter;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentKilled;

/**
 *  Base class for test agents using remote platforms.
 *  Provides proxy creation and cleanup.
 */
@Agent
@Ignore
public class RemoteTestBaseAgent  extends JunitAgentTest
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	private IInternalAccess	agent;
	
	/** The proxies, if any. */
	protected Set<IComponentIdentifier>	proxies	= new LinkedHashSet<IComponentIdentifier>();
	
	//-------- life cycle methods --------
	
	/**
	 *  Cleanup created proxies.
	 */
	@AgentKilled
	public IFuture<Void>	cleanup()
	{
		FutureBarrier<Map<String, Object>>	fubar	= new FutureBarrier<Map<String,Object>>();
		
		IComponentManagementService	cms	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class));
		for(IComponentIdentifier proxy: proxies)
		{
			IFuture<Map<String, Object>>	kill	= cms.destroyComponent(proxy);
			fubar.addFuture(kill);
		}
		proxies	= null;

		return fubar.waitForIgnoreFailures(new ICommand<Exception>()
		{
			@Override
			public void execute(Exception args)
			{
				agent.getLogger().warning("Failure in cleanup: "+SUtil.getExceptionStacktrace(args));
			}
		});
	}

	//-------- methods --------
	
	/**
	 *  Create proxies to connect local and remote platform
	 *  @param	remote	external access of a remote platform (or agent). 
	 */
	protected IFuture<Void>	createProxies(final IExternalAccess remote)
	{
		final Future<Void>	ret	= new Future<Void>();
		Starter.createProxy(agent.getExternalAccess(), remote).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				proxies.add(result.getId());
				
				// inverse proxy from remote to local.
				Starter.createProxy(remote, agent.getExternalAccess())
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
				{
					public void customResultAvailable(IExternalAccess result)
					{
						// Hack!!! Don't remove remote proxies. Expected that platform is killed anyways.
//						proxies.add(result);
//						agent.getLogger().severe("Testagent setup remote platform done: "+agent.getComponentDescription());
						ret.setResult(null);
					}
				}));
			}
		});
		return ret;
	}
}
