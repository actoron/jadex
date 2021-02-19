package jadex.platform.service.awareness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jadex.binary.SBinarySerializer;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.OnService;

/**
 *  Uses the filesystem to find other platforms on the same host.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE,	autostart=Boolean3.TRUE)
@Arguments({@Argument(name="leasetime", clazz=Long.class, defaultvalue="30000L")})
public class LocalHostAwarenessAgent implements IAwarenessService
{
	/** The discovery directory. */
	protected static final File DISCOVERY_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + "jadexlocaldiscovery");

	/** The agent access. */
	@Agent
	protected IInternalAccess	agent;

	@OnService(query=Boolean3.TRUE, required=Boolean3.TRUE)
	protected ITransportAddressService tas;
	
	/** The internal catalog. */
	protected Map<IComponentIdentifier, List<TransportAddress>> platforms = new HashMap<>();
	
	/** The last awareness file that has been posted. */
	protected File lastpostedfile;
	
	/** The directory watch service. */
	protected WatchService watchservice;
	
	/** Do scan flag. */
	protected boolean doscan;
	
	/** ServiceQuery looking for new Transports */
	protected ISubscriptionIntermediateFuture<ITransportService> tpquery;
	
	/**
	 *  Creates the agent empty.
	 */
	public LocalHostAwarenessAgent()
	{
	}
	
	/**
	 *  Implements the init.
	 */
	@OnInit
	public void init()
	{
		if(!DISCOVERY_DIR.exists())
			DISCOVERY_DIR.mkdir();
		
		if(!(DISCOVERY_DIR.isDirectory() && DISCOVERY_DIR.canRead() && DISCOVERY_DIR.canWrite()))
		{
			agent.getLogger().warning("Discovery directory not accessible: " + DISCOVERY_DIR.getAbsolutePath());
			agent.killComponent();
		}
		else
		{
			postInfo();
			
			// Listen for transport to update addresses
			ServiceQuery<ITransportService> query = new ServiceQuery<>(ITransportService.class);
			query.setScope(ServiceScope.PLATFORM);
			tpquery = agent.addQuery(query);
			tpquery.then(res ->
			{
				postInfo();
			});
			
			// Leasetime for posting infos
			final long updaterate = (long)(((Long)agent.getArguments().get("leasetime"))*0.9);
			agent.waitForDelay(updaterate, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Only scan in polling mode when watchservice does not work
					if(watchservice == null)
					{
						doscan = true;
						//scan();
					}
					
					postInfo();
					agent.waitForDelay(updaterate, this, true);
					return IFuture.DONE;
				}
			}, true);
					
			try
			{
				IDaemonThreadPoolService tp = agent.getLocalService(IDaemonThreadPoolService.class);
				final IExternalAccess ea = agent.getExternalAccess();
				
				Path path = DISCOVERY_DIR.toPath();
				FileSystem fs = FileSystems.getDefault();
				watchservice = fs.newWatchService();
				path.register(watchservice, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
				
				tp.execute(new Runnable()
				{
					public void run()
					{
						final Exception[] ex = new Exception[1];
						try
						{
							while(ex[0]==null)
							{
								final WatchKey val = watchservice.poll(5,  TimeUnit.SECONDS);
								// Test if agent is alive in intervals and end thread otherwise
//								System.out.println("pollend: "+val);
								ea.scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
//										System.out.println("poll in agent: "+val);
										// only scan if call was due to new watchkey
										if(val!=null)
										{
											doscan = true;
											//scan();
										}
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
	}
	
	/**
	 *  Implements the start.
	 */
	@OnEnd
//	public IFuture<Void> end(Exception e)
	public IFuture<Void> end()
	{
		if (tpquery != null)
			tpquery.terminate();
//		System.out.println("Terminated agent: "+e);
//		e.printStackTrace();
		return IFuture.DONE;
	}
	
	/**
	 *  Post awareness info via file system.
	 */
	protected void postInfo()
	{
		//ITransportAddressService tas = agent.searchLocalService(new ServiceQuery<ITransportAddressService>(ITransportAddressService.class, ServiceScope.PLATFORM));
		List<TransportAddress> addrs = tas.getAddresses().get();
		
		long leasetime = (Long)agent.getArguments().get("leasetime");
		Tuple2<IComponentIdentifier, List<TransportAddress>> info = new Tuple2<>(agent.getId().getRoot(), addrs);
		byte[] data = SBinarySerializer.writeObjectToByteArray(info, agent.getClassLoader());
		
		long deadline = leasetime + System.currentTimeMillis();
		
		String outfilepath = DISCOVERY_DIR + File.separator + agent.getId().getRoot().getLocalName() + "_" + String.valueOf(deadline) + ".awa";
		File outfile = new File(outfilepath);
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(outfile);
			fos.write(data);
			fos.close();
			outfile.deleteOnExit();
			
			if(lastpostedfile != null)
				lastpostedfile.delete();
			
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
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
		if(doscan)
		{
			doscan = false;
			scan();
		}
		return new IntermediateFuture<>(new LinkedHashSet<>(platforms.keySet()));
	}
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<List<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		Future<List<TransportAddress>> ret = new Future<>();
		List<TransportAddress> addrs = platforms.get(platformid);
		if(addrs != null)
			ret.setResult(new ArrayList<>(addrs));
		else
			ret.setResult(null);
		return ret;
	}
	
	/**
	 *  Scans for new local awareness infos.
	 */
	protected void scan()
	{
		File[] files = DISCOVERY_DIR.listFiles();
		for(File file : files)
		{
			if(file.getAbsolutePath().endsWith(".awa"))
			{
				try
				{
					String leasetimestr = file.getAbsolutePath();
					leasetimestr = leasetimestr.substring(0, leasetimestr.length() - 4);
					int index = leasetimestr.lastIndexOf('_');
					leasetimestr = leasetimestr.substring(index + 1);
					long leasetime = Long.parseLong(leasetimestr);
					if(leasetime < System.currentTimeMillis())
					{
						file.delete();
					}
					else
					{
						Tuple2<IComponentIdentifier, List<TransportAddress>> tup = (Tuple2<IComponentIdentifier, List<TransportAddress>>)SBinarySerializer.readObjectFromStream(new FileInputStream(file), agent.getClassLoader());
						platforms.put(tup.getFirstEntity(), tup.getSecondEntity());
					}
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}
