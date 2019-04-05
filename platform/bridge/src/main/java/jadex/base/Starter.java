package jadex.base;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.VersionInfo;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.MethodInvocationInterceptor;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CmsState;
import jadex.bridge.service.types.cms.CmsState.CmsComponentState;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IBootstrapFactory;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.bytecode.vmhacks.VmHacks;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.BlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.collection.IRwMap;
import jadex.commons.collection.LRU;
import jadex.commons.collection.RwMapWrapper;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.TransformSet;
import jadex.javaparser.SJavaParser;


/**
 *  Starter class for starting the Jadex platform. 
 */
public class Starter
{
	//-------- This is platform specific data kept in a common memory --------

	//-------- Platform data keys --------

    /** Flag if copying parameters for local service calls is allowed. */
    public static String DATA_PARAMETERCOPY = IPlatformConfiguration.PARAMETERCOPY;

    /**  Flag if local timeouts should be realtime (instead of clock dependent). */
    public static String DATA_REALTIMETIMEOUT = IPlatformConfiguration.REALTIMETIMEOUT;

    /** The local service registry data key. */
    public static String DATA_SERVICEREGISTRY = "serviceregistry";

    /** The serialization services for serializing and en/decoding objects including remote reference handling. */
    public static String DATA_SERIALIZATIONSERVICES = "serialservs";

    /** The transport cache used to . */
    public static String DATA_TRANSPORTCACHE = "transportcache";

    /** The used to store the current network names. */
    public static String DATA_NETWORKNAMESCACHE = "networknamescache";

    /** The CMS state. */
    public static String DATA_CMSSTATE = "cmsstate";

    /** Constant for default timeout name. */
    public static String DATA_DEFAULT_TIMEOUT = "default_timeout";
    
    /** The bootstrap component factory. */
    public static String DATA_PLATFORMACCESS = "$platformaccess";
    
    /** The bootstrap component factory. */
    public static String DATA_BOOTSTRAPFACTORY = "$bootstrapfactory";
    
    
    // todo: cannot be used because registry needs to know when superpeer changes (remap queries)
//    /** Constant for the superpeer. */
//    public static String DATA_SUPERPEER = "superpeer";

    
   
    
    
    /**
     *  Convert a (string) parameter
     *  @param val
     *  @param target
     *  @return
     */
    public static Object convertParameter(Object val, Class<?> target)
    {
    	Object ret = null;
    	
    	if(val!=null && SReflect.isSupertype(target, val.getClass()))
    	{
    		ret = val;
    	}
    	else if(val instanceof String && ((String)val).length()>0 && Starter.BASICCONVERTER.isSupportedType(target))
    	{
    		try
    		{
    			ret = Starter.BASICCONVERTER.getStringConverter(target).convertString((String)val, null);
    		}
    		catch(Exception e)
    		{
    		}
    	}
    	
    	return ret;
    }

	/** Global platform data. For each platform stored by  */
    protected static final IRwMap<IComponentIdentifier, IRwMap<String, Object>> platformmem = new RwMapWrapper<IComponentIdentifier, IRwMap<String, Object>>(new HashMap<IComponentIdentifier, IRwMap<String, Object>>());
//	protected static final Map<IComponentIdentifier, Map<String, Object>> platformmem = new HashMap<IComponentIdentifier, Map<String, Object>>();

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
	static
	{
		// set secure random to non-blocking entropy source
		SUtil.ensureNonblockingSecureRandom();
		
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
	}
	
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
//				IComponentManagementService	cms	= ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM));
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
	 */
	public static IFuture<IExternalAccess> createPlatform(Map<String, String> args)
	{
		return createPlatform(null, (Map)args);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(String... args)
	{
		return createPlatform(null, parseArgs(args));
	}
	
	/**
	 *  Create the platform.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(IPlatformConfiguration config)
	{
		return createPlatform(config, (Map<String,Object>)null);
	}
	
	/**
	 *  Create the platform.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform()
	{
		return createPlatform(null, (Map<String,Object>)null);
	}
	
	/**
	 *  Create the platform.
	 *  @param config The PlatformConfiguration object.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(final IPlatformConfiguration pconfig, final String[] args)
	{
		return createPlatform(pconfig, parseArgs(args));
	}

	/**
	 *  Get a boolean value from args (otherwise return default value passed as arg).
	 *  @param args The args map.
	 *  @param argname The argument name.
	 *  @param def The default value.
	 *  @return The args value.
	 */
	public static boolean getBooleanValueWithArgs(Map<String, Object> args, String argname, boolean def)
	{
		boolean ret = def;
		if(args!=null)
		{
			Object val = args.get(argname);
			if(val!=null && val instanceof Boolean)
				ret = ((Boolean)val).booleanValue();
		}
		return ret;
	}
	
	/**
	 *  Create the platform.
	 *  @param config The PlatformConfiguration object.
	 *  @return The external access of the root component.
	 */
	public static IFuture<IExternalAccess> createPlatform(final IPlatformConfiguration pconfig, Map<String, Object> pargs)
	{
		// Make all argument keys lower case.
		final Map<String, Object> args = pargs != null ? new HashMap<>() : null;
		if (args != null)
		{
			for (Map.Entry<String, Object> entry : pargs.entrySet())
				args.put(entry.getKey() != null ? entry.getKey().toLowerCase() : null, entry.getValue()); 
		}
		
//		System.out.println("Java Version: " + System.getProperty("java.version"));
		final IPlatformConfiguration config = pconfig!=null? pconfig: PlatformConfigurationHandler.getDefault();
		
//		if(!Boolean.TRUE.equals(config.getValues().get("bisimulation")))
//			System.out.println("no bisim");
		
		config.setReadOnly(true);
		
		if(config.getExtendedPlatformConfiguration().isDropPrivileges())
			VmHacks.get().tryChangeUser(null);
		
//		IRootComponentConfiguration rootconf = config.getRootConfig();
		
		// pass configuration parameters to static fields:
		MethodInvocationInterceptor.DEBUG = getBooleanValueWithArgs(args, "debugservices", config.getExtendedPlatformConfiguration().getDebugServices());
		ExecutionComponentFeature.DEBUG = getBooleanValueWithArgs(args, "debugsteps", config.getExtendedPlatformConfiguration().getDebugSteps());
//		Future.NO_STACK_COMPACTION	= true;
		Future.NO_STACK_COMPACTION	= getBooleanValueWithArgs(args, "nostackcompaction", config.getExtendedPlatformConfiguration().getNoStackCompaction());
		Future.DEBUG = getBooleanValueWithArgs(args, "debugfutures", config.getExtendedPlatformConfiguration().getDebugFutures()); 
		
//		new FastClasspathScanner(new String[]
//		      {"com.xyz.widget", "com.xyz.gizmo" })  // Whitelisted package prefixes
//		  .matchSubclassesOf(DBModel.class,
//		      // c is a subclass of DBModel
//		      c -> System.out.println("Found subclass of DBModel: " + c.getName()))
//		  .matchClassesImplementing(Runnable.class,
//		      // c is a class that implements Runnable
//		      c -> System.out.println("Found Runnable: " + c.getName()))
//		  .matchClassesWithAnnotation(RestHandler.class,
//		      // c is a class annotated with @RestHandler
//		      c -> System.out.println("Found RestHandler annotation on class: "
//		              + c.getName()))
//		  .matchFilenamePattern("^template/.*\\.html",
//		      // templatePath is a path on the classpath that matches the above pattern;
//		      // inputStream is a stream opened on the file or zipfile entry.
//		      // No need to close inputStream before exiting, it is closed by caller.
//		      (templatePath, inputStream) -> {
//		          try {
//		              String template = IOUtils.toString(inputStream, "UTF-8");
//		              System.out.println("Found template: " + absolutePath
//		                      + " (size " + template.length() + ")");
//		          } catch (IOException e) {
//		              throw new RuntimeException(e);
//		          }
//		      })
//		  .scan();  
		
		
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
			final String configfile = config.getExtendedPlatformConfiguration().getConfigurationFile();
			String cfclname = config.getExtendedPlatformConfiguration().getComponentFactory();
			Class<?> cfclass = SReflect.classForName(cfclname, cl);
			// The providerid for this service is not important as it will be thrown away 
			// after loading the first component model.
			final IComponentFactory cfac = (IComponentFactory)cfclass.getConstructor(new Class[]{String.class})
				.newInstance(new Object[]{"rootid"});
			
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
//				config.checkConsistency(); // todo?
//				ClassInfo ci = config.getExtendedPlatformConfiguration().getPlatformComponent();
//				Class<?> pc = ci.getType(cl);
//				Object	pc = config.getValue(RootComponentConfiguration.PLATFORM_COMPONENT);
//				rootConfig.setValue(RootComponentConfiguration.PLATFORM_COMPONENT, pc);
//				if(pc==null)
//				{
//					ret.setException(new RuntimeException("No platform component class found."));
//				}
//				else
//				{
				// Build platform name.
				String pfname = args!=null && args.containsKey(IPlatformConfiguration.PLATFORM_NAME)? (String)args.get(IPlatformConfiguration.PLATFORM_NAME): config.getPlatformName();
//				Object pfname = config.getValue(RootComponentConfiguration.PLATFORM_NAME);
//				rootConfig.setValue(RootComponentConfiguration.PLATFORM_NAME, pfname);
				final IComponentIdentifier cid = createPlatformIdentifier(pfname!=null? pfname: null);
				if(IComponentIdentifier.LOCAL.get()==null)
					IComponentIdentifier.LOCAL.set(cid);
				
				// Check if platform with same name exists in VM
				if(getPlatformValue(cid, DATA_PLATFORMACCESS)!=null)
				{
					ret.setException(new IllegalArgumentException("Platform already exists: "+cid));
					return ret;
				}
				
//				Class<?> pcclass = pc instanceof Class ? (Class<?>)pc : SReflect.classForName(pc.toString(), cl);
//				final IPlatformComponentAccess component = (IPlatformComponentAccess)pcclass.newInstance();
				final IPlatformComponentAccess component = SComponentManagementService.createPlatformComponent(cl);
//				CmsComponentState compstate = new CmsComponentState();
//				((CmsState) getPlatformValue(cid, DATA_CMSSTATE)).getComponentMap().put(cid, compstate);
				
				/** Here */
//					rootconf.setPlatformAccess(component);
				putPlatformValue(cid, DATA_PLATFORMACCESS, component);
				putPlatformValue(cid, DATA_BOOTSTRAPFACTORY, cfac);
//					putPlatformValue(cid, IPlatformConfiguration.PLATFORMARGS, args);
				putPlatformValue(cid, IPlatformConfiguration.PLATFORMCONFIG, config);
				putPlatformValue(cid, IPlatformConfiguration.PLATFORMMODEL, model);
				
				putPlatformValue(cid, DATA_CMSSTATE, new CmsState());
				CmsComponentState compstate = new CmsComponentState();
				compstate.setAccess(component);
				((CmsState) getPlatformValue(cid, DATA_CMSSTATE)).getComponentMap().put(cid, compstate);
				
				// does not work as create with subscomponents is recursive
//					/** The sequentializer to execute getNewFactory() one by one and not interleaved. */
//					CallSequentializer<Object> cmscaller = new CallSequentializer<Object>();
//					cmscaller.addCommand("create", new IResultCommand<IFuture<Object>, Object[]>()
//					{
//						public IFuture<Object> execute(Object[] args)
//						{
////							SComponentManagementService.createComponent(oname, modelname, info, resultlistener, agent)
//							return (IFuture)SComponentManagementService.createComponentInternal((String)args[0], (String)args[1], (CreationInfo)args[2], (IResultListener<Collection<Tuple2<String, Object>>>)args[3], (IInternalAccess)args[4]);
//						}
//					});
//					cmscaller.addCommand("kill", new IResultCommand<IFuture<Object>, Object[]>()
//					{
//						public IFuture<Object> execute(Object[] args)
//						{
////							SComponentManagementService.createComponent(oname, modelname, info, resultlistener, agent)
//							return (IFuture)SComponentManagementService.destroyComponentInternal((IComponentIdentifier)args[0], (IInternalAccess)args[1]);
//						}
//					});
//					putPlatformValue(cid, DATA_CMSSEQ, cmscaller);
				
//					rootconf.setBootstrapFactory(cfac);
//					config.setPlatformModel(model);
				
				// Perform manual switch to allow users specify next call properties
				ServiceCall sc = CallAccess.getCurrentInvocation();
				ServiceCall scn = CallAccess.getNextInvocation();
				if(sc==null)
				{
					if(scn==null)
					{
						scn = CallAccess.getOrCreateNextInvocation();
					}
//						if(scn.getCause()==null)
//						{
//							scn.setCause(new Cause((String)null, "createPlatform"));
//						}

					CallAccess.setCurrentInvocation(scn);
					sc	= scn;
				}

				// Hack: change rid afterwards?!
				ResourceIdentifier rid = (ResourceIdentifier)model.getResourceIdentifier();
				ILocalResourceIdentifier lid = rid.getLocalIdentifier();
				rid.setLocalIdentifier(new LocalResourceIdentifier(cid, lid.getUri()));
				
				String ctype = cfac.getComponentType(configfile, null, model.getResourceIdentifier()).get();
				IComponentIdentifier caller = sc==null? null: sc.getCaller();
//					Cause cause = sc==null? null: sc.getCause();
//					assert cause!=null;
				
//					Boolean autosd = config.getExtendedPlatformConfiguration().getAutoShutdown();
//					Boolean autosd = (Boolean)config.getValue(RootComponentConfiguration.AUTOSHUTDOWN);
//					rootConfig.setValue(RootComponentConfiguration.AUTOSHUTDOWN, autosd);
				PublishEventLevel monitoring = config.getExtendedPlatformConfiguration().getMonitoring();

//					final CMSComponentDescription desc = new CMSComponentDescription(cid, ctype, false, false, 
//						autosd!=null ? autosd.booleanValue() : false, false, false, monitoring, model.getFullName(),
//						null, model.getResourceIdentifier(), System.currentTimeMillis(), caller, false);

				
				final CMSComponentDescription desc = new CMSComponentDescription(cid).setType(ctype).setModelName(model.getFullName())
					.setResourceIdentifier(model.getResourceIdentifier()).setCreationTime(System.currentTimeMillis()).setCreator(caller)
					.setMonitoring(monitoring).setFilename(model.getFilename());

				putPlatformValue(cid, DATA_REALTIMETIMEOUT, config.getValue(DATA_REALTIMETIMEOUT, model));
//					rootConfig.setValue(PlatformConfiguration.DATA_REALTIMETIMEOUT, config.getValue(PlatformConfiguration.DATA_REALTIMETIMEOUT));
				putPlatformValue(cid, DATA_PARAMETERCOPY, config.getValue(DATA_PARAMETERCOPY, model));
//					rootConfig.setValue(PlatformConfiguration.DATA_PARAMETERCOPY, config.getValue(PlatformConfiguration.DATA_PARAMETERCOPY));
				putPlatformValue(cid, DATA_NETWORKNAMESCACHE, new TransformSet<String>());
				putPlatformValue(cid, DATA_SERVICEREGISTRY, new ServiceRegistry());
				
				try
				{
					Class<?> serialservclass = Class.forName("jadex.platform.service.serialization.SerializationServices", true, cl);
					ISerializationServices servs = (ISerializationServices)serialservclass.getConstructor(IComponentIdentifier.class).newInstance(cid);
					putPlatformValue(cid, DATA_SERIALIZATIONSERVICES, servs);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				putPlatformValue(cid, DATA_TRANSPORTCACHE, Collections.synchronizedMap(new LRU<IComponentIdentifier, Tuple2<ITransportService, Integer>>(2000)));
				
				putPlatformValue(cid, DATA_DEFAULT_TIMEOUT, config.getDefaultTimeout());
				putPlatformValue(cid, IClockService.BISIMULATION_CLOCK_FLAG, config.getValue("bisimulation", model));

				Map<String, Object> argsmap = config==null? new HashMap<String, Object>(): config.getValues();
				if(args!=null)
				{
					for(Map.Entry<String, Object> arg: args.entrySet())
					{
						argsmap.put(arg.getKey(), arg.getValue());
					}
					// Must not use putAll() here because special map overrides put() to add also namemapped entries
//						argsmap.putAll(args);
				}
				putPlatformValue(cid, IPlatformConfiguration.PLATFORMARGS, argsmap);
				ComponentCreationInfo cci = new ComponentCreationInfo(model, config.getConfigurationName(), argsmap, desc, null, null);
				Collection<IComponentFeatureFactory> features = cfac.getComponentFeatures(model).get();
				component.create(cci, features);

				initRescueThread(cid, config);	// Required for bootstrapping init.

				IBootstrapFactory fac = (IBootstrapFactory)SComponentManagementService.getComponentFactory(cid);
				
				// Empty init can be overridden by users
				if(config.getInitCommand()!=null)
					config.getInitCommand().execute(cid);
				
				fac.startService(component.getInternalAccess(), rid).addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
				{
					public void customResultAvailable(Void result)
					{
//						SComponentManagementService.removeInitInfo(cid);
//						CmsComponentState compstate = new CmsComponentState();
//						((CmsState) getPlatformValue(cid, DATA_CMSSTATE)).getComponentMap().put(cid, compstate);
//						compstate.setAccess(component);
//						SComponentManagementService.getComponents(cid).put(cid, component);
					
						component.init().addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(fret)
						{
							public void customResultAvailable(Void result)
							{
								@SuppressWarnings("rawtypes")
								List comps = config.getComponents();
								if(args!=null && args.containsKey("component"))
								{
									comps	= (List<?>)args.get("component");
									if(config.getComponents()!=null)
									{
										comps.addAll((List<?>)config.getComponents());
									}
								}
								
								startComponents(0, comps, component.getInternalAccess())
									.addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(fret)
								{
									public void customResultAvailable(Void result)
									{
										if(Boolean.TRUE.equals(config.getValue(IPlatformConfiguration.WELCOME, model)))
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
				});
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
			platformname	= platformname.replace(':', '$'); // Dot in host name on Mac !?
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
		platformname = SUtil.intern(buf.toString());
		
		// Create an instance of the component.
		return new ComponentIdentifier(platformname).getRoot();
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
		
		if(components!=null && i<components.size())
		{
//			IComponentManagementService cms = instance.getFeature(IRequiredServicesFeature.class).getLocalService(IComponentManagementService.class);
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
//			System.out.println("comp: "+comp+" config: "+config+" args: "+args);
			
			if(args!=null)
			{
				try
				{
//					args = args.replace("\\\"", "\"");
					Object o = SJavaParser.evaluateExpression(args, null);
					if(!(o instanceof Map))
					{
						throw new RuntimeException("Arguments must evaluate to Map<String, Object>"+args);
					}
					oargs = (Map<String, Object>)o;
				}
				catch(Exception e)
				{
//					System.out.println("args: "+args);
//					e.printStackTrace();
					throw new RuntimeException("Arguments evaluation error: "+e);
				}
			}
			
			CreationInfo ci = new CreationInfo(config, oargs);
			ci.setName(name);
			ci.setFilename(comp);
			
			instance.createComponent(ci)
				.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					startComponents(i+1, components, instance)
						.addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			
//			cms.createComponent(name, comp, new CreationInfo(config, oargs), null)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
//			{
//				public void customResultAvailable(IComponentIdentifier result)
//				{
//					startComponents(i+1, components, instance)
//						.addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});
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
	public synchronized static void initRescueThread(IComponentIdentifier cid, IPlatformConfiguration rootconfig)
	{
		IThreadPool	tp	= null;
		if(rootconfig.getExtendedPlatformConfiguration().getThreadpoolClass()!=null)
		{
			try
			{
				tp = (IThreadPool)SReflect.classForName(
					rootconfig.getExtendedPlatformConfiguration().getThreadpoolClass(), Starter.class.getClassLoader()).newInstance();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		assert cid.getParent()==null;
		if(rescuethreads==null)
			rescuethreads = new HashMap<IComponentIdentifier, Tuple2<BlockingQueue, Thread>>();
		
		final BlockingQueue bq = new BlockingQueue();
		final IComponentIdentifier fcid = cid;
		Runnable run = new Runnable()
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
							Logger.getLogger(fcid.getLocalName()).severe("Exception during step on rescue thread: "+SUtil.getExceptionStacktrace(e));
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
			Thread rescuethread = new Thread(run, "rescue_thread_"+cid.getName());
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
					Logger.getLogger(cid.getLocalName()).severe("Exception during step on rescue thread: "+SUtil.getExceptionStacktrace(e));
				}
			}
		}
	}
	
	/**
	 *  Create a proxy for the remote platform.
	 */
	public static IFuture<IExternalAccess> createProxy(final IExternalAccess local, final IExternalAccess remote)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		remote.searchService( new ServiceQuery<>(ITransportAddressService.class)).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IExternalAccess>(ret)
		{
			public void customResultAvailable(ITransportAddressService remotetas) throws Exception
			{
				remotetas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, IExternalAccess>(ret)
				{
					public void customResultAvailable(final List<TransportAddress> remoteaddrs) throws Exception
					{
						local.searchService( new ServiceQuery<>(ITransportAddressService.class)).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IExternalAccess>(ret)
						{
							public void customResultAvailable(ITransportAddressService localtas) throws Exception
							{
								localtas.addManualAddresses(remoteaddrs).addResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
								{
									public void customResultAvailable(Void result) throws Exception
									{
										Map<String, Object>	args = new HashMap<String, Object>();
										args.put("component", remote.getId().getRoot());
										CreationInfo ci = new CreationInfo(args);
										local.createComponent(ci.setFilename("\"jadex/platform/service/remote/ProxyAgent.class\"")).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
										
//										local.searchService( new ServiceQuery<>(IComponentManagementService.class))
//											.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
//										{
//											public void customResultAvailable(final IComponentManagementService localcms)
//											{
//												Map<String, Object>	args = new HashMap<String, Object>();
//												args.put("component", remote.getId().getRoot());
//												CreationInfo ci = new CreationInfo(args);
//												localcms.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", ci, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//											}
//										});
									}
								});
							}
						});
					}
				});
			}
		});
		/*remote.searchService( new ServiceQuery<>( ITransportAddressService.class, ServiceScope.PLATFORM)).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(ITransportAddressService remotetas) throws Exception
			{
				remotetas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(final List<TransportAddress> remoteaddrs) throws Exception
					{
						local.searchService( new ServiceQuery<>( ITransportAddressService.class, ServiceScope.PLATFORM)).addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(ITransportAddressService localtas) throws Exception
							{
								localtas.addManualAddresses(remoteaddrs).addResultListener(new ExceptionDelegationResultListener<Void, IComponentIdentifier>(ret)
								{
									public void customResultAvailable(Void result) throws Exception
									{
										local.searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
											.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
										{
											public void customResultAvailable(final IComponentManagementService localcms)
											{
												Map<String, Object>	args = new HashMap<String, Object>();
												args.put("component", remote.getComponentIdentifier().getRoot());
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
		});*/

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
	public static Object getPlatformValue(IComponentIdentifier platform, String key)
	{
		//System.out.println("getPV: "+platform.getRoot()+" "+key);
		
		Object ret = null;
		IRwMap<String, Object> mem = platformmem.get(platform.getRoot());
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
	public static void putPlatformValue(IComponentIdentifier platform, String key, Object value)
	{
		IRwMap<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem == null)
		{
			platformmem.getWriteLock().lock();
			mem = platformmem.get(platform.getRoot());
			if (mem == null)
			{
				mem = new RwMapWrapper<String, Object>(new HashMap<String, Object>());
				platformmem.put(platform, mem);
			}
			platformmem.getWriteLock().unlock();
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
	public static boolean hasPlatformValue(IComponentIdentifier platform, String key)
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
	public static void removePlatformMemory(IComponentIdentifier platform)
	{
		platformmem.remove(platform.getRoot());
	}

	/**
	 * Get the default timeout.
	 */
	public static long getDefaultTimeout(IComponentIdentifier platform)
	{
		return platform!=null && hasPlatformValue(platform, DATA_DEFAULT_TIMEOUT)
			? ((Long)getPlatformValue(platform, DATA_DEFAULT_TIMEOUT)).longValue()
			: SUtil.DEFTIMEOUT;
	}

	/**
	 * Get the scaled default timeout.
	 */
	public static long getScaledDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		long ret = getDefaultTimeout(platform);
		return ret == -1 ? -1 : (long)(ret * scale);
	}

	/**
	 * Set the default timeout.
	 */
	public static void setDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		putPlatformValue(platform, DATA_DEFAULT_TIMEOUT, timeout);
	}

	/**
	 * Check if the real time timeout flag is set for a platform.
	 */
	public static boolean isRealtimeTimeout(IComponentIdentifier platform)
	{
		// Hack!!! Should default to false?
		return !Boolean.FALSE.equals(getPlatformValue(platform, DATA_REALTIMETIMEOUT));
	}

	/**
	 * Check if the parameter copy flag is set for a platform.
	 */
	public static boolean isParameterCopy(IComponentIdentifier platform)
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
	public static Map<String, Object> parseArgs(String args)
	{
		return parseArgs(args.split("\\s+"));
	}
	
	/**
	 *
	 * @param args
	 * @param config
	 */
	public static Map<String, Object> parseArgs(String[] args)
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		for(int i=0; args!=null && i < args.length; i+=2)
		{
			parseArg(args[i], args[i + 1], ret);
		}
		return ret;
	}

	/**
	 *
	 * @param okey
	 * @param val
	 * @param config
	 */
	public static void parseArg(String okey, String val, Map<String, Object> vals)
	{
		String key = okey.startsWith("-") ? okey.substring(1) : okey;
		Object value = val;
//		if(!IStarterConfiguration.RESERVED.contains(key))
//		{
			// if not reserved, value is parsed and written to root config.
			try
			{
				value = SJavaParser.evaluateExpression(val, null);
			}
			catch(Exception e)
			{
				System.out.println("Argument parse exception using as string: " + key + " \"" + val + "\"");
			}
			
			// Hack!!! Allow multiple "component" args, TODO: other "multi" args?
			if("component".equals(key))
			{
				@SuppressWarnings("unchecked")
				List<String>	comps	= (List<String>)vals.get(key);
				if(comps==null)
				{
					comps	= new ArrayList<String>();
					vals.put(key, comps);
				}
				if(value instanceof Class)
				{
					comps.add(((Class<?>)value).getName()+".class");
				}
				else
				{
					comps.add(value.toString());
				}
			}
			else
			{
				Object	prev	= vals.put(key, value);
				if(prev!=null)
				{
					System.out.println("Duplicate argument '"+key+"': ignoring value '"+prev+"' and using value '"+value+"'");
				}
			}
//			config.getRootConfig().setValue(key, value);
//		}

//		config.getStarterConfig().parseArg(key, val, value);
	}

//	/**
//	 * Create a platform configuration.
//	 *
//	 * @param args The command line arguments.
//	 * @return StarterConfiguration
//	 */
//	public static IPlatformConfiguration processArgs(String[] args)
//	{
//		IPlatformConfiguration config = PlatformConfigurationHandler.getPlatformConfiguration(args);
//		if(args != null)
//		{
//			for(int i = 0; args != null && i + 1 < args.length; i += 2)
//			{
//				parseArg(args[i], args[i + 1], config);
//			}
//		}
//		config.getRootConfig().setProgramArguments(args);
//
//		return config;
//	}
	
//	/**
//	 * Create a platform configuration.
//	 *
//	 * @param args The command line arguments.
//	 * @return StarterConfiguration
//	 */
//	public static IPlatformConfiguration processArgs(String[] args)
//	{
//		IPlatformConfiguration config = PlatformConfigurationHandler.getPlatformConfiguration(args);
//		if(args != null)
//		{
//			for(int i = 0; args != null && i + 1 < args.length; i += 2)
//			{
//				parseArg(args[i], args[i + 1], config);
//			}
//		}
//		config.getRootConfig().setProgramArguments(args);
//
//		return config;
//	}

//	/**
//	 * Create a platform configuration.
//	 *
//	 * @param args The command line arguments.
//	 * @return StarterConfiguration
//	 * @deprecated since 3.0.7. Use other processArgs methods instead.
//	 */
//	@Deprecated
//	public static IPlatformConfiguration processArgs(Map<String, String> args)
//	{
//		IPlatformConfiguration config = PlatformConfigurationHandler.getPlatformConfiguration();
//		// ?! hmm needs to be passed as parameter also?
//		if(args != null)
//		{
//			for(Map.Entry<String, String> arg : args.entrySet())
//			{
//				parseArg(arg.getKey(), arg.getValue(), config);
//			}
//		}
//		return config;
//	}

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

	
}

