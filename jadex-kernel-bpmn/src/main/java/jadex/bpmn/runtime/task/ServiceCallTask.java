package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskProperty;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Call a service.
 *  Service and method name may be specified as parameters.
 *  Rebind parameter is also supported.
 *  All other in and inout parameters are interpreted as method arguments.
 *  One out or inout parameter may be specifed to receive the call result.
 *  Service name may alternatively supplied as name of lane and
 *  method name as name of activity. 
 */
@Task(description="The print task can be used for calling a component service.", properties={
	@TaskProperty(name="service", clazz=String.class, description="The required service name."),
	@TaskProperty(name="method", clazz=String.class, description="The required method name.")
//	@TaskProperty(name="rebind", clazz=boolean.class, description="The rebind flag (forces a frsh search).")
})
public class ServiceCallTask implements ITask
{
	//-------- constants --------
	
	/** Parameter for service name. */
	public static final String PARAMETER_SERVICE	= "service"; 
	
	/** Parameter for method name. */
	public static final String PARAMETER_METHOD	= "method"; 
	
	/** Parameter for rebind flag. */
	public static final String PARAMETER_REBIND	= "rebind"; 
	
	//-------- ITask interface --------
	

	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess process)
	{
		final Future<Void>	ret	= new Future<Void>();
		String	service	= null;
		String	method	= null;
		String	resultparam	= null;
		boolean	rebind	= false;
		
		// Collect arguments and settings.
		final List<Object>	args = new ArrayList<Object>();
		final List<Class<?>> argtypes = new ArrayList<Class<?>>();
		IndexMap<String, MParameter>	mparams	= context.getActivity().getParameters();
		for(Iterator<MParameter> it=mparams.values().iterator(); it.hasNext(); )
		{
			MParameter	param	= (MParameter)it.next();
			if(PARAMETER_SERVICE.equals(param.getName()))
			{
				service	= (String)context.getParameterValue(param.getName());
			}
			else if(PARAMETER_METHOD.equals(param.getName()))
			{
				method	= (String)context.getParameterValue(param.getName());		
			}
			else if(PARAMETER_REBIND.equals(param.getName()))
			{
				Object	val	= context.getParameterValue(param.getName());
				rebind	= val!=null ? ((Boolean)val).booleanValue() : false;
			}
			else if(MParameter.DIRECTION_IN.equals(param.getDirection()))
			{
				args.add(context.getParameterValue(param.getName()));
				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
			}
			else if(MParameter.DIRECTION_INOUT.equals(param.getDirection()))
			{
				if(resultparam!=null)
					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
				
				resultparam	= param.getName();
				args.add(context.getParameterValue(param.getName()));
				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
			}
			else if(MParameter.DIRECTION_OUT.equals(param.getDirection()))
			{
				if(resultparam!=null)
					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
				
				resultparam	= param.getName();
			}
		}
		
		// Apply shortcuts, if necessary.
		if(service==null && context.getActivity().getLane()!=null)
		{
			service	= context.getActivity().getLane().getName();
		}
		if(method==null)
		{
			method	= context.getActivity().getName();
		}
		
		if(service==null)
		{
			throw new RuntimeException("No 'service' specified for ServiceCallTask: "+context);
		}
		if(method==null)
		{
			throw new RuntimeException("No 'method' specified for ServiceCallTask: "+context);
		}
		
		// Fetch service and call method.
		final String	fservice	= service;
		final String	fmethod	= method;
		final String	fresultparam	= resultparam;
		process.getServiceContainer().getRequiredService(service, rebind)
			.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				Method	m	= SReflect.getMethod(result.getClass(), fmethod, (Class[])argtypes.toArray(new Class[argtypes.size()]));
				if(m==null)
				{
					throw new RuntimeException("Method "+fmethod+argtypes+" not found for service "+fservice+": "+context);
				}
				try
				{
					Object	val	= m.invoke(result, args.toArray());
					if(val instanceof IFuture)
					{
						((IFuture<Object>)val).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
						{
							public void customResultAvailable(Object result)
							{
								if(fresultparam!=null)
									context.setParameterValue(fresultparam, result);
								ret.setResult(null);
							}
						});
					}
					else
					{
						if(fresultparam!=null)
							context.setParameterValue(fresultparam, val);
						ret.setResult(null);
					}
				}
				catch(InvocationTargetException ite)
				{
					ret.setException((Exception)ite.getTargetException());
				}
				catch(Exception e)
				{
					ret.setException(e);					
				}
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Execute the task.
//	 *  @param context	The accessible values.
//	 *  @param process	The process instance executing the task.
//	 *  @return	To be notified, when the task has completed.
//	 */
//	public IFuture execute(final ITaskContext context, final IInternalAccess process)
//	{
//		final Future	ret	= new Future();
//		String	service	= null;
//		String	method	= null;
//		String	resultparam	= null;
//		boolean	rebind	= false;
//		
//		// Collect arguments and settings.
//		final List	args	= new ArrayList();
//		final List	argtypes	= new ArrayList();
//		IndexMap<String, MParameter>	mparams	= context.getActivity().getParameters();
//		for(Iterator it=mparams.values().iterator(); it.hasNext(); )
//		{
//			MParameter	param	= (MParameter)it.next();
//			if(PARAMETER_SERVICE.equals(param.getName()))
//			{
//				service	= (String)context.getParameterValue(param.getName());
//			}
//			else if(PARAMETER_METHOD.equals(param.getName()))
//			{
//				method	= (String)context.getParameterValue(param.getName());		
//			}
//			else if(PARAMETER_REBIND.equals(param.getName()))
//			{
//				Object	val	= context.getParameterValue(param.getName());
//				rebind	= val!=null ? ((Boolean)val).booleanValue() : false;
//			}
//			else if(MParameter.DIRECTION_IN.equals(param.getDirection()))
//			{
//				args.add(context.getParameterValue(param.getName()));
//				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
//			}
//			else if(MParameter.DIRECTION_INOUT.equals(param.getDirection()))
//			{
//				if(resultparam!=null)
//					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
//				
//				resultparam	= param.getName();
//				args.add(context.getParameterValue(param.getName()));
//				argtypes.add(param.getClazz().getType(process.getClassLoader(), process.getModel().getAllImports()));
//			}
//			else if(MParameter.DIRECTION_OUT.equals(param.getDirection()))
//			{
//				if(resultparam!=null)
//					throw new RuntimeException("Only one 'out' parameter allowed for ServiceCallTask: "+context);
//				
//				resultparam	= param.getName();
//			}
//		}
//		
//		// Apply shortcuts, if necessary.
//		if(service==null && context.getActivity().getLane()!=null)
//		{
//			service	= context.getActivity().getLane().getName();
//		}
//		if(method==null)
//		{
//			method	= context.getActivity().getName();
//		}
//		
//		if(service==null)
//		{
//			throw new RuntimeException("No 'service' specified for ServiceCallTask: "+context);
//		}
//		if(method==null)
//		{
//			throw new RuntimeException("No 'method' specified for ServiceCallTask: "+context);
//		}
//		
//		// Fetch service and call method.
//		final String	fservice	= service;
//		final String	fmethod	= method;
//		final String	fresultparam	= resultparam;
//		process.getServiceContainer().getRequiredService(service, rebind)
//			.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				Method	m	= SReflect.getMethod(result.getClass(), fmethod, (Class[])argtypes.toArray(new Class[argtypes.size()]));
//				if(m==null)
//				{
//					throw new RuntimeException("Method "+fmethod+argtypes+" not found for service "+fservice+": "+context);
//				}
//				try
//				{
//					Object	val	= m.invoke(result, args.toArray());
//					if(val instanceof IFuture)
//					{
//						((IFuture)val).addResultListener(new DelegationResultListener(ret)
//						{
//							public void customResultAvailable(Object result)
//							{
//								if(fresultparam!=null)
//									context.setParameterValue(fresultparam, result);
//								ret.setResult(null);
//							}
//						});
//					}
//					else
//					{
//						if(fresultparam!=null)
//							context.setParameterValue(fresultparam, val);
//						ret.setResult(null);
//					}
//				}
//				catch(InvocationTargetException ite)
//				{
//					ret.setException((Exception)ite.getTargetException());
//				}
//				catch(Exception e)
//				{
//					ret.setException(e);					
//				}
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		// Todo: how to compensate service call!?
		return IFuture.DONE;
	}
	
	/**
	 *  Get the extra parameters that depend on the property settings of the task.
	 */
	public static List<ParameterMetaInfo> getExtraParameters(Map<String, MProperty> params, IModelInfo mi, ClassLoader cl)
	{
		List<ParameterMetaInfo> ret = new ArrayList<ParameterMetaInfo>();
		
		try
		{
			MProperty msparam = params.get(PARAMETER_SERVICE);
			MProperty mmparam = params.get(PARAMETER_METHOD);
			
			if(msparam!=null && mmparam!=null)
			{
				String reqname = (String)SJavaParser.evaluateExpression(msparam.getInitialValue().getValue(), mi.getAllImports(), null, cl);
				String methodname = (String)SJavaParser.evaluateExpression(mmparam.getInitialValue().getValue(), mi.getAllImports(), null, cl);
				
				if(reqname!=null && methodname!=null)
				{
					RequiredServiceInfo reqser = mi.getRequiredService(reqname);
					Class<?> type = reqser.getType().getType(cl);
					
					Method[] ms = type.getMethods();
					// todo check parameter types?
					for(Method m: ms)
					{
						if(m.getName().equals(methodname))
						{
							Class<?>[] ptypes = m.getParameterTypes();
							Class<?> pret = m.getReturnType();
							
							for(int j=0; j<ptypes.length; j++)
							{
								ret.add(new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, ptypes[j], "param"+j, null, null));
							}
							if(!pret.equals(Void.class) && !pret.equals(void.class))
							{
								ret.add(new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, pret, "return", null, null));
							}
						}
					}
				}
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		return ret;
	}
}
