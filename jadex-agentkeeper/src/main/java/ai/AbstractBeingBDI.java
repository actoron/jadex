package ai;


import ai.base.MoveToNextSectorPosition;
import ai.base.MoveToSectorLocationPlan;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.rules.eca.annotations.Event;


/**
 * Abstract BDI-Agent holds all the similarity (Beliefs, Goals, Plans) from all
 * Beings. A "Being" can be any Creature, Hero or Imp(Worker)
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
@Agent
@Plans({

@Plan(trigger = @Trigger(goals = AbstractBeingBDI.AchieveMoveToSector.class), body = @Body(MoveToSectorLocationPlan.class)),
		@Plan(trigger = @Trigger(goals = AbstractBeingBDI.PerformMoveToNextSector.class), body = @Body(MoveToNextSectorPosition.class))

})
public class AbstractBeingBDI
{
	/** The bdi agent. Automatically injected */
	@Agent
	protected BDIAgent		agent;

	/** The virtual environment of the Dungeon. */
	@Belief
	protected Grid2D		environment;

	/** The position of the "Being". */
	@Belief
	protected Vector2Double	my_position;

	/** The speed of the "Being". */
	@Belief
	protected float			my_speed	= 1;


	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{

		environment = null;
		final Future<IExtensionInstance> ret = new Future<IExtensionInstance>();

		IFuture<IExtensionInstance> test = agent.getParentAccess().getExtension("mygc2dspace");
		test.addResultListener(new DelegationResultListener<IExtensionInstance>(ret));

		// AbstractEnvironmentSpace space = test.;

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
		 * The goal is achieved when the position of the cleaner is near to the
		 * target position.
		 */
		@GoalTargetCondition(events = "my_position")
		public boolean checkTarget()
		{
			// TODO: Check that
			return 1 > my_position.getDistance(target).getAsFloat();
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
	 * The goal is used to move to Sector next to my Position ( on the Grid ).
	 */
	@Goal
	public class PerformMoveToNextSector
	{
		/** The target. */
		protected Vector2Int	next;

		/**
		 * Create a new goal.
		 * 
		 * @param target The target.
		 */
		public PerformMoveToNextSector(Vector2Int target)
		{
			this.next = next;
		}

		/**
		 * Get the target.
		 * 
		 * @return The target.
		 */
		public Vector2Int getNext()
		{
			return this.next;
		}
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


}
