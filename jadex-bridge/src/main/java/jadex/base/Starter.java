package jadex.base;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
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
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.BlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.TransformSet;
import jadex.javaparser.SJavaParser;


/**
 *  Starter class for  
 */
public class Starter
{
	//-------- This is platform specific data kept in a common memory --------

	//-------- Platform data keys --------

    /** Flag if copying parameters for local service calls is allowed. */
    public static String DATA_PARAMETERCOPY = IStarterConfiguration.PARAMETERCOPY;

    /**  Flag if local timeouts should be realtime (instead of clock dependent). */
    public static String DATA_REALTIMETIMEOUT = IStarterConfiguration.REALTIMETIMEOUT;

    /** The local service registry data key. */
    public static String DATA_SERVICEREGISTRY = "serviceregistry";

    /** The serialization services for serializing and en/decoding objects including remote reference handling. */
    public static String DATA_SERIALIZATIONSERVICES = "serialservs";

    /** The transport cache used to . */
    public static String DATA_TRANSPORTCACHE = "transportcache";

    /** The used to store the current network names. */
    public static String DATA_NETWORKNAMESCACHE = "networknamescache";

    /** The CMS component map. */
    public static String DATA_COMPONENTMAP = "componentmap";

    /** Constant for local default timeout name. */
    public static String DATA_DEFAULT_LOCAL_TIMEOUT = "default_local_timeout";

    /** Constant for remote default timeout name. */
    public static String DATA_DEFAULT_REMOTE_TIMEOUT = "default_remote_timeout";
    
    // todo: cannot be used because registry needs to know when superpeer changes (remap queries)
//    /** Constant for the superpeer. */
//    public static String DATA_SUPERPEER = "superpeer";

    

	/** Global platform data. For each platform stored by  */
	protected static final Map<IComponentIdentifier, Map<String, Object>> platformmem = new HashMap<IComponentIdentifier, Map<String, Object>>();


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
	
	// Try adding startcom ssl certificate
	// Does not work because 'cacerts' file cannot be written in Java home (privileges) :-(
	//
	// https://forum.startcom.org/viewtopic.php?f=15&t=1815&st=0&sk=t&sd=a&sid=90c5f7662b53041a50063813eb121d98&start=15
	// manual addition:
	// wget http://www.startssl.com/certs/ca.crt
	// keytool -import -trustcacerts -alias startcom.ca -file ca.crt
	// // wget http://www.startssl.com/certs/sub.class1.server.ca.crt
	// // keytool -import -alias startcom.ca.sub -file sub.class1.server.ca.crt
//	static
//	{
//		try
//		{
//			Class<?> cl = SReflect.findClass0("jadex.platform.service.security.SSecurity", null, null);
//			if(cl!=null)
//			{
//				Method m = cl.getMethod("addStartSSLToTrustStore", new Class[]{String.class});
//				m.invoke(null, new Object[]{"changeit"});
//				System.out.println("Startssl certificate is installed in truststore.");
//			}
//		}
//		catch(Exception e)
//		{
//			System.out.println("Error adding startssl certificate to truststore: "+e.getMessage());
//		}
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
//			.scheduleStep(new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				String remoteaddr = "tcp-mtp://"+"ec2-54-190-58-166.us-west-2.compute.amazonaws.com"+":36000";
//				String platformname = "Allie-Jadex_720F614FB6ED061A";
//				IComponentIdentifier	remotecid	= new ComponentIdentifier(platformname, new String[]{remoteaddr});
//
//				// Create proxy for remote platform such that remote services are found
//				Map<String, Object>	args = new HashMap<String, Object>();
//				args.put("component", remotecid);
//				CreationInfo ci = new CreationInfo(args);
//				ci.setDaemon(true);
//				IComponentManagementService	cms	= SServiceProvider.getLocalService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//				cms.createComponent(platformname, "jadex/platform/service/remote/ProxyAgent.class", ci).getFirstResult();
//				return IFuture.DONE;
//			}
//		});

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
	 *  @deprecated since 3.0.7. Use other createPlatform methods instead.
	 */
	@Deprecated
	public static IFuture<IExternalAccess> createPlatform(Map<String, String> args)
	{
		IPlatformConfiguration config = processArgs(args);
		return createPlatform(config);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(String... args)
	{
		IPlatformConfiguration config = processArgs(args);
		return createPlatform(config);
	}
	
	/**
	 *  Create the platform.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform()
	{
		return createPlatform(PlatformConfigurationHandler.getDefault());
	}

	/**
	 *  Create the platform.
	 *  @param config The PlatformConfiguration object.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(final IPlatformConfiguration config)
	{
		IRootComponentConfiguration rootconf = config.getRootConfig();
		
		// pass configuration parameters to static fields:
		MethodInvocationInterceptor.DEBUG = config.getDebugServices();
		ExecutionComponentFeature.DEBUG = config.getDebugSteps();
//		Future.NO_STACK_COMPACTION	= true;
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
			rootconf.setBootstrapFactory(cfac);
			// Hack: what to use as rid? should not have dependency to standalone.
//			final ResourceIdentifier rid = new ResourceIdentifier(null, 
//				"org.activecomponents.jadex:jadex-standalone-launch:2.1");
			
//			System.out.println("Using config file: "+configfile);
			
			final IModelInfo model	= cfac.loadModel(configfile, null, null).get();	// No execution yet, can only work if method is synchronous.
			
			if(model.getReport()!=null)
			{
				ret.setException(new RuntimeException("Error loading model:\n"+model.getReport().getErrorText()));
			}
			else
			{
				config.setPlatformModel(model);
//				config.checkConsistency(); // todo?
				Class<?> pc = config.getPlatformComponent().getType(cl);
//				Object	pc = config.getValue(RootComponentConfiguration.PLATFORM_COMPONENT);
//				rootConfig.setValue(RootComponentConfiguration.PLATFORM_COMPONENT, pc);
				if(pc==null)
				{
					ret.setException(new RuntimeException("No platform component class found."));
				}
				else
				{
					Class<?> pcclass = pc instanceof Class ? (Class<?>)pc : SReflect.classForName(pc.toString(), cl);
					final IPlatformComponentAccess component = (IPlatformComponentAccess)pcclass.newInstance();
					rootconf.setPlatformAccess(component);
//					final IComponentInterpreter	interpreter	= cfac.createComponentInterpreter(model, component.getInternalAccess(), null).get(null); // No execution yet, can only work if method is synchronous.
					
					// Build platform name.
					String pfname = config.getPlatformName();
//					Object pfname = config.getValue(RootComponentConfiguration.PLATFORM_NAME);
//					rootConfig.setValue(RootComponentConfiguration.PLATFORM_NAME, pfname);
					final IComponentIdentifier cid = createPlatformIdentifier(pfname!=null? pfname.toString(): null);
					if(IComponentIdentifier.LOCAL.get()==null)
					{
						IComponentIdentifier.LOCAL.set(cid);
					}

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

					// Hack: change rid afterwards?!
					ResourceIdentifier rid = (ResourceIdentifier)model.getResourceIdentifier();
					ILocalResourceIdentifier lid = rid.getLocalIdentifier();
					rid.setLocalIdentifier(new LocalResourceIdentifier(cid, lid.getUri()));
					
					String ctype	= cfac.getComponentType(configfile, null, model.getResourceIdentifier()).get();
					IComponentIdentifier caller = sc==null? null: sc.getCaller();
					Cause cause = sc==null? null: sc.getCause();
					assert cause!=null;
					
					Boolean autosd = config.getAutoShutdown();
//					Boolean autosd = (Boolean)config.getValue(RootComponentConfiguration.AUTOSHUTDOWN);
//					rootConfig.setValue(RootComponentConfiguration.AUTOSHUTDOWN, autosd);
					PublishEventLevel monitoring = config.getMonitoring();
	
					final CMSComponentDescription desc = new CMSComponentDescription(cid, ctype, false, false, 
						autosd!=null ? autosd.booleanValue() : false, false, false, monitoring, model.getFullName(),
						null, model.getResourceIdentifier(), System.currentTimeMillis(), caller, cause, false);

					putPlatformValue(cid, DATA_REALTIMETIMEOUT, config.getValue(DATA_REALTIMETIMEOUT));
//					rootConfig.setValue(PlatformConfiguration.DATA_REALTIMETIMEOUT, config.getValue(PlatformConfiguration.DATA_REALTIMETIMEOUT));
					putPlatformValue(cid, DATA_PARAMETERCOPY, config.getValue(DATA_PARAMETERCOPY));
//					rootConfig.setValue(PlatformConfiguration.DATA_PARAMETERCOPY, config.getValue(PlatformConfiguration.DATA_PARAMETERCOPY));

					putPlatformValue(cid, DATA_NETWORKNAMESCACHE, new TransformSet<String>());

//					else if(config.getBooleanValue(PlatformConfiguration.REGISTRY_SYNC))
//					if(config.getRegistrySync())
//					{
						putPlatformValue(cid, DATA_SERVICEREGISTRY, new ServiceRegistry(cid, 5000));
//					}
//					else
//					{
						// ServiceRegistry cannot handle backport for polling in case of global queries
//						PlatformConfiguration.putPlatformValue(cid, PlatformConfiguration.DATA_SERVICEREGISTRY, new GlobalQueryServ);
//					}
					
					try
					{
						Class<?> serialservclass = Class.forName("jadex.platform.service.serialization.SerializationServices", true, cl);
						ISerializationServices servs = (ISerializationServices) serialservclass.getConstructor(IComponentIdentifier.class).newInstance(cid);
						putPlatformValue(cid, DATA_SERIALIZATIONSERVICES, servs);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					putPlatformValue(cid, DATA_TRANSPORTCACHE, new LRU<IComponentIdentifier, Tuple2<ITransportService, Integer>>(2000));
					
					putPlatformValue(cid, DATA_DEFAULT_LOCAL_TIMEOUT, config.getLocalDefaultTimeout());
					putPlatformValue(cid, DATA_DEFAULT_REMOTE_TIMEOUT, config.getRemoteDefaultTimeout());

					ComponentCreationInfo cci = new ComponentCreationInfo(model, config.getConfigurationName(), rootconf.getArgs(), desc, null, null);
					Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
					component.create(cci, features);

					initRescueThread(cid, rootconf);	// Required for bootstrapping init.

					component.init().addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
					{
						public void customResultAvailable(Void result)
						{
							startComponents(0, config.getComponents(), component.getInternalAccess())
								.addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(fret)
							{
								public void customResultAvailable(Void result)
								{
									if(Boolean.TRUE.equals(config.getValue(IRootComponentConfiguration.WELCOME)))
									{
										long startup = System.currentTimeMillis() - starttime;
										// platform.logger.info("Platform startup time: " + startup + " ms.");
										System.out.println(desc.getName()+" platform startup time: " + startup + " ms.");
//										System.out.println(desc.getName()+" platform startup time: " + "799 ms.");
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

		if(config.isPrintExceptions())
		{
			ret.addResultListener(new IResultListener<IExternalAccess>() 
			{
				public void exceptionOccurred(Exception exception) 
				{
					System.out.println(exception.getMessage());
					if(Future.DEBUG)
						exception.printStackTrace();
				}

				public void resultAvailable(IExternalAccess result) 
				{
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Internal method to create a component identifier.
	 *  @param pfname The platform name.
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
				buf.append(Integer.toString(rnd.nextInt(36), 36));
			}
			else if(tok.equals("*"))
			{
				buf.append(Integer.toString(rnd.nextInt(36), 36));
				buf.append(Integer.toString(rnd.nextInt(36), 36));
				buf.append(Integer.toString(rnd.nextInt(36), 36));
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
					String comp	= components.get(i);
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
	public synchronized static void initRescueThread(IComponentIdentifier cid, IRootComponentConfiguration rootconfig)
	{
		IThreadPool	tp	= null;
		if(rootconfig.getThreadpoolClass()!=null)
		{
			try
			{
				tp	= (IThreadPool)SReflect.classForName(
					rootconfig.getThreadpoolClass(), Starter.class.getClassLoader()).newInstance();
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
	public static IFuture<IComponentIdentifier>	createProxy(final IExternalAccess local, final IExternalAccess remote)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		SServiceProvider.getService(remote, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(ITransportAddressService remotetas) throws Exception
			{
				remotetas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(final List<TransportAddress> remoteaddrs) throws Exception
					{
						SServiceProvider.getService(local, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(ITransportAddressService localtas) throws Exception
							{
								localtas.addManualAddresses(remoteaddrs).addResultListener(new ExceptionDelegationResultListener<Void, IComponentIdentifier>(ret)
								{
									public void customResultAvailable(Void result) throws Exception
									{
										SServiceProvider.getService(local, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
											.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
										{
											public void customResultAvailable(final IComponentManagementService localcms)
											{
												Map<String, Object>	args = new HashMap<String, Object>();
												args.put("component", remote.getComponentIdentifier());
												CreationInfo ci = new CreationInfo(args);
												localcms.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", ci, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});

		// Add remote addresses to local address book
//		TransportAddressBook	tab1	= TransportAddressBook.getAddressBook(local.getComponentIdentifier());
//		TransportAddressBook	tab2	= TransportAddressBook.getAddressBook(remote.getComponentIdentifier());
//		tab1.addPlatformAddresses(remote.getComponentIdentifier(), "tcp",
//			tab2.getPlatformAddresses(remote.getComponentIdentifier(), "tcp"));
//		System.out.println("adresses from "+agent+" to "+exta+": "+tab2.getPlatformAddresses(exta.getComponentIdentifier(), "tcp"));



		return ret;
	}

	/**
	 * Get a global platform value.
	 *
	 * @param platform The platform name.
	 * @param key The key.
	 * @return The value.
	 */
	public static synchronized Object getPlatformValue(IComponentIdentifier platform, String key)
	{
		Object ret = null;
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem != null)
			ret = mem.get(key);
		return ret;
	}

	/**
	 * Get a global platform value.
	 *
	 * @param platform The platform name.
	 * @param key The key.
	 * @param value The value.
	 */
	public static synchronized void putPlatformValue(IComponentIdentifier platform, String key, Object value)
	{
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem == null)
		{
			mem = new HashMap<String, Object>();
			platformmem.put(platform, mem);
		}
		mem.put(key, value);
	}

	/**
	 * Get a global platform value.
	 *
	 * @param platform The platform name.
	 * @param key The key.
	 * @return The value.
	 */
	public static synchronized boolean hasPlatformValue(IComponentIdentifier platform, String key)
	{
		boolean ret = false;
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem != null)
			ret = mem.containsKey(key);
		return ret;
	}

	/**
	 * Get a global platform value.
	 *
	 * @param platform The platform name.
	 */
	public static synchronized void removePlatformMemory(IComponentIdentifier platform)
	{
		platformmem.remove(platform.getRoot());
	}

	/**
	 * Get the remote default timeout.
	 */
	public static synchronized long getRemoteDefaultTimeout(IComponentIdentifier platform)
	{
		if(platform == null)
			return IStarterConfiguration.DEFAULT_REMOTE_TIMEOUT;

		platform = platform.getRoot();
		return hasPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT) ? ((Long)getPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT)).longValue() :IStarterConfiguration.DEFAULT_REMOTE_TIMEOUT;
	}

	/**
	 * Get the scaled remote default timeout.
	 */
	public static synchronized long getScaledRemoteDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		long ret = getRemoteDefaultTimeout(platform);
		return ret == -1 ? -1 : (long)(ret * scale);
	}

	/**
	 * Get the local default timeout.
	 */
	public static synchronized long getLocalDefaultTimeout(IComponentIdentifier platform)
	{
		if(platform == null)
			return IStarterConfiguration.DEFAULT_LOCAL_TIMEOUT;

		platform = platform.getRoot();
		return hasPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT) ? ((Long)getPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT)).longValue() : IStarterConfiguration.DEFAULT_LOCAL_TIMEOUT;
	}

	/**
	 * Get the scaled local default timeout.
	 */
	public static synchronized long getScaledLocalDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		long ret = getLocalDefaultTimeout(platform);
		return ret == -1 ? -1 : (long)(ret * scale);
	}

	/**
	 * Set the remote default timeout.
	 */
	public static synchronized void setRemoteDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		putPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT, timeout);
	}

	/**
	 * Set the local default timeout.
	 */
	public static synchronized void setLocalDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		putPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT, timeout);
	}

	/**
	 * Check if the real time timeout flag is set for a platform.
	 */
	public static synchronized boolean isRealtimeTimeout(IComponentIdentifier platform)
	{
		// Hack!!! Should default to false?
		return !Boolean.FALSE.equals(getPlatformValue(platform, DATA_REALTIMETIMEOUT));
	}

	/**
	 * Check if the parameter copy flag is set for a platform.
	 */
	public static synchronized boolean isParameterCopy(IComponentIdentifier platform)
	{
		// not equals false to make true the default.
		return !Boolean.FALSE.equals(getPlatformValue(platform, DATA_PARAMETERCOPY));
	}
	

	/**
	 * Create a platform configuration.
	 *
	 * @param args The command line arguments.
	 * @return StarterConfiguration
	 */
	public static IPlatformConfiguration processArgs(String args)
	{
		return processArgs(args.split("\\s+"));
	}

	/**
	 * Create a platform configuration.
	 *
	 * @param args The command line arguments.
	 * @return StarterConfiguration
	 */
	public static IPlatformConfiguration processArgs(String[] args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getPlatformConfiguration(args);
		if(args != null)
		{
			for(int i = 0; args != null && i + 1 < args.length; i += 2)
			{
				parseArg(args[i], args[i + 1], config);
			}
		}
		config.getRootConfig().setProgramArguments(args);

		return config;
	}

	/**
	 * Create a platform configuration.
	 *
	 * @param args The command line arguments.
	 * @return StarterConfiguration
	 * @deprecated since 3.0.7. Use other processArgs methods instead.
	 */
	@Deprecated
	public static IPlatformConfiguration processArgs(Map<String, String> args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getPlatformConfiguration();
		// ?! hmm needs to be passed as parameter also?
		if(args != null)
		{
			for(Map.Entry<String, String> arg : args.entrySet())
			{
				parseArg(arg.getKey(), arg.getValue(), config);
			}
		}
		return config;
	}

	// public static void parseArg(String key, String stringValue,
	// PlatformConfiguration config) {
	// config.parseArg(key, stringValue);
	// }

//	/**
//	 *
//	 * @param args
//	 */
//	public static void parseArgs(String[] args)
//	{
//		parseArgs(args, this);
//	}
//
//	/**
//	 *
//	 * @param key
//	 * @param val
//	 */
//	public static void parseArg(String key, String val)
//	{
//		parseArg(key, val, this);
//	}

	/**
	 *
	 * @param args
	 * @param config
	 */
	public static void parseArgs(String[] args, IPlatformConfiguration config)
	{
		for(int i = 0; args != null && i < args.length; i += 2)
		{
			parseArg(args[i], args[i + 1], config);
		}
	}

	/**
	 *
	 * @param okey
	 * @param val
	 * @param config
	 */
	public static void parseArg(String okey, String val, IPlatformConfiguration config)
	{
		String key = okey.startsWith("-") ? okey.substring(1) : okey;
		Object value = val;
		if(!IStarterConfiguration.RESERVED.contains(key))
		{
			// if not reserved, value is parsed and written to root config.
			try
			{
				value = SJavaParser.evaluateExpression(val, null);
			}
			catch(Exception e)
			{
				System.out.println("Argument parse exception using as string: " + key + " \"" + val + "\"");
			}
			config.getRootConfig().setValue(key, value);
		}

		config.getStarterConfig().parseArg(key, val, value);
	}
}

