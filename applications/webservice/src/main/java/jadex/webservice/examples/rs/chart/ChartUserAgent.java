package jadex.webservice.examples.rs.chart;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.RestServiceAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that searches and uses the chart service
 *  of another Jadex agent.
 */
@Agent
@RequiredServices(@RequiredService(name="chartservice", type=IChartService.class))
public class ChartUserAgent extends RestServiceAgent
{
	//-------- attributes --------
	
	protected JFrame	f; 
	
	//-------- emthods --------

	/**
	 *  The agent init.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f = ChartPanel.createChartFrame(agent.getExternalAccess());
				f.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						agent.killComponent();
					}
				});
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called when the agent is killed.
	 */
	@AgentKilled
	public IFuture<Void>	cleanup()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f.dispose();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}


