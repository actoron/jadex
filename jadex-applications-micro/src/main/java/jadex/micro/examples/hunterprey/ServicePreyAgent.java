package jadex.micro.examples.hunterprey;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  A prey implemented using the EnvSupport service interface.
 */
@Agent
@RequiredServices(
	@RequiredService(name="env", type=IHunterPreyEnvironmentService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class ServicePreyAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The environment. */
	@AgentService(name="env")
	protected IHunterPreyEnvironmentService	env;
	
	/** The last movement direction. */
	protected String lastdir;
	
	//-------- methods --------
	
	/**
	 *  Register the agent as a prey at startup.
	 */
	@AgentCreated
	public void	start()
	{
		env.registerPrey().addResultListener(new IntermediateDefaultResultListener<Object>(agent.getLogger())
		{
			public void intermediateResultAvailable(Object percept)
			{
				perceptReceived(percept);
			}
		});
	}
	
	/**
	 *  Agent behavior for random movement.
	 */
	@AgentBody
	public void	body()
	{
		if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
		{
			lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
		}
		else
		{
			lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
		}

		env.move(lastdir).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				body();
			}
			
			public void exceptionOccurred(Exception e)
			{
				System.out.println(agent.getComponentIdentifier()+" action caused exception: "+e);
				body();
//				throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
			}
		});
	}
	
	/**
	 *  Called on each received percept.
	 */
	public void	perceptReceived(Object percept)
	{
		System.out.println(agent.getComponentIdentifier()+" percept received: "+percept);
	}
}
