package jadex.base.service.autoupdate;

import jadex.base.Starter;
import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.writer.AWriter;
import jadex.xml.writer.XMLWriterFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Daemon agent provides functionalities for managing platforms.
 */
@Agent
@RequiredServices(
{	
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="depser", type=IDependencyService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="daeser", type=IDaemonService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, componenttype="daemon"))
})
@Arguments(
{
	@Argument(name="interval", clazz=long.class, defaultvalue="1000"),
	@Argument(name="creator", clazz=IComponentIdentifier.class),
})
@ComponentTypes(
{
	@ComponentType(name="daemon", filename="jadex/base/service/daemon/DaemonAgent.class")
})
public class UpdateAgent implements IUpdateService
{
	/** The resource to update. */
	protected IResourceIdentifier rid = new ResourceIdentifier(null, new GlobalResourceIdentifier("net.sourceforge.jadex:jadex-platform-standalone:2.1-SNAPSHOT", null, null));

	/** The creator. */
	@AgentArgument
	protected IComponentIdentifier creator;
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The check for update interval. */
	@AgentArgument
	protected long interval;
	
	/** The cms. */
	@AgentService
	protected IComponentManagementService cms;
	
	/** The new cid (need to be acknowledge by create and via call ack). */
	protected IComponentIdentifier newcomp;
	
	/**
	 * 
	 */
	@AgentCreated
	public void start()
	{
		ackCreator(creator, 0).addResultListener(new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
		});
	}
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		getVersion(rid).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
		{
			public void customResultAvailable(IResourceIdentifier newrid)
			{
				startUpdating(newrid);
			}
		});
		
		return ret;
	}
	
	//-------- interface methods --------
	
	/**
	 * 
	 */
	public IFuture<Void> acknowledgeUpdate()
	{
		IComponentIdentifier caller = ServiceCall.getInstance().getCaller();
		if(caller.getRoot().equals(newcomp))
		{
			System.out.println("update acknowledged, shutting down old");
			cms.destroyComponent(agent.getComponentIdentifier().getRoot());
		}
		else if(newcomp==null)
		{
			newcomp = caller;
		}
		
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> performUpdate()
	{
		System.out.println("perform update");
		
		return performUpdate(rid);
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> performUpdate(IResourceIdentifier rid)
	{
		final Future<Void> ret = new Future<Void>();
		
		
		IFuture<IDaemonService> fut = agent.getRequiredService("daeser");
		fut.addResultListener(new ExceptionDelegationResultListener<IDaemonService, Void>(ret)
		{
			public void customResultAvailable(IDaemonService daeser)
			{
				// todo: create new classpath for new version? 
				
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("creator", agent.getComponentIdentifier());
				String argsstr = AWriter.objectToXML(XMLWriterFactory.getInstance().createWriter(true, false, false), args, null, JavaWriter.getObjectHandler());
//				String argsstr = JavaWriter.objectToXML(args, null);
				argsstr = argsstr.replaceAll("\"", "\\\\\"");
				String deser = "jadex.xml.bean.JavaReader.objectFromXML(\""+argsstr+"\""+",null)";
				String comstr = "-component jadex.base.service.autoupdate.UpdateAgent.class:():"+deser;
				System.out.println("generated: "+comstr);
				
				StartOptions so = new StartOptions();
				so.setMain("jadex.base.Starter");
				so.setProgramArguments(comstr);
				
				Starter.createPlatform(new String[]{"-component", "jadex.base.service.autoupdate.UpdateAgent.class:():"+deser});
				
				daeser.startPlatform(so).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret) 
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						if(newcomp==null)
						{
							newcomp = cid;
						}
						else if(newcomp.equals(cid))
						{
							System.out.println("update acknowledged, shutting down old");
							cms.destroyComponent(agent.getComponentIdentifier().getRoot());
							ret.setResult(null);
						}
					}
				});
			}
		});
		
		return ret;
	}

	//-------- helper methods --------
	
	
	/**
	 * 
	 */
	protected IFuture<Void> ackCreator(final IComponentIdentifier cid, final int cnt)
	{
		if(cid==null)
			return IFuture.DONE;
		
		final Future<Void> ret = new Future<Void>();
		
		cms.getExternalAccess(cid).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess exta)
			{
				IFuture<IUpdateService> fut = SServiceProvider.getService(exta.getServiceProvider(), IUpdateService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				fut.addResultListener(new ExceptionDelegationResultListener<IUpdateService, Void>(ret)
				{
					public void customResultAvailable(IUpdateService us)
					{
						us.acknowledgeUpdate().addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.waitFor(5000, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(cnt<3)
						{
							ackCreator(cid, cnt+1).addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							ret.setException(new RuntimeException("Could not acknowledge at creator."));
						}
						return IFuture.DONE;
					}
				});
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
						if(d1s!=null && d2s!=null)
						{
							try
							{
								Date d1 = new Date(new Long(d1s).longValue());
								Date d2 = new Date(new Long(d2s).longValue());
								if(d2.after(d1))
								{
									performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
								
								// todo: hack
								performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
							}
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
