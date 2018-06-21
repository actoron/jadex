package jadex.wfms.client.standard;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

import javax.swing.SwingUtilities;

@Agent
@Description("This agent implements a WfMS Client Application.")
public class StandardClientAgent
{
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public void body()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new StandardClientWindow(agent.getExternalAccess());
			}
		});
	}
}
