package jadex.micro.examples.hunterprey.service.prey;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.hunterprey.service.IHunterPreyEnvironmentService;


/**
 *  A prey implemented using the EnvSupport service interface.
 */
@Agent
@RequiredServices(@RequiredService(name="env", type=IHunterPreyEnvironmentService.class, scope=RequiredService.SCOPE_PLATFORM))
public class ServicePreyAgent
{
//	//-------- attributes --------
//	
//	/** The environment. */
//	@AgentService(name="env")
//	protected IHunterPreyEnvironmentService	env;
//	
//	//-------- agent methods --------
//	
//	/**
//	 *  Register the agent as a prey at startup.
//	 */
//	@AgentCreated
//	public void	start()
//	{
//		env.registerPrey().addResultListener(new IntermediateDefaultResultListener<Collection<IPreyPerceivable>>()
//		{
//			public void intermediateResultAvailable(Collection<IPreyPerceivable> percepts)
//			{
//				perceptsReceived(percepts);
//			}
//		});
//	}
//	
//	//-------- prey methods --------
//	
//	/**
//	 *  Called once in each round.
//	 */
//	public void	perceptsReceived(Collection<IPreyPerceivable> percepts)
//	{
//		boolean	done	= false;
//		for(IPreyPerceivable percept: percepts)
//		{
//			if(percept instanceof IFood)
//			{
//				IFood	food	= (IFood)percept;
//				// At location of food -> eat
//				if(food.getX()==0 && food.getY()==0)
//				{
//					env.eat(food);
//					done	= true;
//				}
//			}
//		}
//		
//		if(!done)
//		{
//			String[]	dirs	= new String[]{
//				IHunterPreyEnvironmentService.DIRECTION_UP, IHunterPreyEnvironmentService.DIRECTION_DOWN,
//				IHunterPreyEnvironmentService.DIRECTION_LEFT, IHunterPreyEnvironmentService.DIRECTION_RIGHT};
//			env.move(dirs[new Random().nextInt(dirs.length)]);
//		}
//	}
}
