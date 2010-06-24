package jadex.bdi.examples.blackjack.manager;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Update the Gui when manager informs about changes.
 */
public class ManagerGuiUpdatePlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{		
		IMessageEvent msg = (IMessageEvent)getReason();
		String	content	= (String)msg.getParameter(SFipa.CONTENT).getValue();
		final String playerPlaying = content.substring(content.indexOf(':')+1, content.length());
		final ManagerFrame	gui	= (ManagerFrame)getBeliefbase().getBelief("gui").getFact();
		getLogger().info("received playerPlaying-Message " + playerPlaying);

		// AWTThread.
		EventQueue.invokeLater(new Runnable()
		{
			public void	run()
			{
				gui.setPlayerPlaying(playerPlaying);
			}
		});

		getScope().addAgentListener(new IAgentListener()
		{
			public void agentTerminated(AgentEvent ae)
			{
			}
			
			public void agentTerminating(AgentEvent ae)
			{
				closeGui();
			}
		}
		);
		
//		waitFor(IFilter.NEVER);
	}

	/**
	 *  On abort close the gui.
	 */
	public void	aborted()
	{
		closeGui();
	}
	
	/**
	 *  Close gui.
	 */
	public void closeGui()
	{
		// Use invoke later to avoid deadlocks,
		// when killAgent was issued by AWT thread.
		getExternalAccess().getBeliefbase().getBeliefFact("GUI").addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object source, Object result)
			{
				final JFrame gui = (JFrame)result;
				if(gui!=null)
				{
					gui.dispose();
				}
			}
		});
	}
}
