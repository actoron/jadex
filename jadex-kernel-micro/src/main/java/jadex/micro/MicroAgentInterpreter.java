package jadex.micro;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.FieldInfo;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.kernelbase.AbstractInterpreter;
import jadex.micro.annotation.AgentService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  The micro agent interpreter is the connection between the agent platform 
 *  and a user-written micro agent. 
 */
public class MicroAgentInterpreter	implements IComponentInterpreter
{
	//-------- attributes --------
	
	/** The interpreter. */
	protected IInternalAccess	component;
	
	/** The micro agent. */
	protected PojoMicroAgent microagent;
	
	/** The list of message handlers. */
	protected List<IMessageHandler> messagehandlers;
	
	/** The micro model. */
	protected MicroModel micromodel;
	
	/** The value fetcher (cached for speed). */
	protected IValueFetcher	fetcher;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 *  @param adapter The adapter.
	 *  @param microagent The microagent.
	 */
	public MicroAgentInterpreter(final MicroModel model, Class<?> microclass, IInternalAccess component, MicroAgentPersistInfo persistinfo)
	{
		this.micromodel = model;
		this.component	= component;
		
		this.microagent = createAgent(microclass, model, persistinfo);
		
		// Todo:
//		injectArguments(microagent.getPojoAgent(), model);
//		injectServices(...); // after components.
//		injectParent(); // asynchronous
		
		// component fetcher for service init.
	}
	
	/**
	 *  Create the agent.
	 */
	protected PojoMicroAgent createAgent(Class<?> microclass, MicroModel model, MicroAgentPersistInfo mapi)
	{
		PojoMicroAgent ret;
		try
		{
			// Todo: reinject values in user agent?
			Object agent = mapi!=null ? mapi.getUserAgentObject() : microclass.newInstance();
			ret = new PojoMicroAgent(component, agent);
	
			// Todo: what to inject?
//			FieldInfo[] fields = model.getAgentInjections();
//			for(int i=0; i<fields.length; i++)
//			{
//				Field f = fields[i].getField(interpreter.getClassLoader());
//				f.setAccessible(true);
//				f.set(agent, ret);
//			}
		}
		catch(Throwable t)
		{
			if(t instanceof InvocationTargetException)
			{
				t	= ((InvocationTargetException)t).getTargetException();
			}
			if(t instanceof Error)
			{
				throw (Error)t;
			}
			else if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else
			{
				throw new RuntimeException(t);
			}
		}
		
		return ret;
	}
		
	/**
	 *  Get the component fetcher.
	 */
	protected IResultCommand<Object, Class<?>>	getComponentFetcher()
	{
		return new IResultCommand<Object, Class<?>>()
		{
			public Object execute(Class<?> type)
			{
				Object ret	= null;
				if(SReflect.isSupertype(type, microagent.getClass()))
				{
					ret	= microagent;
				}
				else if(microagent instanceof IPojoMicroAgent
					&& SReflect.isSupertype(type, ((IPojoMicroAgent)microagent).getPojoAgent().getClass()))
				{
					ret	= ((IPojoMicroAgent)microagent).getPojoAgent();
				}
				return ret;
			}
		};
	}
	
	/**
	 *  Inject the arguments to the annotated fields.
	 */
	protected void	injectArguments(final Object agent, final MicroModel model)
	{
		if(component.getArguments()!=null)
		{
			String[] names = model.getArgumentInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					Object val = component.getArguments().get(names[i]);
					
//					if(val!=null || getModel().getArgument(names[i]).getDefaultValue()!=null)
					final Tuple2<FieldInfo, String>[] infos = model.getArgumentInjections(names[i]);
					
					for(int j=0; j<infos.length; j++)
					{
						Field field = infos[j].getFirstEntity().getField(component.getClassLoader());
						String convert = infos[j].getSecondEntity();
//						System.out.println("seting arg: "+names[i]+" "+val);
						setFieldValue(val, field, convert);
					}
				}
			}
		}
		
		// Inject default result values
		if(component.getResults()!=null)
		{
			String[] names = model.getResultInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					if(component.getResults().containsKey(names[i]))
					{
						Object val = component.getResults().get(names[i]);
						final Tuple3<FieldInfo, String, String> info = model.getResultInjection(names[i]);
						
						Field field = info.getFirstEntity().getField(component.getClassLoader());
						String convert = info.getSecondEntity();
//						System.out.println("seting res: "+names[i]+" "+val);
						setFieldValue(val, field, convert);
					}
				}
			}
		}
	}
	
	/**
	 *  Set an injected field value.
	 */
	protected void setFieldValue(Object val, Field field, String convert)
	{
		if(val!=null || !SReflect.isBasicType(field.getType()))
		{
			try
			{
				Object agent = microagent instanceof IPojoMicroAgent? ((IPojoMicroAgent)microagent).getPojoAgent(): microagent;
				if(convert!=null)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher(getFetcher());
					fetcher.setValue("$value", val);
					val = SJavaParser.evaluateExpression(convert, component.getModel().getAllImports(), fetcher, component.getClassLoader());
				}
				field.setAccessible(true);
				field.set(agent, val);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 *  Inject the services to the annotated fields.
	 */
	protected IFuture<Void> injectServices(final Object agent, final MicroModel model)
	{
		Future<Void> ret = new Future<Void>();

		String[] sernames = model.getServiceInjectionNames();
		
		if(sernames.length>0)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(sernames.length, 
				new DelegationResultListener<Void>(ret));
	
			for(int i=0; i<sernames.length; i++)
			{
				final Object[] infos = model.getServiceInjections(sernames[i]);
				final CounterResultListener<Void> lis2 = new CounterResultListener<Void>(infos.length, lis);

				RequiredServiceInfo	info	= model.getModelInfo().getRequiredService(sernames[i]);				
				final IFuture<Object>	sfut;
				if(info!=null && info.isMultiple())
				{
					IFuture	ifut	= component.getServiceContainer().getRequiredServices(sernames[i]);
					sfut	= ifut;
				}
				else
				{
					sfut	= component.getServiceContainer().getRequiredService(sernames[i]);					
				}
				
				for(int j=0; j<infos.length; j++)
				{
					if(infos[j] instanceof FieldInfo)
					{
						final Field	f	= ((FieldInfo)infos[j]).getField(component.getClassLoader());
						if(SReflect.isSupertype(IFuture.class, f.getType()))
						{
							try
							{
								f.setAccessible(true);
								f.set(agent, sfut);
								lis2.resultAvailable(null);
							}
							catch(Exception e)
							{
								component.getLogger().warning("Field injection failed: "+e);
								lis2.exceptionOccurred(e);
							}	
						}
						else
						{
							sfut.addResultListener(new IResultListener<Object>()
							{
								public void resultAvailable(Object result)
								{
									try
									{
										f.setAccessible(true);
										f.set(agent, result);
										lis2.resultAvailable(null);
									}
									catch(Exception e)
									{
										component.getLogger().warning("Field injection failed: "+e);
										lis2.exceptionOccurred(e);
									}	
								}
								
								public void exceptionOccurred(Exception e)
								{
									if(!(e instanceof ServiceNotFoundException)
										|| f.getAnnotation(AgentService.class).required())
									{
										component.getLogger().warning("Field injection failed: "+e);
										lis2.exceptionOccurred(e);
									}
									else
									{
										if(SReflect.isSupertype(f.getType(), List.class))
										{
											// Call self with empty list as result.
											resultAvailable(Collections.EMPTY_LIST);
										}
										else
										{
											// Don't set any value.
											lis2.resultAvailable(null);
										}
									}
								}
							});
						}
					}
					else if(infos[j] instanceof MethodInfo)
					{
						final Method	m	= SReflect.getMethod(agent.getClass(), ((MethodInfo)infos[j]).getName(),
							((MethodInfo)infos[j]).getParameterTypes(component.getClassLoader()));
						if(info.isMultiple())
						{
							lis2.resultAvailable(null);
							IFuture	tfut	= sfut;
							final IIntermediateFuture<Object>	ifut	= (IIntermediateFuture<Object>)tfut;
							
							ifut.addResultListener(new IIntermediateResultListener<Object>()
							{
								public void intermediateResultAvailable(final Object result)
								{
									if(SReflect.isSupertype(m.getParameterTypes()[0], result.getClass()))
									{
										component.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												try
												{
													m.setAccessible(true);
													m.invoke(agent, new Object[]{result});
												}
												catch(Throwable t)
												{
													t	= t instanceof InvocationTargetException ? ((InvocationTargetException)t).getTargetException() : t;
													throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
												}
												return IFuture.DONE;
											}
										});
									}
								}
								
								public void resultAvailable(Collection<Object> result)
								{
									finished();
								}
								
								public void finished()
								{
									if(SReflect.isSupertype(m.getParameterTypes()[0], Collection.class))
									{
										component.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												try
												{
													m.setAccessible(true);
													m.invoke(agent, new Object[]{ifut.getIntermediateResults()});
												}
												catch(Throwable t)
												{
													t	= t instanceof InvocationTargetException ? ((InvocationTargetException)t).getTargetException() : t;
													throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
												}
												return IFuture.DONE;
											}
										});
									}
								}
								
								public void exceptionOccurred(Exception e)
								{
									if(!(e instanceof ServiceNotFoundException)
										|| m.getAnnotation(AgentService.class).required())
									{
										component.getLogger().warning("Method injection failed: "+e);
									}
									else
									{
										// Call self with empty list as result.
										finished();
									}
								}
							});

						}
						else
						{
							lis2.resultAvailable(null);
							sfut.addResultListener(new IResultListener<Object>()
							{
								public void resultAvailable(final Object result)
								{
									component.scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											try
											{
												m.setAccessible(true);
												m.invoke(agent, new Object[]{result});
												lis2.resultAvailable(null);
											}
											catch(Throwable t)
											{
												t	= t instanceof InvocationTargetException ? ((InvocationTargetException)t).getTargetException() : t;
												throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
											}
											return IFuture.DONE;
										}
									});
								}
								
								public void exceptionOccurred(Exception e)
								{
									if(!(e instanceof ServiceNotFoundException)
										|| m.getAnnotation(AgentService.class).required())
									{
										component.getLogger().warning("Method service injection failed: "+e);
									}
								}
							});
						}
					}
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Inject the parent to the annotated fields.
	 */
	protected IFuture<Void> injectParent(final Object agent, final MicroModel model)
	{
		Future<Void> ret = new Future<Void>();
		FieldInfo[]	pis	= model.getParentInjections();
		
		if(pis.length>0)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(pis.length, 
				new DelegationResultListener<Void>(ret));
	
			for(int i=0; i<pis.length; i++)
			{
				final Future<Void>	fut	= new Future<Void>();
				fut.addResultListener(lis);
				
				final Field	f	= pis[i].getField(component.getClassLoader());
				component.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						cms.getExternalAccess(component.getComponentIdentifier().getParent())
							.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(fut)
						{
							public void customResultAvailable(IExternalAccess exta)
							{
								if(IExternalAccess.class.equals(f.getType()))
								{
									try
									{
										f.setAccessible(true);
										f.set(agent, exta);
										fut.setResult(null);
									}
									catch(Exception e)
									{
										exceptionOccurred(e);
									}
								}
								else if(component.getComponentDescription().isSynchronous())
								{
									exta.scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											if(SReflect.isSupertype(f.getType(), ia.getClass()))
											{
												try
												{
													f.setAccessible(true);
													f.set(agent, ia);
												}
												catch(Exception e)
												{
													throw new RuntimeException(e);
												}
											}
											else if(ia instanceof IPojoMicroAgent)
											{
												Object	pagent	= ((IPojoMicroAgent)ia).getPojoAgent();
												if(SReflect.isSupertype(f.getType(), pagent.getClass()))
												{
													try
													{
														f.setAccessible(true);
														f.set(agent, pagent);
													}
													catch(Exception e)
													{
														exceptionOccurred(e);
													}
												}
												else
												{
													throw new RuntimeException("Incompatible types for parent injection: "+pagent+", "+f);													
												}
											}
											else
											{
												throw new RuntimeException("Incompatible types for parent injection: "+ia+", "+f);													
											}
											return IFuture.DONE;
										}
									}).addResultListener(new DelegationResultListener<Void>(fut));
								}
								else
								{
									exceptionOccurred(new RuntimeException("Non-external parent injection for non-synchronous subcomponent not allowed: "+f));
								}
							}
						});
					}
				});
			}
		}
		else
		{
			ret.setResult(null);
		}

		return	ret;
	}
	
	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
//		System.out.println("started: "+getComponentIdentifier());
		component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("body: "+getComponentAdapter().getComponentIdentifier());
				microagent.executeBody().addResultListener(component.createResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						// result?!
//						System.out.println("Killing (res): "+getComponentIdentifier().getName());
						component.killComponent();
					}
					public void exceptionOccurred(Exception exception)
					{
						// Throw exception to cause fatal error message
						if(!(exception instanceof ComponentTerminatedException)
							|| !component.getComponentIdentifier().equals(((ComponentTerminatedException)exception).getComponentIdentifier()))
						{
							Throwable	t	= exception instanceof InvocationTargetException
								? ((InvocationTargetException)exception).getTargetException() : exception;
								
							if(t instanceof RuntimeException)
							{
								throw (RuntimeException)exception;
							}
							else if(t instanceof Error)
							{
								throw (Error)t;
							}
							else
							{
								throw new RuntimeException(exception);
							}
						}
					}
				}));
				return IFuture.DONE;
			}
			public String toString()
			{
				return "microagent.executeBody()_#"+this.hashCode();
			}
		});
	}
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock()
	{
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock()
	{
	}

	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(component.getFetcher());
			if(microagent instanceof IPojoMicroAgent)
			{
				fetcher.setValue("$pojoagent", ((IPojoMicroAgent)microagent).getPojoAgent());
			}
			this.fetcher = fetcher;
		}
		return fetcher;
	}
	
	/**
	 *  Inform the agent that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(final IMessageAdapter message)
	{
		boolean done = false;
		if(messagehandlers!=null)
		{
			for(int i=0; i<messagehandlers.size(); i++)
			{
				IMessageHandler mh = (IMessageHandler)messagehandlers.get(i);
				if(mh.getFilter().filter(message))
				{
					mh.handleMessage(message.getParameterMap(), message.getMessageType());
					if(mh.isRemove())
					{
						messagehandlers.remove(i);
					}
					done = true;
				}
			}
		}
		
		if(!done)
		{
			microagent.messageArrived(Collections.unmodifiableMap(message.getParameterMap()), message.getMessageType());
		}
	}
	
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(final IConnection con)
	{
		microagent.streamArrived(con);
	}

	/**
	 *  Start the end steps of the component.
	 *  Called as part of cleanup behavior.
	 */
	public IFuture<Void> cleanupComponent()
	{
		final Future<Void> ret = new Future<Void>(); 
		
		microagent.agentKilled().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				collectInjectedResults();
				super.customResultAvailable(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				collectInjectedResults();
				StringWriter	sw	= new StringWriter();
				exception.printStackTrace(new PrintWriter(sw));
				component.getLogger().severe(component.getComponentIdentifier()+", "+component.getModel().getFullName()+": Exception during cleanup: "+sw);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Collect the results of the fields.
	 */
	protected void collectInjectedResults()
	{
		for(String name: micromodel.getResultInjectionNames())
		{
			Tuple3<FieldInfo, String, String> inj = micromodel.getResultInjection(name);
			Field field = inj.getFirstEntity().getField(component.getClassLoader());
			String convback = inj.getThirdEntity();
			
			try
			{
				Object agent = microagent instanceof IPojoMicroAgent? ((IPojoMicroAgent)microagent).getPojoAgent(): microagent;
				field.setAccessible(true);
				Object val = field.get(agent);
				
				if(convback!=null)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher(getFetcher());
					fetcher.setValue("$value", val);
					val = SJavaParser.evaluateExpression(convback, component.getModel().getAllImports(), fetcher, component.getClassLoader());
				}
				
				component.setResultValue(name, val);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
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
		return microagent.isAtBreakpoint(breakpoints);
	}
	
	//-------- helpers --------
	
	/**
	 *  Get the pojo agent object.
	 *  @return The pojo agent object.
	 */
	public Object getPojoAgent()
	{
		return microagent.getPojoAgent();
	}

	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public void addMessageHandler(final IMessageHandler handler)
	{
		if(handler.getFilter()==null)
			throw new RuntimeException("Filter must not null in handler: "+handler);
			
		if(messagehandlers==null)
		{
			messagehandlers = new ArrayList<IMessageHandler>();
		}
		if(handler.getTimeout()>0)
		{
			component.waitForDelay(handler.getTimeout(), new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Only call timeout when handler is still present
					if(messagehandlers.contains(handler))
					{
						handler.timeoutOccurred();
						if(handler.isRemove())
						{
							removeMessageHandler(handler);
						}
					}
					return IFuture.DONE;
				}
			}, handler.isRealtime());
		}
		messagehandlers.add(handler);
	}
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public void removeMessageHandler(IMessageHandler handler)
	{
		if(messagehandlers!=null)
		{
			messagehandlers.remove(handler);
		}
	}
	
	/**
	 *  Get the micro model.
	 *  @return The micro model.
	 */
	public MicroModel getMicroModel()
	{
		return micromodel;
	}
	
	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public PojoMicroAgent getAgent()
	{
		return microagent;
	}
	
	/**
	 *  Get the persistable state.
	 *  @return The persistable state.
	 */
	public MicroAgentPersistInfo	getPersistableState()
	{
		return new MicroAgentPersistInfo(microagent);
	}
}
