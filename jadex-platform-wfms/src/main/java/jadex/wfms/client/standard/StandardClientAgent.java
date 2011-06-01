package jadex.wfms.client.standard;

import javax.swing.SwingUtilities;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;

@Description("This agent implements a WfMS Client Application.")
public class StandardClientAgent extends MicroAgent
{
	public void executeBody()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new StandardClientWindow(getExternalAccess());
			}
		});
	}
}
