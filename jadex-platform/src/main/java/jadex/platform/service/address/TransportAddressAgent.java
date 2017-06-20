package jadex.platform.service.address;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.commons.Base64;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Properties;

/**
 * 
 */
@Agent 
//@ProvidedServices(@ProvidedService(type=ITransportAddressService.class, implementation=@Implementation(TransportAddressService.class)))
@Properties(value=@NameValue(name="system", value="true"))
@Arguments(
{
	@Argument(name="leasetime", clazz=Long.class, defaultvalue="30000L")
})
public class TransportAddressAgent implements ITransportAddressService, IChangeListener<IComponentIdentifier>
{
	/** The local address directory. */
	protected static final File ADDRESS_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".jadex" + File.separator + "addresses");
	
	/** The agent. */
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
		if(!ADDRESS_DIR.exists())
		{
			ADDRESS_DIR.mkdirs();
		}
		
		if(!(ADDRESS_DIR.isDirectory() && ADDRESS_DIR.canRead() && ADDRESS_DIR.canWrite()))
		{
			agent.getLogger().warning("Local address directory not accessible: " + ADDRESS_DIR.getAbsolutePath());
		}
		else
		{
			scan();
			monitor();
			TransportAddressBook.getAddressBook(agent).addListener(this);
			postInfo();
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Set the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> addPlatformAddresses(IComponentIdentifier platform, String transport, String[] addresses)
	{
		TransportAddressBook.getAddressBook(agent).addPlatformAddresses(platform, transport, addresses);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> removePlatformAddresses(IComponentIdentifier platform)
	{
		TransportAddressBook.getAddressBook(agent).removePlatformAddresses(platform);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the transport specific addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 *  @param transport The transport name
	 */
	public synchronized String[] getPlatformAddresses(IComponentIdentifier platform, String transport)
	{
		return TransportAddressBook.getAddressBook(agent).getPlatformAddresses(platform, transport);
	}
	
	/**
	 *  Create a transport component identifier.
	 *  @param The component identifier.
	 *  @return The transport component identifier.
	 */
//	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier(IComponentIdentifier component); 
	
	/**
	 *  Create a transport component identifiers.
	 *  @param The component identifiers.
	 *  @return The transport component identifiers.
	 */
//	public IFuture<ITransportComponentIdentifier[]> getTransportComponentIdentifiers(IComponentIdentifier[] component); 

	/**
	 *  Get direct access to the map of the addresses.
	 *  @return The map.
	 */
	public @Reference(local=true, remote=false) IFuture<TransportAddressBook> getTransportAddresses()
	{
		return new Future<TransportAddressBook>(TransportAddressBook.getAddressBook(agent));
	}
	
	/**
	 *  Notifies a change occurred.
	 */
	public void changeOccurred(ChangeEvent<IComponentIdentifier> event)
	{
		System.out.println("Change occured: " + event.getSource());
		if (agent.getComponentIdentifier().getRoot().equals(event.getSource()))
			postInfo();
	}
	
	/**
	 *  Scans for new local address infos.
	 */
	protected void scan()
	{
		File[] files = ADDRESS_DIR.listFiles();
		for (File file : files)
		{
			if (file.getAbsolutePath().endsWith(".addr"))
			{
				try
				{
					String leasetimestr = file.getAbsolutePath();
					leasetimestr = leasetimestr.substring(0, leasetimestr.length() - 5);
					leasetimestr = leasetimestr.substring(leasetimestr.lastIndexOf(File.separator) + 1);
					int index = leasetimestr.lastIndexOf('_');
					String pfname = new String(Base64.decodeNoPadding(leasetimestr.substring(0, index).getBytes(SUtil.UTF8)), SUtil.UTF8);
					System.out.println("pfname " + pfname);
					leasetimestr = leasetimestr.substring(index + 1);
					long leasetime = Long.parseLong(leasetimestr);
					if (leasetime < System.currentTimeMillis())
					{
						file.delete();
					}
					else
					{
						byte[] addrdata = SUtil.readFile(file);
						@SuppressWarnings("unchecked")
						Map<String, List<String>> addr = (Map<String, List<String>>) SBinarySerializer.readObjectFromByteArray(addrdata, null, null, agent.getClassLoader(), null);
						for (Map.Entry<String, List<String>> entry : addr.entrySet())
						{
							try
							{
								TransportAddressBook.getAddressBook(agent).addPlatformAddresses(new ComponentIdentifier(pfname), entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
							}
							catch (Exception e)
							{
							}
						}
					}
				}
				catch (Exception e)
				{
				}
			}
		}
	}
	
	protected void postInfo()
	{
		long leasetime = (Long) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("leasetime");
		
		IComponentIdentifier pfid = agent.getComponentIdentifier().getRoot();
		Map<String, List<String>> info = TransportAddressBook.getAddressBook(agent).getAllPlatformAddresses(pfid);
		
		if (info == null)
			return;
		
		byte[] data = SBinarySerializer.writeObjectToByteArray(info, agent.getClassLoader());
		long deadline = leasetime + System.currentTimeMillis();
		String outfilepath = ADDRESS_DIR + File.separator + new String(Base64.encodeNoPadding(pfid.toString().getBytes(SUtil.UTF8)), SUtil.UTF8) + "_" + String.valueOf(deadline) + ".addr";
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
	
	protected void monitor()
	{
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
			Object path = topathmethod.invoke(ADDRESS_DIR, (Object[]) null);
			
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
//							System.out.println("pollend: "+val);
							ea.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									System.out.println("poll in agent: "+val);
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
//			e.printStackTrace();
			// Use polling as fallback.
			watchservice = null;
		}
	}
}
