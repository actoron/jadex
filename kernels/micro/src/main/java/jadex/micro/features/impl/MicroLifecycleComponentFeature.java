package jadex.micro.features.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCallInfo;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.SAccess;
import jadex.commons.FieldInfo;
import jadex.commons.IParameterGuesser;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.SimpleParameterGuesser;
import jadex.commons.Tuple3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.MicroModel;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class MicroLifecycleComponentFeature extends	AbstractComponentFeature implements ILifecycleComponentFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(ILifecycleComponentFeature.class, MicroLifecycleComponentFeature.class,
		new Class<?>[]{IPojoComponentFeature.class, IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null, false);
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroLifecycleComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	/**
	 *  Check if a method using an annotation was already invoked.
	 *  @param ann The annotation.
	 *  @return True, if it was already called.
	 */
	public boolean wasAnnotationCalled(Class<? extends Annotation> ann)
	{
		Object pojo = component.getFeature(IPojoComponentFeature.class).getPojoAgent();
		Map<Object, Set<String>> invocs = (Map<Object, Set<String>>)Starter.getPlatformValue(component.getId(), Starter.DATA_INVOKEDMETHODS);
		Set<String> invans = invocs.get(pojo);
		if(invans!=null && invans.contains(SReflect.getUnqualifiedClassName(ann)))
		{
			return true;
		}
		else
		{
			if(invans==null)
			{
				invans = new HashSet<>();
				invocs.put(pojo, invans);
			}
			invans.add(SReflect.getUnqualifiedClassName(ann));
			return false;
		}
	}
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		MicroModel model = (MicroModel)component.getModel().getRawModel();
		
		Class<? extends Annotation> ann = OnInit.class;
		if(model.getAgentMethod(ann)!=null)
		{
			//return invokeMethod(getInternalAccess(), OnInit.class, null);
			if(wasAnnotationCalled(ann))
				return IFuture.DONE;
			else
				return invokeMethod(getInternalAccess(), ann, null);
		}
		else
		{
			return invokeMethod(getInternalAccess(), AgentCreated.class, null);
		}
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		//System.out.println("body on: "+getComponent().getId());
		// Invoke initial service calls.
		invokeServices();
		
		MicroModel model = (MicroModel)component.getModel().getRawModel();
		
		Class<? extends Annotation> ann = OnStart.class;
		if(model.getAgentMethod(ann)!=null)
		{
			//return invokeMethod(getInternalAccess(), OnInit.class, null);
			if(wasAnnotationCalled(ann))
				return IFuture.DONE;
			else
				return invokeMethod(getInternalAccess(), ann, null);
		}
		else
		{
			return invokeMethod(getInternalAccess(), AgentBody.class, null);
		}
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void> shutdown()
	{
		boolean debug	= component instanceof IPlatformComponentAccess && ((IPlatformComponentAccess)component).getPlatformComponent().debug;
		if(debug)
		{
			component.getLogger().severe("lifecycle feature shutdown start: "+getComponent());
		}
			
		final Future<Void> ret = new Future<Void>();
		
		MicroModel model = (MicroModel)component.getModel().getRawModel();
		
		IFuture<Void> fut;
		Class<? extends Annotation> ann = OnEnd.class;
		if(model.getAgentMethod(ann)!=null)
		{
			//return invokeMethod(getInternalAccess(), OnInit.class, null);
			if(wasAnnotationCalled(ann))
			{
				fut = IFuture.DONE;
				if(debug)
				{
					component.getLogger().severe("lifecycle feature shutdown method already invoked: "+getComponent());
				}
			}
			else
			{
				fut = invokeMethod(getInternalAccess(), ann, null);
				if(debug)
				{
					component.getLogger().severe("lifecycle feature shutdown method invoked: "+getComponent()+" done="+fut.isDone());
				}
			}
		}
		else
		{
			fut = invokeMethod(getInternalAccess(), AgentKilled.class, null);
			if(debug)
			{
				component.getLogger().severe("lifecycle feature shutdown agent killed invoked: "+getComponent()+" done="+fut.isDone());
			}
		}
		
		fut.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(debug)
				{
					component.getLogger().severe("lifecycle feature shutdown end result: "+getComponent());
				}
				proceed(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(debug)
				{
					component.getLogger().severe("lifecycle feature shutdown end exception: "+getComponent()+"\n"+SUtil.getExceptionStacktrace(exception));
				}
				proceed(exception);
			}
			
			protected void proceed(Exception e)
			{
				try
				{
					MicroModel micromodel = (MicroModel)getComponent().getModel().getRawModel();
					Object agent = getPojoAgent();
					
					for(String name: micromodel.getResultInjectionNames())
					{
						Tuple3<FieldInfo, String, String> inj = micromodel.getResultInjection(name);
						Field field = inj.getFirstEntity().getField(getComponent().getClassLoader());
						String convback = inj.getThirdEntity();
						
						SAccess.setAccessible(field, true);
						Object val = field.get(agent);
						
						if(convback!=null)
						{
							SimpleValueFetcher fetcher = new SimpleValueFetcher(getComponent().getFetcher());
							fetcher.setValue("$value", val);
							val = SJavaParser.evaluateExpression(convback, getComponent().getModel().getAllImports(), fetcher, getComponent().getClassLoader());
						}
						
						getComponent().getFeature(IArgumentsResultsFeature.class).getResults().put(name, val);
					}
				}
				catch(Exception e2)
				{
					ret.setException(e2);
//					throw new RuntimeException(e2);
				}
				
				if(!ret.isDone())
				{
					if(e!=null)
					{
						ret.setException(e);
					}
					else
					{
						ret.setResult(null);
					}
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the pojoagent.
	 *  @return The pojoagent
	 */
	public Object getPojoAgent()
	{
		return getComponent().getFeature(IPojoComponentFeature.class).getPojoAgent();
	}
	
	/**
	 *  Execute initial service calls.
	 */
	protected void invokeServices()
	{
		MicroModel mm = (MicroModel)getComponent().getModel().getRawModel();
		List<ServiceCallInfo> calls = mm.getServiceCalls();
		if(calls!=null)
		{
			for(final ServiceCallInfo call: calls)
			{
				IFuture<IService> fut = getComponent().getFeature(IRequiredServicesFeature.class).getService(call.getRequiredName());
				fut.addResultListener(new IResultListener<IService>()
				{
					public void resultAvailable(IService service)
					{
						MethodInfo mi = call.getServiceMethod();
						Method method = null;
						if(mi==null)
						{
							Class<?> iface = service.getServiceId().getServiceType().getType(getComponent().getClassLoader());
							Method[] methods = iface.getMethods();
							if(methods!=null)
							{
								for(Method m: methods)
								{
									if(m.getParameterTypes().length==0)
									{
										method = m;
									}
								}
							}
						}
						else
						{
							method = mi.getMethod(getComponent().getClassLoader());
						}
						
						if(method!=null)
						{
							try
							{
								Object ret = method.invoke(service, new Object[0]);
								if(ret instanceof IIntermediateFuture)
								{
									IIntermediateFuture<Object> sfut = (IIntermediateFuture<Object>)ret;
									sfut.addResultListener(new IntermediateDefaultResultListener<Object>()
									{
										public void intermediateResultAvailable(Object result)
										{
											handleCallback(call, result);
										}
										
										public void finished()
										{
											// todo:?
//											System.out.println("initial service call finished");
										}
										
										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
										}
									});
								}
								else if(ret instanceof IFuture)
								{
									IFuture<Object> sfut = (IFuture<Object>)ret;
									sfut.addResultListener(new IResultListener<Object>()
									{
										public void resultAvailable(Object result)
										{
											handleCallback(call, result);
										}
										
										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
										}
									});
								}
								else
								{
									handleCallback(call, ret);
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
		}
	}
	
	/**
	 *  Handle the result by setting a variable or by calling
	 *  a callback method.
	 */
	protected void handleCallback(ServiceCallInfo call, Object result)
	{
		try
		{
			if(call.getCallbackMethod()!=null)
			{
				Method m = call.getCallbackMethod().getMethod(getComponent().getClassLoader());
				SAccess.setAccessible(m, true);
				m.invoke(getPojoAgent(), new Object[]{result});
			}
			else
			{
				Field f = call.getCallbackField().getField(getComponent().getClassLoader());
				SAccess.setAccessible(f, true);
				Class<?> ft = f.getType();
				if(SReflect.isSupertype(ft, result.getClass()))
				{
					f.set(getPojoAgent(), result);
				}
				else if(SReflect.isSupertype(Collection.class, ft))
				{
					Collection<Object> coll = (Collection<Object>)f.get(getPojoAgent());
					coll.add(result);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

	//-------- helper methods --------
	
	/**
	 *  Invoke an agent method by injecting required arguments.
	 */
	public static IFuture<Void> invokeMethod(IInternalAccess component, Class<? extends Annotation> ann, Object[] args)
	{
		IFuture<Void> ret;
		
		MicroModel	model = (MicroModel)component.getModel().getRawModel();
		MethodInfo	mi	= model.getAgentMethod(ann);
		if(mi!=null)
		{
			Method	method	= null;
			try
			{
				Object pojo = component.getFeature(IPojoComponentFeature.class).getPojoAgent();
				method = mi.getMethod(pojo.getClass().getClassLoader());
				
				// Try to guess parameters from given args or component internals.
				IParameterGuesser	guesser	= args!=null ? new SimpleParameterGuesser(component.getParameterGuesser(), Arrays.asList(args)) : component.getParameterGuesser();
				Object[]	iargs	= new Object[method.getParameterTypes().length];
				for(int i=0; i<method.getParameterTypes().length; i++)
				{
					iargs[i]	= guesser.guessParameter(method.getParameterTypes()[i], false);
				}
				
				try
				{
					// It is now allowed to use protected/private agent created, body, terminate methods
					SAccess.setAccessible(method, true);
					Object res = method.invoke(pojo, iargs);
					if(res instanceof IFuture)
					{
						ret	= (IFuture<Void>)res;
					}
					else
					{
						ret	= IFuture.DONE;
					}
				}
				catch(Exception e)
				{
					if(e instanceof InvocationTargetException)
					{
						if(((InvocationTargetException)e).getTargetException() instanceof Exception)
						{
							e	= (Exception)((InvocationTargetException)e).getTargetException();
						}
						else if(((InvocationTargetException)e).getTargetException() instanceof Error)
						{
							// re-throw errors, e.g. StepAborted
							throw (Error)((InvocationTargetException)e).getTargetException();
						}
					}
					ret	= new Future<Void>(e);
				}

			}
			catch(Exception e)
			{
				// Error in method search or parameter guesser
				if(method==null)
				{
					ret	= new Future<Void>(new RuntimeException("Cannot find method: "+mi, e));
				}
				else
				{
					ret	= new Future<Void>(new RuntimeException("Cannot inject values for method: "+method, e));
				}
			}
		}
		else
		{
			ret	= IFuture.DONE;
		}
		
		return ret;
	}
}
