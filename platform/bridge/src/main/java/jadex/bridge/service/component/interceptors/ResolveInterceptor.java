package jadex.bridge.service.component.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.nonfunctional.INFMethodPropertyProvider;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.Base64;
import jadex.commons.IParameterGuesser;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.IStringConverter;

/**
 *  The resolve interceptor is responsible for determining
 *  the object on which the method invocation is finally performed.
 * 
 *  Checks whether the object is a ServiceInfo. In this case
 *  it delegates method calls of I(Internal)Service to 
 *  the automatically created BasicService instance and all
 *  other calls to the domain object.
 *  
 *  // todo: much annotation stuff and injection of objects to the pojo.
 */
public class ResolveInterceptor extends AbstractApplicableInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	public static final Set<Method> SERVICEMETHODS;
	protected static final Method START_METHOD;
	protected static final Method SHUTDOWN_METHOD;
	protected static final Method INVOKE_METHOD;
//	protected static final Method CREATESID_METHOD;
	
	static
	{
		try
		{
			START_METHOD = IInternalService.class.getMethod("startService", new Class[0]);
			SHUTDOWN_METHOD = IInternalService.class.getMethod("shutdownService", new Class[0]);
			INVOKE_METHOD = IService.class.getMethod("invokeMethod", new Class[]{String.class, ClassInfo[].class, Object[].class, ClassInfo.class});
			SERVICEMETHODS = new HashSet<Method>();
			SERVICEMETHODS.add(IService.class.getMethod("getServiceId", new Class[0]));
			SERVICEMETHODS.add(IService.class.getMethod("getMethodInfos", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("getPropertyMap", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("isValid", new Class[0]));

			// internal methods???
			SERVICEMETHODS.add(IInternalService.class.getMethod("setServiceIdentifier", new Class[]{IServiceIdentifier.class}));
			SERVICEMETHODS.add(IInternalService.class.getMethod("setComponentAccess", new Class[]{IInternalAccess.class}));
//			SERVICEMETHODS.add(IService.class.getMethod("getExternalComponentFeature", new Class[]{Class.class}));
			
			Method[] ms = INFPropertyProvider.class.getDeclaredMethods();
			for(Method m: ms)
			{
//				System.out.println("m: "+m.getName());
				SERVICEMETHODS.add(m);
			}
			
			ms = INFMethodPropertyProvider.class.getDeclaredMethods();
			for(Method m: ms)
			{
//				System.out.println("m: "+m.getName());
				SERVICEMETHODS.add(m);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/** The component. */
	protected IInternalAccess ia;
	
	/**
	 *  Create a new ResolveInterceptor.
	 */
	public ResolveInterceptor(IInternalAccess ia)
	{
		this.ia = ia;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
				
		Object service = sic.getObject();
		if(service instanceof ServiceInfo)
		{
			final ServiceInfo si = (ServiceInfo)service;
			
			if(START_METHOD.equals(sic.getMethod()))
			{
				// invoke 1) basic service start 2) domain service start
				invokeDoubleMethod(sic, si, START_METHOD, ServiceStart.class, true, false)
					.thenAccept(x->ret.setResult(null))
					.exceptionally(x->
					{
						invokeDoubleMethod(sic, si, START_METHOD, OnStart.class, true, true).delegate(ret);	
					});
				//.addResultListener(new DelegationResultListener<Void>(ret));
			}
			else if(SHUTDOWN_METHOD.equals(sic.getMethod()))
			{
				// invoke 1) domain service shutdown 2) basic service shutdown
				
				invokeDoubleMethod(sic, si, SHUTDOWN_METHOD, ServiceShutdown.class, true, false)
					.thenAccept(x->ret.setResult(null))
					.exceptionally(x->
					{
						invokeDoubleMethod(sic, si, SHUTDOWN_METHOD, OnEnd.class, true, true).delegate(ret);	
					});
				//invokeDoubleMethod(sic, si, SHUTDOWN_METHOD, ServiceShutdown.class, false).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else if(INVOKE_METHOD.equals(sic.getMethod()))
			{
				// If reflective method invokeMethod() is invoked it will be redirected to the real method
				// String servicename, String methodname, ClassInfo[] argtypes, Object[] args, ClassInfo returntype)
				sic.setObject(si.getDomainService());
				
				List<Object> args = sic.getArguments();
				String methodname = (String)args.get(0);
				ClassInfo[] argtypes = (ClassInfo[])args.get(1);
				Object[] as = (Object[])args.get(2);
				//ClassInfo rettype = (ClassInfo)args.get(3);
				
				//if(methodname.indexOf("getNF")!=-1)
				//	System.out.println("herere");
				
				// todo: always try decoding strings with json?
				if(as!=null)
				{
					for(int i=0; i<as.length; i++)
					{
						if(as[i] instanceof String)
						{
							try
							{
								Object val = convertFromJsonString((String)as[i], null);
								as[i] = val;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
				
				Tuple2<java.lang.reflect.Method, Object[]> res = findMethod(as, argtypes, si.getDomainService().getClass(), methodname);
				
				if(res.getFirstEntity()!=null)
				{
					sic.setMethod(res.getFirstEntity());
					sic.setArguments(SUtil.arrayToList(res.getSecondEntity()));
					sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					sic.setResult(new RuntimeException("Method not found: "+methodname+" "+Arrays.toString(argtypes)));
					return IFuture.DONE;
				}
			}
			else if(SERVICEMETHODS.contains(sic.getMethod()))
			{
				sic.setObject(si.getManagementService());
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				sic.setObject(si.getDomainService());
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
		}
		else
		{
			sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Search an annotation method.
	 */
	protected Method searchMethod(Class<?> impl, Class<? extends Annotation>  annotation)
	{
		Method[] methods = SReflect.getAllMethods(impl);
		Method found = null;
		
		for(int i=0; found==null && i<methods.length; i++)
		{
			if(methods[i].isAnnotationPresent(annotation))
			{
				if(found==null)
				{
					// todo: why must be public?
					if((methods[i].getModifiers()&Modifier.PUBLIC)!=0)
					{
						found = methods[i];
					}
					else
					{
						throw new RuntimeException("Annotated method @"+annotation.getSimpleName()+" must be public: "+methods[i]);
					}
				}
				
				// Fail on duplicate annotation if not from overridden method.
				/*else if(!Arrays.equals(methods[i].getParameterTypes(), found.getParameterTypes()))
				{
					throw new RuntimeException("Duplicate annotation @"+annotation.getSimpleName()+" in methods "+methods[i]+" and "+found);
				}*/
			}
		}
		
		return found;
	}
	
	/**
	 *  Invoke double methods.
	 *  The boolean 'firstorig' determines if basicservice method is called first.
	 */
	protected IFuture<Void> invokeDoubleMethod(final ServiceInvocationContext sic, final ServiceInfo si, Method m, Class<? extends Annotation> annotation, boolean firstorig, boolean ignorenotfound)
	{
		final Future<Void> ret = new Future<Void>();
		
		Object obj = ProxyFactory.isProxyClass(si.getDomainService().getClass())? ProxyFactory.getInvocationHandler(si.getDomainService()): si.getDomainService();

		Map<Object, Set<String>> invocs = (Map<Object, Set<String>>)Starter.getPlatformValue(ia.getId(), Starter.DATA_INVOKEDMETHODS);
		Set<String> invans = invocs.get(obj); 
		
		// if pojo was already inited (e.g. agent and service impl are in same class) domain init is not invoked again
		String anname = SReflect.getUnqualifiedClassName(annotation);
		if(invans!=null && invans.contains(anname))
		{
			System.out.println("already called: "+obj+" "+anname);
			sic.setObject(si.getManagementService());
			sic.invoke().delegate(ret);
		}
		// both must be inited
		else
		{	
			// todo: refactor when deprecated annotations are phased out
			Method found = null;
			try
			{
				found = searchMethod(obj.getClass(), annotation);
			}
			catch(Exception e)
			{
			}
			
			if(!ret.isDone())
			{
				if(found!=null)
				{
					if(invans==null)
					{
						invans = new HashSet<>();
						invocs.put(obj, invans);
					}
					invans.add(anname);
					
					final ServiceInvocationContext domainsic = new ServiceInvocationContext(sic);
					domainsic.setMethod(found);
					domainsic.setObject(obj);
					// Guess parameters for allowing injected value in pojo methods
					IParameterGuesser guesser = ia.getParameterGuesser();
					List<Object> args = new ArrayList<Object>();
					for(int i=0; i<found.getParameterTypes().length; i++)
					{
						args.add(guesser.guessParameter(found.getParameterTypes()[i], false));
					}
					domainsic.setArguments(args);
					
					sic.setObject(si.getManagementService());
					
					if(firstorig)
					{
						sic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								// Mgmt method is always future<void>
								IResultListener<Object>	lis	= new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										domainsic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
										{
											public void customResultAvailable(Void result)
											{
	//											if(sic.getObject() instanceof BasicService && ((BasicService)sic.getObject()).getInterfaceType().getName().indexOf("Peer")!=-1)
	//												System.out.println("hhhhhhhhhhhhhhhhhh");
												
												// If domain result is future, replace finished mgmt result with potentially not yet finished domain future.
												if(domainsic.getResult() instanceof IFuture<?> || domainsic.getResult() instanceof Exception)
													sic.setResult(domainsic.getResult());
												super.customResultAvailable(result);
											}
										});
									}
									public void exceptionOccurred(Exception exception)
									{
										// Invocation finished, exception available in result future. 
										ret.setResult(null);
									}
								};
								((IFuture)sic.getResult()).addResultListener(lis);
							}
						});
					}
					else
					{
						domainsic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								// Domain method may be void or future<void>
								if(domainsic.getResult() instanceof IFuture<?>)
								{
									IResultListener<Object>	lis	= new IResultListener<Object>()
									{
										public void resultAvailable(Object result)
										{
											sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
										}
										public void exceptionOccurred(Exception exception)
										{
											// Make exception available in result future.
											sic.setResult(new Future<Void>(exception));
											ret.setResult(null);
										}
									};
									((IFuture)domainsic.getResult()).addResultListener(lis);
								}
								else
								{
									sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
								}
							}
						});
					}
				}
				else if(ignorenotfound)
				{				
					sic.setObject(si.getManagementService());
					sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					ret.setException(new RuntimeException());
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Find the correct method by its name and parameter values.
	 *  The parameter values are half-evaluated, i.e. if they are
	 *  deserialized as far as possible. Still serialized parameters
	 *  are saved as SerialiedObject. Those are deserialized using
	 *  the parameter class as hint.
	 *
	 *  @param decparams Partially decoded parameters.
	 *  @param serclazz The target class.
	 *  @param methodname The method name
	 *  @return The method and further decoded parameters.
	 */
	protected Tuple2<java.lang.reflect.Method, Object[]> findMethod(Object[] params, ClassInfo[] argtypes, Class<?> serclazz, String methodname)
	{
		java.lang.reflect.Method ret = null;
		java.lang.reflect.Method[] ms = null;
		
		if(argtypes!=null)
		{
//			Class<?>[] types = new Class[argtypes.length];
//			for(int i=0; i<argtypes.length; i++)
//				types[i] = argtypes[i].getType(ia.getClassLoader());
//			ms = new java.lang.reflect.Method[1];
//			ms[0] = SReflect.getMethod(serclazz, methodname, types);
			
			List<Method> okms = new ArrayList<>();
			ms = SReflect.getMethods(serclazz, methodname);
			for(int i=0; i<ms.length; i++)
			{
				Class<?>[] pts = ms[i].getParameterTypes();
				boolean ok = true;
				for(int j=0; j<pts.length && j<argtypes.length; j++)
				{
					if(!new ClassInfo(pts[j]).equals(argtypes[j]))
						ok = false;
				}
				if(ok)
					okms.add(ms[i]);
			}
		}
		else
		{
			ms = SReflect.getMethods(serclazz, methodname);
		}
		
		Object[] pvals = null;

		if(ms.length==1)
		{
			ret = ms[0];
		}
		else if(ms.length>1)
		{
			// Find the 'best' method

			// First check the number of arguments
			Set<java.lang.reflect.Method> msok = new HashSet<java.lang.reflect.Method>();
			Set<java.lang.reflect.Method> msmaybeok = new HashSet<java.lang.reflect.Method>();

			for(java.lang.reflect.Method tmp1: ms)
			{
				if(tmp1.getParameterTypes().length==params.length)
				{
					msok.add(tmp1);
				}
			}

			if(msok.size()==1)
			{
				ret = msok.iterator().next();
				if(ret.getParameterTypes().length!=params.length)
					ret = null;
			}
			else if(msok.size()>1)
			{
				// Check the argument types

				for(Iterator<java.lang.reflect.Method> it=msok.iterator(); it.hasNext();)
				{
					java.lang.reflect.Method meth = it.next();
					boolean maybeok = true;
					for(int i=0; i<meth.getParameterTypes().length; i++)
					{
						Class<?> ptype = meth.getParameterTypes()[i];
						Object pval = params[i];

						boolean ok = true;
						if(pval!=null && !SUtil.NULL.equals(pval))
						{
							Class<?> wptype = SReflect.getWrappedType(ptype); // method parameter type
							Class<?> wpvtype = SReflect.getWrappedType(pval.getClass()); // value type

							ok = SReflect.isSupertype(wptype, wpvtype);

							if(!ok)
							{
								// Javascript only has float (no integer etc.)
								ok = SReflect.isSupertype(Number.class, wptype) &&
									SReflect.isSupertype(Number.class, wpvtype);

								// Test if we got String value and have a basic type or wrapper on the
								maybeok &= SReflect.isSupertype(Number.class, wptype) &&
									SReflect.isSupertype(String.class, wpvtype);
							}
						}

						if(!ok)
						{
							it.remove();
							break; // skip other parameters and try next method
						}
					}

					if(maybeok)
						msmaybeok.add(meth);
				}

				if(msok.size()==1)
				{
					ret = msok.iterator().next();
				}
				else if(msok.size()>1)
				{
					System.out.println("Found more than one method that could be applicable, choosing first: "+msok);
					ret = msok.iterator().next();
				}
				else
				{
					if(msmaybeok.size()>0)
					{
						// check if parameter conversion works
						// do this as long as a suitable method was found
						for(Iterator<java.lang.reflect.Method> it=msmaybeok.iterator(); it.hasNext();)
						{
							java.lang.reflect.Method meth = it.next();

							try
							{
								pvals = generateParameters(params, meth);
								ret = meth;
								break;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
			}
		}

		if(ret!=null && pvals==null)
		{
			try
			{
				pvals = generateParameters(params, ret);
			}
			catch(Exception e)
			{
				ret = null;
			}
		}
		
		return new Tuple2<java.lang.reflect.Method, Object[]>(ret, pvals);
	}
	
	/**
	 *  Generate call parameters.
	 *  @param vals The current parameters.
	 *  @return The adapted method call parameters.
	 */
	protected Object[] generateParameters(Object[] vals, java.lang.reflect.Method m) throws Exception
	{
		Object[] ret = new Object[m.getParameterCount()];
		
		for(int i=0; i<ret.length && vals!=null && i<vals.length; i++)
		{
			if(vals[i]==null)
			{
				ret[i] = null;
				continue;
			}
			
			Type wptype = m.getGenericParameterTypes()[i];
//			Class<?> wptype = SReflect.getWrappedType(m.getParameterTypes()[i]);
			
			ret[i] = convertParameter(vals[i], wptype);
		}
		
		return ret;
	}
	
	
	/**
	 *  Convert a parameter to a target type.
	 */
	protected Object convertParameter(Object value, Type targettype) throws Exception
	{
		Class<?> targetclass = SReflect.getClass(targettype);
		Class<?> targetclasswrapped = SReflect.getWrappedType(targetclass);
		
		String text = null;
		if(value instanceof SerializedValue)
			text = ((SerializedValue)value).getValue();
		else if(value instanceof String)
			text = (String)value;
		
		if(text!=null)
		{
			try
			{
//				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclass, readprocs);
				value = convertFromJsonString(text, targetclass);
			}
			catch(Exception e)
			{
//				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclasswrapped, readprocs);
				try
				{
					value = convertFromJsonString(text, targetclasswrapped);
				}
				catch(Exception e2)
				{
				}
			}
		}

		Object ret = value;

		Class<?> valuewrapped = SReflect.getWrappedType(value.getClass());

		if(!SReflect.isSupertype(targetclass, valuewrapped))
		{
//			System.out.println("type problem: "+targetType+" "+actualValueWrapped+" "+sim.getParameterValues()[i]);
			
			if(value instanceof String)
			{
				if(isSupportedBasicType(targetclass))
				{
					Object cval = convertFromString((String)value, targetclass);
					ret = cval;
				}
				// base 64 case (problem could be normal string?!)
				else if(SReflect.isSupertype(byte[].class, targetclass))
				{
					ret = Base64.decode(((String) value).toCharArray());
				}
			}
			// Javascript only has float (no integer etc.)
			else if(SReflect.isSupertype(Number.class, targetclass) && SReflect.isSupertype(Number.class, valuewrapped))
			{
				if(Integer.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).intValue();
				}
				else if(Long.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).longValue();
				}
				else if(Double.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).doubleValue();
				}
				else if(Float.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).floatValue();
				}
				else if(Short.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).shortValue();
				}
				else if(Byte.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).byteValue();
				}
			}
			else if(valuewrapped.isArray())
			{
				Type itype;
				if(SReflect.isSupertype(List.class, targetclass))
				{
					ret = new ArrayList<Object>();
					itype = SReflect.getInnerGenericType(targettype);
				}
				else if(SReflect.isSupertype(Set.class, targetclass))
				{
					ret = new HashSet<Object>();
					itype = SReflect.getInnerGenericType(targettype);
				}
				else if(targetclass.isArray())
				{
					ret = Array.newInstance(targetclass.getComponentType(), Array.getLength(value));
					itype = targetclass.getComponentType();
				}
				else
				{
					throw new RuntimeException("Parameter conversion not possible: "+value+" "+targettype);
				}
					
				if(Array.getLength(value)>0)
				{
					for(int i=0; i<Array.getLength(value); i++)
					{
						Object v = convertParameter(Array.get(value, i), itype);
						if(ret instanceof Collection)
						{
							((Collection)ret).add(v);
						}
						else
						{
							Array.set(ret, i, v);
						}
					}
				}
			}
			else
			{
				throw new RuntimeException("Parameter conversion not possible: "+value+" "+targettype);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert an object to the json string representation.
	 *  @param val The value.
	 *  @return The string representation.
	 */
	protected String convertToJsonString(Object val)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(ia.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_JSON);
		String data = conv.convertObject(val, null, this.getClass().getClassLoader(), null);
		return data;
	}
	
	/**
	 *  Convert json to object representation.
	 *  @param val The json value.
	 *  @return The object.
	 */
	protected Object convertFromJsonString(String val, Class<?> type)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(ia.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_JSON);
		Object data = conv.convertString(val, type, this.getClass().getClassLoader(), null);
		return data;
	}
	
	/**
	 *  Convert to object representation.
	 *  @param val The string value.
	 *  @return The object.
	 */
	protected Object convertFromString(String val, Class<?> type)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(ia.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_BASIC);
		Object data = conv.convertString(val, type, this.getClass().getClassLoader(), null);
		return data;
	}
	
	/**
	 *  Test if basic converter can handle this type.
	 *  @param type The type.
	 *  @return True if it can handle this type.
	 */
	protected boolean isSupportedBasicType(Class<?> type)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(ia.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_BASIC);
		return conv.isSupportedType(type);
	}
	
	/**
	 *  Struct for serialized value.
	 */
	public static class SerializedValue
	{
		/** The serialized value. */
		protected String value;

		/**
		 *  Create a new serialized value.
		 */
		public SerializedValue()
		{
		}

		/**
		 *  Create a new serialized value.
		 */
		public SerializedValue(String value)
		{
			this.value = value;
		}

		/**
		 *  Get the value.
		 *  @return The value
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 *  Set the value.
		 *  @param value The value to set
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
	}
}
