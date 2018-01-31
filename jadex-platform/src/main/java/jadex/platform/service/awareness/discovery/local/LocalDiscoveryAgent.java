package jadex.platform.service.awareness.discovery.local;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing local discovery using the file system.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
@RequiredServices(
{
//	@RequiredService(name="ms", type=IMessageService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="threadpool", type=IDaemonThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IAwarenessManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@ProvidedServices(@ProvidedService(type=IDiscoveryService.class, scope=Binding.SCOPE_PLATFORM))
@Arguments(
{
	@Argument(name="leasetime", clazz=Long.class, defaultvalue="30000L")
})
//@Properties(@NameValue(name="system", value="true"))
public class LocalDiscoveryAgent implements IDiscoveryService
{
	/** The discovery directory. */
	protected static final File DISCOVERY_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".jadex" + File.separator + "discovery");
	
	static
	{
		System.out.println("Jadex local discovery dir: "+DISCOVERY_DIR);
	}
	
	/** Access to agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The last awareness file that has been posted. */
	protected File lastpostedfile;
	
	/** The directory watch service. */
	protected Object watchservice;
	
	/** The undeleted files. */
	protected List<File> undeleted;
	
	/**
	 *  Implements the start.
	 *  
	 *  @return Null, when done.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		this.undeleted = new ArrayList<File>();
		
		if(!DISCOVERY_DIR.exists())
			DISCOVERY_DIR.mkdirs();
		
//		System.out.println("Local awareness dir: "+DISCOVERY_DIR);
		
		if(!(DISCOVERY_DIR.isDirectory() && DISCOVERY_DIR.canRead() && DISCOVERY_DIR.canWrite()))
		{
			agent.getLogger().warning("Discovery directory not accessible: " + DISCOVERY_DIR.getAbsolutePath());
			agent.killComponent();
		}
		else
		{
			try
			{
				final String old = DISCOVERY_DIR + File.separator+URLEncoder.encode(agent.getComponentIdentifier().getRoot().getLocalName(), "UTF-8");
				File[] files = DISCOVERY_DIR.listFiles(new FileFilter()
				{
					public boolean accept(File pathname)
					{
						return pathname.getAbsolutePath().startsWith(old);
					}
				});
				for(File file: files)
				{
//					System.out.println("Delete myself: "+file.getName());
					file.delete();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			scan();
			postInfo();
			
			final long updaterate = (long) (((Long) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("leasetime")) * 0.9);
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(updaterate, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if(watchservice == null)
						scan();
					
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
//				Object entrymodify = standardwatcheventkindsclazz.getField("ENTRY_MODIFY").get(null);
				
				// Register WatchService on path.
				Object kindsarray = Array.newInstance(wekindsclazz, 1);
				Array.set(kindsarray, 0, entrycreate);
//				Array.set(kindsarray, 1, entrymodify);
				Method registermethod = pathclazz.getMethod("register", new Class<?>[] { wsclazz, kindsarray.getClass() });
				registermethod.invoke(path, new Object[]{watchservice, kindsarray});
				
				IFuture<IDaemonThreadPoolService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("threadpool");
				IDaemonThreadPoolService tp = fut.get();
				final IExternalAccess ea = agent.getExternalAccess();
				
				tp.executeForever(new Runnable()
				{
					public void run()
					{
						final Exception[] ex = new Exception[1];
						try
						{
							Class<?> wkclazz = Class.forName("java.nio.file.WatchKey");
							Method polleventsmethod = wkclazz.getMethod("pollEvents", (Class<?>[]) null);
							final Method resetmethod = wkclazz.getMethod("reset", (Class<?>[]) null);
							Method takemethod = wsclazz.getMethod("take", (Class<?>[])null);
							while(ex[0]==null)
							{
								final Object val = takemethod.invoke(watchservice, (Object[])null);
								if (val!=null)
								{
									Object evs = polleventsmethod.invoke(val, (Object[]) null);
									try{resetmethod.invoke(val, (Object[]) null);} catch(Exception e){}

//									for(Object ev: SReflect.getIterable(evs))
//									{
//										WatchEvent we = (WatchEvent)ev;
//										System.out.println(we.kind()+" "+we.context());
//									}
									
									// Test if agent is alive in intervals and end thread otherwise
//									System.out.println("pollend: "+val);
									
									ea.scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											scan();
											return IFuture.DONE;
										}
									}).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
//											try{resetmethod.invoke(val, (Object[]) null);} catch(Exception e){}
										}
										
										public void exceptionOccurred(Exception exception)
										{
											ex[0] = exception;
//											try{resetmethod.invoke(val, (Object[]) null);} catch(Exception e){}
										}
									});
								}
							}
						}
						catch (InvocationTargetException e)
						{
							if(e.getTargetException() instanceof InterruptedException)
							{
								// Platform shutdown -> ignore
								// TODO: clean shutdown of discovery agent
							}
							else
							{
								e.printStackTrace();
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				// Use polling as fallback.
				watchservice = null;
			}
		}
		
		SServiceProvider.getLocalService(agent, ITransportAddressService.class).subscribeToLocalAddresses().addIntermediateResultListener(new IIntermediateResultListener<Tuple2<TransportAddress,Boolean>>()
		{
			public void exceptionOccurred(Exception exception)
			{
				agent.getLogger().warning(exception.toString());
			}
			
			public void resultAvailable(Collection<Tuple2<TransportAddress, Boolean>> result)
			{				
			}
			
			public void intermediateResultAvailable(Tuple2<TransportAddress, Boolean> result)
			{
//				System.out.println("new result " + result.getFirstEntity());
				postInfo();
			}
			
			public void finished()
			{
			}
		});
		
		return IFuture.DONE;
	}
	
	/**
	 *  Notifies a change occurred.
	 */
//	public void changeOccurred()
//	{
////		System.out.println("Change occured: " + agent + " " + event.getSource());
////		if (agent.getComponentIdentifier().getRoot().equals(event.getSource()))
////			postInfo();
//	}
	
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
	
	/**
	 *  Post awareness info about myself.
	 */
	protected void postInfo()
	{
		removeUndeleted();
		
//		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>() 
//		{
//			public IFuture<Void> execute(IInternalAccess ia) 
//			{
//				System.out.println("post info");
//				Thread.dumpStack();
				
//				final String awa = SReflect.getInnerClassName(this.getClass());
				final String awa = "Local";
//				IFuture<IMessageService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("ms");
//				IMessageService cms = fut.get();
//				IMessageService	cms	= SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//				ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				
//				IFuture<IComponentIdentifier> fut2 = cms.updateComponentIdentifier(agent.getComponentIdentifier().getRoot());
//				IFuture<ITransportComponentIdentifier> fut2 = tas.getTransportComponentIdentifier(agent.getComponentIdentifier().getRoot());
//				ITransportComponentIdentifier root = fut2.get();
				IComponentIdentifier root = agent.getComponentIdentifier().getRoot();
//				Map<String, String[]> addr = TransportAddressBook.getAddressBook(root).getAllPlatformAddresses(root);
				List<TransportAddress> addr = SServiceProvider.getLocalService(agent, ITransportAddressService.class).getAddresses().get();
				
//				System.out.println("=====" + agent + "======");
//				for (TransportAddress entry : addr)
//				{
//					System.out.println("POST " + agent + " " + entry);
//				}
//				System.out.println("=====" + agent + "======");
//				
				long leasetime = (Long)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("leasetime");
				AwarenessInfo info = new AwarenessInfo(root, addr, AwarenessInfo.STATE_ONLINE, leasetime, null, null, null, awa);
				byte[] data = SBinarySerializer.writeObjectToByteArray(info, agent.getClassLoader());
				long deadline = leasetime + System.currentTimeMillis();
				String outfilepath = DISCOVERY_DIR + File.separator; 
//				outfilepath += new String(Base64.encodeNoPadding(agent.getComponentIdentifier().getRoot().getLocalName().getBytes(SUtil.UTF8)), SUtil.UTF8);
				
				FileOutputStream fos = null;
				try
				{
					outfilepath += URLEncoder.encode(agent.getComponentIdentifier().getRoot().getLocalName(), "UTF-8");
					outfilepath += new String();
					outfilepath += "_" + String.valueOf(deadline) + ".awa";
					File outfile = new File(outfilepath);
					
					fos = new FileOutputStream(outfile);
					fos.write(data);
					fos.close();
					outfile.deleteOnExit();
					
					if(lastpostedfile != null)
					{
						if(!lastpostedfile.delete())
							undeleted.add(lastpostedfile);
//						try
//						{
//							java.nio.file.Files.delete(Paths.get(lastpostedfile.getAbsolutePath()));
//						}
//						catch(Exception e)
//						{
////							e.printStackTrace();
//							undeleted.add(lastpostedfile);
//						}
//						if(!lastpostedfile.delete())
//							System.out.println("Could not delete old file: "+lastpostedfile.getName());
					}
//					System.out.println("Created: "+outfile.getName());
//					if(lastpostedfile!=null)
//						System.out.println("Dele: "+lastpostedfile.getName());

					lastpostedfile = outfile;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					if(fos != null)
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
				
//				return IFuture.DONE;
//			}
//		});
	}
	
	/**
	 *  Remove the undeleted files.
	 */
	protected void removeUndeleted()
	{
		File[] files = undeleted.toArray(new File[undeleted.size()]);
		for(File file: files)
		{
			if(file.delete())
				undeleted.remove(file);
		}
	}
	
	/**
	 *  Scans for new local awareness infos.
	 */
	protected void scan()
	{
		removeUndeleted();
		
//		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>() 
//		{
//			public IFuture<Void> execute(IInternalAccess ia) 
//			{
				File[] files = DISCOVERY_DIR.listFiles();
//				System.out.println("FILES of " + agent + ": " + Arrays.toString(files));
				Map<String, List<Tuple2<AwarenessInfo, Long>>> awas = new HashMap<String, List<Tuple2<AwarenessInfo, Long>>>();
				for(File file : files)
				{
					if(file.getAbsolutePath().endsWith(".awa"))
					{
						try
						{
//							String leasetimestr = file.getAbsolutePath();
							String leasetimestr = file.getName();
							leasetimestr = leasetimestr.substring(0, leasetimestr.length() - 4);
							int index = leasetimestr.lastIndexOf('_');
							String pname = leasetimestr.substring(0, index);
							leasetimestr = leasetimestr.substring(index + 1);
							long leasetime = Long.parseLong(leasetimestr);
							if(leasetime < System.currentTimeMillis())
							{
		//						System.out.println("Delete: "+file.getName()+" "+System.currentTimeMillis());
								file.delete();
							}
							else
							{
								byte[] awadata = SUtil.readFile(file);
								final AwarenessInfo awainfo = (AwarenessInfo)SBinarySerializer.readObjectFromByteArray(awadata, null, null, agent.getClassLoader(), null);
								if(!awainfo.getSender().equals(agent.getComponentIdentifier().getRoot()))
								{
									List<Tuple2<AwarenessInfo, Long>> ls = awas.get(pname);
									if(ls==null)
									{
										ls = new ArrayList<Tuple2<AwarenessInfo, Long>>();
										awas.put(pname, ls);
									}
									ls.add(new Tuple2<AwarenessInfo, Long>(awainfo, leasetime));
								}
							}
						}
						catch (Exception e)
						{
						}
					}
				}
				
				// Using windows it cannot be avoided that entries are cleaned up with some delay
				// Hence first all entries for a platform are collected and only the newest is advertised
				for(Map.Entry<String, List<Tuple2<AwarenessInfo, Long>>> entry: awas.entrySet())
				{
					final List<Tuple2<AwarenessInfo, Long>> es = entry.getValue();
					Collections.sort(es, new Comparator<Tuple2<AwarenessInfo, Long>>()
//					es.sort(new Comparator<Tuple2<AwarenessInfo, Long>>()
					{
						public int compare(Tuple2<AwarenessInfo, Long> o1, Tuple2<AwarenessInfo, Long> o2)
						{
							return (int)(o2.getSecondEntity()-o1.getSecondEntity());
						}
					});
					
					final AwarenessInfo ai = es.get(0).getFirstEntity();
					
					IFuture<IAwarenessManagementService> msfut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("management");
					msfut.addResultListener(new IResultListener<IAwarenessManagementService>()
					{
						public void resultAvailable(IAwarenessManagementService ms)
						{
//							System.out.println("addAware: "+ai+" "+es);
							ms.addAwarenessInfo(ai);
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					});
				}
				
//				return IFuture.DONE;
//			}
//		});
	}
}
