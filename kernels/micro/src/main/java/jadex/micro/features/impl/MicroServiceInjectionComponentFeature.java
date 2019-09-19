package jadex.micro.features.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.UnresolvedServiceInvocationHandler;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
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
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.features.IMicroServiceInjectionFeature;

/**
 *  Inject required services into annotated field values of the agent.
 *  Performed after subcomponent creation and provided service initialization.
 *  
 *  Method injectServices also used for injecting into separate service.
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
		MicroModel model = (MicroModel)getComponent().getModel().getRawModel();
		Object agent = getComponent().getFeature(IPojoComponentFeature.class).getPojoAgent();

		// Fetch all injection names - field and method injections
		String[] sernames = model.getServiceInjectionNames();
		Stream<Tuple2<String, ServiceInjectionInfo[]>> s = Arrays.stream(sernames).map(sername -> new Tuple2<String, ServiceInjectionInfo[]>(sername, model.getServiceInjections(sername)));
		Map<String, ServiceInjectionInfo[]> serinfos = s.collect(Collectors.toMap(t -> t.getFirstEntity(), t -> t.getSecondEntity())); 

		//InjectionInfoHolder ii = model.getInjectionInfoHolder();
		
		return injectServices(component, agent, sernames, serinfos, model.getModelInfo());
	}
	
	/**
	 * 
	 * @param component
	 * @param target
	 * @return
	 */
	public static IFuture<Void> injectServices(IInternalAccess component, Object target, String[] sernames, Map<String, ServiceInjectionInfo[]> serinfos, IModelInfo model)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Inject required services
		if(component.getFeature0(IRequiredServicesFeature.class)==null)
		{
			ret.setResult(null);
		}
		else
		{
			// Fetch all injection names - field and method injections
			//String[] sernames = model.getServiceInjectionNames();
			
			if(sernames.length>0)
			{
				CounterResultListener<Void> lis = new CounterResultListener<Void>(sernames.length, 
					new DelegationResultListener<Void>(ret));
		
				for(int i=0; i<sernames.length; i++)
				{
					final ServiceInjectionInfo[] infos = serinfos.get(sernames[i]); //model.getServiceInjections(sernames[i]);
					final CounterResultListener<Void> lis2 = new CounterResultListener<Void>(infos.length, lis);
	
					String sername = (String)SJavaParser.evaluateExpressionPotentially(sernames[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader());
							
					//if("secser".equals(sername))
					//	System.out.println("sdbgjh");
					
					for(int j=0; j<infos.length; j++)
					{
						// Uses required service info to search service
						
						RequiredServiceInfo	info = infos[j].getRequiredServiceInfo()!=null? infos[j].getRequiredServiceInfo(): model.getService(sername);				
						ServiceQuery<Object> query = ServiceQuery.getServiceQuery(component, info);
												
						// if query
						if(infos[j].getQuery()!=null && infos[j].getQuery().booleanValue())
						{							
							//ServiceQuery<Object> query = new ServiceQuery<>((Class<Object>)info.getType().getType(component.getClassLoader()), info.getDefaultBinding().getScope());
							//query = info.getTags()==null || info.getTags().size()==0? query: query.setServiceTags(info.getTags().toArray(new String[info.getTags().size()]), component.getExternalAccess()); 
							
							long to = infos[j].getActive();
							ISubscriptionIntermediateFuture<Object> sfut = to>0?
								component.getFeature(IRequiredServicesFeature.class).addQuery(query, to):
								component.getFeature(IRequiredServicesFeature.class).addQuery(query);
							
							if(infos[j].getRequired()==null || !infos[j].getRequired().booleanValue())
								lis2.resultAvailable(null);
							final int fj = j;
							
							// Invokes methods for each intermediate result
							sfut.addResultListener(new IIntermediateResultListener<Object>()
							{
								boolean first = true;
								public void intermediateResultAvailable(final Object result)
								{
									/*if(result==null)
									{
										System.out.println("received null as service: "+infos[fj]);
										return;
									}*/
									// todo: multiple parameters and using parameter annotations?!
									// todo: multiple parameters and wait until all are filled?!
									
									if(infos[fj].getMethodInfo()!=null)
									{
										Method m = SReflect.getAnyMethod(target.getClass(), infos[fj].getMethodInfo().getName(), infos[fj].getMethodInfo().getParameterTypes(component.getClassLoader()));
										
										invokeMethod(m, target, result, component);
									}
									else if(infos[fj].getFieldInfo()!=null)
									{
										final Field	f = infos[fj].getFieldInfo().getField(component.getClassLoader());

										setDirectFieldValue(f, target, result);
									}
									
									if(first)
									{
										first = false;
										lis2.resultAvailable(null);
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
									// todo:
									
//									if(!(e instanceof ServiceNotFoundException)
//										|| m.getAnnotation(AgentServiceSearch.class).required())
//									{
//										component.getLogger().warning("Method injection failed: "+e);
//									}
//									else
									{
										// Call self with empty list as result.
										finished();
									}
								}
							});
						}
						// if is search
						else
						{
							if(infos[j].getFieldInfo()!=null)
							{
								final Field	f = infos[j].getFieldInfo().getField(component.getClassLoader());
								Class<?> ft = f.getDeclaringClass();
								boolean multiple = ft.isArray() || SReflect.isSupertype(Collection.class, ft) || info.getMax()>2;
								
								final IFuture<Object> sfut = callgetService(sername, info, component, multiple);

								
								// todo: what about multi case?
								// why not add values to a collection as they come?!
								// currently waits until the search has finished before injecting
								
								// Is annotation is at field and field is of type future directly set it
								if(SReflect.isSupertype(IFuture.class, f.getType()))
								{
									try
									{
										f.setAccessible(true);
										f.set(target, sfut);
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
									// if future is already done 
									if(sfut.isDone() && sfut.getException() == null)
									{
										try
										{
											setDirectFieldValue(f, target, sfut.get());
											lis2.resultAvailable(null);
										}
										catch(Exception e)
										{
											lis2.exceptionOccurred(e);
										}
									}
									else if(infos[j].getLazy()!=null && infos[j].getLazy().booleanValue() && !multiple)
									{
										//RequiredServiceInfo rsi = ((IInternalRequiredServicesFeature)component.getFeature(IRequiredServicesFeature.class)).getServiceInfo(sername);
										Class<?> clz = info.getType().getType(component.getClassLoader(), component.getModel().getAllImports());
										//ServiceQuery<Object> query = RequiredServicesComponentFeature.getServiceQuery(component, info);
										
										UnresolvedServiceInvocationHandler h = new UnresolvedServiceInvocationHandler(component, query);
										Object proxy = ProxyFactory.newProxyInstance(component.getClassLoader(), new Class[]{IService.class, clz}, h);
									
										try
										{
											f.setAccessible(true);
											f.set(target, proxy);
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
										// todo: remove!
										// todo: disallow multiple field injections!
										// This is problematic because search can defer the agent startup esp. when remote search

										// Wait for result and block init until available
										// Dangerous because agent blocks
										final int fj = j;
										sfut.addResultListener(new IResultListener<Object>()
										{
											public void resultAvailable(Object result)
											{
												try
												{
													setDirectFieldValue(f, target, result);
													lis2.resultAvailable(null);
												}
												catch(Exception e)
												{
													lis2.exceptionOccurred(e);
												}
											}
											
											public void exceptionOccurred(Exception e)
											{
												if(!(e instanceof ServiceNotFoundException)
													|| (infos[fj].getRequired()!=null && infos[fj].getRequired().booleanValue()))
												{
													component.getLogger().warning("Field injection failed: "+e);
													lis2.exceptionOccurred(e);
												}
												else
												{
													// Set empty list, set on exception 
													if(SReflect.isSupertype(f.getType(), List.class))
													{
														// Call self with empty list as result.
														resultAvailable(new ArrayList<Object>());
													}
													else if(SReflect.isSupertype(f.getType(), Set.class))
													{
														// Call self with empty list as result.
														resultAvailable(new HashSet<Object>());
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
								// injection of future as parameter not considered meanigful case
								
								// injection of lazy proxy not considered as meaningful case

								final Method m = SReflect.getAnyMethod(target.getClass(), infos[j].getMethodInfo().getName(), infos[j].getMethodInfo().getParameterTypes(component.getClassLoader()));
	
								boolean multiple = info.getMax()>2;

								final IFuture<Object> sfut = callgetService(sername, info, component, multiple);
								
								// if future is already done 
								if(sfut.isDone() && sfut.getException() == null)
								{
									try
									{
										invokeMethod(m, target, sfut.get(), component);
										lis2.resultAvailable(null);
									}
									catch(Exception e)
									{
										lis2.exceptionOccurred(e);
									}
								}
								else 
								{
									sfut.addResultListener(new IResultListener<Object>() 
									{
										@Override
										public void resultAvailable(Object result) 
										{
											try
											{
												invokeMethod(m, target, sfut.get(), component);
												lis2.resultAvailable(null);
											}
											catch(Exception e)
											{
												lis2.exceptionOccurred(e);
											}
										}
										
										@Override
										public void exceptionOccurred(Exception exception) 
										{
											lis2.exceptionOccurred(exception);
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
	 * 
	 */
	protected static void setDirectFieldValue(Field f, Object target, Object result)
	{
		Class<?> ft = f.getType();
		//boolean multiple = ft.isArray() || SReflect.isSupertype(Collection.class, ft) || info.getMax()>2;

		if(SReflect.isSupertype(ft, result.getClass()))
		{
			try
			{
				f.setAccessible(true);
				f.set(target, result);
			}
			catch(Throwable t)
			{
				throw SUtil.throwUnchecked(t);
			}
		}
		else if(ft.isArray())
		{
			// find next null value and insert new value there
			Class<?> ct = ft.getComponentType();
			if(SReflect.isSupertype(ct, result.getClass()))
			{
				try
				{
					Object ar = f.get(target);
				
					for(int i=0; i<Array.getLength(ar); i++)
					{
						if(Array.get(ar, i)==null)
						{
							try
							{
								f.setAccessible(true);
								f.set(target, result);
							}
							catch(Exception e)
							{
								throw SUtil.throwUnchecked(e);
							}
						}
					}
				}
				catch(Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
			else
			{
				throw new RuntimeException("cannot invoke method as result type does not fit field types: "+result+" "+f);
			}
		}
		else if(SReflect.isSupertype(List.class, ft))
		{
			try
			{
				List<Object> coll = (List<Object>)f.get(target);
				if(coll==null)
				{
					coll = new ArrayList<Object>();
					try
					{
						f.setAccessible(true);
						f.set(target, coll);
					}
					catch(Exception e)
					{
						throw SUtil.throwUnchecked(e);
					}
				}
				
				coll.add(result);
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
		else if(SReflect.isSupertype(Set.class, ft))
		{
			try
			{
				Set<Object> coll = (Set<Object>)f.get(target);
				if(coll==null)
				{
					coll = new HashSet<Object>();
					try
					{
						f.setAccessible(true);
						f.set(target, coll);
					}
					catch(Exception e)
					{
						throw SUtil.throwUnchecked(e);
					}
				}
				
				coll.add(result);
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
		else
		{
			throw new RuntimeException("injection error: "+f+" "+target+" "+result);
		}
	}

	/**
	 * 
	 * @param m
	 * @param target
	 * @param result
	 */
	protected static void invokeMethod(Method m, Object target, Object result, IInternalAccess component)
	{
		Object[] args = new Object[m.getParameterCount()];
		
		boolean invoke = false;
		for(int i=0; i<m.getParameterCount(); i++)
		{
			if(SReflect.isSupertype(m.getParameterTypes()[i], result.getClass()))
			{
				args[i] = result;
				invoke = true;
			}
		}
		
		if(invoke)
		{
			component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						m.setAccessible(true);
						m.invoke(target, args);
					}
					catch(Throwable t)
					{
						throw SUtil.throwUnchecked(t);
					}
					return IFuture.DONE;
				}
			});
		}
		else
		{
			component.getLogger().warning("cannot invoke method as result type does not fit parameter types: "+result+" "+m);
		}
	}
		
		
	/**
	 *  Call
	 *  @param sername
	 *  @param info
	 *  @return
	 */
	protected static IFuture<Object> callgetService(String sername, RequiredServiceInfo info, IInternalAccess component, boolean multiple)
	{
		final IFuture<Object> sfut;
		
		// if info is available use it. in case of services it is not available in the agent (model)
		if(info!=null)
		{
			if(multiple)
			{
				IFuture	ifut = component.searchServices(ServiceQuery.getServiceQuery(component, info));
				sfut = ifut;
			}
			else
			{
				IFuture	ifut = component.searchService(ServiceQuery.getServiceQuery(component, info));
				sfut = ifut;
			}
		}
		else
		{
			if(multiple)
			{
				IFuture	ifut = component.getServices(sername);
				sfut = ifut;
			}
			else
			{
				IFuture	ifut = component.getServices(sername);
				sfut = ifut;
			}
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
	
	/**
	 * 
	 * @param component
	 * @param target
	 * @return
	 * /
	public static IFuture<Void> injectServices(IInternalAccess component, Object target, String[] sernames, Map<String, ServiceInjectionInfo[]> serinfos, IModelInfo model)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Inject required services
		if(component.getFeature(IRequiredServicesFeature.class)==null)
		{
			ret.setResult(null);
		}
		else
		{
			// Fetch all injection names - field and method injections
			//String[] sernames = model.getServiceInjectionNames();
			
			if(sernames.length>0)
			{
				CounterResultListener<Void> lis = new CounterResultListener<Void>(sernames.length, 
					new DelegationResultListener<Void>(ret));
		
				for(int i=0; i<sernames.length; i++)
				{
					final ServiceInjectionInfo[] infos = serinfos.get(sernames[i]); //model.getServiceInjections(sernames[i]);
					final CounterResultListener<Void> lis2 = new CounterResultListener<Void>(infos.length, lis);
	
					String sername = (String)SJavaParser.evaluateExpressionPotentially(sernames[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader());
										
					for(int j=0; j<infos.length; j++)
					{
						// Uses required service info to search service
						
						RequiredServiceInfo	info = infos[j].getRequiredServiceInfo()!=null? infos[j].getRequiredServiceInfo(): model.getService(sername);				
						
						if(infos[j].getFieldInfo()!=null)
						{
							final Field	f = infos[j].getFieldInfo().getField(component.getClassLoader());
							Class<?> ft = f.getDeclaringClass();
							boolean multiple = ft.isArray() || SReflect.isSupertype(Collection.class, ft) || info.getMax()>2;

							final IFuture<Object> sfut = callgetService(sername, info, component, multiple);
							
							// todo: what about multi case?
							// why not add values to a collection as they come?!
							// currently waits until the search has finished before injecting
							
							// Is annotation is at field and field is of type future directly set it
							if(SReflect.isSupertype(IFuture.class, f.getType()))
							{
								try
								{
									f.setAccessible(true);
									f.set(target, sfut);
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
								// todo: disallow multiple field injections!
								// This is problematic because search can defer the agent startup esp. when remote search
								if(sfut.isDone() && sfut.getException() == null)
								{
									try
									{
										f.setAccessible(true);
										f.set(target, sfut.get());
										lis2.resultAvailable(null);
									}
									catch(Exception e)
									{
										component.getLogger().warning("Field injection failed: "+e);
										lis2.exceptionOccurred(e);
									}	
								}
								else if(infos[j].isLazy() && !multiple)
								{
									RequiredServiceInfo rsi = ((IInternalRequiredServicesFeature)component.getFeature(IRequiredServicesFeature.class)).getServiceInfo(sername);
									Class<?> clz = rsi.getType().getType(component.getClassLoader(), component.getModel().getAllImports());
									ServiceQuery<Object> query = ServiceQuery.getServiceQuery(component, info);
									
									UnresolvedServiceInvocationHandler h = new UnresolvedServiceInvocationHandler(component, query);
									Object proxy = ProxyFactory.newProxyInstance(component.getClassLoader(), new Class[]{IService.class, clz}, h);
								
									try
									{
										f.setAccessible(true);
										f.set(target, proxy);
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
									// todo: remove!
									
									// Wait for result and block init until available
									// Dangerous because agent blocks
									
									sfut.addResultListener(new IResultListener<Object>()
									{
										public void resultAvailable(Object result)
										{
											try
											{
												f.setAccessible(true);
												f.set(component, result);
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
												|| f.getAnnotation(AgentServiceSearch.class).required())
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
							final Method m = SReflect.getAnyMethod(target.getClass(), infos[j].getMethodInfo().getName(), infos[j].getMethodInfo().getParameterTypes(component.getClassLoader()));

							if(infos[j].isQuery())
							{
								@SuppressWarnings("unchecked")
								ServiceQuery<Object> query = new ServiceQuery<>((Class<Object>)info.getType().getType(component.getClassLoader()), info.getDefaultBinding().getScope());
								query	= info.getTags()==null || info.getTags().size()==0? query: query.setServiceTags(info.getTags().toArray(new String[info.getTags().size()]), component.getExternalAccess()); 
								ISubscriptionIntermediateFuture<Object> sfut = component.getFeature(IRequiredServicesFeature.class).addQuery(query);
								lis2.resultAvailable(null);
								
								// Invokes methods for each intermediate result
								sfut.addResultListener(new IIntermediateResultListener<Object>()
								{
									public void intermediateResultAvailable(final Object result)
									{
										if(SReflect.isSupertype(m.getParameterTypes()[0], result.getClass()))
										{
											component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													try
													{
														m.setAccessible(true);
														m.invoke(target, new Object[]{result});
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
											|| m.getAnnotation(AgentServiceSearch.class).required())
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
								boolean multiple = info.getMax()>2;
								final IFuture<Object> sfut = callgetService(sername, info, component, multiple);
								
								if(multiple)
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
												component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														try
														{
															m.setAccessible(true);
															m.invoke(target, new Object[]{result});
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
												component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														try
														{
															m.setAccessible(true);
															m.invoke(target, new Object[]{ifut.getIntermediateResults()});
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
												|| m.getAnnotation(AgentServiceSearch.class).required())
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
											component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													try
													{
														m.setAccessible(true);
														m.invoke(target, new Object[]{result});
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
												|| m.getAnnotation(AgentServiceSearch.class).required())
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
	}*/
	
}