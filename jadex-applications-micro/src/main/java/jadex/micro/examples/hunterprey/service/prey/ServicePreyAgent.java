package jadex.micro.examples.hunterprey.service.prey;

import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.hunterprey.service.IFood;
import jadex.micro.examples.hunterprey.service.IHunterPreyEnvironmentService;
import jadex.micro.examples.hunterprey.service.IPreyPerceivable;

import java.util.Collection;
import java.util.Random;


/**
 *  A prey implemented using the EnvSupport service interface.
 */
@Agent
@RequiredServices(@RequiredService(name="env", type=IHunterPreyEnvironmentService.class,
	binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class ServicePreyAgent
{
	//-------- attributes --------
	
	/** The environment. */
	@AgentService(name="env")
	protected IHunterPreyEnvironmentService	env;
	
	//-------- agent methods --------
	
	/**
	 *  Register the agent as a prey at startup.
	 */
	@AgentCreated
	public void	start()
	{
		env.registerPrey().addResultListener(new IntermediateDefaultResultListener<Collection<IPreyPerceivable>>()
		{
			public void intermediateResultAvailable(Collection<IPreyPerceivable> percepts)
			{
				perceptsReceived(percepts);
			}
		});
	}
	
	//-------- prey methods --------
	
	/**
	 *  Called once in each round.
	 */
	public void	perceptsReceived(Collection<IPreyPerceivable> percepts)
	{
		boolean	done	= false;
		for(IPreyPerceivable percept: percepts)
		{
			if(percept instanceof IFood)
			{
				IFood	food	= (IFood)percept;
				// At location of food -> eat
				if(food.getX()==0 && food.getY()==0)
				{
					env.eat(food);
					done	= true;
				}
			}
		}
		
		if(!done)
		{
			String[]	dirs	= new String[]{
				IHunterPreyEnvironmentService.DIRECTION_UP, IHunterPreyEnvironmentService.DIRECTION_DOWN,
				IHunterPreyEnvironmentService.DIRECTION_LEFT, IHunterPreyEnvironmentService.DIRECTION_RIGHT};
			env.move(dirs[new Random().nextInt(dirs.length)]);
		}
	}
}
