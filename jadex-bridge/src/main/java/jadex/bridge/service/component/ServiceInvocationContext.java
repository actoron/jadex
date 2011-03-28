package jadex.bridge.service.component;

import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *  Context for service invocations.
 *  Contains all method call information. 
 */
public class ServiceInvocationContext
{
	protected static final IServiceInvocationInterceptor DEFAULT_COMMAND = new MethodInvocationInterceptor();
	
	//-------- attributes --------
	
	/** The origin (proxy object. */
	protected Object proxy;
	
	
	/** The object. */
	protected List object;
	
	/** The method to be called. */
	protected List method;
	
	/** The invocation arguments. */
	protected List arguments;
	
	/** The call result. */
	protected List result;
	

	/** The service interceptors. */
	protected IServiceInvocationInterceptor[] interceptors;

	/** The stack of used interceptos. */
	protected List used;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(Object proxy, IServiceInvocationInterceptor[] interceptors)
	{
		this.proxy = proxy;
		this.object = new ArrayList();
		this.method = new ArrayList();
		this.arguments = new ArrayList();
		this.result = new ArrayList();
		
		this.used = new ArrayList();
		this.interceptors = interceptors;
	}

	//-------- methods --------
	
	/**
	 *  Get the proxy.
	 *  @return The proxy.
	 */
	public Object getProxy()
	{
		return proxy;
	}

	/**
	 *  Set the proxy.
	 *  @param proxy The proxy to set.
	 */
	public void setProxy(Object proxy)
	{
		this.proxy = proxy;
	}
	
	/**
	 *  Get the object.
	 *  @return the object.
	 */
	public Object getObject()
	{
		return object.get(used.size()-1);
	}

	/**
	 *  Set the object.
	 *  @param object The object to set.
	 */
	public void setObject(Object object)
	{
		this.object.set(used.size()-1, object);
	}

	/**
	 *  Get the method.
	 *  @return the method.
	 */
	public Method getMethod()
	{
		return (Method)method.get(used.size()-1);
	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(Method method)
	{
		this.method.set(used.size()-1, method);
	}

	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public List getArguments()
	{
		return (List)arguments.get(used.size()-1);
	}
	
	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public Object[] getArgumentArray()
	{
		List args = (List)arguments.get(used.size()-1);
		return args!=null? args.toArray(): new Object[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param args The arguments to set.
	 */
	public void setArguments(List args)
	{
		this.arguments.set(used.size()-1, args);
	}

	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result.get(used.size()-1);
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result.set(used.size()-1, result);
	}

	
	
//	/**
//	 *  Get the objects.
//	 *  @return The objects.
//	 */
//	public List getObjectStack()
//	{
//		return object;
//	}
//	
//	/**
//	 *  Get the objects.
//	 *  @param objects The objects.
//	 */
//	public void setObjectStack(List objects)
//	{
//		this.object = objects;
//	}
//	
//	/**
//	 *  Get the method.
//	 *  @return the method.
//	 */
//	public List getMethodStack()
//	{
//		return method;
//	}
//
//	/**
//	 *  Set the method.
//	 *  @param method The method to set.
//	 */
//	public void setMethodStack(List methods)
//	{
//		this.method = methods;
//	}
//
//	/**
//	 *  Get the args.
//	 *  @return the args.
//	 */
//	public List getArgumentStack()
//	{
//		return arguments;
//	}
//	
//	/**
//	 *  Set the arguments.
//	 *  @param args The arguments to set.
//	 */
//	public void setArgumentStack(List args)
//	{
//		this.arguments = args;
//	}
//	
//	/**
//	 *  Get the result.
//	 *  @return the result.
//	 */
//	public List getResultStack()
//	{
//		return result;
//	}
//
//	/**
//	 *  Set the results.
//	 *  @param result The results to set.
//	 */
//	public void setResultStack(List results)
//	{
//		this.result = results;
//	}
//	
//	/**
//	 *  Get the cnt.
//	 *  @return The cnt.
//	 */
//	public int getCounter()
//	{
//		return cnt;
//	}
//
//	/**
//	 *  Set the cnt.
//	 *  @param cnt The cnt to set.
//	 */
//	public void setCounter(int cnt)
//	{
//		this.cnt = cnt;
//	}

	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture invoke(Object object, Method method, List args)
	{
		final Future ret = new Future();
		
//		if(method.getName().equals("add"))
//			System.out.println("invoke: "+Thread.currentThread());
		
		push(object, method, args, null);
		
		IServiceInvocationInterceptor interceptor = getNextInterceptor();

		if(method.getName().equals("add"))
			System.out.println("add: "+used.get(used.size()-1)+" "+interceptor+" "+Thread.currentThread());
		
		if(interceptor!=null)
		{
			interceptor.execute(this).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					pop();
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					pop();
					ret.setException(exception);
				}
			});
		}
		else
		{
			System.out.println("No interceptor: "+method.getName());
			ret.setException(new RuntimeException("No interceptor found: "+method.getName()));
		}

		return ret;
	}
	
	/**
	 * 
	 */
	public IServiceInvocationInterceptor getNextInterceptor()
	{
		IServiceInvocationInterceptor ret = null;
		
		if(interceptors!=null)
		{
			int start = used.size()==0? -1: (Integer)used.get(used.size()-1);
			for(int i=start+1; i<interceptors.length; i++)
			{
				if(interceptors[i].isApplicable(this))
				{
					ret = interceptors[i];
					used.add(new Integer(i));
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture invoke()
	{
		return invoke(getObject(), getMethod(), getArguments());
	}

	/**
	 * 
	 */
	protected void push(Object o, Method m, List args, Object res)
	{
		object.add(o);
		method.add(m);
		arguments.add(args);
		result.add(res);
	}
	
	/**
	 * 
	 */
	protected void pop()
	{
		// Keep last results
		if(used.size()>1)
		{
			used.remove(used.size()-1);
			object.remove(object.size()-1);
			method.remove(method.size()-1);
			arguments.remove(arguments.size()-1);
			Object res = this.result.remove(this.result.size()-1);
			result.set(result.size()-1, res);
		}
	}
	
//	/**
//	 * 
//	 */
//	public void copy(ServiceInvocationContext sic)
//	{
//		setObjectStack(sic.getObjectStack());
//		setMethodStack(sic.getMethodStack());
//		setArgumentStack(sic.getArgumentStack());
//		setResultStack(sic.getResultStack());
//		
//	}
}


