package jadex.bdiv3.tutorial.stamp;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.IComponentIdentifier;
import jadex.micro.annotation.Agent;


@Agent(type = BDIAgentFactory.TYPE)
@Goals(@Goal(clazz = StampGoal.class, publish = @Publish(type = IStampService.class)))
public class StampBDI
{
	@Plan(trigger = @Trigger(goals = StampGoal.class))
	public void stamp(IComponentIdentifier wp, String text)
	{
		// transport work piece to stamp and stamp with text
		System.out.println("Stamped workpiece: " + wp + " with text: " + text);
	}
}
