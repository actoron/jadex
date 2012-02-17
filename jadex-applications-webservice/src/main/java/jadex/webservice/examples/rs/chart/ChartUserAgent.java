package jadex.webservice.examples.rs.chart;

import jadex.extension.rs.invoke.RestServiceAgent;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Agent that searches and uses the chart service
 *  of another Jadex agent.
 */
@Agent
@RequiredServices(@RequiredService(name="chartservice", type=IChartService.class))
public class ChartUserAgent extends RestServiceAgent
{
	//-------- attributes --------
	
	@Agent
	protected MicroAgent agent;
	
	//-------- emthods --------

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JFrame f = ChartPanel.createChartFrame(agent.getExternalAccess());
				f.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						agent.killAgent();
					}
				});
			}
		});
	}
}


