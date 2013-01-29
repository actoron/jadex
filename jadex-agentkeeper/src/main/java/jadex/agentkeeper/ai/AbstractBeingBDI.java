package jadex.agentkeeper.ai;



import jadex.agentkeeper.ai.base.IdlePlan;
import jadex.agentkeeper.ai.base.MoveToGridSectorPlan;
import jadex.agentkeeper.ai.base.PatrolPlan;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;


/**
 * Abstract BDI-Agent holds all the similarity (Beliefs, Goals, Plans) from all
 * Beings. A "Being" can be any Creature, Hero or Imp(Worker)
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
@Agent
@Plans({

@Plan(trigger = @Trigger(goals = AbstractBeingBDI.AchieveMoveToSector.class), body = @Body(MoveToGridSectorPlan.class)),
@Plan(trigger=@Trigger(goals=AbstractBeingBDI.PerformIdle.class), body=@Body(PatrolPlan.class)),
@Plan(trigger=@Trigger(goals=AbstractBeingBDI.PerformIdle.class), body=@Body(IdlePlan.class))


})
public abstract class AbstractBeingBDI
{
	/** The bdi agent. Automatically injected */
	@Agent
	protected BDIAgent		agent;

	/** The virtual environment of the Dungeon. */
	protected Grid2D		environment;
	
	/** The virtual SpaceObject of the "Being" in the virtual environment. */
	protected ISpaceObject mySpaceObject;

	/** The position of the "Being". */
	@Belief
	protected Vector2Double	my_position;

	public Vector2Double getUpdatedPosition()
	{
		my_position = (Vector2Double)mySpaceObject.getProperty(Space2D.PROPERTY_POSITION);
		return my_position;
	}
	
	/** The speed of the "Being". */
	protected float			my_speed	= 1;

	/**
	 *  Initialize the agent.
	 *  Called at startup.
	 */
	@AgentCreated
	public IFuture<Void>	init()
	{
		final Future<Void>	ret	= new Future<Void>();
		agent.getParentAccess().getExtension("mygc2dspace")
			.addResultListener(new ExceptionDelegationResultListener<IExtensionInstance, Void>(ret)
		{
			public void customResultAvailable(IExtensionInstance ext)
			{
				environment	= (Grid2D)ext;
				mySpaceObject = environment.getAvatar(agent.getComponentDescription(), agent.getModel().getFullName());
				my_position = (Vector2Double)mySpaceObject.getProperty(Space2D.PROPERTY_POSITION);
				ret.setResult(null);
			}
		});
		return ret;
	}

	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		agent.dispatchTopLevelGoal(new PerformIdle());
//		agent.dispatchTopLevelGoal(new AchieveMoveToSector(new Vector2Int(9,18)));
	}

	/**
	 * The goal is used to move to a specific location ( on the Grid ).
	 */
	@Goal
	public class AchieveMoveToSector
	{
		/** The target. */
		protected Vector2Int	target;

		/**
		 * Create a new goal.
		 * 
		 * @param target The target.
		 */
		public AchieveMoveToSector(Vector2Int target)
		{
			this.target = target;
		}

		/**
		 * The goal is achieved when the position of the cleaner is on the
		 * target sector position.
		 */
		@GoalTargetCondition(events = "my_position")
		public boolean checkTarget()
		{
			boolean ret = my_position.equals(target);
			return ret;
		}

		/**
		 * Get the target.
		 * 
		 * @return The target.
		 */
		public Vector2Int getTarget()
		{
			return this.target;
		}
	}
	
	/**
	 *  Goal that lets the Being perform idle.
	 */
	@Goal(excludemode=MGoal.EXCLUDE_NEVER, succeedonpassed=false, randomselection=true)
	public class PerformIdle
	{
		
	}


	/**
	 * @return the my_position
	 */
	public Vector2Double getMyPosition()
	{
		return my_position;
	}


	/**
	 * @param my_position the my_position to set
	 */
	public void setMyPosition(Vector2Double my_position)
	{
		this.my_position = my_position;
	}


	/**
	 * @return the my_speed
	 */
	public float getMySpeed()
	{
		return my_speed;
	}


	/**
	 * @param my_speed the my_speed to set
	 */
	public void setMySpeed(float my_speed)
	{
		this.my_speed = my_speed;
	}


	/**
	 * Get the agent.
	 * 
	 * @return The agent.
	 */
	public BDIAgent getAgent()
	{
		return agent;
	}

	/**
	 * @return the environment
	 */
	public Grid2D getEnvironment()
	{
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(Grid2D environment)
	{
		this.environment = environment;
	}

	public ISpaceObject getMySpaceObject()
	{
		return mySpaceObject;
	}

	public void setMySpaceObject(ISpaceObject mySpaceObject)
	{
		this.mySpaceObject = mySpaceObject;
	}


}
