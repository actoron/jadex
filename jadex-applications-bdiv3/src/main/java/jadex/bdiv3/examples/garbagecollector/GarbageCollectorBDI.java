package jadex.bdiv3.examples.garbagecollector;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.model.MProcessableElement;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.IEvent;

import java.util.List;

/**
 * 
 */
public class GarbageCollectorBDI
{
	@Agent
	protected BDIAgent agent;
	
	/** The environment. */
	protected Grid2D env = (Grid2D)agent.getParentAccess().getExtension("mygc2dspace").get();
	
	/** The environment. */
	protected ISpaceObject myself = env.getAvatar(agent.getComponentDescription(), agent.getModel().getFullName());

	/** The position. */
	@Belief(dynamic=true)
	protected IVector2 pos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

	/** The garbages. */
	@Belief
	protected List<ISpaceObject> garbages;
	
	
	/**
	 *  Goal for picking up a piece of waste, bringing it
	 *  to some burner and going back. A new goal is created
	 *  whenever the actual position is dirty and there is no
	 *  burner present.
	 */
	@Goal(deliberation=@Deliberation(inhibits=Check.class))
	public static class Take
	{
		/**
		 * 
		 */
		// todo: support directly factadded etc.
		@GoalCreationCondition(events="garbages")
		public static boolean checkCreate(GarbageCollectorBDI outer, ISpaceObject garbage, IEvent event)
		{
			return outer.isDirty() && outer.getEnv().getSpaceObjectsByGridPosition(outer.pos, "burner")==null;
		}
	}
	
	/**
	 *  Goal for running around on the grid and searching for garbage.
	 */
	@Goal(excludemode=MProcessableElement.EXCLUDE_NEVER)
	public class Check
	{
	}
	
	/**
	 *  Goal for going to a specified position.
	 */
	@Goal(excludemode=MProcessableElement.EXCLUDE_NEVER)
	public class Go
	{
		/** The position. */
		protected IVector2 pos;

		/**
		 *  Create a new Go. 
		 */
		public Go(IVector2 pos)
		{
			this.pos = pos;
		}

		/**
		 *  Get the pos.
		 *  @return The pos.
		 */
		public IVector2 getPosition()
		{
			return pos;
		}
	}
	
	/**
	 *  The goal for picking up waste. Tries endlessly to pick up.
	 */
	@Goal(excludemode=MProcessableElement.EXCLUDE_NEVER, retrydelay=100)
	public class Pick
	{
		/**
		 * 
		 */
		@GoalDropCondition
		public boolean checkDrop()
		{
			//!$beliefbase.is_dirty &amp;&amp; !$beliefbase.has_garbage
		}
	}
	
	/**
	 * 
	 */
	protected boolean isDirty()
	{
		return garbages.size()>0;
	}
	
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public Grid2D getEnv()
	{
		return env;
	}
}
