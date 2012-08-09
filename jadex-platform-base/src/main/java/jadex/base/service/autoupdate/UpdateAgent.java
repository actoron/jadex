package jadex.base.service.autoupdate;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.modelinfo.Startable;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Iterator;
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
			
			// Acknowledge creation if creator is not null
			
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
		return startUpdating();
	}
	
	/**
	 *  Start the update check.
	 */
	protected IFuture<Void> startUpdating()
	{
		final Future<Void> ret = new Future<Void>();
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> self = this;
				checkForUpdate().addResultListener(new ExceptionDelegationResultListener<UpdateInfo, Void>(ret)
				{
					public void customResultAvailable(UpdateInfo ui)
					{
						if(ui!=null)
						{
							performUpdate(ui).addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							agent.waitFor(interval, self);
						}
					}
				});
				return IFuture.DONE;
			}
		};
		
		agent.waitFor(interval, step);

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
	public IFuture<Void> performUpdate(UpdateInfo ui)
	{
		System.out.println("perform update: "+ui);
		
		final Future<Void> ret = new Future<Void>();
		
		if(separatevm)
		{
			generateStartOptions(ui).addResultListener(new ExceptionDelegationResultListener<StartOptions, Void>(ret)
			{
				public void customResultAvailable(StartOptions so)
				{
					startPlatformInSeparateVM(so).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
					{
						public void customResultAvailable(IComponentIdentifier result) 
						{
							acknowledgeUpdate(result);
						}
					});
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
	 *  Start a new platform in the same vm.
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
		
//		String comstr = "-component jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")";
//		String comstr = "-maven_dependencies true -component jadex.base.service.autoupdate.UpdateAgent.class(:"+deser+")";
//		System.out.println("generated: "+comstr);
		
//		StartOptions so = new StartOptions();
//		so.setMain("jadex.base.Starter");
//		so.setProgramArguments(comstr);
		
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
	 *  Start a new platform in a separate vm.
	 */
	public IFuture<IComponentIdentifier> startPlatformInSeparateVM(final StartOptions so)
	{
		System.out.println("Starting platform in separate vm");
		Thread.dumpStack();
		
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

//		final StartOptions so = new StartOptions();
//		so.setMain("jadex.base.Starter");
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
	 *  Generate the vm start options.
	 */
	protected IFuture<StartOptions> generateStartOptions(UpdateInfo ui)
	{
		final Future<StartOptions> ret = new Future<StartOptions>();
		final StartOptions so = new StartOptions();
		
		so.setMain("jadex.base.Starter");
		
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> vmargs = RuntimemxBean.getInputArguments();

		if(vmargs!=null && vmargs.size()>0)
		{
			so.setVMArguments(flattenStrings((Iterator)SReflect.getIterator(vmargs), " "));
		}
		
//		String cmd = System.getProperty("sun.java.command");
		
		IFuture<IComponentManagementService> fut = agent.getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, StartOptions>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(agent.getComponentIdentifier().getRoot())
					.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, StartOptions>(ret)
				{
					public void customResultAvailable(IExternalAccess plat)
					{
						plat.getArguments().addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, StartOptions>(ret)
						{
							public void customResultAvailable(Map<String, Object> args)
							{
								String[] pargs = (String[])args.get(Starter.PROGRAM_ARGUMENTS);
								if(pargs!=null)
								{
									so.setProgramArguments(flattenStrings((Iterator)SReflect.getIterator(pargs), " "));
								}
								ret.setResult(so);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public String flattenStrings(Iterator<String> it, String delim)
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0; it.hasNext(); i++)
		{
			buf.append(it.next());
			if(it.hasNext())
				buf.append(delim);
		}
		return buf.toString();
	}
	
	/**
	 *  Check if an update is available.
	 */
	protected IFuture<UpdateInfo> checkForUpdate()
	{
		return new Future<UpdateInfo>((UpdateInfo)null);
	}
	
}
