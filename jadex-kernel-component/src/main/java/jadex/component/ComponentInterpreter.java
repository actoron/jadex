package jadex.component;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SimpleValueFetcher;
import jadex.kernelbase.AbstractInterpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The application interpreter provides a closed environment for components.
 *  If components spawn other components, these will automatically be added to
 *  the context.
 *  When the context is deleted all components will be destroyed.
 *  An component must only be in one application context.
 */
public class ComponentInterpreter extends AbstractInterpreter implements IInternalAccess
{
	//-------- attributes --------
	
	/** The application configuration. */
	protected String config;
	
	/** The properties. */
	protected Map properties;
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The application type. */
	protected IModelInfo model;
	
	/** The parent component. */
	protected IExternalAccess parent;
	
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
	public ComponentInterpreter(final IComponentDescription desc, final IModelInfo model, final String config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, 
		final RequiredServiceBinding[] bindings, final Future inited)
	{
		this.config = config!=null? config: model.getConfigurationNames().length>0? 
			model.getConfigurationNames()[0]: null;
		this.model = model;
		this.parent = parent;
		this.arguments = arguments;
		this.properties = new HashMap();
		this.ctypes = new HashMap(); 
		this.instances = new MultiCollection(); 
		this.steps	= new ArrayList();
		this.willdostep	= true;
		this.bindings = bindings;
	
		try
		{
			this.adapter = factory.createComponentAdapter(desc, getModel(), this, parent);
			addStep((new Object[]{new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					init(getModel(), ComponentInterpreter.this.config, getModel().getProperties(), ComponentInterpreter.this.properties)
						.addResultListener(createResultListener(new DelegationResultListener(inited)
					{
						public void customResultAvailable(Object result)
						{
							inited.setResult(new Object[]{ComponentInterpreter.this, adapter});
						}
					}));
					
					return null;
				}
			}, new Future()}));
		}
		catch(Exception e)
		{
			inited.setException(e);
		}
	}

//	/**
//	 *  Schedule a step of the component.
//	 *  May safely be called from external threads.
//	 *  @param step	Code to be executed as a step of the component.
//	 */
//	public IFuture scheduleStep(final IComponentStep step)
//	{
//		Future ret = new Future();
//		
//		boolean dowakeup;
//		synchronized(steps)
//		{
//			steps.add(new Object[]{step, ret});
//			dowakeup	= !willdostep;	// only wake up if not already scheduled.
//		}
////		notifyListeners(new ChangeEvent(this, "addStep", step));
//		
//		if(dowakeup)
//		{
//			adapter.wakeup();
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public IFuture scheduleStep(final IComponentStep step)
	{
		final Future ret = new Future();
//		System.out.println("ss: "+getAgentAdapter().getComponentIdentifier()+" "+Thread.currentThread()+" "+step);
		try
		{
			if(adapter.isExternalThread())
			{
				adapter.invokeLater(new Runnable()
				{			
					public void run()
					{
						addStep(new Object[]{step, ret});
					}
					
					public String toString()
					{
						return "invokeLater("+step+")";
					}
				});
			}
			else
			{
				addStep(new Object[]{step, ret});
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}

	/**
	 *  Add a new step.
	 */
	protected void addStep(Object[] step)
	{
//		if(nosteps)
//		{
//			((Future)step[1]).setException(new ComponentTerminatedException(getComponentAdapter().getComponentIdentifier()));
//		}
//		else
//		{
			steps.add(step);
//			notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_STEP, step[0].getClass().getName(), 
//				step[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)step[0])));
//		}
	}
	
//	/**
//	 *  Add a new step.
//	 */
//	protected Object[] removeStep()
//	{
//		Object[] ret = (Object[])steps.remove(0);
//		notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_STEP, 
//			ret[0].getClass().getName(), ret[0].toString(), microagent.getComponentIdentifier(), getStepDetails((IComponentStep)ret[0])));
////		notifyListeners(new ChangeEvent(this, "removeStep", new Integer(0)));
//		return ret;
//	}
	
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
		ComponentChangeEvent.dispatchTerminatingEvent(adapter, getModel(), getServiceProvider(), componentlisteners, null);
		
		// todo: call some application functionality for terminating?!
		
		final Future ret = new Future();
		
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				ComponentChangeEvent.dispatchTerminatedEvent(adapter, getModel(), getServiceProvider(), componentlisteners, ret);
			}
		});
		
		return ret;
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
	
	
//	/**
//	 *  Kill the component.
//	 */
//	public IFuture killComponent()
//	{
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				((IComponentManagementService)result).destroyComponent(adapter.getComponentIdentifier())
//					.addResultListener(new DelegationResultListener(ret));
//			}
//		});
//		
//		return ret;
//	}
	
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


//	/**
//	 *  Get the class loader of the component.
//	 *  The component class loader is required to avoid incompatible class issues,
//	 *  when changing the platform class loader while components are running. 
//	 *  This may occur e.g. when decoding messages and instantiating parameter values.
//	 *  @return	The component class loader. 
//	 */
//	public ClassLoader getClassLoader()
//	{
//		return model.getModelInfo().getClassLoader();
//	}

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
		if(results==null)
				results	= new HashMap();
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

//	/**
//	 *  Get the logical component type for a given component id.
//	 */
//	public String getComponentType(IComponentIdentifier cid)
//	{
//		return (String)ctypes.get(cid);
//	}
//
//	/**
//	 *  Get the file name for a logical type name of a subcomponent of this application.
//	 */
//	public String	getComponentFilename(String type)
//	{
//		return model.getMSubcomponentType(type).getFilename();
//	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}	
	
//	/**
//	 *  Create a result listener which is executed as an component step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public IResultListener createResultListener(IResultListener listener)
//	{
//		return new ComponentResultListener(listener, adapter);
//	}
//
//	/**
//	 *  Create a result listener which is executed as an component step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
//	{
//		return new IntermediateComponentResultListener(listener, adapter);
//	}

//	/**
//	 *  Create subcomponents.
//	 *  NOTE: parent cannot declare itself initing while subcomponents are created
//	 *  because they need the external access of the parent, which is available only
//	 *  after init is finished (otherwise there is a cyclic init dependency between parent and subcomps). 
//	 */
//	protected void createComponent(final List components, final IComponentManagementService cms, final int i, final Future inited)
//	{
//		if(i<components.size())
//		{
//			final MComponentInstance component = (MComponentInstance)components.get(i);
////			System.out.println("Create: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration()+" "+Thread.currentThread());
//			int num = getNumber(component);
//			final IResultListener crl = new CollectionResultListener(num, false, new IResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
////					System.out.println("Create finished: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration()+" "+Thread.currentThread());
////					if(getParent()==null)
////					{
////						addStep(new Runnable()
////						{
////							public void run()
////							{
////								createComponent(components, cl, ces, i+1, inited);
////							}
////						});
////					}
////					else
////					{
//						scheduleStep(new IComponentStep()
//						{
//							@XMLClassname("createChild")
//							public Object execute(IInternalAccess ia)
//							{
//								createComponent(components, cms, i+1, inited);
//								return null;
//							}
//						});
////					}
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					inited.setException(exception);
//				}
//			});
//			for(int j=0; j<num; j++)
//			{
//				MSubcomponentType	type	= component.getType(model);
//				if(type!=null)
//				{
//					final Boolean suspend	= component.getSuspend()!=null ? component.getSuspend() : type.getSuspend();
//					Boolean	master = component.getMaster()!=null ? component.getMaster() : type.getMaster();
//					Boolean	daemon = component.getDaemon()!=null ? component.getDaemon() : type.getDaemon();
//					Boolean	autoshutdown = component.getAutoShutdown()!=null ? component.getAutoShutdown() : type.getAutoShutdown();
//					List bindings = component.getRequiredServiceBindings();
//					IFuture ret = cms.createComponent(component.getName(), component.getType(model).getFilename(),
//						new CreationInfo(component.getConfiguration(), getArguments(component), adapter.getComponentIdentifier(),
//						suspend, master, daemon, autoshutdown, model.getAllImports(), 
//						bindings!=null? (RequiredServiceBinding[])bindings.toArray(new RequiredServiceBinding[bindings.size()]): null), null);
//					ret.addResultListener(crl);
//				}
//				else
//				{
//					crl.exceptionOccurred(new RuntimeException("No such component type: "+component.getTypeName()));
//				}
//			}
//		}
//		else
//		{
//			// Init is now finished. Notify cms.
////			System.out.println("Application init finished: "+ApplicationInterpreter.this);
//
//			// master, daemon, autoshutdown
////			Boolean[] bools = new Boolean[3];
////			bools[2] = model.getAutoShutdown();
//			
////			for(int j=0; j<tostart.size(); j++)
////			{
////				IComponentIdentifier cid = (IComponentIdentifier)tostart.get(j);
////				cms.resumeComponent(cid);
////			}
//			
//			inited.setResult(new Object[]{ComponentInterpreter.this, adapter});
//		}
//	}
	
//	/**
//	 *  Get the file name of a component type.
//	 *  @param ctype The component type.
//	 *  @return The file name of this component type.
//	 */
//	public String getFileName(String ctype)
//	{
//		String ret = null;
//		List componenttypes = model.getMComponentTypes();
//		for(int i=0; ret==null && i<componenttypes.size(); i++)
//		{
//			MSubcomponentType at = (MSubcomponentType)componenttypes.get(i);
//			if(at.getName().equals(ctype))
//				ret = at.getFilename();
//		}
//		return ret;
//	}

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
	
//	/**
//	 *  Get the arguments.
//	 *  @return The arguments as a map of name-value pairs.
//	 */
//	public Map getArguments(MComponentInstance component)
//	{
//		Map ret = null;		
//		List	arguments	= component.getArguments();
//
//		if(arguments!=null && !arguments.isEmpty())
//		{
//			ret = new HashMap();
//
//			for(int i=0; i<arguments.size(); i++)
//			{
//				MExpressionType p = (MExpressionType)arguments.get(i);
//				Object val = p.getParsedValue().getValue(fetcher);
//				ret.put(p.getName(), val);
//			}
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the number of components to start.
//	 *  @return The number.
//	 */
//	public int getNumber(MComponentInstance component)
//	{
//		Object val = component.getNumber()!=null? component.getNumber().getValue(fetcher): null;
//		
//		return val instanceof Integer? ((Integer)val).intValue(): 1;
//	}

	
//	/**
//	 *  Get the service provider.
//	 */
//	public IServiceProvider getServiceProvider()
//	{
//		return getServiceContainer();
//	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		if(container==null)
		{
			// Init service container.
//			MExpressionType mex = model.getContainer();
//			if(mex!=null)
//			{
//				container = (IServiceContainer)mex.getParsedValue().getValue(fetcher);
//			}
//			else
//			{
//				container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
				container = new ComponentServiceContainer(getComponentAdapter(), 
					ComponentComponentFactory.FILETYPE_COMPONENT, getModel().getRequiredServices(), bindings);
//			}			
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
		return addComponentListener(componentlisteners, listener);
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener)
	{
		return removeComponentListener(componentlisteners, listener);
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
	
	//-------- abstract interpreter methods --------
	
	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter getComponentAdapter()
	{
		return adapter;
	}

	/**
	 *  Get the model.
	 */
	public IModelInfo getModel()
	{
		return model;
	}
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
//		if(imports==null)
//		{
//			List imp = new ArrayList();
//			imp.add(microagent.getClass().getPackage().getName()+".*");
//			
//			// todo: http://stackoverflow.com/questions/3734825/find-out-which-classes-of-a-given-api-are-used
//			
//			imports = (String[])imp.toArray(new String[imp.size()]);
//		}
//		return imports;
		return model.getAllImports();
	}
	
	/**
	 *  Get the service bindings.
	 */
	public RequiredServiceBinding[] getServiceBindings()
	{
		return bindings;
	}
	
	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			final SimpleValueFetcher sfetcher = new SimpleValueFetcher();
			sfetcher.setValue("$args", getArguments());
			sfetcher.setValue("$properties", properties);
			sfetcher.setValue("$results", getResults());
			sfetcher.setValue("$component", this);
			sfetcher.setValue("$provider", getServiceContainer());
			fetcher = sfetcher;
		}
		return fetcher;
	}

	/**
	 *  Add a default value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public void	addDefaultArgument(String name, Object value)
	{
		if(arguments==null)
		{
			arguments	= new HashMap();
		}
		if(!arguments.containsKey(name))
		{
			arguments.put(name, value);
		}
	}

	/**
	 *  Add a default value for a result (if not already present).
	 *  Called once for each result during init.
	 *  @param name	The result name.
	 *  @param value	The result value.
	 */
	public void	addDefaultResult(String name, Object value)
	{
		if(results==null)
		{
			results	= new HashMap();
		}
		results.put(name, value);
	}
	
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return this;
	}
	
}
