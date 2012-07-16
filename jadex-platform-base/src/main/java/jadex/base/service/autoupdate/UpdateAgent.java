package jadex.base.service.autoupdate;

import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Daemon agent provides functionalities for managing platforms.
 */
@RequiredServices(
{	
	@RequiredService(name="libser", type=ILibraryService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="depser", type=IDependencyService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Agent
@Arguments(@Argument(name="interval", clazz=long.class, defaultvalue="10000"))
public class UpdateAgent 
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The check for update interval. */
	@AgentArgument
	protected long interval;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IResourceIdentifier rid = new ResourceIdentifier(null, new GlobalResourceIdentifier("net.sourceforge.jadex:jadex-platform-standalone:2.1-SNAPSHOT", null, null));
		
		getVersion(rid).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
		{
			public void customResultAvailable(IResourceIdentifier newrid)
			{
				startUpdating(newrid);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> startUpdating(final IResourceIdentifier rid)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.waitFor(interval, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				checkForUpdate(rid).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
				{
					public void customResultAvailable(IResourceIdentifier newrid)
					{
						String d1s = rid.getGlobalIdentifier().getVersionInfo();
						String d2s = rid.getGlobalIdentifier().getVersionInfo();
						Date d1 = new Date(new Long(d1s).longValue());
						Date d2 = new Date(new Long(d2s).longValue());
						if(d2.after(d1))
						{
							System.out.println("update needed: "+d1+" "+d2);
						}
						ret.setResult(null);
					}
				});
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<IResourceIdentifier> checkForUpdate(final IResourceIdentifier rid)
	{
		return getVersion(rid);
	}
	
	/**
	 * 
	 */
	protected IFuture<IResourceIdentifier> getVersion(final IResourceIdentifier rid)
	{
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		IFuture<IDependencyService> fut = agent.getServiceContainer().getRequiredService("depser");
		fut.addResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
		{
			public void customResultAvailable(IDependencyService depser)
			{
				depser.loadDependencies(rid, false).addResultListener(new ExceptionDelegationResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>, IResourceIdentifier>(ret)
				{
					public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> result)
					{
						IResourceIdentifier newrid = result.getFirstEntity();
						System.out.println("versions: "+rid.getGlobalIdentifier().getVersionInfo()+" "+newrid.getGlobalIdentifier().getVersionInfo());
						ret.setResult(newrid);
					}
				});
			}
		});
		return ret;
	}
}
