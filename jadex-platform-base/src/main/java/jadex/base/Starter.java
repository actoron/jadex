package jadex.base;

import jadex.base.fipa.CMSComponentDescription;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.javaparser.SJavaParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Starter class for  
 */
public class Starter
{
	//-------- constants --------

	/** The fallback platform configuration. */
	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/standalone/Platform.application.xml";

	/** The component factory to be used for platform component. */
	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.application.ApplicationComponentFactory";

	/** The component factory to be used for platform adapter. */
	public static final String FALLBACK_ADAPTER_FACTORY = "jadex.standalone.ComponentAdapterFactory";

	/** The termination timeout. */
	// Todo: use configuration/argument value if present.
	public static final long	TERMINATION_TIMEOUT	= 20000;

	
	/** The configuration file. */
	public static final String CONFIGURATION_FILE = "conf";
	
	/** The configuration name. */
	public static final String CONFIGURATION_NAME = "configname";
	
	/** The platform name. */
	public static final String PLATFORM_NAME = "platformname";

	/** The component factory classname. */
	public static final String COMPONENT_FACTORY = "componentfactory";
	
	/** The adapter factory classname. */
	public static final String ADAPTER_FACTORY = "adapterfactory";
	
	
	/** The reserved platform parameters. */
	public static final Set RESERVED;
	
	static
	{
		RESERVED = new HashSet();
		RESERVED.add(CONFIGURATION_FILE);
		RESERVED.add(CONFIGURATION_NAME);
		RESERVED.add(PLATFORM_NAME);
		RESERVED.add(COMPONENT_FACTORY);
		RESERVED.add(ADAPTER_FACTORY);
	}
	
	/** The shutdown in progress flag. */
	protected static boolean	shutdown;
	
	//-------- static methods --------
	
	/**
	 *  Test if shutdown is in progress.
	 */
	public static boolean	isShutdown()
	{
		return shutdown;
	}
	
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args)
	{
		createPlatform(args).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IExternalAccess	access	= (IExternalAccess)result;
				Runtime.getRuntime().addShutdownHook(new Thread()
				{
					public void run()
					{
						try
						{
//							System.out.println("killing: "+access.getComponentIdentifier().getPlatformName());
							shutdown	= true;
							access.killComponent().get(new ThreadSuspendable(), TERMINATION_TIMEOUT);
//							System.out.println("killed: "+access.getComponentIdentifier().getPlatformName());
						}
						catch(ComponentTerminatedException cte)
						{
							// Already killed.
						}
						catch(Throwable t)
						{
							t.printStackTrace();
						}
					}
				});
				
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
//						System.exit(0);
//					}
//				}, 5000);
			}
		});
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture createPlatform(String[] args)
	{
		final Future ret = new Future();
		try
		{
			// Absolute start time (for testing and benchmarking).
			final long starttime = System.currentTimeMillis();
		
			final Map cmdargs = new HashMap();
			final Map compargs = new HashMap();
			for(int i=0; args!=null && i<args.length; i+=2)
			{
				String key = args[i].substring(1);
				Object val = args[i+1];
				if(!RESERVED.contains(key))
				{
					try
					{
						val = SJavaParser.evaluateExpression(args[i+1], null);
						compargs.put(key, val);
					}
					catch(Exception e)
					{
						System.out.println("Argument parse exception using as string: "+args[i]+"="+args[i+1]);
					}
				}
				cmdargs.put(key, val);
			}
			
			// Load the platform (component) model.
			final ClassLoader cl = Starter.class.getClassLoader();
			final String configfile = (String)cmdargs.get(CONFIGURATION_FILE)!=null? 
				(String)cmdargs.get(CONFIGURATION_FILE): FALLBACK_PLATFORM_CONFIGURATION;
			String cfclname = (String)cmdargs.get(COMPONENT_FACTORY)!=null? 
				(String)cmdargs.get(COMPONENT_FACTORY): FALLBACK_COMPONENT_FACTORY;
			Class cfclass = SReflect.findClass(cfclname, null, cl);
			// The providerid for this service is not important as it will be thrown away 
			// after loading the first component model.
			final IComponentFactory cfac = (IComponentFactory)cfclass.getConstructor(new Class[]{String.class})
				.newInstance(new Object[]{"rootid"});
			
			cfac.loadModel(configfile, null, cl).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result) 
				{
					final IModelInfo model = (IModelInfo)result;
					
					if(model.getReport()!=null)
						throw new RuntimeException("Error loading model:\n"+model.getReport().getErrorText());
					
					// Create an instance of the component.
					String configname = (String)cmdargs.get("configname")!=null? (String)cmdargs.get("configname"): 
						model.getConfigurations().length>0?  model.getConfigurations()[0]: null;
					
					String platformname = (String)cmdargs.get(PLATFORM_NAME);
					if(platformname==null)
					{
						IArgument[] cargs = model.getArguments();
						for(int i=0; i<cargs.length; i++)
						{
							Object argval = cargs[i].getDefaultValue(configname);
//							if(!compargs.containsKey(cargs[i].getName()))
//							{
//								compargs.put(cargs[i].getName(), argval);
//							}
							if("platformname".equals(cargs[i].getName()))
							{
								platformname = (String)argval;
							}
						}
					}
					if(platformname==null)
					{
						try
						{
							platformname = SUtil.createUniqueId(InetAddress.getLocalHost().getHostName(), 3);
						}
						catch(UnknownHostException e)
						{
							platformname = SUtil.createUniqueId("platform", 3);
						}
					}
					
					final IComponentIdentifier cid = new ComponentIdentifier(platformname);
					// Hack!!! Autoshutdown!?
					
					cfac.getComponentType(configfile, null, cl).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result) 
						{
							try
							{
								String ctype = (String)result;
								final CMSComponentDescription desc = new CMSComponentDescription(cid, ctype, null, null, Boolean.TRUE, model.getFullName());
								
								String afclname = (String)cmdargs.get(ADAPTER_FACTORY)!=null? 
									(String)cmdargs.get(ADAPTER_FACTORY): FALLBACK_ADAPTER_FACTORY;
								Class afclass = SReflect.findClass(afclname, null, cl);
								final IComponentAdapterFactory afac = (IComponentAdapterFactory)afclass.newInstance();
								
								Future future = new Future();
								future.addResultListener(new IResultListener()
								{
									public void resultAvailable(Object result)
									{
										Object[] root = (Object[])result;
										IComponentInstance instance = (IComponentInstance)root[0];
			//							final IComponentAdapter adapter = (IComponentAdapter)root[1];
			//							System.out.println("Instance: "+instance);
										
										long startup = System.currentTimeMillis() - starttime;
										System.out.println(desc.getName()+" platform startup time: " + startup + " ms.");
								//		platform.logger.info("Platform startup time: " + startup + " ms.");
										
										ret.setResult(instance.getExternalAccess());
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ret.setException(exception);
									}
								});
								
								Object[] root = cfac.createComponentInstance(desc, afac, model, (String)cmdargs.get(CONFIGURATION_NAME),
									compargs, null, null, future);
								IComponentAdapter adapter = (IComponentAdapter)root[1];
								
								// Execute init steps of root component on main thread (i.e. platform).
								boolean again = true;
								while(again)
								{
			//						System.out.println("Execute step: "+cid);
									again = afac.executeStep(adapter);
								}
			//					System.out.println("starting component execution");
								
								// Start normal execution of root component (i.e. platform).
								afac.initialWakeup(adapter);
							}
							catch(Exception e)
							{
								ret.setException(e);
							}
						};
					});
				}
			});
	//		System.out.println("Model: "+model);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}
}

