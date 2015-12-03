package jadex.bdi.examples.blackjack.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import jadex.bdi.examples.blackjack.GameState;
import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.gui.GUIImageLoader;
import jadex.bdi.examples.blackjack.gui.GameStateFrame;
import jadex.bdi.examples.blackjack.gui.PlayerPanel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  The human player interface for playing blackjack.
 */
public class HumanPlayerInterface extends GameStateFrame
{
	//-------- attributes --------

	/** The property change support. */
	protected SimplePropertyChangeSupport pcs;
	
	/** The bet. */
	protected int bet;
	
	/** The draw state. */
	protected boolean drawcard;


	//-------- constructors --------

	/**
	 *  Create a new human player interface.
	 */
	public HumanPlayerInterface(Player player, GameState gamestate, final IExternalAccess agent)
	{
		super(gamestate, null);
		setControlPanel(new HumanPlayerControlPanel(player, gamestate));
		pcs = new SimplePropertyChangeSupport(this);

		// Kill agent on exit.
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});

		// Close window on agent death.
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("human")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						HumanPlayerInterface.this.dispose();
					}
				}));
				return IFuture.DONE;
			}
		});
//		agent.addAgentListener(new IAgentListener()
//		{
//			public void agentTerminating(AgentEvent e)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						HumanPlayerInterface.this.dispose();
//					}
//				});
//			}
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});

		this.setTitle("Human player: "+player.getName());
		this.pack();
		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);
	}

	//-------- property methods --------

	/**
	 *  Add a PropertyChangeListener to the listener list.
	 *  The listener is registered for all properties.
	 *  @param listener  The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 *  Remove a PropertyChangeListener from the listener list.
	 *  This removes a PropertyChangeListener that was registered
	 *  for all properties.
	 *  @param listener  The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 *  Get the bet.
	 */
	public int	getBet()
	{
		return bet;
	}

	/**
	 *  Set the bet.
	 *  @param bet The bet.
	 */
	public void	setBet(int bet)
	{
		this.bet = bet;
		pcs.firePropertyChange("bet", null, Integer.valueOf(bet));
	}
	
	/**
	 *  Does the player want another card.
	 *  @return True, if another card.
	 */
	public boolean isDrawCard()
	{
		return drawcard;
	}

	/**
	 *  Set the draw card decision.
	 *  @param drawcard
	 */
	public void setDrawCard(boolean drawcard)
	{
		this.drawcard = drawcard;
		pcs.firePropertyChange("drawCard", null, Boolean.valueOf(drawcard));
	}

	/**
	 *  The control planel for interaction a human player.
	 */
	public class HumanPlayerControlPanel extends JPanel
	{
		/** The bet. */
		protected JSpinner betsp;

		/** The button for placing a bet. */
		protected JButton betbut;

		/** The bet progress bar. */
		protected JProgressBar betprogress;

		/** The draw card decision. */
		protected JButton draw;

		/** The stop button. */
		protected JButton stop;

		/** The draw card progress bar. */
		protected JProgressBar drawprogress;

		/** The timer. */
		protected Timer timer;

		/**
		 *  Create a new panel.
		 */
		public HumanPlayerControlPanel(Player player, GameState gamestate)
		{
			this.setLayout(new BorderLayout());

			betsp = new JSpinner(new SpinnerNumberModel(10,1,100,1));
			betbut = new JButton(GUIImageLoader.getImage("bet"));
			betbut.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					makeBet();
				}
			});
			betbut.setEnabled(false);
			betprogress = new JProgressBar(0, 5); // todo: use variable

			draw = new JButton(GUIImageLoader.getImage("hit"));
			draw.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//System.out.println("Human player chose to draw another card");
					setDrawCard(true);
				}
			});
			draw.setEnabled(false);
			stop = new JButton(GUIImageLoader.getImage("stand"));
			stop.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//System.out.println("Human player chose to stop.");
					setDrawCard(false);
				}
			});
			stop.setEnabled(false);
			drawprogress = new JProgressBar(0, 5); // todo: use variable

			JPanel control = new JPanel(new GridBagLayout());
			control.add(betprogress, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
			control.add(betsp, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
			control.add(betbut, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

			control.add(drawprogress, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
			control.add(draw, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
			control.add(stop, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

			/*Dimension d = new Dimension(80,20);
			betprogress.setMaximumSize(d);
			betprogress.setPreferredSize(d);
			drawprogress.setMaximumSize(d);
			drawprogress.setPreferredSize(d);*/
			Dimension d = new Dimension(50,30);
			betsp.setMaximumSize(d);
			betsp.setPreferredSize(d);
			betbut.setMinimumSize(d);
			betbut.setPreferredSize(d);
			draw.setMinimumSize(d);
			draw.setPreferredSize(d);
			stop.setMinimumSize(d);
			stop.setPreferredSize(d);

			this.add("Center", new PlayerPanel(player));
			this.add("South", control);
		}

		/**
		 *  Make a bet.
		 */
		protected void makeBet()
		{
			try
			{
				setBet(((Integer)betsp.getValue()).intValue());
				//System.out.println("Human player made his bet: "+bettf.getText());
			}
			catch(NumberFormatException ne)
			{
				// do nothing.
			}
		}

		/**
		 *  Tell the human that his bet is required.
		 */
		public void enableBid()
		{
			//bettf.setBackground(Color.GREEN);
			betbut.setEnabled(true);
			//bettf.setEditable(true);
			betprogress.setValue(5); // todo: use variable timeout
			betprogress.setString(betprogress.getValue()+" sec left");
			betprogress.setStringPainted(true);
			timer = new Timer(1000, null);
			ActionListener al = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int time = betprogress.getValue()-1;
					betprogress.setValue(betprogress.getValue()-1);
					betprogress.setString(time+" sec left");
					if(betprogress.getValue()==0)
					{
						timer.stop();
						makeBet();
					}
				}
			};
			timer.addActionListener(al);
			timer.start();
		}

		/**
		 *  Disable bid.
		 */
		public void disableBid()
		{
			//bettf.setBackground(Color.RED);
			betbut.setEnabled(false);
			//bettf.setEditable(false);
			timer.stop();
			betprogress.setValue(0);
			betprogress.setStringPainted(false);
		}

		/**
		 *  Enable drawing a card.
		 */
		public void enableDrawCard()
		{
			//draw.setBackground(Color.GREEN);
			//stop.setBackground(Color.GREEN);
			draw.setEnabled(true);
			stop.setEnabled(true);
			drawprogress.setValue(5); // todo: use variable timeout
			drawprogress.setString(drawprogress.getValue()+" sec left");
			drawprogress.setStringPainted(true);
			timer = new Timer(1000, null);
			ActionListener al = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int time = drawprogress.getValue()-1;
					drawprogress.setString(time+" sec left");
					drawprogress.setValue(time);
					if(drawprogress.getValue()==0)
						timer.stop();
				}
			};
			timer.addActionListener(al);
			timer.start();
		}

		/**
		 *  Disable to draw a card.
		 */
		public void disableDrawCard()
		{
			//draw.setBackground(Color.RED);
			//stop.setBackground(Color.RED);
			draw.setEnabled(false);
			stop.setEnabled(false);
			timer.stop();
			drawprogress.setValue(0);
			drawprogress.setStringPainted(false);
		}
	}
}