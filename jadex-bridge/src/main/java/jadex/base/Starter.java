package jadex.base;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.MethodInvocationInterceptor;
import jadex.bridge.service.search.DistributedServiceRegistry;
import jadex.bridge.service.search.PlatformServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.BlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;


/**
 *  Starter class for  
 */
public class Starter
{

	
//	/** The shutdown in progress flag. */
//	protected static boolean	shutdown;
	
	//-------- static methods --------
	
//	/**
//	 *  Test if shutdown is in progress.
//	 */
//	public static boolean	isShutdown()
//	{
//		return shutdown;
//	}
	
	/**
	 *  Unescape a string.
	 */
   	protected static String unescape(String str)
    {
    	StringBuffer	buf	= new StringBuffer(str);
    	int	idx	= buf.indexOf("\\");
    	while(idx!=-1 && buf.length()>idx+1)
    	{
    		if(buf.charAt(idx+1)=='b')
    		{
    			buf.replace(idx, idx+2, "\b");
    		}
    		else if(buf.charAt(idx+1)=='t')
    		{
    			buf.replace(idx, idx+2, "\t");
    		}
    		else if(buf.charAt(idx+1)=='n')
    		{
    			buf.replace(idx, idx+2, "\n");
    		}
    		else if(buf.charAt(idx+1)=='f')
    		{
    			buf.replace(idx, idx+2, "\f");
    		}
    		else if(buf.charAt(idx+1)=='r')
    		{
    			buf.replace(idx, idx+2, "\r");
    		}
    		else if(buf.charAt(idx+1)=='"')
    		{
    			buf.replace(idx, idx+2, "\"");
    		}
    		else if(buf.charAt(idx+1)=='\'')
    		{
    			buf.replace(idx, idx+2, "'");
    		}
    		else if(buf.charAt(idx+1)=='\\')
    		{
    			buf.replace(idx, idx+2, "\\");
    		}
    		
        	idx	= buf.indexOf("\\", idx+1);
    	}

       // Todo: escape octal codes.
       return buf.toString();
    }
	
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args)
	{		
//		try
//		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		createPlatform(args).get();
		
//		IExternalAccess access	= createPlatform(args).get();
//				Runtime.getRuntime().addShutdownHook(new Thread()
//				{
//					public void run()
//					{
//						try
//						{
////							System.out.println("killing: "+access.getComponentIdentifier().getPlatformName());
//							shutdown	= true;
//							access.killComponent().get(new ThreadSuspendable(), TERMINATION_TIMEOUT);
////							System.out.println("killed: "+access.getComponentIdentifier().getPlatformName());
//						}
//						catch(ComponentTerminatedException cte)
//						{
//							// Already killed.
//						}
//						catch(Throwable t)
//						{
//							t.printStackTrace();
//						}
//					}
//				});
				
//				// Continuously run garbage collector and finalizers.
//				Timer	gctimer	= new Timer();
//				gctimer.scheduleAtFixedRate(new TimerTask()
//				{
//					public void run()
//					{
//						System.gc();
//						System.runFinalization();
//					}
//				}, 1000, 1000);
				
				
				// Test CTRL-C shutdown behavior.
//				Timer	timer	= new Timer();
//				timer.schedule(new TimerTask()
//				{
//					public void run()
//					{
//						System.out.println(getClass().getName()+": Calling System.exit() for testing.");
//						System.exit(0);
//					}
//				}, 5000);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(Map<String, String> args)
	{
		PlatformConfiguration config = PlatformConfiguration.processArgs(args);
		return createPlatform(config);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(String[] args)
	{
		PlatformConfiguration config = PlatformConfiguration.processArgs(args);
		return createPlatform(config);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(final PlatformConfiguration config)
	{
		RootComponentConfiguration rootConfig = config.getRootConfig();
		
		// pass configuration parameters to static fields:
		MethodInvocationInterceptor.DEBUG = config.getDebugServices();
		ExecutionComponentFeature.DEBUG = config.getDebugSteps();
		Future.NO_STACK_COMPACTION	= config.getNoStackCompaction();
		Future.DEBUG = config.getDebugFutures();
		
//		final Object args, final Map<String, Object> cmdargs, final Map<String, Object> compargs, final List<String> components
		
		// Fix below doesn't work. WLAN address is missing :-(
//		// ANDROID: Selector.open() causes an exception in a 2.2
//		// emulator due to IPv6 addresses, see:
//		// http://code.google.com/p/android/issues/detail?id=9431
//		// Also fixes Java bug on windows 7 regarding network prefix:
//		// http://bugs.sun.com/view_bug.do?bug_id=6707289
//		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
//		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");

//		System.out.println("Arguments: "+args.length/2+" "+SUtil.arrayToString(args));
		
		Future<IExternalAccess> ret = new Future<IExternalAccess>();
		final Future<IExternalAccess>	fret	= ret;
		
		// Perform manual switch to allow users specify next call properties
		ServiceCall sc = CallAccess.getCurrentInvocation();
		ServiceCall scn = CallAccess.getNextInvocation();
		if(sc==null)
		{
			if(scn==null)
			{
				scn = CallAccess.getOrCreateNextInvocation();
			}
			if(scn.getCause()==null)
			{
				scn.setCause(new Cause((String)null, "createPlatform"));
			}
			
			CallAccess.setCurrentInvocation(scn);
			sc	= scn;
		}
		
		try
		{
			// Absolute start time (for testing and benchmarking).
			final long starttime = System.currentTimeMillis();
		
			// Load the platform (component) model.
			final ClassLoader cl = Starter.class.getClassLoader();
			final String configfile = config.getConfigurationFile();
			String cfclname = config.getComponentFactory();
			Class<?> cfclass = SReflect.classForName(cfclname, cl);
			// The providerid for this service is not important as it will be thrown away 
			// after loading the first component model.
			final IComponentFactory cfac = (IComponentFactory)cfclass.getConstructor(new Class[]{String.class})
				.newInstance(new Object[]{"rootid"});
			rootConfig.setValue(RootComponentConfiguration.COMPONENT_FACTORY, cfac);
			
			// Hack: what to use as rid? should not have dependency to standalone.
//			final ResourceIdentifier rid = new ResourceIdentifier(null, 
//				"net.sourceforge.jadex:jadex-standalone-launch:2.1");
			
//			System.out.println("Using config file: "+configfile);
			
			final IModelInfo model	= cfac.loadModel(configfile, null, null).get();	// No execution yet, can only work if method is synchronous.
			
			if(model.getReport()!=null)
			{
				ret.setException(new RuntimeException("Error loading model:\n"+model.getReport().getErrorText()));
			}
			else
			{
				Object	pc = config.getArgumentValue(RootComponentConfiguration.PLATFORM_COMPONENT, model);
				if(pc==null)
				{
					ret.setException(new RuntimeException("No platform component class found."));
				}
				else
				{
					Class<?> pcclass = pc instanceof Class ? (Class<?>)pc : SReflect.classForName(pc.toString(), cl);
					final IPlatformComponentAccess component = (IPlatformComponentAccess)pcclass.newInstance();
					rootConfig.setValue(RootComponentConfiguration.PLATFORM_ACCESS, component);
//					final IComponentInterpreter	interpreter	= cfac.createComponentInterpreter(model, component.getInternalAccess(), null).get(null); // No execution yet, can only work if method is synchronous.
					
					// Build platform name.
					Object pfname = config.getArgumentValue(RootComponentConfiguration.PLATFORM_NAME, model);
					final IComponentIdentifier cid = createPlatformIdentifier(pfname!=null? pfname.toString(): null);
					if(IComponentIdentifier.LOCAL.get()==null)
						IComponentIdentifier.LOCAL.set(cid);
					
					// Hack: change rid afterwards?!
					ResourceIdentifier rid = (ResourceIdentifier)model.getResourceIdentifier();
					ILocalResourceIdentifier lid = rid.getLocalIdentifier();
					rid.setLocalIdentifier(new LocalResourceIdentifier(cid, lid.getUri()));
					
					String ctype	= cfac.getComponentType(configfile, null, model.getResourceIdentifier()).get();
					IComponentIdentifier caller = sc==null? null: sc.getCaller();
					Cause cause = sc==null? null: sc.getCause();
					assert cause!=null;
					
					Boolean autosd = (Boolean)config.getArgumentValue(RootComponentConfiguration.AUTOSHUTDOWN, model);
					Object tmpmoni = config.getArgumentValue(PlatformConfiguration.MONITORING, model);
					PublishEventLevel moni = PublishEventLevel.OFF;
					if(tmpmoni instanceof Boolean)
					{
						moni = ((Boolean)tmpmoni).booleanValue()? PublishEventLevel.FINE: PublishEventLevel.OFF;
					}
					else if(tmpmoni instanceof String)
					{
						moni = PublishEventLevel.valueOf((String)tmpmoni);
					}
					else if(tmpmoni instanceof PublishEventLevel)
					{
						moni = (PublishEventLevel)tmpmoni;
					}
	
					final CMSComponentDescription desc = new CMSComponentDescription(cid, ctype, false, false, 
						autosd!=null ? autosd.booleanValue() : false, false, false, moni, model.getFullName(),
						null, model.getResourceIdentifier(), System.currentTimeMillis(), caller, cause, false);
					
					PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_REALTIMETIMEOUT, config.getArgumentValue(PlatformConfiguration.DATA_REALTIMETIMEOUT, model));
					PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_PARAMETERCOPY, config.getArgumentValue(PlatformConfiguration.DATA_PARAMETERCOPY, model));
					
					if (Boolean.TRUE.equals(config.getArgumentValue(PlatformConfiguration.DHT_PROVIDE, model))) {
						boolean provideonly = true;
						if (Boolean.TRUE.equals(config.getArgumentValue(PlatformConfiguration.DHT, model))) {
							provideonly = false;
						}
						PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_SERVICEREGISTRY, new DistributedServiceRegistry(component.getInternalAccess(), provideonly));
					} else {
						PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_SERVICEREGISTRY, new PlatformServiceRegistry());
					}
					PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_ADDRESSBOOK, new TransportAddressBook());

					PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_DEFAULT_LOCAL_TIMEOUT, config.getLocalDefaultTimeout());
					PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_DEFAULT_REMOTE_TIMEOUT, config.getRemoteDefaultTimeout());
					
					Map<String, Object> rootArgs = rootConfig.getArgs();
					ComponentCreationInfo	cci	= new ComponentCreationInfo(model, null, rootArgs, desc, null, null);
					Collection<IComponentFeatureFactory>	features	= cfac.getComponentFeatures(model).get();
					component.create(cci, features);

					initRescueThread(cid, rootArgs);	// Required for bootstrapping init.

					component.init().addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
					{
						public void customResultAvailable(Void result)
						{
							startComponents(0, config.getComponents(), component.getInternalAccess())
								.addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(fret)
							{
								public void customResultAvailable(Void result)
								{
									if(Boolean.TRUE.equals(config.getArgumentValue(RootComponentConfiguration.WELCOME, model)))
									{
										long startup = System.currentTimeMillis() - starttime;
										// platform.logger.info("Platform startup time: " + startup + " ms.");
										System.out.println(desc.getName()+" platform startup time: " + startup + " ms.");
									}
									fret.setResult(component.getInternalAccess().getExternalAccess());
								}
								
								public void exceptionOccurred(Exception exception)
								{
									// On exception in init: kill platform.
									component.getInternalAccess().getExternalAccess().killComponent();
									super.exceptionOccurred(exception);
								}
							});
						}
					});
					
					if(cid.equals(IComponentIdentifier.LOCAL.get()))
					{
						IComponentIdentifier.LOCAL.set(null);
					}
				}
			}
	//		System.out.println("Model: "+model);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			if(!ret.setExceptionIfUndone(e))
			{
				ret	= new Future<IExternalAccess>(e);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected static IComponentIdentifier createPlatformIdentifier(String pfname)
	{
		// Build platform name.
		String	platformname	= null; 
		if(pfname==null)
		{
			try
			{
				platformname	= InetAddress.getLocalHost().getHostName();
				platformname	+= "_*";
			}
			catch(UnknownHostException e)
			{
			}
		}
		else
		{
			platformname	= pfname.toString(); 
		}
		// Replace special characters used in component ids.
		if(platformname!=null)
		{
			platformname	= platformname.replace('.', '$'); // Dot in host name on Mac !?
			platformname	= platformname.replace('@', '$');
		}
		else
		{
			platformname = "platform_*";
		}
		Random	rnd	= new Random();
		StringBuffer	buf	= new StringBuffer();
		StringTokenizer	stok	= new StringTokenizer(platformname, "*+", true);
		while(stok.hasMoreTokens())
		{
			String	tok	= stok.nextToken();
			if(tok.equals("+"))
			{
				buf.append(Integer.toHexString(rnd.nextInt(16)));
			}
			else if(tok.equals("*"))
			{
				buf.append(Integer.toHexString(rnd.nextInt(16)));
				buf.append(Integer.toHexString(rnd.nextInt(16)));
				buf.append(Integer.toHexString(rnd.nextInt(16)));
			}
			else
			{
				buf.append(tok);
			}
		}
		platformname = buf.toString();
		
		// Create an instance of the component.
		return new BasicComponentIdentifier(platformname);
	}
	
	/**
	 *  Loop for starting components.
	 *  @param i Number to start.
	 *  @param components The list of components.
	 *  @param instance The instance.
	 *  @return True, when done.
	 */
	protected static IFuture<Void> startComponents(final int i, final List<String> components, final IInternalAccess instance)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		if(i<components.size())
		{
			SServiceProvider.getService(instance, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					String name	= null;
					String config	= null;
					String args = null;
					String comp	= (String)components.get(i);
					Map<String, Object> oargs = null;
					
					// check if name:type are both present (to not find last : check that no ( before)
					int	i1	= comp.indexOf(':');
					int i11 = comp.indexOf('(');
					if(i1!=-1 && (i11==-1 || i11>i1))
					{
						name	= comp.substring(0, i1);
						comp	= comp.substring(i1+1);
					}
					
					// check if (config:args) part is present
					int	i2	= comp.indexOf('(');
					if(i2!=-1)
					{
						// must end with )
						// must have : if both are presents otherwise all is configname
						if(!comp.endsWith(")"))
						{
							throw new RuntimeException("Component specification does not match scheme [<name>:]<type>[(<config>)[:<args>]]) : "+components.get(i));
						}

						int i3 = comp.indexOf(":");
						if(i3!=-1)
						{
							if(comp.length()-i3>1)
								args = comp.substring(i3+1, comp.length()-1);
							if(i3-i2>1)
								config	= comp.substring(i2+1, i3-1);
						}
						else
						{
							config = comp.substring(i2+1, comp.length()-1);
						}
						
						comp = comp.substring(0, i2);	
					}
//					System.out.println("comp: "+comp+" config: "+config+" args: "+args);
					
					if(args!=null)
					{
						try
						{
//							args = args.replace("\\\"", "\"");
							Object o = SJavaParser.evaluateExpression(args, null);
							if(!(o instanceof Map))
							{
								throw new RuntimeException("Arguments must evaluate to Map<String, Object>"+args);
							}
							oargs = (Map<String, Object>)o;
						}
						catch(Exception e)
						{
//							System.out.println("args: "+args);
//							e.printStackTrace();
							throw new RuntimeException("Arguments evaluation error: "+e);
						}
					}
					
					cms.createComponent(name, comp, new CreationInfo(config, oargs), null)
						.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
					{
						public void customResultAvailable(IComponentIdentifier result)
						{
							startComponents(i+1, components, instance)
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}	
	
	/** The rescue threads - per platform. */
	protected static Map<IComponentIdentifier, Tuple2<BlockingQueue, Thread>> rescuethreads;
	
	/**
	 *  Init the rescue thread for a platform..
	 */
	public synchronized static void initRescueThread(IComponentIdentifier cid, Map<String, Object> compargs)
	{
		IThreadPool	tp	= null;
		if(compargs.get("threadpoolclass")!=null)
		{
			try
			{
				tp	= (IThreadPool)SReflect.classForName(
					(String)compargs.get("threadpoolclass"), Starter.class.getClassLoader()).newInstance();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		assert cid.getParent()==null;
		if(rescuethreads==null)
		{
			rescuethreads = new HashMap<IComponentIdentifier, Tuple2<BlockingQueue, Thread>>();
		}	
		
		final BlockingQueue bq = new BlockingQueue();
		final IComponentIdentifier fcid = cid;
		Runnable	run	= new Runnable()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						Runnable next = (Runnable)bq.dequeue();
						try
						{
							next.run();
						}
						catch(Exception e)
						{
							Logger.getLogger(fcid.getLocalName()).severe("Exception during step on rescue thread: "+e);
						}
					}
				}
				catch(IBlockingQueue.ClosedException bqce)
				{
				}
			}
		};
		if(tp!=null)
		{
			tp.execute(run);
		}
		else
		{
			Thread rescuethread =new Thread(run, "rescue_thread_"+cid.getName());
			Tuple2<BlockingQueue, Thread> tup = new Tuple2<BlockingQueue, Thread>(bq, rescuethread);
			rescuethreads.put(cid, tup);
			// rescue thread must not be daemon, otherwise shutdown code like writing platform settings might be interrupted by vm exit. 
	//		rescuethread.setDaemon(true);
			rescuethread.start();
		}
	}
	
	/**
	 *  Schedule a rescue step.
	 *  @param cid The id of a component of the platform.
	 *  @param run The runnable to execute.
	 */
	public synchronized static void scheduleRescueStep(IComponentIdentifier cid, Runnable run)
	{
		Tuple2<BlockingQueue, Thread> tup = rescuethreads!=null ? rescuethreads.get(cid.getRoot()) : null;
		if(tup!=null)
		{
			tup.getFirstEntity().enqueue(run);
		}
		else
		{
			// Rescue thread after platform shutdown -> just execute
			run.run();
		}
	}
	
	/**
	 *  Test if the current thread is the rescue thread of the platform.
	 *  @param cid The id of a component of the platform.
	 *  @return True, if is the rescue thread.
	 */
	public static synchronized boolean isRescueThread(IComponentIdentifier cid)
	{
		boolean ret = false;
		Tuple2<BlockingQueue, Thread> tup = rescuethreads==null? null: rescuethreads.get(cid.getRoot());
		if(tup!=null)
		{
			ret = Thread.currentThread().equals(tup.getSecondEntity());
		}
		return ret;
	}
	
	/**
	 *  Shutdown the rescue thread of a platform. 
	 */
	public static synchronized void	shutdownRescueThread(IComponentIdentifier cid)
	{
		assert cid.getParent()==null : cid;
		Tuple2<BlockingQueue, Thread> tup = rescuethreads==null? null: rescuethreads.remove(cid.getRoot());
		if(tup!=null)
		{
			List<Runnable>	steps	= tup.getFirstEntity().setClosed(true);
			for(Runnable next: steps)
			{
				try
				{
					next.run();
				}
				catch(Exception e)
				{
					Logger.getLogger(cid.getLocalName()).severe("Exception during step on rescue thread: "+e);
				}
			}
		}
	}
	
	/**
	 *  Create a proxy for the remote platform.
	 */
	public static IFuture<IComponentIdentifier>	createProxy(IExternalAccess local, final IExternalAccess remote)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		SServiceProvider.getService(local, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IComponentManagementService flocal)
			{
				SServiceProvider.getService(remote, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(ITransportAddressService tas)
					{
						tas.getTransportComponentIdentifier(remote.getComponentIdentifier().getRoot()).addResultListener(new ExceptionDelegationResultListener<ITransportComponentIdentifier, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(ITransportComponentIdentifier extacid)
							{
								Map<String, Object>	args = new HashMap<String, Object>();
								args.put("component", extacid);
								CreationInfo ci = new CreationInfo(args);
								flocal.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", ci, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}

	
	
	/**
	 *  Get the remote default timeout.
	 */
	public static long getRemoteDefaultTimeout(IComponentIdentifier platform)
	{
		return PlatformConfiguration.getRemoteDefaultTimeout(platform);
	}
	
	/**
	 *  Get the scaled remote default timeout.
	 */
	public static long	getScaledRemoteDefaultTimeout(IComponentIdentifier platform, double scale) 
	{
		return PlatformConfiguration.getScaledRemoteDefaultTimeout(platform, scale);
	}
	
	/**
	 *  Get the local default timeout.
	 */
	public static long getLocalDefaultTimeout(IComponentIdentifier platform)
	{
		return PlatformConfiguration.getLocalDefaultTimeout(platform);
	}

	/**
	 *  Get the scaled local default timeout.
	 */
	public static long getScaledLocalDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		return PlatformConfiguration.getScaledLocalDefaultTimeout(platform, scale);
	}
	
	/**
	 *  Set the remote default timeout.
	 */
	public static void setRemoteDefaultTimeout(IComponentIdentifier platform, long timeout) 
	{
		PlatformConfiguration.setRemoteDefaultTimeout(platform, timeout);
	}
	
	/**
	 *  Set the local default timeout.
	 */
	public static void setLocalDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		PlatformConfiguration.setLocalDefaultTimeout(platform, timeout);
	}

	/**
	 *  Check if the real time timeout flag is set for a platform.
	 */
	public static boolean	isRealtimeTimeout(IComponentIdentifier platform)
	{
		return PlatformConfiguration.isRealtimeTimeout(platform);
	}

	/**
	 *  Check if the parameter copy flag is set for a platform.
	 */
	public static boolean	isParameterCopy(IComponentIdentifier platform)
	{
		return PlatformConfiguration.isParameterCopy(platform);
	}
	
}

