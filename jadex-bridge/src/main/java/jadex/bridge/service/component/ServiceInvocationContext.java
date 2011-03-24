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

	/** The number of interceptors called. */
	protected int cnt;
	
	/** The last interceptor number. */
	protected int last;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(IServiceInvocationInterceptor[] interceptors)
	{
		this.object = new ArrayList();
		this.method = new ArrayList();
		this.arguments = new ArrayList();
		this.result = new ArrayList();
		
		this.last = -1;
		this.interceptors = interceptors;
	}

	//-------- methods --------
	
	/**
	 *  Get the object.
	 *  @return the object.
	 */
	public Object getObject()
	{
		return object.get(cnt-1);
	}
	
	/**
	 *  Set the object.
	 *  @param object The object to set.
	 */
	public void setObject(Object object)
	{
		this.object.set(cnt-1, object);
	}

	/**
	 *  Get the method.
	 *  @return the method.
	 */
	public Method getMethod()
	{
		return (Method)method.get(cnt-1);
	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(Method method)
	{
		this.method.set(cnt-1, method);
	}

	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public List getArguments()
	{
		return (List)arguments.get(cnt-1);
	}
	
	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public Object[] getArgumentArray()
	{
		List args = (List)arguments.get(cnt-1);
		return args!=null? args.toArray(): new Object[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param args The arguments to set.
	 */
	public void setArguments(List args)
	{
		this.arguments.set(cnt-1, args);
	}

	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result.get(cnt-1);
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result.set(cnt-1, result);
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
		
		push(object, method, args, null);
		
		IServiceInvocationInterceptor interceptor = getNextInterceptor();
		
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
			for(int i=last+1; i<interceptors.length; i++)
			{
				if(interceptors[i].isApplicable(this))
				{
					ret = interceptors[i];
					last = i;
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
		cnt++;
	}
	
	/**
	 * 
	 */
	protected void pop()
	{
		// Keep last results
		if(cnt>1)
		{
			cnt--;
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


