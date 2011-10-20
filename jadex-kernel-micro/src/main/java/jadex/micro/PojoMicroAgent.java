package jadex.micro;

import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentMessageArrived;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Micro agent class that redirects calls to a pojo agent object.
 */
public class PojoMicroAgent extends MicroAgent implements IPojoMicroAgent
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
	public IFuture agentCreated()
	{
		return invokeMethod(AgentCreated.class, null);
	}
		
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		invokeMethod(AgentBody.class, null).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
			}
		});
	}

	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		invokeMethod(AgentMessageArrived.class, new Object[]{msg, mt}).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
			}
		});
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture agentKilled()
	{
		return invokeMethod(AgentKilled.class, null);
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
	protected IFuture invokeMethod(Class annotation, Object[] args)
	{
		final Future ret = new Future();
		
		Method[] methods = agent.getClass().getMethods();
		boolean found = false;
		
		for(int i=0; i<methods.length && !found; i++)
		{
			if(methods[i].isAnnotationPresent(annotation))
			{
				found = true;
				try
				{
					Object res = methods[i].invoke(agent, args);
					if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(createResultListener(
							new DelegationResultListener(ret)));
					}
					else
					{
						ret.setResult(null);
					}
				}
				catch(Exception e)
				{
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
