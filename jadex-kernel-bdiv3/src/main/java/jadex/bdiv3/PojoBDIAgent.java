package jadex.bdiv3;

import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.impl.CapabilityWrapper;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgentInterpreter;
import jadex.micro.MicroModel;
import jadex.micro.PojoMicroAgent;
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
public class PojoBDIAgent extends BDIAgent implements IPojoMicroAgent
{
	//-------- attributes --------
	
	/** The agent. */
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
			createResultListener(new ExceptionDelegationResultListener<Tuple2<Method, Object>, Void>(ret)
		{
			public void customResultAvailable(Tuple2<Method, Object> result)
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
				new ExceptionDelegationResultListener<Tuple2<Method, Object>, Void>(ret)
		{
			public void customResultAvailable(Tuple2<Method, Object> res)
			{
				// Only end body if future or void and kill is true 
				Boolean found = null;
				
				Method method = res!=null? res.getFirstEntity(): null;
				
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
				
				if(found!=null && found.booleanValue())
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
			interpreter.createResultListener(new IResultListener<Tuple2<Method, Object>>()
		{
			public void resultAvailable(Tuple2<Method, Object> result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof RuntimeException)
				{
					throw (RuntimeException)exception;
				}
				else
				{
					throw new RuntimeException(exception);
				}
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
			interpreter.createResultListener(new IResultListener<Tuple2<Method, Object>>()
		{
			public void resultAvailable(Tuple2<Method, Object> result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof RuntimeException)
				{
					throw (RuntimeException)exception;
				}
				else
				{
					throw new RuntimeException(exception);
				}
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
			createResultListener(new ExceptionDelegationResultListener<Tuple2<Method, Object>, Void>(ret)
		{
			public void customResultAvailable(Tuple2<Method, Object> result)
			{
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 *  Test if the agent's execution is currently at one of the
	 *  given breakpoints. If yes, the agent will be suspended by
	 *  the platform.
	 *  Available breakpoints can be specified in the
	 *  micro agent meta info.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		boolean ret = false;
		
		MicroModel mm = getInterpreter().getMicroModel();
		if(mm.getBreakpointMethod()!=null)
		{
			try
			{
				Object res = mm.getBreakpointMethod().invoke(agent, new Object[]{breakpoints});
				ret = ((Boolean)res).booleanValue();
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
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
	protected IFuture<Tuple2<Method, Object>> invokeMethod(Class<? extends Annotation> annotation, Object[] args)
	{
		final Future<Tuple2<Method, Object>> ret = new Future<Tuple2<Method, Object>>();
		
		Method[] methods = agent.getClass().getMethods();
		boolean found = false;
		
		for(int i=0; i<methods.length && !found; i++)
		{
			final Method method = methods[i];
			if(method.isAnnotationPresent(annotation))
			{
				found = true;
				
				// Try to guess additional parameters as internal or external access.
				if(args==null || method.getParameterTypes().length>args.length)
				{
					Object[]	tmp	= new Object[method.getParameterTypes().length];
					if(args!=null)
					{
						System.arraycopy(args, 0, tmp, 0, args.length);
					}
					for(int j=args==null?0:args.length; j<method.getParameterTypes().length; j++)
					{
						Class<?>	clazz	= method.getParameterTypes()[j];
						if(SReflect.isSupertype(clazz, PojoBDIAgent.class))
						{
							tmp[j]= PojoBDIAgent.this;
						}
						else if(SReflect.isSupertype(clazz, IExternalAccess.class))
						{
							tmp[j]= PojoBDIAgent.this.getExternalAccess();
						}
						else if(SReflect.isSupertype(clazz, ICapability.class))
						{
							tmp[j]= new CapabilityWrapper(this, agent, null);
						}
					}
					args	= tmp;
				}
				
				try
				{
					Object res = method.invoke(agent, args);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(createResultListener(
							new ExceptionDelegationResultListener<Object, Tuple2<Method, Object>>(ret)
						{
							public void customResultAvailable(Object result)
							{
								ret.setResult(new Tuple2<Method, Object>(method, result));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								super.exceptionOccurred(exception);
							}
						}
						));
					}
					else
					{
						ret.setResult(new Tuple2<Method, Object>(method, res));
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
						ret.setException(new RuntimeException("Method must be declared public: "+methods[i]));
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
	
//	/**
//	 *  Get parameter values for injection into method and constructor calls.
//	 */
//	protected Object[] getInjectionValues(Method method, Object[] args)
//	{
//		Object[] tmp = new Object[method.getParameterTypes().length];
//		if(args!=null)
//		{
//			System.arraycopy(args, 0, tmp, 0, args.length);
//		}
//		for(int j=args==null? 0: args.length; j<method.getParameterTypes().length; j++)
//		{
//			Class<?>	clazz	= method.getParameterTypes()[j];
//			if(SReflect.isSupertype(clazz, PojoMicroAgent.class))
//			{
//				tmp[j]= PojoMicroAgent.this;
//			}
//			else if(SReflect.isSupertype(clazz, IExternalAccess.class))
//			{
//				tmp[j]= PojoMicroAgent.this.getExternalAccess();
//			}
//		}
//		return tmp;
//	}
}
