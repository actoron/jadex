package jadex.base;

import jadex.base.fipa.CMSComponentDescription;
import jadex.base.gui.SwingDefaultResultListener;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Starter class for  
 */
public class Starter
{
	//-------- constants --------

	/** The fallback platform configuration. */
	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/standalone/Platform.component.xml";

	/** The component factory to be used for platform component. */
	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.component.ComponentComponentFactory";

	/** The component factory to be used for platform adapter. */
	public static final String FALLBACK_ADAPTER_FACTORY = "jadex.standalone.ComponentAdapterFactory";

	/** The termination timeout. */
	// Todo: use configuration/argument value if present.
	public static final long	TERMINATION_TIMEOUT	= 2000000;

	
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
	
	/** The autoshutdown flag. */
	public static final String AUTOSHUTDOWN = "autoshutdown";

	/** The component flag (for starting an additional component). */
	public static final String COMPONENT = "component";

	
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
		RESERVED.add(COMPONENT);
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
//				final IExternalAccess	access	= (IExternalAccess)result;
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
			final List components = new ArrayList();
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
				
				if(COMPONENT.equals(key))
				{
					components.add(val);
				}
				else
				{
					cmdargs.put(key, val);
				}
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
			
			compargs.put(COMPONENT_FACTORY, cfac);
			
			cfac.loadModel(configfile, null, cl).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result) 
				{
					final IModelInfo model = (IModelInfo)result;
					
					if(model.getReport()!=null)
						throw new RuntimeException("Error loading model:\n"+model.getReport().getErrorText());
					
					// Create an instance of the component.
					
					String platformname = (String)cmdargs.get(PLATFORM_NAME);
					if(platformname==null)
					{
						String	configname	= (String)cmdargs.get("configname");
						ConfigurationInfo	config	= configname!=null
							? model.getConfiguration(configname) 
							: model.getConfigurations().length>0 ? model.getConfigurations()[0] : null;
							
						boolean	found	= false;
						Object	value	= null;
						if(config!=null)
						{
							UnparsedExpression[]	upes	= config.getArguments();
							for(int i=0; !found && i<upes.length; i++)
							{
								if(PLATFORM_NAME.equals(upes[i].getName()))
								{
									found	= true;
									value	= null;
								}
							}
						}
						if(!found)
						{
							 IArgument	arg	= model.getArgument(PLATFORM_NAME);
							 if(arg!=null)
							 {
								value	= arg.getDefaultValue(); 
							 }
						}
						if(value instanceof UnparsedExpression)
						{
							// todo: language
							UnparsedExpression	upe	= (UnparsedExpression)value;
							value = SJavaParser.evaluateExpression(upe.getValue(), model.getAllImports(), null, null);
						}
						if(value instanceof String)
						{
							platformname	= (String)value;
						}
						else if(value!=null)
						{
							ret.setException(new RuntimeException("platformname not string: "+value));
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
								Boolean autosd = (Boolean)cmdargs.get(AUTOSHUTDOWN);
								final CMSComponentDescription desc = new CMSComponentDescription(cid, ctype, null, null, autosd, model.getFullName(), null);
								
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
										final IComponentInstance instance = (IComponentInstance)root[0];
			//							final IComponentAdapter adapter = (IComponentAdapter)root[1];
			//							System.out.println("Instance: "+instance);
										
										
										final CounterResultListener	crl	= new CounterResultListener(components.size(), new DelegationResultListener(ret)
										{
											public void customResultAvailable(Object result)
											{
												long startup = System.currentTimeMillis() - starttime;
												//		platform.logger.info("Platform startup time: " + startup + " ms.");
												System.out.println(desc.getName()+" platform startup time: " + startup + " ms.");
												ret.setResult(instance.getExternalAccess());
											}
										});
										
										// Start additional components.
										if(!components.isEmpty())
										{
											SServiceProvider.getService(instance.getServiceContainer(), IComponentManagementService.class)
												.addResultListener(new DelegationResultListener(ret)
											{
												public void customResultAvailable(Object result)
												{
													IComponentManagementService	cms	= (IComponentManagementService)result;
													for(int i=0; i<components.size(); i++)
													{
														String	name	= null;
														String	config	= null;
														String	comp	= (String)components.get(i);
														int	i1	= comp.indexOf(':');
														if(i1!=-1)
														{
															name	= comp.substring(0, i1);
															comp	= comp.substring(i1+1);
														}
														int	i2	= comp.indexOf('(');
														if(i2!=-1)
														{
															if(comp.endsWith("("))
															{
																config	= comp.substring(i2+1, comp.length()-1);
																comp	= comp.substring(0, i2);
															}
															else
															{
																throw new RuntimeException("Component specification does not match scheme [<name>:]<type>[(<config>)] : "+components.get(i));
															}
														}
														
														cms.createComponent(name, comp, new CreationInfo(config, null), null)
															.addResultListener(crl);
													}
												}
											});
										}
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ret.setException(exception);
									}
								});
								
								cfac.createComponentInstance(desc, afac, model, (String)cmdargs.get(CONFIGURATION_NAME),
									compargs, null, null, future).addResultListener(new SwingDefaultResultListener()
								{
									public void customResultAvailable(Object result)
									{
										Object[] root = (Object[]) result;
										
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
								});
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

