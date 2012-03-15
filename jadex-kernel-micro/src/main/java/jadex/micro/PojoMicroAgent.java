package jadex.micro;

import jadex.bridge.IConnection;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.AgentStreamArrived;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Micro agent class that redirects calls to a pojo agent object.
 */
public class PojoMicroAgent extends MicroAgent implements IPojoMicroAgent
{
	//-------- attributes --------
	
	/** The pojo agent object. */
	protected Object agent;

	//-------- constructors --------
	
	/**
	 *  Init the micro agent with the interpreter.
	 *  @param interpreter The interpreter.
	 */
	public void init(MicroAgentInterpreter interpreter, Object agent)
	{
		super.init(interpreter);
		this.agent = agent;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
		invokeMethod(AgentCreated.class, null).addResultListener(
			createResultListener(new ExceptionDelegationResultListener<Method, Void>(ret)
		{
			public void customResultAvailable(Method result)
			{
				ret.setResult(null);
			}
		}));
		return ret;
	}
		
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		invokeMethod(AgentBody.class, null)
			.addResultListener(interpreter.createResultListener(
				new ExceptionDelegationResultListener<Method, Void>(ret)
		{
			public void customResultAvailable(Method method)
			{
				// Only end body if future or void and kill is true 
				Boolean found = null;
				
				if(method!=null)
				{
					if(SReflect.isSupertype(IFuture.class, method.getReturnType()))
					{
						found = Boolean.TRUE;
					}
					else if(void.class.equals(method.getReturnType()))
					{
						AgentBody ab = method.getAnnotation(AgentBody.class);
						found = ab.keepalive()? Boolean.FALSE: Boolean.TRUE;
					}
				}
				else
				{
					Agent ag = agent.getClass().getAnnotation(Agent.class);
					found = ag.keepalive()? Boolean.FALSE: Boolean.TRUE;
				}
				
				if(found.booleanValue())
					ret.setResult(null);
			}
		}));
		
		return ret;
	}

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		invokeMethod(AgentMessageArrived.class, new Object[]{msg, mt}).addResultListener(
			interpreter.createResultListener(new IResultListener<Method>()
		{
			public void resultAvailable(Method result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof RuntimeException)
					throw (RuntimeException)exception;
				else
					throw new RuntimeException(exception);
			}
		}));
	}
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void streamArrived(IConnection con)
	{
		invokeMethod(AgentStreamArrived.class, new Object[]{con}).addResultListener(
			interpreter.createResultListener(new IResultListener<Method>()
		{
			public void resultAvailable(Method result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof RuntimeException)
					throw (RuntimeException)exception;
				else
					throw new RuntimeException(exception);
			}
		}));
	}


	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void> agentKilled()
	{
		final Future<Void> ret = new Future<Void>();
		invokeMethod(AgentKilled.class, null).addResultListener(
			createResultListener(new ExceptionDelegationResultListener<Method, Void>(ret)
		{
			public void customResultAvailable(Method result)
			{
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public Object getPojoAgent()
	{
		return agent;
	}

	/**
	 *  Invoke double methods.
	 *  The boolean 'firstorig' determines if basicservice method is called first.
	 */
	protected IFuture<Method> invokeMethod(Class<? extends Annotation> annotation, Object[] args)
	{
		final Future<Method> ret = new Future<Method>();
		
		Method[] methods = agent.getClass().getMethods();
		boolean found = false;
		
		for(int i=0; i<methods.length && !found; i++)
		{
			final Method method = methods[i];
			if(methods[i].isAnnotationPresent(annotation))
			{
				found = true;
				try
				{
					Object res = methods[i].invoke(agent, args);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(createResultListener(
							new ExceptionDelegationResultListener<Void, Method>(ret)
						{
							public void customResultAvailable(Void result)
							{
								ret.setResult(method);
							}
						}
						));
					}
					else
					{
						ret.setResult(method);
					}
				}
				catch(Exception e)
				{
					e = (Exception)(e instanceof InvocationTargetException && ((InvocationTargetException)e)
						.getCause() instanceof Exception? ((InvocationTargetException)e).getCause(): e);
					ret.setException(e);
					break;
				}
			}
		}
		
		if(!found)
		{
			// Check if annotation is present and complain that method is not public.
			
			Class clazz = agent.getClass();
			
			while(!Object.class.equals(clazz) && !found)
			{
				methods =clazz.getDeclaredMethods();
				
				for(int i=0; i<methods.length && !found; i++)
				{
					if(methods[i].isAnnotationPresent(annotation))
					{
						found = true;
						ret.setException(new RuntimeException(
							"Method must be declared public: "+methods[i]));
						break;
					}
				}
				
				clazz = clazz.getSuperclass();
			}
			
			if(!found)
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
}
