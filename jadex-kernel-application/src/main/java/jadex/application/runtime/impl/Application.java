package jadex.application.runtime.impl;

import jadex.application.model.ApplicationModel;
import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MComponentInstance;
import jadex.application.model.MComponentType;
import jadex.application.model.MExpressionType;
import jadex.application.model.MSpaceInstance;
import jadex.application.runtime.IApplication;
import jadex.application.runtime.ISpace;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.SimpleValueFetcher;
import jadex.service.BasicServiceContainer;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.library.ILibraryService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The application context provides a closed environment for components.
 *  If components spawn other components, these will automatically be added to
 *  the context.
 *  When the context is deleted all components will be destroyed.
 *  An component must only be in one application context.
 */
public class Application implements IApplication, IComponentInstance
{
	//-------- attributes --------
	
	/** The application configuration. */
	protected MApplicationInstance	config;
	
	/** The contained spaces. */
	protected Map spaces;
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The application type. */
	protected ApplicationModel model;
	
	/** The parent component. */
	protected IExternalAccess parent;
	
	/** Flag to indicate that the application is already inited. */
	protected boolean initstarted;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more components can be added). */
	protected boolean	terminating;
	
	/** Component type mapping (cid -> logical type name). */
	protected Map ctypes;
	
	/** The arguments. */
	protected Map arguments;
	
	/** The arguments. */
	protected Map results;
	
	/** The children cnt (without daemons). */
	protected int children;
	
	/** The own service container. */
	protected BasicServiceContainer mycontainer;

	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public Application(ApplicationModel model, MApplicationInstance config, IComponentAdapter adapter, IExternalAccess parent, Map arguments)
	{
		this.config	= config;
		this.adapter = adapter;
		this.model = model;
		this.parent = parent;
		this.arguments = arguments==null ? new HashMap() : arguments;
		this.results = new HashMap();
		this.mycontainer = new BasicServiceContainer();
		
		// Init the arguments with default values.
		String configname = config!=null? config.getName(): null;
		IArgument[] args = model.getArguments();
		for(int i=0; i<args.length; i++)
		{
			if(args[i].getDefaultValue(configname)!=null)
			{
				if(this.arguments.get(args[i].getName())==null)
				{
					this.arguments.put(args[i].getName(), args[i].getDefaultValue(configname));
				}
			}
		}
		
		// Init the results with default values.
		IArgument[] res = model.getResults();
		for(int i=0; i<res.length; i++)
		{
			if(res[i].getDefaultValue(configname)!=null)
			{
				this.results.put(res[i].getName(), res[i].getDefaultValue(configname));
			}
		}
	}

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
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(final IResultListener listener)
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
			for(Iterator it=spaces.values().iterator(); it.hasNext(); )
			{
				ISpace space = (ISpace)it.next();
				space.terminate();
			}
			
			if(listener!=null)
				listener.resultAvailable(this, this);
		}
	}

	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public void	componentCreated(final IComponentDescription desc, final ILoadableComponentModel model)
	{
		// Checks if loaded model is defined in the application component types
		
		adapter.getServiceContainer().getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				
				IComponentIdentifier comp = desc.getName();
				List atypes	= Application.this.model.getApplicationType().getMComponentTypes();
				boolean	found	= false;
				String	type	= null;
				
				for(int i=0; !found	&& i<atypes.size(); i++)
				{
					MComponentType	atype	= (MComponentType)atypes.get(i);
					
					// Hack!!! simplify lookup!?
					IComponentFactory factory = null;
					if(facts!=null)
					{
						for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
						{
							IComponentFactory	cf	= (IComponentFactory)it.next();
							if(cf.isLoadable(atype.getFilename(), Application.this.model.getApplicationType().getAllImports(), model.getClassLoader()))
							{
								factory	= cf;
							}
						}
					}
					ILoadableComponentModel amodel = factory.loadModel(atype.getFilename(), Application.this.model.getApplicationType().getAllImports(), model.getClassLoader());
					
					if(SUtil.equals(amodel.getPackage(), model.getPackage()) && amodel.getName().equals(model.getName()))
//					if(amodel.getFilename().equals(model.getFilename()))
					{
						synchronized(this)
						{
							if(ctypes==null)
								ctypes	= new HashMap();
							ctypes.put(comp, atype.getName());
						}
						type	= atype.getName();
						found	= true;
					}
				}
				if(!found)
				{
					throw new RuntimeException("Unsupported component for application: "+model.getFilename());
				}
				
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
						aspaces[i].componentAdded(comp, type);
					}
				}
				
				if(!desc.isDaemon())
					children++;
			}
		});
	
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public void	componentDestroyed(IComponentDescription desc)
	{
		IComponentIdentifier comp = desc.getName();
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
				aspaces[i].componentRemoved(comp);
			}
		}
		
		synchronized(this)
		{
			if(ctypes!=null)
			{
				ctypes.remove(comp);
			}
		}
		
		if(desc.isMaster())
			killApplication();
		
		if(!desc.isDaemon())
			children--;
		
		if(children==0 && model.getApplicationType().isAutoShutdown())
			killApplication();
	}
	
	/**
	 *  Kill the application.
	 */
	public void killApplication()
	{
		((IComponentManagementService)getComponentAdapter().getServiceContainer()
			.getService(IComponentManagementService.class))
			.destroyComponent(getComponentAdapter().getComponentIdentifier());
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
	public ILoadableComponentModel getModel()
	{
		return model;
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
		return model.getApplicationType();
	}
	
	/**
	 *  Get the service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		return adapter.getServiceContainer();
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
		return model.getApplicationType().getAllImports();
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
		if(!initstarted)
		{
			// todo: set inited = true after all has been done
			initstarted = true;
			
//			adapter.getServiceContainer().getService(IClockService.class).addResultListener(createResultListener(new DefaultResultListener()
//			{
//				public void resultAvailable(Object source, Object result)
//				{
//					IClockService clock = (IClockService)result;
					
					final SimpleValueFetcher fetcher = new SimpleValueFetcher();
					fetcher.setValue("$platform", getServiceContainer());
					fetcher.setValue("$args", getArguments());
					fetcher.setValue("$results", getResults());
					// todo: hack remove clock somehow (problem services are behind future in xml)
//					fetcher.setValue("$clock", clock);

					// Init service container and init service.
					// Create the services.
					List services = model.getApplicationType().getServices();
					if(services!=null)
					{
						for(int i=0; i<services.size(); i++)
						{
							MExpressionType exp = (MExpressionType)services.get(i);
							IService service = (IService)exp.getParsedValue().getValue(fetcher);
							mycontainer.addService(exp.getClazz(), exp.getName(), service);
						}
					}
					mycontainer.start().addResultListener(new ComponentResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							// Create spaces for context.
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
										space.initSpace(Application.this, si, fetcher);
									}
									catch(Exception e)
									{
										System.out.println("Exception while creating space: "+si.getName());
										e.printStackTrace();
									}
								}
							}

							final List components = config.getMComponentInstances();
							adapter.getServiceContainer().getService(ILibraryService.class).addResultListener(createResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									final ILibraryService ls = (ILibraryService)result;
									final ClassLoader cl = ls.getClassLoader();
									adapter.getServiceContainer().getService(IComponentManagementService.class).addResultListener(createResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											final IComponentManagementService	ces	= (IComponentManagementService)result;
											
											for(int i=0; i<components.size(); i++)
											{
												final MComponentInstance component = (MComponentInstance)components.get(i);
												
									//			System.out.println("Create: "+component.getName()+" "+component.getTypeName()+" "+component.getConfiguration());
												int num = component.getNumber(Application.this, cl, fetcher);
												for(int j=0; j<num; j++)
												{
						//							IComponentManagementService	ces	= (IComponentManagementService)adapter.getServiceContainer().getService(IComponentManagementService.class);
													ces.createComponent(component.getName(), component.getType(model.getApplicationType()).getFilename(),
														new CreationInfo(component.getConfiguration(), component.getArguments(Application.this, cl, fetcher), adapter.getComponentIdentifier(),
															component.isSuspended(), component.isMaster(), component.isDaemon(), model.getApplicationType().getAllImports()), null);					
									//				context.createComponent(component.getName(), component.getTypeName(),
									//					component.getConfiguration(), component.getArguments(container, apptype, cl), component.isStart(), component.isMaster(),
									//					DefaultResultListener.getInstance(), null);	
												}
											}
										}
									}));
								}
							}));
						}
					}, adapter));
//				}
//			}));
		}
		// todo: is this necessary? can we ensure that this is not called?
//		else
//		{
//			throw new UnsupportedOperationException();
//		}
					
		return false;
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
	public IFuture killComponent()
	{
		return new Future(null);
		// Todo: application cleanup?
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this component.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public IFuture getExternalAccess()
	{
		return new Future(new ExternalAccess(this));
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
		return model.getClassLoader();
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
		return model.getApplicationType().getMComponentType(type).getFilename();
	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}
	
	/**
	 *  Create a result listener which is executed as an agent step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, adapter);
	}
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceContainer internalGetServiceContainer()
	{
		return mycontainer;
	}
}
