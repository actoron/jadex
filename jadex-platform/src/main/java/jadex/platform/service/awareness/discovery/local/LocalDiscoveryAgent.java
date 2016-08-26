package jadex.platform.service.awareness.discovery.local;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing local discovery using the file system.
 *
 */
@Agent(autoprovide=true)
@Service
@RequiredServices(
{
	@RequiredService(name="ms", type=IMessageService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="threadpool", type=IDaemonThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IAwarenessManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Arguments(
{
	@Argument(name="leasetime", clazz=Long.class, defaultvalue="30000L")
})
@Properties(@NameValue(name="system", value="true"))
public class LocalDiscoveryAgent implements IDiscoveryService
{
	/** The discovery directory. */
	protected static final File DISCOVERY_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + "jadexlocaldiscovery");
	
	/** Access to agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The last awareness file that has been posted. */
	protected File lastpostedfile;
	
	/** The directory watch service. */
	protected Object watchservice;
	
	/**
	 *  Implements the start.
	 *  
	 *  @return Null, when done.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		if(!DISCOVERY_DIR.exists())
		{
			DISCOVERY_DIR.mkdir();
		}
		
		if(!(DISCOVERY_DIR.isDirectory() && DISCOVERY_DIR.canRead() && DISCOVERY_DIR.canWrite()))
		{
			agent.getLogger().warning("Discovery directory not accessible: " + DISCOVERY_DIR.getAbsolutePath());
			agent.killComponent();
		}
		else
		{
			scan();
			postInfo();
			
			final long updaterate = (long) (((Long) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("leasetime")) * 0.9);
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(updaterate, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if (watchservice == null)
					{
						scan();
					}
					
					postInfo();
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(updaterate, this, true);
					return IFuture.DONE;
				}
			}, true);
			
			try
			{
				// Directory modification notification using WatchService, reflection used for
				// Java 6 compatibility.
				// Create the path object.
				Class<?> pathclazz = Class.forName("java.nio.file.Path", true, agent.getClassLoader());
				Method topathmethod = File.class.getMethod("toPath", (Class<?>[]) null);
				Object path = topathmethod.invoke(DISCOVERY_DIR, (Object[]) null);
				
				// Get the default FileSystem using the factory.
				Class<?> fssclazz = Class.forName("java.nio.file.FileSystems", true, agent.getClassLoader());
				Method getdefaultmethod = fssclazz.getMethod("getDefault", (Class<?>[]) null);
				Class<?> fsclazz = Class.forName("java.nio.file.FileSystem", true, agent.getClassLoader());
				Object fs = getdefaultmethod.invoke(null, (Object[]) null);
				
				// Create new WatchService.
				final Class<?> wsclazz = Class.forName("java.nio.file.WatchService", true, agent.getClassLoader());
				Method newwsmethod = fsclazz.getMethod("newWatchService", (Class<?>[]) null);
				watchservice = newwsmethod.invoke(fs, (Object[]) null);
				
				// Get ENTRY_CREATE event type.
				Class<?> wekindsclazz = Class.forName("java.nio.file.WatchEvent$Kind", true, agent.getClassLoader());
				Class<?> standardwatcheventkindsclazz = Class.forName("java.nio.file.StandardWatchEventKinds", true, agent.getClassLoader());
				Object entrycreate = standardwatcheventkindsclazz.getField("ENTRY_CREATE").get(null);
				
				// Register WatchService on path.
				Object kindsarray = Array.newInstance(wekindsclazz, 1);
				Array.set(kindsarray, 0, entrycreate);
				Method registermethod = pathclazz.getMethod("register", new Class<?>[] { wsclazz, kindsarray.getClass() });
				registermethod.invoke(path, new Object[]{watchservice, kindsarray});
				
				IFuture<IThreadPool> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("threadpool");
				IThreadPool tp = fut.get();
				final IExternalAccess ea = agent.getExternalAccess();
				
				tp.execute(new Runnable()
				{
					public void run()
					{
						final Exception[] ex = new Exception[1];
						try
						{
							Method pollmethod = wsclazz.getMethod("poll", (Class<?>[])new Class[]{long.class, TimeUnit.class});
							while(ex[0]==null)
							{
								final Object val = pollmethod.invoke(watchservice, (Object[])new Object[]{5l, TimeUnit.SECONDS});
								// Test if agent is alive in intervals and end thread otherwise
//								System.out.println("pollend: "+val);
								ea.scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
//										System.out.println("poll in agent: "+val);
										// only scan if call was due to new watchkey
										if(val!=null)
											scan();
										return IFuture.DONE;
									}
								}).addResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ex[0] = exception;
									}
								});
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
			catch (Exception e)
			{
//				e.printStackTrace();
				// Use polling as fallback.
				watchservice = null;
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Set the send delay.
	 *  @param delay The delay.
	 */
	public void setDelay(long delay)
	{
	}
	
	/**
	 *  Set the fast awareness flag.
	 *  @param fast The fast flag.
	 */
	public void setFast(boolean fast)
	{
	}
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
	}
	
	/**
	 *  Republish the awareness info.
	 *  Called when some important property has changed, e.g. platform addresses.
	 */
	public void republish()
	{
		postInfo();
	}
	
	protected void postInfo()
	{
//		final String awa = SReflect.getInnerClassName(this.getClass());
		final String awa = "Local";
//		IFuture<IMessageService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("ms");
//		IMessageService cms = fut.get();
//		IMessageService	cms	= SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
//		IFuture<IComponentIdentifier> fut2 = cms.updateComponentIdentifier(agent.getComponentIdentifier().getRoot());
		IFuture<ITransportComponentIdentifier> fut2 = tas.getTransportComponentIdentifier(agent.getComponentIdentifier().getRoot());
		ITransportComponentIdentifier root = fut2.get();
		long leasetime = (Long) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("leasetime");
		AwarenessInfo info = new AwarenessInfo(root, AwarenessInfo.STATE_ONLINE, leasetime, null, null, null, awa);
		byte[] data = SBinarySerializer.writeObjectToByteArray(info, agent.getClassLoader());
		long deadline = leasetime + System.currentTimeMillis();
		String outfilepath = DISCOVERY_DIR + File.separator + agent.getComponentIdentifier().getRoot().getLocalName() + "_" + String.valueOf(deadline) + ".awa";
		File outfile = new File(outfilepath);
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(outfile);
			fos.write(data);
			fos.close();
			outfile.deleteOnExit();
			
			if (lastpostedfile != null)
			{
				lastpostedfile.delete();
			}
			
			lastpostedfile = outfile;
		}
		catch(Exception e)
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (Exception e1)
				{
				}
			}
		}
	}
	
	/**
	 *  Scans for new local awareness infos.
	 */
	protected void scan()
	{
		File[] files = DISCOVERY_DIR.listFiles();
		for (File file : files)
		{
			if (file.getAbsolutePath().endsWith(".awa"))
			{
				try
				{
					String leasetimestr = file.getAbsolutePath();
					leasetimestr = leasetimestr.substring(0, leasetimestr.length() - 4);
					int index = leasetimestr.lastIndexOf('_');
					leasetimestr = leasetimestr.substring(index + 1);
					long leasetime = Long.parseLong(leasetimestr);
					if (leasetime < System.currentTimeMillis())
					{
						file.delete();
					}
					else
					{
						byte[] awadata = SUtil.readFile(file);
						final AwarenessInfo awainfo = (AwarenessInfo) SBinarySerializer.readObjectFromByteArray(awadata, null, null, agent.getClassLoader(), null);
						if(!awainfo.getSender().equals(agent.getComponentIdentifier().getRoot()))
						{
							IFuture<IAwarenessManagementService> msfut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("management");
							msfut.addResultListener(new IResultListener<IAwarenessManagementService>()
							{
								public void resultAvailable(IAwarenessManagementService ms)
								{
									ms.addAwarenessInfo(awainfo);
								}
								
								public void exceptionOccurred(Exception exception)
								{
								}
							});
						}
					}
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}
