package jadex.platform.service.registryv2;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registryv2.ISuperpeerStatusService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Base class for infrastructure tests that require multiple platforms.
 */
public abstract class AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** The delay time as factor of the default remote timeout. */
	public static final double	WAITFACTOR	= 0.05;	// 30 * 0.05 secs  -> 1500 millis.
	
	//-------- life cycle and helpers --------
	
	/** Started platforms for later cleanup. */
	protected Collection<IExternalAccess>	platforms;
	
	/**
	 *  Test setup code.
	 */
	@Before
	public void setup()
	{
		platforms	= new ArrayList<>();
	}
	
	/**
	 *  Test cleanup code.
	 */
	@After
	public void tearDown()
	{
		for(IExternalAccess platform: platforms)
		{
			try
			{
				platform.killComponent().get();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Create a platform with a given config.
	 */
	protected IExternalAccess	createPlatform(IPlatformConfiguration config)
	{
		IExternalAccess	ret	= Starter.createPlatform(config).get();
		platforms.add(ret);
		return ret;
	}
	
	/**
	 *  Stop and remove the given platform.
	 */
	protected void	removePlatform(IExternalAccess platform)
	{
		platform.killComponent().get();
		platforms.remove(platform);
	}
	
	/**
	 *  Wait a small amount of time (@see WAITFACTOR).
	 */
	protected void waitALittle(IExternalAccess platform)
	{
		platform.waitForDelay(Starter.getScaledRemoteDefaultTimeout(platform.getId(), WAITFACTOR), new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return IFuture.DONE;
			}
		}, true).get();
	}
	
	/**
	 *  Wait until all clients have connected to superpeer.
	 *  @param platforms The superpeer (first value) and other platforms that need to connect.
	 */
	protected void	waitForSuperpeerConnections(IExternalAccess sp, IExternalAccess... clients)
	{
		ISuperpeerStatusService	status	= sp.searchService(new ServiceQuery<>(ISuperpeerStatusService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
		ISubscriptionIntermediateFuture<IComponentIdentifier>	connected	= status.getRegisteredClients();
		Set<IComponentIdentifier>	platformids	= new LinkedHashSet<>();
		for(IExternalAccess ea: clients)
		{
			platformids.add(ea.getId());
		}
		while(!platformids.isEmpty())
		{
			// Hack??? Wait three times the wait factor!?
			IComponentIdentifier	cid	= connected.getNextIntermediateResult(Starter.getScaledRemoteDefaultTimeout(sp.getId(), WAITFACTOR*30), true);
			platformids.remove(cid.getRoot());
			System.out.println("Client "+cid+" connected to SP: "+sp.getId());
		}
	}
}
