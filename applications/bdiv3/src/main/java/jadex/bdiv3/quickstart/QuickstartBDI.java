package jadex.bdiv3.quickstart;

import java.awt.MouseInfo;

import javax.swing.JOptionPane;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

/**
 *  A simple BDI agent that likes to have the mouse in the upper left corner.
 */
@Agent
public class QuickstartBDI
{
	/** The belief automatically updates the field to the current mouse location every 100 milliseconds. */
	@Belief(updaterate=100)
	int x	= MouseInfo.getPointerInfo().getLocation().x;
	
	/** The belief automatically updates the field to the current mouse location every 100 milliseconds. */
	@Belief(updaterate=100)
	int y	= MouseInfo.getPointerInfo().getLocation().y;

	/**
	 *  This goal maintains the condition that the mouse be in the upper left corner,
	 *  i.e. starts executing plans, whenever the condition is violated.
	 *  Recur means that the goal is not dropped, when no more plans are available,
	 *  instead the agent waits for the condition to become true again.
	 */
	@Goal(recur=true)
	class InUpperLeftCorner
	{
		/**
		 *  The maintain condition describes what the agent would like to be true.
		 *  The beliefs describe, on which changes the condition needs to be reevaluated. 
		 */
		@GoalMaintainCondition(beliefs={"x","y"})
		boolean isInUpperLeftCorner()
		{
			return x<200 && y<200;
		}
	}
	
	/**
	 *  The plans responds to the goal being violated and shows a dialog,
	 *  that asks the user to move the mouse.
	 */
	@Plan(trigger=@Trigger(goals=InUpperLeftCorner.class))
	void moveToCorner()
	{
		JOptionPane.showMessageDialog(null, "Please move the mouse to the upper left corner.");
	}
	
	/**
	 *  On startup, the agent should adopt an instance of the goal.
	 */
	@AgentCreated
	public void start(IInternalAccess self)
	{
		self.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new InUpperLeftCorner());
	}
}
