package jadex.bdi.examples.blackjack.manager;

import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.bridge.fipa.SFipa;
import jadex.commons.future.IFuture;
import jadex.xml.annotation.XMLClassname;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Update the Gui when manager informs about changes.
 */
public class ManagerGuiUpdatePlan extends Plan
{
	protected ManagerFrame gui;
	
	/**
	 *  Execute a plan.
	 */
	public void body()
	{		
		IMessageEvent msg = (IMessageEvent)getReason();
		String	content	= (String)msg.getParameter(SFipa.CONTENT).getValue();
		final String playerPlaying = content.substring(content.indexOf(':')+1, content.length());
		gui	= (ManagerFrame)getBeliefbase().getBelief("gui").getFact();
		getLogger().info("received playerPlaying-Message " + playerPlaying);

		// AWTThread.
		EventQueue.invokeLater(new Runnable()
		{
			public void	run()
			{
				gui.setPlayerPlaying(playerPlaying);
			}
		});

		getScope().addComponentListener(new TerminationAdapter()
		{
			public void componentTerminated()
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
		
		getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			@XMLClassname("guidispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				final JFrame gui = (JFrame)bia.getBeliefbase().getBelief("GUI").getFact();
				if(gui!=null)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							gui.dispose();	
						}
					});
				}
				return IFuture.DONE;
			}
		});
//		getExternalAccess().getBeliefbase().getBeliefFact("GUI").addResultListener(new SwingDefaultResultListener(gui)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				final JFrame gui = (JFrame)result;
//				if(gui!=null)
//				{
//					gui.dispose();
//				}
//			}
//		});
	}
}
