package jadex.base.service.autoupdate;

import jadex.base.Starter;
import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.bean.JavaWriter;
import jadex.xml.writer.AWriter;
import jadex.xml.writer.XMLWriterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  The update agent can be used to restart the platform with a newer version.
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
	@Argument(name="interval", clazz=long.class, defaultvalue="5000"),
	@Argument(name="creator", clazz=IComponentIdentifier.class),
	@Argument(name="separatevm", clazz=boolean.class, defaultvalue="true")
})
@ComponentTypes(
{
	@ComponentType(name="daemon", filename="jadex/base/service/daemon/DaemonAgent.class")
})
public class UpdateAgent implements IUpdateService
{
	//-------- attributes --------
	
	/** The resource to update. */
	protected IResourceIdentifier rid = new ResourceIdentifier(null, new GlobalResourceIdentifier("net.sourceforge.jadex:jadex-platform-standalone:2.1-SNAPSHOT", null, null));

	/** The creator. */
	@AgentArgument
	protected IComponentIdentifier creator;
	
	/** The check for update interval. */
	@AgentArgument
	protected long interval;

	/** Flag if new vm should be used. */
	@AgentArgument
	protected boolean separatevm;
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The cms. */
	@AgentService
	protected IComponentManagementService cms;
	
	/** The new cid (need to be acknowledge by create and via call ack). */
	protected IComponentIdentifier newcomp;
	
	//-------- methods --------
	
	/**
	 *  Called on component startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(creator!=null)
		{
//			System.out.println("ack creator: "+creator);
			
			Map<String, Object> msg = new HashMap<String, Object>();
			msg.put(SFipa.RECEIVERS, creator);
			agent.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).addResultListener(new DelegationResultListener<Void>(ret));
			
			// difficult with service as no proxy to the other platform may exist
	//		agent.getServiceContainer().getService(IUpdateService.class, cid)
	//			.addResultListener(new ExceptionDelegationResultListener<IUpdateService, Void>(ret)
	//		{
	//			public void customResultAvailable(IUpdateService us)
	//			{
	//				us.acknowledgeUpdate().addResultListener(new DelegationResultListener<Void>(ret));
	//			}
	//		});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(creator==null)
		{
			getVersion(rid).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
			{
				public void customResultAvailable(IResourceIdentifier newrid)
				{
					startUpdating(newrid);
				}
			});
		}
		
		return ret;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Called when message arrived.
	 */
	// hack?: done with message as awareness must not be used so there
	// is no gauarantee that a proxy to the other platform exists (or would have to be created).
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
//		System.out.println("rec: "+msg);
		if(mt.getName().equals(SFipa.MESSAGE_TYPE_NAME_FIPA))
		{
			IComponentIdentifier sender = (IComponentIdentifier)msg.get(SFipa.SENDER);
			acknowledgeUpdate(sender.getRoot());
		}
	}
	
//	/**
//	 * 
//	 */
//	public IFuture<Void> acknowledgeUpdate()
//	{
//		IComponentIdentifier caller = ServiceCall.getInstance().getCaller();
//		acknowledgeUpdate(caller);
//		return IFuture.DONE;
//	}
	
	/**
	 *  Called by new platform after correct startup.
	 *  
	 *  Acknowledgement is complete when called twice:
	 *  a) after creation with cid of new platform
	 *  b) after new update agent has send ack to this agent (handshake)
	 *  
	 *  After ack is complete platform shutdown will be initiated.
	 */
	public void acknowledgeUpdate(IComponentIdentifier caller)
	{
//		System.out.println("ack: "+caller);
		
		if(caller.equals(newcomp))
		{
//			System.out.println("Update acknowledged, shutting down old platform: "+agent.getComponentIdentifier());
			cms.destroyComponent(agent.getComponentIdentifier().getRoot());
		}
		else if(newcomp==null)
		{
			newcomp = caller;
		}
	}
	
	/**
	 *  Perform the update.
	 */
	public IFuture<Void> performUpdate()
	{
//		System.out.println("perform update");
		
		return performUpdate(rid);
	}
	
	/**
	 *  Perform the update.
	 */
	protected IFuture<Void> performUpdate(IResourceIdentifier rid)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(separatevm)
		{
			startPlatformInSeparateVM().addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
			{
				public void customResultAvailable(IComponentIdentifier result) 
				{
					acknowledgeUpdate(result);
				}
			});
		}
		else
		{
			startPlatformInSameVM().addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
			{
				public void customResultAvailable(IComponentIdentifier result) 
				{
					acknowledgeUpdate(result);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<IComponentIdentifier> startPlatformInSameVM()
	{
		System.out.println("Starting platform in same vm");
		
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		// todo: create new classpath for new version 
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("creator", agent.getComponentIdentifier());
		String argsstr = AWriter.objectToXML(XMLWriterFactory.getInstance().createWriter(true, false, false), args, null, JavaWriter.getObjectHandler());
//		String argsstr = JavaWriter.objectToXML(args, null);
		argsstr = argsstr.replaceAll("\"", "\\\\\"");
		String deser = "jadex.xml.bean.JavaReader.objectFromXML(\""+argsstr+"\""+",null)";
//		
		// todo: find out original configuration and parameters to replay on new
		// todo for major release: make checkpoint and let new use checkpoint
		
		String comstr = "-component jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")";
//		String comstr = "-maven_dependencies true -component jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")";
//		System.out.println("generated: "+comstr);
		
		StartOptions so = new StartOptions();
		so.setMain("jadex.base.Starter");
		so.setProgramArguments(comstr);
		
//		Starter.createPlatform(new String[]{"-component", "jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")"})
//		Starter.createPlatform(new String[]{"-deftimeout", "-1", "-component", "jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")"})
		Starter.createPlatform(new String[]{"-maven_dependencies", "true", "-component", "jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")"})
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				IComponentIdentifier cid = result.getComponentIdentifier();
				ret.setResult(cid);
//				acknowledgeUpdate(cid.getRoot());
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<IComponentIdentifier> startPlatformInSeparateVM()
	{
		System.out.println("Starting platform in separate vm");
		
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		// todo: create new classpath for new version? 
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("creator", agent.getComponentIdentifier());
		String argsstr = AWriter.objectToXML(XMLWriterFactory.getInstance().createWriter(true, false, false), args, null, JavaWriter.getObjectHandler());
//		String argsstr = JavaWriter.objectToXML(args, null);
		argsstr = argsstr.replaceAll("\"", "\\\\\\\\\\\\\"");
		String deser = "jadex.xml.bean.JavaReader.objectFromXML(\\\""+argsstr+"\\\""+",null)";
//		
		// todo: find out original configuration and parameters to replay on new
		// todo for major release: make checkpoint and let new use checkpoint

//		String comstr = "-maven_dependencies true -component \"jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")\"";
		String comstr = "-component \"jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")\"";
		System.out.println("generated: "+comstr);

		final StartOptions so = new StartOptions();
		so.setMain("jadex.base.Starter");
		so.setProgramArguments(comstr);
		
		IFuture<IDaemonService> fut = agent.getRequiredService("daeser");
		fut.addResultListener(new ExceptionDelegationResultListener<IDaemonService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IDaemonService daeser)
			{
				daeser.startPlatform(so).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)); 
			}
		});
		
		return ret;
	}

	//-------- helper methods --------
	
	/**
	 *  Start the update check.
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
						performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
						
//						String d1s = rid.getGlobalIdentifier().getVersionInfo();
//						String d2s = rid.getGlobalIdentifier().getVersionInfo();
//						if(d1s!=null && d2s!=null)
//						{
//							try
//							{
//								Date d1 = new Date(new Long(d1s).longValue());
//								Date d2 = new Date(new Long(d2s).longValue());
//								if(d2.after(d1))
//								{
//									performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
//								}
//							}
//							catch(Exception e)
//							{
//								e.printStackTrace();
//							}
//						}
//						else
//						{
//							ret.setResult(null);
//						}
					}
				});
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check if an update is available.
	 */
	protected IFuture<IResourceIdentifier> checkForUpdate(final IResourceIdentifier rid)
	{
		return getVersion(rid);
	}
	
	/**
	 *  Get the version for a resource.
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
//						System.out.println("versions: "+rid.getGlobalIdentifier().getVersionInfo()+" "+newrid.getGlobalIdentifier().getVersionInfo());
						ret.setResult(newrid);
					}
				});
			}
		});
		return ret;
	}
	

	
}
