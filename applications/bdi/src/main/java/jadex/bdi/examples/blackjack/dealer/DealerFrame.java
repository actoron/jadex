package jadex.bdi.examples.blackjack.dealer;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bdi.examples.blackjack.Dealer;
import jadex.bdi.examples.blackjack.GameState;
import jadex.bdi.examples.blackjack.gui.GUIImageLoader;
import jadex.bdi.examples.blackjack.gui.GameStateFrame;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 * This GUI gives an overview of the ongoings during the game.
 * It shows different panels representing the 'internal states'
 * (the cards, accounts and bets) of the dealer- and all the
 * player-agents as well as one special panel containing a
 * progressBar and a few buttons (the 'OptionPanel').
 * But this class is not just a viewing-component, but also an
 * agent-plan and therefor it contains some jadex-specific code,
 * which is perhaps worth looking at.
 */
public class DealerFrame extends GameStateFrame
{
	//-------- attributes --------

	/** The agent access object. */
	protected IExternalAccess	agent;
	
	/** child windows (e.g. statistics). */
	protected Set	children;

	//-------- constructors --------
	
	/**
	 * Creates a new instance of the dealer frame.
	 * Here, the GUI is build up for the first time, all
	 * panels are instantiated and shown on the screen.
	 */
	public DealerFrame(final Dealer me, final IExternalAccess agent)
	{
		super(null, null);
		
		this.agent	= agent;
		this.children	= new HashSet();

		// Show Jadex version information.
		String	title	= "Blackjack Dealer";
			//+ Configuration.getConfiguration().getReleaseNumber()
			//+ " (" + Configuration.getConfiguration().getReleaseDate() + ")";

		//Create the 'Main'-Window and the contentPane
		setTitle(title);
		//setResizable(false);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				DealerFrame.this.agent.killComponent();
			}
		});
		// set the icon to be displayed for the frame
		setIconImage(GUIImageLoader.getImage("heart_small_d").getImage());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					agent.scheduleStep(new IComponentStep<Void>()
					{
						@Classname("gamestate")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							IBDIXAgentFeature bia = ia.getFeature(IBDIXAgentFeature.class);
							final GameState gs = (GameState)bia.getBeliefbase().getBelief("gamestate").getFact();
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									DealerFrame.this.setGameState(gs);
								}
							});
							return IFuture.DONE;
						}
					});
				}
				catch(ComponentTerminatedException cte)
				{
					
				}
//				agent.getBeliefbase().getBeliefFact("gamestate").addResultListener(new SwingDefaultResultListener(DealerFrame.this)
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						DealerFrame.this.setGameState((GameState)result);
//					}
//				});
			}
		});
		setControlPanel(new DealerOptionPanel(agent, DealerFrame.this));

		/*getContentPane().setLayout(new GridLayout(0, 2));

		// initialise the JPanels for the Dealer and the progressBar
		PlayerPanel	dealerPanel = new PlayerPanel(me);

		// add these JPanels as the first-row to the contentPane
		getContentPane().add(dealerPanel);
		getContentPane().add(new DealerOptionPanel(agent, DealerFrame.this));*/

		// display the gui on the screen
		pack();
		setLocation(SGUI.calculateMiddlePosition(DealerFrame.this));
		setVisible(true);
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						DealerFrame.this.dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				DealerFrame.this.dispose();
			}
		});
	}

	/**
	 *  Add a child window to be disposed on exit.
	 */
	public void	addChildWindow(Window child)
	{
		children.add(child);
	}

	/**
	 *  Remove a child window to be disposed on exit.
	 */
	public void	removeChildWindow(Window child)
	{
		children.remove(child);
	}

	/**
	 *  Dispose this window and child windows.
	 */
	public void	dispose()
	{
		super.dispose();

		for(Iterator i=children.iterator(); i.hasNext(); )
			((Window)i.next()).dispose();
	}
}
