package jadex.platform.service.daemon;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.bean.JavaWriter;
import jadex.xml.writer.AWriter;
import jadex.xml.writer.XMLWriterFactory;

/**
 *  The daemon service.
 */
@Service
public class DaemonService implements IDaemonService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
		
	/** The started platforms. */
	protected Map<IComponentIdentifier, Process> platforms;

	/** The futures waiting for starting platforms. */
	protected Map<String, Future<IComponentIdentifier>> futures;

	/** The listeners. */
	protected List<IRemoteChangeListener<IComponentIdentifier>>	listeners;

	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public void start()
	{
		platforms = new HashMap<IComponentIdentifier, Process>();
		futures = new HashMap<String, Future<IComponentIdentifier>>();
		listeners = new ArrayList<IRemoteChangeListener<IComponentIdentifier>>();
	}
	
	/**
	 *  Called from agent when a handshake message is recieved.
	 */
	protected void	messageReceived(IComponentIdentifier cid, String pid)
	{
//		System.out.println("Received message from "+cid+": "+pid+", "+futures.containsKey(pid));
		if(futures.containsKey(pid))
		{
			futures.remove(pid).setResult(cid.getRoot());
		}
	}
	
	//-------- IDaemonService interface --------
	
	/**
	 *  Start a platform using a configuration.
	 *  Performs no checking if the new platform runs.
	 *  @param options The start arguments.
	 */
	public IFuture<Void> startPlatform(StartOptions options)
	{
		return startPlatform(options, 0);
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  Wait for some time to check if the platform doesn't fail.
	 *  Only detects, if the new platform process exits during that time.
	 *  @param options The start arguments.
	 */
	public IFuture<Void> startPlatform(StartOptions options, final long wait)
	{
		final Future<Void> ret = new Future<Void>();

		agent.getLogger().info("start in daemon");
		
		adjustOptions(options).addResultListener(new ExceptionDelegationResultListener<StartOptions, Void>(ret)
		{
			public void customResultAvailable(StartOptions options)
			{
				try
				{
					final Process	proc	= options.startProcess();
				
					// Wait for process
					if(wait>0)
					{
						agent.getFeature(IExecutionFeature.class).waitForDelay(wait, new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								IFuture<Void>	ret;
								try
								{
									proc.exitValue();
									ret	= new Future<Void>(new RuntimeException("Platform exited."));
								}
								catch(Exception e)
								{
									// Not yet terminated -> ignore.
									ret	= IFuture.DONE;
								}
								return ret;
							}
						}, true).addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ret.setResult(null);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ret.setException(e);
				}
			}
		});

		return ret;
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  Wait for successful handshake and return the component identifier of the new platform.
	 *  Successful handshake means that the init phase of the new platform, including starting
	 *  of initial components, has completed successfully.
	 *  @param opt The start arguments.
	 */
	public IFuture<IComponentIdentifier> startPlatformAndWait(StartOptions options)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();

//		System.out.println("start in daemon 2");
		final long timeout	= ServiceCall.getCurrentInvocation().getTimeout();

		adjustOptions(options).addResultListener(new ExceptionDelegationResultListener<StartOptions, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(StartOptions options)
			{
				// Change arguments to include responder agent for handshake.
				final String	pid	= UUID.randomUUID().toString();
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("cid", agent.getId());
				args.put("content", pid);
				String	argsstr = AWriter.objectToXML(XMLWriterFactory.getInstance().createWriter(true, false, false), args, null, JavaWriter.getObjectHandler());
				argsstr	= SUtil.escapeString(argsstr);	// First: escape string
				argsstr	= argsstr.replace("\"", "\\\\\""); // then escape quotes again for argument parser
				String	deser = "jadex.xml.bean.JavaReader.objectFromXML(\\\""+argsstr+"\\\",null)";
				String	responder	= " -component \""+DaemonResponderAgent.class.getName().replace(".", "/")+".class(:"+deser+")\"";
				options.setProgramArguments(options.getProgramArguments()+responder);
				futures.put(pid, ret);
				
				try
				{
					options.startProcess();
				
					// Wait for handshake.
					agent.getLogger().info("Waiting for platform "+pid);
					ret.addResultListener(new TimeoutResultListener<IComponentIdentifier>(timeout, agent.getExternalAccess(),
						new IResultListener<IComponentIdentifier>()
					{
						public void resultAvailable(IComponentIdentifier result)
						{
							agent.getLogger().info("Platform found: "+pid+", "+result);
						}
						public void exceptionOccurred(Exception exception)
						{
							agent.getLogger().info("No platform found: "+pid);
							futures.remove(pid);
							ret.setExceptionIfUndone(exception);
						}
					}));
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					futures.remove(pid);
					ret.setException(e);
				}
			}
		});

		return ret;
	}

//	public IFuture startPlatform(StartOptions opt)
//	{
//		final Future ret = new Future();
//		// Start platform in the same VM.
//		Starter.createPlatform(args).addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				IExternalAccess platform = (IExternalAccess)result;
//				platforms.put(platform.getComponentIdentifier(), platform);
//				ret.setResult(result);
//			}
//			
//			public void exceptionOccurred(Object source, Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
//		return ret;
//	}
	
	/**
	 *  Create or adjust options (e.g. fill in classpath) if necessary.
	 */
	protected IFuture<StartOptions>	adjustOptions(StartOptions opt)
	{
		final Future<StartOptions>	ret	= new Future<StartOptions>();
	
		final StartOptions options = opt==null ? new StartOptions() : opt;

		// Clone current classpath if not set. 
		if(options.getClassPath()==null || options.getClassPath().length()==0)
		{
			IFuture<ILibraryService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("libservice");
			fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, StartOptions>(ret)
			{
				public void customResultAvailable(ILibraryService result)
				{
					ILibraryService ls = (ILibraryService)result;
					ls.getAllURLs().addResultListener(new ExceptionDelegationResultListener<List<URL>, StartOptions>(ret)
					{
						public void customResultAvailable(List<URL> urls)
						{
							List<String> res = new ArrayList<String>();
							for(int i = 0; i<urls.size(); i++)
							{
								URL url = (URL)urls.get(i);
								String name = SUtil.convertURLToString(url);
								if(name != null)
								{
									res.add(name);
								}
								else
								{
									agent.getLogger().warning("Cannot convert url to file: "+ url);
								}
							}
							StringBuffer buf = new StringBuffer();
							for(int i = 0; i < res.size(); i++)
							{
								buf.append(res.get(i));
								if(i + 1 < res.size())
									buf.append(File.pathSeparator);
							}
							options.setClassPath(buf.toString());

//							System.out.println("cp: "+options.getClassPath());
							
							ret.setResult(options);
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(options);
		}

		return ret;
	}
	
	
//	/**
//	 *  Check the platform identifier.
//	 */
//	protected boolean checkPlatformIdentifier(StringBuffer buf, Process proc, Future<IComponentIdentifier> fut)
//	{
//		boolean ret = false;
//		
//		IComponentIdentifier cid = getPlatformIdentifier(buf);
//		if(cid!=null)
//		{
//			ret = true;
//			platforms.put(cid, proc);
//			notifyListeners(new ChangeEvent<IComponentIdentifier>(null,IDaemonService.ADDED, cid));
//			fut.setResult(cid);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the platform identifier.
//	 */
//	protected static IComponentIdentifier getPlatformIdentifier(StringBuffer buf)
//	{
//		IComponentIdentifier ret = null;
//		
//		String str = buf.toString();
//		int idx = str.indexOf("platform startup time"); // hack?! better way to identify platform cid?
//		if(idx!=-1)
//		{
//			str = str.substring(0, idx);
//			idx = str.lastIndexOf(SUtil.LF);
//			if(idx!=-1)
//			{
//				str = str.substring(idx+SUtil.LF.length());
//			}
//			
////			System.out.println("platform id: "+str);
//			ret = new ComponentIdentifier(str.trim());
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		String s = "Using stored platform password: a6e73638-094+\n\rLars-PC_a71 platform startup time: 7542 ms.";
//		System.out.println(getPlatformIdentifier(new StringBuffer().append(s)));
//	}

	/**
	 * Shutdown a platform.
	 * @param cid The platform id.
	 */
	public IFuture<Void> shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		Process proc = (Process)platforms.get(cid);
		if(proc == null)
		{
			ret.setException(new RuntimeException("Platform not found: " + cid));
		}
		else
		{
			proc.destroy();
			ret.setResult(null);
			notifyListeners(new ChangeEvent<IComponentIdentifier>(this, IDaemonService.REMOVED, cid));
		}

		return ret;
	}

	/**
	 * Shutdown a platform.
	 * 
	 * @param cid The platform id. / public IFuture shutdownPlatform(final
	 *        IComponentIdentifier cid) { final Future ret = new Future();
	 *        IExternalAccess platform = (IExternalAccess)platforms.get(cid);
	 *        if(platform==null) { ret.setException(new
	 *        RuntimeException("No platform found: "+cid)); } else {
	 *        SServiceProvider.getService(platform.getServiceProvider(),
	 *        IComponentManagementService.class) .addResultListener(new
	 *        IResultListener() { public void resultAvailable(Object source,
	 *        Object result) { IComponentManagementService cms =
	 *        (IComponentManagementService)result;
	 *        cms.destroyComponent(cid).addResultListener(new
	 *        DelegationResultListener(ret)); } public void
	 *        exceptionOccurred(Object source, Exception exception) {
	 *        ret.setException(exception); } }); } return ret; }
	 */

	/**
	 * Get the component identifiers of all (managed) platforms.
	 * 
	 * @return Collection of platform ids.
	 */
	public IFuture<Set<IComponentIdentifier>> getPlatforms()
	{
		return new Future<Set<IComponentIdentifier>>(platforms.keySet());
	}

	/**
	 * Add a change listener.
	 * 
	 * @param listener The change listener.
	 */
	public IFuture<Void> addChangeListener(IRemoteChangeListener<IComponentIdentifier> listener)
	{
		listeners.add(listener);
		return IFuture.DONE;
	}

	/**
	 * Remove a change listener.
	 * 
	 * @param listener The change listener.
	 */
	public IFuture<Void> removeChangeListener(IRemoteChangeListener<IComponentIdentifier> listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}

	/**
	 * Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent<IComponentIdentifier> event)
	{
		IRemoteChangeListener<IComponentIdentifier>[] alisteners;
		synchronized(this)
		{
			alisteners = listeners.isEmpty() ? null: (IRemoteChangeListener<IComponentIdentifier>[])listeners.toArray(new IRemoteChangeListener[0]);
		}
		if(alisteners != null)
		{
			for(int i = 0; i < alisteners.length; i++)
			{
				final IRemoteChangeListener<IComponentIdentifier> lis = alisteners[i];
				alisteners[i].changeOccurred(event).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
					}

					public void exceptionOccurred(Exception exception)
					{
						// System.out.println("Removing listener: "+lis);
						removeChangeListener(lis);
					}
				}));
			}
		}
	}
}
