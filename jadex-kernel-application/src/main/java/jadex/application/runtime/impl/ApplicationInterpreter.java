package jadex.application.runtime.impl;

import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MComponentInstance;
import jadex.application.model.MComponentType;
import jadex.application.model.MExpressionType;
import jadex.application.model.MProvidedServiceType;
import jadex.application.model.MSpaceInstance;
import jadex.application.runtime.IApplication;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.runtime.ISpace;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.ComponentServiceContainer;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.CreationInfo;
import jadex.bridge.DecouplingServiceInvocationInterceptor;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IModelInfo;
import jadex.commons.ChangeEvent;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.CacheServiceContainer;
import jadex.commons.service.IInternalService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.ServiceNotFoundException;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The application interpreter provides a closed environment for components.
 *  If components spawn other components, these will automatically be added to
 *  the context.
 *  When the context is deleted all components will be destroyed.
 *  An component must only be in one application context.
 */
public class ApplicationInterpreter implements IApplication, IComponentInstance, IInternalAccess
{
	//-------- constants --------
	
	/** Application state, while waiting for initial futures. */
	protected static final String	STATE_INITFUTURES	= "init-futures";
	
	/** Application state, when ready to init. */
	protected static final String	STATE_INITREADY	= "init-ready";
	
	/** Application state, when already started. */
	protected static final String	STATE_STARTED	= "started";
	
	//-------- attributes --------
	
	/** The application configuration. */
	protected MApplicationInstance	config;
	
	/** The contained spaces. */
	protected Map spaces;
	
	/** The properties. */
	protected Map properties;
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The application type. */
	protected MApplicationType model;
	
	/** The parent component. */
	protected IExternalAccess parent;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more components can be added). */
	protected boolean	terminating;
	
	/** Component type mapping (cid -> modelname) and (modelname->application component type). */
	protected Map ctypes;
	protected MultiCollection instances;
	
	/** The arguments. */
	protected Map arguments;
	
	/** The arguments. */
	protected Map results;
	
	/** The value fetcher. */
	protected IValueFetcher	fetcher;
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** The scheduled steps of the component. */
	protected List steps;
	
	/** Flag indicating an added step will be executed without the need for calling wakeup(). */
	// Required for startup bug fix in scheduleStep (synchronization between main thread and executor).
	// While main is running the root component steps, invoke later must not be called to prevent double execution.
	protected boolean willdostep;
	
	/** The component listeners. */
	protected List componentlisteners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ApplicationInterpreter(final IComponentDescription desc, final MApplicationType model, final MApplicationInstance config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, final Future inited)
	{
		this.config	= config;
		this.model = model;
		this.parent = parent;
		this.arguments = arguments==null ? new HashMap() : arguments;
		this.results = new HashMap();
		this.properties = new HashMap();
		this.ctypes = new HashMap(); 
		this.instances = new MultiCollection(); 
		this.steps	= new ArrayList();
		this.willdostep	= true;
		this.adapter = factory.createComponentAdapter(desc, model.getModelInfo(), this, parent);
	
		// Init the arguments with default values.
		String[] configs = model.getModelInfo().getConfigurations();
		String configname = config!=null? config.getName(): configs.length>0? configs[0]: null;
		IArgument[] args = model.getModelInfo().getArguments();
		for(int i=0; i<args.length; i++)
		{
			if(args[i].getDefaultValue(configname)!=null)
			{
				if(ApplicationInterpreter.this.arguments.get(args[i].getName())==null)
				{
					ApplicationInterpreter.this.arguments.put(args[i].getName(), args[i].getDefaultValue(configname));
				}
			}
		}
		
		// Init the results with default values.
		IArgument[] res = model.getModelInfo().getResults();
		for(int i=0; i<res.length; i++)
		{
			if(res[i].getDefaultValue(configname)!=null)
			{
				ApplicationInterpreter.this.results.put(res[i].getName(), res[i].getDefaultValue(configname));
			}
		}
		
		final SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$args", getArguments());
		fetcher.setValue("$properties", properties);
		fetcher.setValue("$results", getResults());
		fetcher.setValue("$component", ApplicationInterpreter.this);
		ApplicationInterpreter.this.fetcher = fetcher;
		
		// Init service container.
		MExpressionType mex = model.getContainer();
		if(mex!=null)
		{
			container = (IServiceContainer)mex.getParsedValue().getValue(fetcher);
		}
		else
		{
//			container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
			container = new ComponentServiceContainer(getComponentAdapter());
		}
		
		fetcher.setValue("$provider", getServiceProvider());
		
		// Schedule the futures (first) init step.
		scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "init"; 
			public Object execute(final IInternalAccess ia)
			{
				final List futures = new ArrayList();
		
				List services = model.getProvidedServices();
				if(services!=null)
				{
					for(int i=0; i<services.size(); i++)
					{
						IInternalService service;
						final MProvidedServiceType st = (MProvidedServiceType)services.get(i);
						if(st.getParsedValue()!=null)
						{
							try
							{
								service = (IInternalService)st.getParsedValue().getValue(fetcher);
								if(!st.isDirect())
								{
//									System.out.println("creating decoupled service: "+st.getClassName());
									service = DecouplingServiceInvocationInterceptor.createServiceProxy(getExternalAccess(), getComponentAdapter(), service);
								}
								container.addService(service);
							}
							catch(Exception e)
							{
//								e.printStackTrace();
								getLogger().warning("Service creation error: "+st.getParsedValue());
							}
						}
						else 
						{
							if(st.getComponentName()!=null)
							{
								final Future futu = new Future();
								futures.add(futu);
								SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class)
									.addResultListener(ia.createResultListener(new DelegationResultListener(futu)
								{
									public void customResultAvailable(Object result)
									{
										final IComponentManagementService cms = (IComponentManagementService)result;
										IComponentIdentifier cid = cms.createComponentIdentifier(st.getComponentName(), st.getComponentName().indexOf("@")==-1);
										IInternalService service = CompositeServiceInvocationInterceptor.createServiceProxy(st.getClazz(), null, 
											(IApplicationExternalAccess)getExternalAccess(), getModel().getClassLoader(), cid);
										container.addService(service);
										futu.setResult(null);
									}
								}));
							}
							else if(st.getComponentType()==null)
							{	
								final Future futu = new Future();
								futures.add(futu);
								findComponentType(0, model.getMComponentTypes(), st.getClazz(), futu);
							}
							else
							{
//								service = (IInternalService)Proxy.newProxyInstance(getClassLoader(), new Class[]{IInternalService.class, st.getClazz()}, 
//									new CompositeServiceInvocationInterceptor((IApplicationExternalAccess)getExternalAccess(), componenttype, st.getClazz()));
								service = CompositeServiceInvocationInterceptor.createServiceProxy(st.getClazz(), st.getComponentType(), 
									(IApplicationExternalAccess)getExternalAccess(), getModel().getClassLoader(), null);
								container.addService(service);
							}
						}
						
//						System.out.println("added: "+service+" "+getComponentIdentifier());
					}
				}
		
				// Evaluate (future) properties.
				List props	= model.getPropertyList();
				if(props!=null)
				{
					for(int i=0; i<props.size(); i++)
					{
						final MExpressionType	mexp	= (MExpressionType)props.get(i);
						final Object	val	= mexp.getParsedValue().getValue(fetcher);
						if(mexp.getClazz()!=null && SReflect.isSupertype(IFuture.class, mexp.getClazz()))
						{
		//					System.out.println("Future property: "+mexp.getName()+", "+val);
							if(val instanceof IFuture)
							{
								// Use second future to start component only when value has already been set.
								final Future retu = new Future();
								((IFuture)val).addResultListener(new ComponentResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										synchronized(properties)
										{
		//									System.out.println("Setting future property: "+mexp.getName()+" "+result);
											properties.put(mexp.getName(), result);
										}
										retu.setResult(result);
									}
								}, getComponentAdapter()));
								futures.add(retu);
							}
							else if(val!=null)
							{
								throw new RuntimeException("Future property must be instance of jadex.commons.IFuture: "+mexp.getName()+", "+mexp.getValue());
							}
						}
						else
						{
							// Todo: handle specific properties (logging etc.)
							properties.put(mexp.getName(), val);
						}
					}
				}
				
				final IComponentStep init2 = new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						container.start().addResultListener(new ComponentResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								// Create spaces for context.
//								System.out.println("comp services start finished: "+getComponentIdentifier());
								List spaces = config.getMSpaceInstances();
								if(spaces!=null)
								{
									for(int i=0; i<spaces.size(); i++)
									{
										MSpaceInstance si = (MSpaceInstance)spaces.get(i);
										try
										{
											ISpace space = (ISpace)si.getClazz().newInstance();
											addSpace(si.getName(), space);
											space.initSpace(ApplicationInterpreter.this, si, fetcher);
										}
										catch(Exception e)
										{
											System.out.println("Exception while creating space: "+si.getName());
											e.printStackTrace();
										}
									}
								}

								final List components = config.getMComponentInstances();
								SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class).addResultListener(createResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										// NOTE: in current implementation application waits for subcomponents
										// to be finished and cms implements a hack to get the external
										// access of an uninited parent.
										
										// (NOTE1: parent cannot wait for subcomponents to be all created
										// before setting itself inited=true, because subcomponents need
										// the parent external access.)
										
										// (NOTE2: subcomponents must be created one by one as they
										// might depend on each other (e.g. bdi factory must be there for jcc)).
										
										final IComponentManagementService ces = (IComponentManagementService)result;
										createComponent(components, ces, 0, inited);
									}
								}));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								inited.setException(exception);
							}
						}, adapter));
						
						return null;
					}
				};
				
				IResultListener	crl	= new CounterResultListener(futures.size(), new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						scheduleStep(init2);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						inited.setException(exception);
					}
				});
				for(int i=0; i<futures.size(); i++)
				{
					((IFuture)futures.get(i)).addResultListener(crl);
				}
				return null;
			}	
		});
	}
	
	/**
	 *  Find component type that provided a specific service.
	 */
	protected void findComponentType(final int i, final List componenttypes, final Class servicetype, final Future ret)
	{
		final MComponentType ct = (MComponentType)componenttypes.get(i);
	
		SServiceProvider.getService(getServiceProvider(), new ComponentFactorySelector(ct.getFilename(), 
			model.getAllImports(), model.getModelInfo().getClassLoader())).addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println("create start2: "+ct.getFilename());
				
				final IComponentFactory factory = (IComponentFactory)result;
				if(factory!=null)
				{
					final IModelInfo lmodel = factory.loadModel(ct.getFilename(), model.getAllImports(), model.getModelInfo().getClassLoader());
					Class[] sers = lmodel.getProvidedServices();
					if(SUtil.arrayContains(sers, servicetype))
					{
						IInternalService service = CompositeServiceInvocationInterceptor.createServiceProxy(servicetype, ct.getName(), 
							(IApplicationExternalAccess)getExternalAccess(), getModel().getClassLoader(), null);
						container.addService(service);
						ret.setResult(result);
					}
					else if(i+1<componenttypes.size())
					{
						findComponentType(i+1, componenttypes, servicetype, ret);
					}
					else
					{
						ret.setException(new RuntimeException("No component type offers service type: "+servicetype));
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("No factory found for: "+ct);
				if(i+1<componenttypes.size())
				{
					findComponentType(i+1, componenttypes, servicetype, ret);
				}
				else
				{
					ret.setException(new RuntimeException("No component type offers service type: "+servicetype));
				}
			}
		}));
	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 */
	public IFuture scheduleStep(final IComponentStep step)
	{
		Future ret = new Future();
		
		boolean dowakeup;
		synchronized(steps)
		{
			steps.add(new Object[]{step, ret});
			dowakeup	= !willdostep;	// only wake up if not already scheduled.
		}
//		notifyListeners(new ChangeEvent(this, "addStep", step));
		
		if(dowakeup)
		{
			adapter.wakeup();
		}
		
		return ret;
	}
	
//	/**
//	 * Load an component model.
//	 * @param model The model.
//	 * @return The loaded model.
//	 */
//	public static IFuture loadModel(final IServiceProvider provider, final String model)
//	{
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(provider, ILibraryService.class).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				final ILibraryService ls = (ILibraryService)result;
//				
//				SServiceProvider.getService(provider, new ComponentFactorySelector(model, null, ls.getClassLoader())).addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object source, Object result)
//					{
//						IComponentFactory fac = (IComponentFactory)result;
//						ret.setResult(fac!=null ? fac.loadModel(model, null, ls.getClassLoader()) : null);
//					}
//				});
//			}
//		});
//		return ret;
//	}

	//-------- space handling --------
		
	/**
	 *  Add a space to the context.
	 *  @param space The space.
	 */
	public synchronized void addSpace(String name, ISpace space)
	{
		if(spaces==null)
			spaces = new HashMap();
		
		spaces.put(name, space);
		
		// Todo: Add spaces dynamically (i.e. add existing components to space).
	}
	
	/**
	 *  Add a space to the context.
	 *  @param name The space name.
	 */
	public synchronized void removeSpace(String name)
	{
		if(spaces!=null)
		{
			spaces.remove(name);
			if(spaces.isEmpty())
			{
				spaces = null;
			}
		}

//		System.out.println("Removed space: "+name);
	}
	
	/**
	 *  Get a space by name.
	 *  @param name The name.
	 *  @return The space.
	 */
	public synchronized ISpace getSpace(String name)
	{
		return spaces==null? null: (ISpace)spaces.get(name);
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return adapter.getLogger();
	}
	
	//-------- template methods --------

	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Default context behavior is to do nothing.
	 *  @param application	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void deleteContext()
	{
		this.setTerminating(true);
//		final IComponentIdentifier[]	components	= getComponents();
//		if(components!=null && components.length>0)
//		{
//			// Create AMS result listener (l2), when listener is used.
//			// -> notifies listener, when last component is killed.
//			IResultListener	l2	= listener!=null ? new IResultListener()
//			{
//				int tokill	= components.length;
//				Exception	exception;
//				
//				public void resultAvailable(Object result)
//				{
//					result();
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					if(this.exception==null)	// Only return first exception.
//						this.exception	= exception;
//					result();
//				}
//				
//				/**
//				 *  Called for each killed component.
//				 *  Decrease counter and notify listener, when last component is killed.
//				 */
//				protected void	result()
//				{
//					tokill--;
//					if(tokill==0)
//					{
//						for(Iterator it=spaces.values().iterator(); it.hasNext(); )
//						{
//							ISpace space = (ISpace)it.next();
//							space.terminate();
//						}
//						
//						if(exception!=null)
//							listener.exceptionOccurred(exception);
//						else
//							listener.resultAvailable(Application.this);
//					}
//				}
//			} : null;
//			
//			// Kill all components in the context. 
////			IAMS ams = (IAMS) platform.getService(IAMS.class);
//			for(int i=0; i<components.length; i++)
//			{
//				IComponentManagementService ces = (IComponentManagementService)container.getService(IComponentManagementService.class);
//				ces.destroyComponent(components[i], l2);
////				ams.destroyComponent(components[i], l2);
//			}
//		}
//		else
		{
			if(spaces!=null && spaces.values()!=null)
			{
				for(Iterator it=spaces.values().iterator(); it.hasNext(); )
				{
					ISpace space = (ISpace)it.next();
					space.terminate();
				}
			}
		}
	}

	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public IFuture	componentCreated(final IComponentDescription desc, final IModelInfo model)
	{
//		System.out.println("comp created: "+desc.getName()+" "+Application.this.getComponentIdentifier()+" "+children);

		// Checks if loaded model is defined in the application component types
		return scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "created"; 
			public Object execute(IInternalAccess ia)
			{
				IComponentIdentifier cid = desc.getName();
				
				String modelname = model.getFullName();
				String appctype = (String)ctypes.get(modelname);
				if(appctype==null)
				{
					List atypes	= ApplicationInterpreter.this.model.getMComponentTypes();
					for(int i=0; i<atypes.size(); i++)
					{
						final MComponentType atype = (MComponentType)atypes.get(i);
						String tmp = atype.getFilename().replace('/', '.');
						if(tmp.indexOf(modelname)!=-1)
						{
							ctypes.put(modelname, atype.getName());
							appctype = atype.getName();
							break;
						}
					}
				}
				if(appctype!=null)
				{
					ctypes.put(cid, appctype);
					instances.put(appctype, cid);
				}
				/* TODO: Check removed because WfMS requires adding arbitrary subcomponents (processes).
				else if(parent!=null)
				{
					throw new RuntimeException("Unknown/undefined component type: "+model);
				}*/
				
				ISpace[]	aspaces	= null;
				synchronized(this)
				{
					if(spaces!=null)
					{
						aspaces	= (ISpace[])spaces.values().toArray(new ISpace[spaces.size()]);
					}
				}

				if(aspaces!=null)
				{
					for(int i=0; i<aspaces.length; i++)
					{
						aspaces[i].componentAdded(cid);
					}
				}
				return null;
			}
		});
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public IFuture	componentDestroyed(final IComponentDescription desc)
	{
		return scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "destroyed"; 
			public Object execute(IInternalAccess ia)
			{
		//		System.out.println("comp removed: "+desc.getName()+" "+this.getComponentIdentifier());
				IComponentIdentifier cid = desc.getName();
				ISpace[]	aspaces	= null;
				synchronized(this)
				{
					if(spaces!=null)
					{
						aspaces	= (ISpace[])spaces.values().toArray(new ISpace[spaces.size()]);
					}
				}
		
				if(aspaces!=null)
				{
					for(int i=0; i<aspaces.length; i++)
					{
						aspaces[i].componentRemoved(cid);
					}
				}
				
				if(ctypes!=null)
				{
					try
					{
						String appctype = (String)ctypes.remove(cid);
						if(appctype!=null)
							instances.remove(appctype, cid);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
		});
	}
	
	/**
	 *  Add an component property. 
	 *  @param component The component.
	 *  @param key The key.
	 *  @param prop The property.
	 * /
	public synchronized void addProperty(IComponentIdentifier component, String key, Object prop)
	{
		if(!containsComponent(component))
			throw new RuntimeException("Component not contained in context: "+component+" "+this);
			
		Map componentprops = (Map)properties.get(component);
		if(componentprops==null)
		{
			componentprops = new HashMap();
			properties.put(component, componentprops);
		}
		
		componentprops.put(key, prop);
	}
	
	/**
	 *  Get component property. 
	 *  @param component The component.
	 *  @param key The key.
	 *  @return The property. 
	 * /
	public synchronized Object getProperty(IComponentIdentifier component, String key)
	{
		Object ret = null;
		
		if(!containsComponent(component))
			throw new RuntimeException("Component not contained in context: "+component+" "+this);
			
		Map componentprops = (Map)properties.get(component);
		if(componentprops!=null)
			ret = componentprops.get(key);
		
		return ret;
	}*/
	
	//-------- methods --------

	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter getComponentAdapter() 
	{
		return adapter;
	}

	/**
	 *  Get the name.
	 */
	// todo: remove.
	public String	getName()
	{
		return adapter.getComponentIdentifier().getLocalName();		
	}
	
	/**
	 *  Get a string representation of the context.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(SReflect.getInnerClassName(getClass()));
		ret.append("(name=");
		ret.append(adapter.getComponentIdentifier().getLocalName());
//		ret.append(", parent=");
//		ret.append(getParentContext());
//		IComponentIdentifier[]	aids	= getComponents(); 
//		if(aids!=null)
//		{
//			ret.append(", components=");
//			ret.append(SUtil.arrayToString(aids));
//		}
//		IContext[]	subcs	= getSubContexts(); 
//		if(subcs!=null)
//		{
//			ret.append(", subcontexts=");
//			ret.append(SUtil.arrayToString(subcs));
//		}
		ret.append(")");
		return ret.toString();
	}
		
	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public IModelInfo getModel()
	{
		return model.getModelInfo();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
	/**
	 *  Get the application type.
	 */
	public MApplicationType	getApplicationType()
	{
		return model;
	}
	
	/**
	 *  Create an component in the context.
	 *  @param name	The name of the newly created component.
	 *  @param type	The component type as defined in the application type.
	 *  @param configuration	The component configuration.
	 *  @param arguments	Arguments for the new component.
	 *  @param start	Should the new component be started?
	 *  
	 *  @param istener	A listener to be notified, when the component is created (if any).
	 *  @param creator	The component that wants to create a new component (if any).	
	 * /
	public void createComponent(String name, final String type, String configuration,
			Map arguments, final boolean start, final boolean master, 
			final IResultListener listener)
	{
		MComponentType	at	= model.getApplicationType().getMComponentType(type);
		if(at==null)
			throw new RuntimeException("Unknown component type '"+type+"' in application: "+model);
//		final IAMS	ams	= (IAMS) platform.getService(IAMS.class);
		IComponentManagementService ces = (IComponentManagementService)container.getService(IComponentManagementService.class);

		
		ces.createComponent(name, at.getFilename(), configuration, arguments, true, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				if(listener!=null)
					listener.exceptionOccurred(exception);
			}
			public void resultAvailable(Object result)
			{
				IComponentIdentifier aid = (IComponentIdentifier)result;
				synchronized(Application.this)
				{
					if(componenttypes==null)
						componenttypes	= new HashMap();
					componenttypes.put(aid, type);
				}
				
				if(!containsComponent(aid))
					addComponent(aid);	// Hack??? componentCreated() may be called from AMS.
				
				if(master)
				{
					addProperty(aid, PROPERTY_COMPONENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
				}
				
				if(start)
				{
					IComponentManagementService ces = (IComponentManagementService)container.getService(IComponentManagementService.class);
					ces.resumeComponent(aid, listener);
				}
				else
				{
					if(listener!=null)
						listener.resultAvailable(result);
				}
			}
		}, creator);
	}*/
	
	/**
	 *  Remove an component from a context.
	 * /
	// Cannot be synchronized due to deadlock with space (uses context.getComponentType()).
	public void	removeComponent(IComponentIdentifier component)
	{
		boolean master = isComponentMaster(component);
			
		super.removeComponent(component);
		
		if(master)
			((IContextService)container.getService(IContextService.class)).deleteContext(this, null);
	}*/

	/**
	 *  Get the flag indicating if the context is about to be deleted
	 *  (no more components can be added).
	 */
	public boolean	isTerminating()
	{
		return this.terminating;
	}

	/**
	 *  Set the flag indicating if the context is about to be deleted
	 *  (no more components can be added).
	 */
	protected void setTerminating(boolean terminating)
	{
		if(!terminating || this.terminating)
			throw new RuntimeException("Cannot terminate; illegal state: "+this.terminating+", "+terminating);
			
		this.terminating	= terminating;
	}

	/**
	 *  Set an component as master (causes context to be terminated on its deletion).
	 *  @param component The component.
	 *  @param master The master.
	 * /
	public void setComponentMaster(IComponentIdentifier component, boolean master)
	{
		addProperty(component, PROPERTY_COMPONENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set an component as master (causes context to be terminated on its deletion).
	 *  @param component The component.
	 *  @return True, if component is master.
	 * /
	public boolean isComponentMaster(IComponentIdentifier component)
	{
		Boolean ret = (Boolean)getProperty(component, PROPERTY_COMPONENT_MASTER);
		return ret==null? false: ret.booleanValue();
	}*/
	
	/**
	 *  Get the component type for an component id.
	 *  @param aid	The component id.
	 *  @return The component type name.
	 * /
	public synchronized String	getComponentType(IComponentIdentifier aid)
	{
		return componenttypes!=null ? (String)componenttypes.get(aid) : null;
	}*/
	
	/**
	 *  Get the component types.
	 *  @return The component types.
	 * /
	public String[] getComponentTypes()
	{
		List atypes = model.getApplicationType().getMComponentTypes();
		String[] ret = atypes!=null? new String[atypes.size()]: SUtil.EMPTY_STRING_ARRAY;
		
		for(int i=0; i<ret.length; i++)
		{
			MComponentType at = (MComponentType)atypes.get(i);
			ret[i] = at.getName();
		}
		
		return ret;
	}
	
	/**
	 *  Get the component type for an component filename.
	 *  @param aid	The component filename.
	 *  @return The component type name.
	 * /
	public String getComponentType(String filename)
	{
		String ret = null;
		filename = filename.replace('\\', '/');
		
		List componenttypes = model.getApplicationType().getMComponentTypes();
		for(Iterator it=componenttypes.iterator(); it.hasNext(); )
		{
			MComponentType componenttype = (MComponentType)it.next();
			if(filename.endsWith(componenttype.getFilename()))
				ret = componenttype.getName();
		}
		
		return ret;
	}*/
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return model.getAllImports();
	}
	
	//-------- methods to be called by adapter --------
	
	/**
	 *  Can be called on the component thread only.
	 * 
	 *  Main method to perform component execution.
	 *  Whenever this method is called, the component performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for components
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep()
	{
		try
		{
			Object[] step	= null;
			synchronized(steps)
			{
				if(!steps.isEmpty())
				{
					step = (Object[])steps.remove(0);
				}
			}

			if(step!=null)
			{
				Future future = (Future)step[1];
				try
				{
					Object res = ((IComponentStep)step[0]).execute(this);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(new DelegationResultListener(future));
					}
					else
					{
						future.setResult(res);
					}
				}
				catch(RuntimeException e)
				{
//					e.printStackTrace();
					future.setException(e);
					throw e;
				}
			}
			
			boolean ret;
			synchronized(steps)
			{
				ret = !steps.isEmpty();
				willdostep	= ret;
			}
			return ret;
		}
		catch(ComponentTerminatedException ate)
		{
			// Todo: fix kernel bug.
			ate.printStackTrace();
			return false; 
		}
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the component that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *   
	 *  Request component to kill itself.
	 *  The component might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param listener	When cleanup of the component is finished, the listener must be notified.
	 */
	public IFuture cleanupComponent()
	{
		if(componentlisteners!=null)
		{
			for(int i=0; i<componentlisteners.size(); i++)
			{
				IComponentListener lis = (IComponentListener)componentlisteners.get(i);
				lis.componentTerminating(new ChangeEvent(getComponentIdentifier()));
			}
		}
		
		// todo: call some application functionality for terminating?!
		deleteContext();
		
		if(componentlisteners!=null)
		{
			for(int i=0; i<componentlisteners.size(); i++)
			{
				IComponentListener lis = (IComponentListener)componentlisteners.get(i);
				lis.componentTerminated(new ChangeEvent(getComponentIdentifier()));
			}
		}
		
		return new Future(null);
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				((IComponentManagementService)result).destroyComponent(adapter.getComponentIdentifier())
					.addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this component.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public IExternalAccess getExternalAccess()
	{
		return new ExternalAccess(this);
	}

	/**
	 *  Get the class loader of the component.
	 *  The component class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while components are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The component class loader. 
	 */
	public ClassLoader getClassLoader()
	{
		return model.getModelInfo().getClassLoader();
	}

	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		// Todo: application breakpoints!?
		return false;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{	
		results.put(name, value);
	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map getResults()
	{
		return results;
	}

	/**
	 *  Get the logical component type for a given component id.
	 */
	public String getComponentType(IComponentIdentifier cid)
	{
		return (String)ctypes.get(cid);
	}

	/**
	 *  Get the file name for a logical type name of a subcomponent of this application.
	 */
	public String	getComponentFilename(String type)
	{
		return model.getMComponentType(type).getFilename();
	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}	
	
	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, adapter);
	}

	/**
	 *  Create subcomponents.
	 *  NOTE: parent cannot declare itself initing while subcomponents are created
	 *  because they need the external access of the parent, which is available only
	 *  after init is finished (otherwise there is a cyclic init dependency between parent and subcomps). 
	 */
	protected void createComponent(final List components, final IComponentManagementService ces, final int i, final Future inited)
	{
		if(i<components.size())
		{
			final MComponentInstance component = (MComponentInstance)components.get(i);
//			System.out.println("Create: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration()+" "+Thread.currentThread());
			int num = getNumber(component);
			IResultListener	crl	= new CollectionResultListener(num, false, new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("Create finished: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration()+" "+Thread.currentThread());
//					if(getParent()==null)
//					{
//						addStep(new Runnable()
//						{
//							public void run()
//							{
//								createComponent(components, cl, ces, i+1, inited);
//							}
//						});
//					}
//					else
//					{
						scheduleStep(new IComponentStep()
						{
							public static final String XML_CLASSNAME = "createChild"; 
							public Object execute(IInternalAccess ia)
							{
								createComponent(components, ces, i+1, inited);
								return null;
							}
						});
//					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					inited.setException(exception);
				}
			});
			for(int j=0; j<num; j++)
			{
				MComponentType	type	= component.getType(model);
				if(type!=null)
				{
					Boolean	suspend	= component.getSuspend()!=null ? component.getSuspend() : type.getSuspend();
					Boolean	master	= component.getMaster()!=null ? component.getMaster() : type.getMaster();
					Boolean	daemon	= component.getDaemon()!=null ? component.getDaemon() : type.getDaemon();
					Boolean	autoshutdown	= component.getAutoShutdown()!=null ? component.getAutoShutdown() : type.getAutoShutdown();
					IFuture ret = ces.createComponent(component.getName(), component.getType(model).getFilename(),
						new CreationInfo(component.getConfiguration(), getArguments(component), adapter.getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, model.getAllImports()), null);
					ret.addResultListener(crl);
				}
				else
				{
					crl.exceptionOccurred(new RuntimeException("No such component type: "+component.getTypeName()));
				}
			}
		}
		else
		{
			// Init is now finished. Notify cms.
//			System.out.println("Application init finished: "+ApplicationInterpreter.this);

			// master, daemon, autoshutdown
//			Boolean[] bools = new Boolean[3];
//			bools[2] = model.getAutoShutdown();
			
			inited.setResult(new Object[]{ApplicationInterpreter.this, adapter});
		}
	}
	
	/**
	 *  Get the file name of a component type.
	 *  @param ctype The component type.
	 *  @return The file name of this component type.
	 */
	public String getFileName(String ctype)
	{
		String ret = null;
		List componenttypes = model.getMComponentTypes();
		for(int i=0; ret==null && i<componenttypes.size(); i++)
		{
			MComponentType at = (MComponentType)componenttypes.get(i);
			if(at.getName().equals(ctype))
				ret = at.getFilename();
		}
		return ret;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 * /
	public Map getArguments(String appname, ClassLoader classloader)
	{
		Map ret = null;	
		
		IArgument[] args = getModel().getArguments();
		if(args!=null)
		{
			ret = new HashMap();

			JavaCCExpressionParser	parser = new JavaCCExpressionParser();
			String[] imports = getApplicationType().getAllImports();
			for(int i=0; i<args.length; i++)
			{
				IArgument arg = (IArgument)args[i];
				String valtext = (String)arg.getDefaultValue(appname);
				
				Object val = parser.parseExpression(valtext, imports, null, classloader).getValue(fetcher);
				ret.put(arg.getName(), val);
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	public Map getArguments(MComponentInstance component)
	{
		Map ret = null;		
		List	arguments	= component.getArguments();

		if(arguments!=null && !arguments.isEmpty())
		{
			ret = new HashMap();

			for(int i=0; i<arguments.size(); i++)
			{
				MExpressionType p = (MExpressionType)arguments.get(i);
				Object val = p.getParsedValue().getValue(fetcher);
				ret.put(p.getName(), val);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the number of components to start.
	 *  @return The number.
	 */
	public int getNumber(MComponentInstance component)
	{
		Object val = component.getNumber()!=null? component.getNumber().getValue(fetcher): null;
		
		return val instanceof Integer? ((Integer)val).intValue(): 1;
	}

	
	/**
	 *  Get the service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return container;
	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		return container;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public Collection getChildren(final String type)
	{
		return (Collection)instances.get(type);
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return adapter.getChildrenAccesses();
	}
	
	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public void addComponentListener(IComponentListener listener)
	{
		if(componentlisteners==null)
			componentlisteners = new ArrayList();
		componentlisteners.add(listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public void removeComponentListener(IComponentListener listener)
	{
		if(componentlisteners!=null)
			componentlisteners.remove(listener);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name)
	{
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(String name)
	{
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(String name, boolean rebind)
	{
		RequiredServiceInfo info = getModel().getRequiredService(name);
		if(info==null)
		{
			Future ret = new Future();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			return getServiceContainer().getRequiredService(info, rebind);
		}
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		RequiredServiceInfo info = getModel().getRequiredService(name);
		if(info==null)
		{
			IntermediateFuture ret = new IntermediateFuture();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			return getServiceContainer().getRequiredServices(info, rebind);
		}
	}
	
}
