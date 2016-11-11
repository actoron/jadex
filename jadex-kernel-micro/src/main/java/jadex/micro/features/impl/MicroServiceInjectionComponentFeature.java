package jadex.micro.features.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.UnresolvedServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.TagFilter;
import jadex.commons.FieldInfo;
import jadex.commons.IResultCommand;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.javaparser.SJavaParser;
import jadex.micro.MicroModel;
import jadex.micro.MicroModel.ServiceInjectionInfo;
import jadex.micro.annotation.AgentService;
import jadex.micro.features.IMicroServiceInjectionFeature;

/**
 *  Inject required services into annotated field values.
 *  Performed after subcomponent creation and provided service initialization.
 */
public class MicroServiceInjectionComponentFeature extends	AbstractComponentFeature	implements IMicroServiceInjectionFeature
{
	//-------- constants ---------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(
		IMicroServiceInjectionFeature.class, MicroServiceInjectionComponentFeature.class,
		new Class<?>[]{ISubcomponentsFeature.class, IPojoComponentFeature.class}, null);

	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroServiceInjectionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
		
		final MicroModel model = (MicroModel)getComponent().getModel().getRawModel();
		final Object agent = getComponent().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();

		// Inject required services
		if(component.getComponentFeature(IRequiredServicesFeature.class)==null)
		{
			ret.setResult(null);
		}
		else
		{
			// Fetch all injection names - field and method injections
			String[] sernames = model.getServiceInjectionNames();
			
			if(sernames.length>0)
			{
				CounterResultListener<Void> lis = new CounterResultListener<Void>(sernames.length, 
					new DelegationResultListener<Void>(ret));
		
				for(int i=0; i<sernames.length; i++)
				{
					final ServiceInjectionInfo[] infos = model.getServiceInjections(sernames[i]);
					final CounterResultListener<Void> lis2 = new CounterResultListener<Void>(infos.length, lis);
	
					String sername = (String)SJavaParser.evaluateExpressionPotentially(sernames[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader());
					
					RequiredServiceInfo	info	= model.getModelInfo().getRequiredService(sername);				
										
					for(int j=0; j<infos.length; j++)
					{
						if(infos[j].getFieldInfo()!=null)
						{
							final IFuture<Object> sfut = callGetRequiredService(sername, info);
							final Field	f	= infos[j].getFieldInfo().getField(component.getClassLoader());
							
							// todo: what about multi case?
							// why not add values to a collection as they come?!
							// currently waits until the search has finised before injecting
							
							// Is annotation is at field and field is of type future directly set it
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
								Class<?> ft = f.getDeclaringClass();
								
								// todo: disallow multiple field injections!
								// This is problematic because search can defer the agent startup esp. when remote search
								if(sfut.isDone())
								{
									try
									{
										f.setAccessible(true);
										f.set(agent, sfut.get());
										lis2.resultAvailable(null);
									}
									catch(Exception e)
									{
										component.getLogger().warning("Field injection failed: "+e);
										lis2.exceptionOccurred(e);
									}	
								}
								else if(!(info.isMultiple() || ft.isArray() || SReflect.isSupertype(Collection.class, ft) 
									|| !infos[j].isLazy()))
								{
									RequiredServiceInfo rsi = component.getComponentFeature(IRequiredServicesFeature.class).getRequiredServiceInfo(sername);
									Class<?> clz = rsi.getType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
									UnresolvedServiceInvocationHandler h = new UnresolvedServiceInvocationHandler(new IResultCommand<IFuture<Object>, Void>()
									{
										public IFuture<Object> execute(Void args)
										{
											return sfut;
										}
									});
									Object proxy = Proxy.newProxyInstance(getComponent().getClassLoader(), new Class[]{IService.class, clz}, h);
								
									try
									{
										f.setAccessible(true);
										f.set(agent, proxy);
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
									// Wait for result and block init until available
									// Dangerous because agent blocks
									
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
												// Set empty list on exception (why only list, what about set etc?!)
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
						}
						else if(infos[j].getMethodInfo()!=null)
						{
							final Method	m	= SReflect.getMethod(agent.getClass(), infos[j].getMethodInfo().getName(), infos[j].getMethodInfo().getParameterTypes(component.getClassLoader()));

							if(infos[j].isQuery())
							{
								TagFilter<Object> tagfil = info.getTags()==null || info.getTags().size()==0? null: new TagFilter<Object>(component.getExternalAccess(), info.getTags());
								ISubscriptionIntermediateFuture<Object> sfut = SServiceProvider.addQuery(getComponent(), (Class<Object>)info.getType().getType(getComponent().getClassLoader()), info.getDefaultBinding().getScope(), tagfil);
								
								// Invokes methods for each intermediate result
								sfut.addResultListener(new IIntermediateResultListener<Object>()
								{
									public void intermediateResultAvailable(final Object result)
									{
										if(SReflect.isSupertype(m.getParameterTypes()[0], result.getClass()))
										{
											component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
														throw SUtil.throwUnchecked(t);
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
								final IFuture<Object> sfut = callGetRequiredService(sername, info);
								
								if(info.isMultiple())
								{
									lis2.resultAvailable(null);
									IFuture	tfut	= sfut;
									final IIntermediateFuture<Object>	ifut	= (IIntermediateFuture<Object>)tfut;
									
									// Invokes methods for each intermediate result
									ifut.addResultListener(new IIntermediateResultListener<Object>()
									{
										public void intermediateResultAvailable(final Object result)
										{
											if(SReflect.isSupertype(m.getParameterTypes()[0], result.getClass()))
											{
												component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
															throw SUtil.throwUnchecked(t);
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
											// Inject all values at once if parameter is a collection
											if(SReflect.isSupertype(m.getParameterTypes()[0], Collection.class))
											{
												component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
															throw SUtil.throwUnchecked(t);
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
									// Invoke method once if required service is not multiple
									sfut.addResultListener(new IResultListener<Object>()
									{
										public void resultAvailable(final Object result)
										{
											component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
														throw SUtil.throwUnchecked(t);
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
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Call
	 *  @param sername
	 *  @param info
	 *  @return
	 */
	protected IFuture<Object> callGetRequiredService(String sername, RequiredServiceInfo info)
	{
		final IFuture<Object>	sfut;
		if(info!=null && info.isMultiple())
		{
			IFuture	ifut	= component.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices(sername);
			sfut	= ifut;
		}
		else
		{
			sfut	= component.getComponentFeature(IRequiredServicesFeature.class).getRequiredService(sername);					
		}
		
		return sfut;
	}
	
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
//	/**
//	 * 
//	 */
//	public static class MyListener implements IIntermediateResultListener<Object>
//	{
//		protected IInternalAccess component;
//		protected Method method;
//		
//		public MyListener(IInternalAccess component, Method method)
//		{
//			this.component = component;
//			this.method = method;
//		}
//		
//		public void intermediateResultAvailable(final Object result)
//		{
//			if(SReflect.isSupertype(method.getParameterTypes()[0], result.getClass()))
//			{
//				component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						try
//						{
//							method.setAccessible(true);
//							method.invoke(component, new Object[]{result});
//						}
//						catch(Throwable t)
//						{
//							throw SUtil.throwUnchecked(t);
//						}
//						return IFuture.DONE;
//					}
//				});
//			}
//		}
//		
//		public void resultAvailable(Collection<Object> result)
//		{
//			finished();
//		}
//		
//		public void finished()
//		{
//			// Inject all values at once if parameter is a collection
//			if(SReflect.isSupertype(method.getParameterTypes()[0], Collection.class))
//			{
//				component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						try
//						{
//							method.setAccessible(true);
//							method.invoke(component, new Object[]{ifut.getIntermediateResults()});
//						}
//						catch(Throwable t)
//						{
//							throw SUtil.throwUnchecked(t);
//						}
//						return IFuture.DONE;
//					}
//				});
//			}
//		}
//		
//		public void exceptionOccurred(Exception e)
//		{
//			if(!(e instanceof ServiceNotFoundException)
//				|| method.getAnnotation(AgentService.class).required())
//			{
//				component.getLogger().warning("Method injection failed: "+e);
//			}
//			else
//			{
//				// Call self with empty list as result.
//				finished();
//			}
//		}
//	}
}
