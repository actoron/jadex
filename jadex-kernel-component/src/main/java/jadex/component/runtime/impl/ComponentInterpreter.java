package jadex.component.runtime.impl;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IModelInfo;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.SComponentEvent;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.component.ComponentComponentFactory;
import jadex.component.model.MComponentInstance;
import jadex.component.model.MComponentType;
import jadex.component.model.MConfiguration;
import jadex.component.model.MExpressionType;
import jadex.component.model.MProvidedServiceType;
import jadex.component.model.MSubcomponentType;
import jadex.component.runtime.IComponent;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class ComponentInterpreter implements IComponent, IComponentInstance, IInternalAccess
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
	protected MConfiguration	config;
	
	/** The contained spaces. */
	protected Map spaces;
	
	/** The properties. */
	protected Map properties;
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The application type. */
	protected MComponentType model;
	
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
	
	/** The external access (cached). */
	protected IExternalAccess	access;
	
	/** The required service binding information. */
	protected RequiredServiceBinding[] bindings;

	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ComponentInterpreter(final IComponentDescription desc, final MComponentType model, final MConfiguration config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, 
		final RequiredServiceBinding[] bindings, final Future inited)
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
		this.bindings = bindings;
	
		// Init the arguments with default values.
		String[] configs = model.getModelInfo().getConfigurations();
		String configname = config!=null? config.getName(): configs.length>0? configs[0]: null;
		IArgument[] args = model.getModelInfo().getArguments();
		for(int i=0; i<args.length; i++)
		{
			if(args[i].getDefaultValue(configname)!=null)
			{
				if(ComponentInterpreter.this.arguments.get(args[i].getName())==null)
				{
					ComponentInterpreter.this.arguments.put(args[i].getName(), args[i].getDefaultValue(configname));
				}
			}
		}
		
		// Init the results with default values.
		IArgument[] res = model.getModelInfo().getResults();
		for(int i=0; i<res.length; i++)
		{
			if(res[i].getDefaultValue(configname)!=null)
			{
				ComponentInterpreter.this.results.put(res[i].getName(), res[i].getDefaultValue(configname));
			}
		}
		
		final SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$args", getArguments());
		fetcher.setValue("$properties", properties);
		fetcher.setValue("$results", getResults());
		fetcher.setValue("$component", ComponentInterpreter.this);
		this.fetcher = fetcher;		
		this.adapter = factory.createComponentAdapter(desc, model.getModelInfo(), this, parent);
		fetcher.setValue("$provider", getServiceContainer());
		
		// Schedule the futures (first) init step.
		scheduleStep(new IComponentStep()
		{
			@XMLClassname("init")
			public Object execute(final IInternalAccess ia)
			{
				final List futures = new ArrayList();
		
				List services = model.getProvidedServices();
//				System.out.println("init sers: "+services);
				if(services!=null)
				{
					for(int i=0; i<services.size(); i++)
					{
						final MProvidedServiceType st = (MProvidedServiceType)services.get(i);
						
						IInternalService service;
						
						if(st.getValue()!=null || st.getImplementation()!=null)
						{
							try
							{
								Object ser = null;
								
								if(st.getImplementation()!=null)
								{
									ser = st.getImplementation().newInstance();
								}
								else if(st.getParsedValue()!=null)
								{
									ser = (IInternalService)st.getParsedValue().getValue(fetcher);
								}
								else
								{
									getLogger().warning("Could not parse: "+st.getValue());
								}
								
								if(ser!=null)
								{
									service = BasicServiceInvocationHandler.createProvidedServiceProxy(ia, getComponentAdapter(), ser, st.isDirect());
									getServiceContainer().addService(service);
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
								getLogger().warning("Service creation error: "+st.getParsedValue());
							}
						}
						else 
						{
							RequiredServiceInfo info = new RequiredServiceInfo("virtual", st.getClazz());
							IServiceIdentifier sid = BasicService.createServiceIdentifier(getExternalAccess().getServiceProvider().getId(), 
								info.getType(), BasicServiceInvocationHandler.class);
							service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(getExternalAccess(), getComponentAdapter(), sid, info, st.getBinding());
							getServiceContainer().addService(service);
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
								((IFuture)val).addResultListener(createResultListener(new DefaultResultListener()
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
								}));
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
						getServiceContainer().start().addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								// Create spaces for context.
//								System.out.println("comp services start finished: "+getComponentIdentifier());
//								List spaces = config.getMSpaceInstances();
//								if(spaces!=null)
//								{
//									for(int i=0; i<spaces.size(); i++)
//									{
//										MSpaceInstance si = (MSpaceInstance)spaces.get(i);
//										try
//										{
//											ISpace space = (ISpace)si.getClazz().newInstance();
//											addSpace(si.getName(), space);
//											space.initSpace(ComponentInterpreter.this, si, fetcher);
//										}
//										catch(Exception e)
//										{
//											System.out.println("Exception while creating space: "+si.getName());
//											e.printStackTrace();
//										}
//									}
//								}

								final List components = config.getMComponentInstances();
								SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
									.addResultListener(createResultListener(new DefaultResultListener()
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
						}));
						
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
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return adapter.getLogger();
	}
	
	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public IFuture	componentCreated(final IComponentDescription desc, final IModelInfo model)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public IFuture	componentDestroyed(final IComponentDescription desc)
	{
		return IFuture.DONE;
	}
	
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
	public MComponentType	getApplicationType()
	{
		return model;
	}
	
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
		SComponentEvent.dispatchTerminatingEvent(adapter, getModel(), getServiceProvider(), componentlisteners, null);
		
		// todo: call some application functionality for terminating?!
//		deleteContext();
		
		final Future ret = new Future();
		
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				SComponentEvent.dispatchTerminatedEvent(adapter, getModel(), getServiceProvider(), componentlisteners, ret);
			}
		});
		
		return ret;
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
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
		if(access==null)
		{
			synchronized(this)
			{
				if(access==null)
				{
					access	= new ExternalAccess(this);
				}
			}
		}
		
		return access;
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
		return model.getMSubcomponentType(type).getFilename();
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
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
	{
		return new IntermediateComponentResultListener(listener, adapter);
	}

	/**
	 *  Create subcomponents.
	 *  NOTE: parent cannot declare itself initing while subcomponents are created
	 *  because they need the external access of the parent, which is available only
	 *  after init is finished (otherwise there is a cyclic init dependency between parent and subcomps). 
	 */
	protected void createComponent(final List components, final IComponentManagementService cms, final int i, final Future inited)
	{
		if(i<components.size())
		{
			final MComponentInstance component = (MComponentInstance)components.get(i);
//			System.out.println("Create: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration()+" "+Thread.currentThread());
			int num = getNumber(component);
			final IResultListener crl = new CollectionResultListener(num, false, new IResultListener()
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
							@XMLClassname("createChild")
							public Object execute(IInternalAccess ia)
							{
								createComponent(components, cms, i+1, inited);
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
				MSubcomponentType	type	= component.getType(model);
				if(type!=null)
				{
					final Boolean suspend	= component.getSuspend()!=null ? component.getSuspend() : type.getSuspend();
					Boolean	master = component.getMaster()!=null ? component.getMaster() : type.getMaster();
					Boolean	daemon = component.getDaemon()!=null ? component.getDaemon() : type.getDaemon();
					Boolean	autoshutdown = component.getAutoShutdown()!=null ? component.getAutoShutdown() : type.getAutoShutdown();
					List bindings = component.getRequiredServiceBindings();
					IFuture ret = cms.createComponent(component.getName(), component.getType(model).getFilename(),
						new CreationInfo(component.getConfiguration(), getArguments(component), adapter.getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, model.getAllImports(), 
						bindings!=null? (RequiredServiceBinding[])bindings.toArray(new RequiredServiceBinding[bindings.size()]): null), null);
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
			
//			for(int j=0; j<tostart.size(); j++)
//			{
//				IComponentIdentifier cid = (IComponentIdentifier)tostart.get(j);
//				cms.resumeComponent(cid);
//			}
			
			inited.setResult(new Object[]{ComponentInterpreter.this, adapter});
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
			MSubcomponentType at = (MSubcomponentType)componenttypes.get(i);
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
		return getServiceContainer();
	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		if(container==null)
		{
			// Init service container.
			MExpressionType mex = model.getContainer();
			if(mex!=null)
			{
				container = (IServiceContainer)mex.getParsedValue().getValue(fetcher);
			}
			else
			{
//				container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
				container = new ComponentServiceContainer(getComponentAdapter(), 
					ComponentComponentFactory.FILETYPE_COMPONENT, getModel().getRequiredServices(), bindings);
			}			
		}
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
	public IFuture addComponentListener(IComponentListener listener)
	{
		if(componentlisteners==null)
			componentlisteners = new ArrayList();
		componentlisteners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener)
	{
		if(componentlisteners!=null)
			componentlisteners.remove(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public IFuture waitFor(final long delay, final IComponentStep step)
	{
		// todo: remember and cleanup timers in case of component removal.
		
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	

//	/**
//	 *  Get a required service of a given name.
//	 *  @param name The service name.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(String name)
//	{
//		return getRequiredService(name, false);
//	}
//	
//	/**
//	 *  Get a required services of a given name.
//	 *  @param name The services name.
//	 *  @return The service.
//	 */
//	public IIntermediateFuture getRequiredServices(String name)
//	{
//		return getRequiredServices(name, false);
//	}
//	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(String name, boolean rebind)
//	{
//		RequiredServiceInfo info = getModel().getRequiredService(name);
//		RequiredServiceBinding binding = getRequiredServiceBinding(name);
//		if(info==null)
//		{
//			Future ret = new Future();
//			ret.setException(new ServiceNotFoundException(name));
//			return ret;
//		}
//		else
//		{
//			return getServiceContainer().getRequiredService(info, binding, rebind);
//		}
//	}
//	
//	/**
//	 *  Get a required services.
//	 *  @return The services.
//	 */
//	public IIntermediateFuture getRequiredServices(String name, boolean rebind)
//	{
//		RequiredServiceInfo info = getModel().getRequiredService(name);
//		RequiredServiceBinding binding = getRequiredServiceBinding(name);
//		if(info==null)
//		{
//			IntermediateFuture ret = new IntermediateFuture();
//			ret.setException(new ServiceNotFoundException(name));
//			return ret;
//		}
//		else
//		{
//			return getServiceContainer().getRequiredServices(info, binding, rebind);
//		}
//	}
//	
//	/**
//	 *  Get the binding info of a service.
//	 *  @param name The required service name.
//	 *  @return The binding info of a service.
//	 */
//	protected RequiredServiceBinding getRequiredServiceBinding(String name)
//	{
//		return bindings!=null? (RequiredServiceBinding)bindings.get(name): null;
//	}
	
}
