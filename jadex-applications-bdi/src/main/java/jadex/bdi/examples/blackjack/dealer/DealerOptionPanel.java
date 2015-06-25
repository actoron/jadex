package jadex.bdi.examples.blackjack.dealer;

import jadex.bdi.examples.blackjack.GameStatistics;
import jadex.bdi.examples.blackjack.gui.GUIImageLoader;
import jadex.bdi.examples.blackjack.gui.StatisticGraph;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
	This class is used for managing the progressBar in the upper right 
	corner of the GUI. On the one hand, the progressBar is used by the 
	GUIUpdatePlan to indicate on the GUI that something's going on, 
	on the other hand, this progressBar is used to throw ActionEvents 
	to the GUIUpdatePlan when something happens, for example when the time 
	to wait is over.
	There's nothing of interest in here, if you're looking for 
	agent- or jadex-specific code, just pure javax.swing.
*/
 
public class DealerOptionPanel	extends JPanel	//	implements ActionListener, ChangeListener
{
	//-------- constants ---------
	
	/** upper bound-value of the two spinners */
	private final static int MAX_SECONDS = 60;
		
	//-------- attributes --------

	/** The agent access. */
	protected IExternalAccess	agent;
	
	/** the progressbar */
	private JProgressBar progressBar;
	
	/** the spinner, that adjusts the seconds to wait until a game-restart */
	private JSpinner restartWaitSpinner;
	
	/** the spinner, that adjusts the seconds to wait until dealer or 
	    player may draw the next card */
	private JSpinner cardWaitSpinner;
	
	/** singleStep-checkBox, if 'selected' dealer and players receive 
	    their cards after pressing the single-step button. */
	private JCheckBox singleStepCheckBox;
	
	/** Step-Button for stepping forward in 'single step mode'*/
	private JButton stepButton;
		
	/** this label shows, what the dealer is waiting for, i.e. player-arrivals
	    game-restart and so on */
	private JLabel messageLabel;

	//-------- constructors --------
	
	/**
	 *  Create a new option panel.
	 */
	public DealerOptionPanel(final IExternalAccess agent, final DealerFrame frame)
	{
		super(new BorderLayout());

		this.agent	= agent;
		
		this.setBorder(BorderFactory.createTitledBorder(" Options "));
		this.setBackground(Color.WHITE);

		messageLabel = new JLabel("Welcome to Blackjack!");

		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(Color.WHITE);
		messagePanel.add(messageLabel);

		singleStepCheckBox = new JCheckBox("Single Step");
		singleStepCheckBox.setBackground(Color.WHITE);
		singleStepCheckBox.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final Boolean b = Boolean.valueOf(singleStepCheckBox.isSelected());
//				agent.getBeliefbase().setBeliefFact("singleStepMode", new Boolean(singleStepCheckBox.isSelected()));

				// Change state of button and trigger next step, if agent currently waiting.
				if(singleStepCheckBox.isSelected())
				{
					stepButton.setEnabled(true);
				}
				else
				{
					stepButton.doClick();
					stepButton.setEnabled(false);
				}
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("singleStep")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						bia.getBeliefbase().getBelief("singleStepMode").setFact(b);
						return IFuture.DONE;
					}
				});
			}
		});

		JButton statisticButton = new JButton("statistic");
		statisticButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("statistics")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						final GameStatistics stats = (GameStatistics)bia.getBeliefbase().getBelief("statistics").getFact();
				
						if(stats!=null)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									JFrame statsgui	= new JFrame("Blackjack Statistics");
									
									// set the icon to be displayed for the frame
									statsgui.setIconImage(GUIImageLoader.getImage("statistics").getImage());				
									statsgui.getContentPane().add(new StatisticGraph(stats));
									statsgui.setSize(400, 300);
									statsgui.setLocation(SGUI.calculateMiddlePosition(statsgui));
									statsgui.setVisible(true);
									frame.addChildWindow(statsgui);
								}
							});
						}
						return IFuture.DONE;
					}
				});
//				agent.getBeliefbase().getBeliefFact("statistics").addResultListener(new SwingDefaultResultListener(DealerOptionPanel.this)
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						GameStatistics stats = (GameStatistics)result;
//						
//						if(stats!=null)
//						{
//							JFrame statsgui	= new JFrame("Blackjack Statistics");
//	
//							// set the icon to be displayed for the frame
//							statsgui.setIconImage(GUIImageLoader.getImage("statistics").getImage());				
//							statsgui.getContentPane().add(new StatisticGraph(stats));
//							statsgui.setSize(400, 300);
//							statsgui.setLocation(SGUI.calculateMiddlePosition(statsgui));
//							statsgui.setVisible(true);
//							frame.addChildWindow(statsgui);
//						}
//					}
//				});
			}
		});
		
		stepButton = new JButton("step");
		stepButton.setEnabled(false);
		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("step")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						IInternalEvent ie = bia.getEventbase().createInternalEvent("step");
						bia.getEventbase().dispatchInternalEvent(ie);
						return IFuture.DONE;
					}
				});
//				agent.createInternalEvent("step").addResultListener(new SwingDefaultResultListener(DealerOptionPanel.this)
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						agent.dispatchInternalEvent((IEAInternalEvent)result);
//					}
//				});
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(statisticButton);
		buttonPanel.add(stepButton);
		buttonPanel.add(singleStepCheckBox);

		cardWaitSpinner = new JSpinner();
		cardWaitSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final Integer value = Integer.valueOf(Math.max(0, Math.min(MAX_SECONDS,
					((Integer)cardWaitSpinner.getValue()).intValue())));
				cardWaitSpinner.setValue(value);
				
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("stepDelay")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						bia.getBeliefbase().getBelief("stepdelay").setFact(value);
						return IFuture.DONE;
					}
				});
//				agent.getBeliefbase().setBeliefFact("stepdelay", value);
			}
		});
		
		restartWaitSpinner = new JSpinner();
		restartWaitSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final int value = Math.max(0, Math.min(MAX_SECONDS,
					((Integer)restartWaitSpinner.getValue()).intValue()));
				restartWaitSpinner.setValue(Integer.valueOf(value));
				
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("restartDelay")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
						bia.getBeliefbase().getBelief("restartdelay").setFact(Integer.valueOf(value));
						return IFuture.DONE;
					}
				});
				
//				agent.getBeliefbase().setBeliefFact("restartdelay", Integer.valueOf(value));
				
				// todo
//				IGoal[]	play	= agent.getGoalbase().getGoals("play_game");
//				if(play.length>0)
//				{
//					play[0].setRetryDelay(1000*value);
//				}
			}
		});
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setBackground(Color.WHITE);
		spinnerPanel.add(new JLabel("restart"));
		spinnerPanel.add(restartWaitSpinner);
		
		spinnerPanel.add(new JLabel("sec."));
		spinnerPanel.add(cardWaitSpinner);
		spinnerPanel.add(new JLabel("carddraw"));

		progressBar = new JProgressBar(0, 3);//initialRestartWaitSeconds);
		
		progressBar.setValue(0);
		progressBar.setBackground(Color.WHITE);
		progressBar.setStringPainted(true);
		progressBar.setString("");

		JPanel progressPanel = new JPanel(new GridLayout(2,1));
		progressPanel.setBackground(Color.WHITE);
		progressPanel.add(progressBar);
		progressPanel.add(spinnerPanel);

		this.add(messagePanel, BorderLayout.NORTH);
		this.add(progressPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("ref")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
				Boolean sel = (Boolean)bia.getBeliefbase().getBelief("singleStepMode").getFact();
				Object sd = bia.getBeliefbase().getBelief("stepdelay").getFact();
				Object rd = bia.getBeliefbase().getBelief("restartdelay").getFact();
				singleStepCheckBox.setSelected(sel.booleanValue());
				cardWaitSpinner.setValue(sd);
				restartWaitSpinner.setValue(rd);
				return IFuture.DONE;
			}
		});
		
//		agent.getBeliefbase().getBeliefFact("singleStepMode").addResultListener(new SwingDefaultResultListener(DealerOptionPanel.this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				singleStepCheckBox.setSelected(((Boolean)result).booleanValue());
//			}
//		});
//		agent.getBeliefbase().getBeliefFact("stepdelay").addResultListener(new SwingDefaultResultListener(DealerOptionPanel.this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				cardWaitSpinner.setValue(result);
//			}
//		});
//		agent.getBeliefbase().getBeliefFact("restartdelay").addResultListener(new SwingDefaultResultListener(DealerOptionPanel.this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				restartWaitSpinner.setValue(result);
//			}
//		});
//				singleStepCheckBox.setSelected(((Boolean)agent.getBeliefbase().getBelief("singleStepMode").getFact()).booleanValue());
//				cardWaitSpinner.setValue(agent.getBeliefbase().getBelief("stepdelay").getFact());
//				restartWaitSpinner.setValue((Integer)agent.getBeliefbase().getBelief("restartdelay").getFact());
	}

	//-------- methods --------

	/**
		This method sets the progressBar's mode.
		If indeterminateMode==true, a blue box is scamping from the left to the 
		right side of the progressBar until the indeterminateMode is set to 'false' 
		again. if set to 'false' the progressBars shows the time left until the next
		event is fired.
		@param indeterminateMode Sets the progressBar's mode.
	*/	
	public void setIndeterminate(boolean indeterminateMode)
	{
		progressBar.setIndeterminate(indeterminateMode);
		
//		if (indeterminateMode == false)
//		{
//			progressBarTimer.start();
//		}
//		else
//		{
//			progressBarTimer.stop();
//		}
	}
	
//	/**
//		With this method one can change the message showing what the 
//		dealer is waiting for, this method is called from inside this class
//		as well as by the GUIUpdatePlan.
//		@param message String to show above the progressBar.
//	*/		
//	public void setText(String message)
//	{
//		messageLabel.setText(message);
//		JPanel progressPanel = (JPanel)this.getComponent(0);
//		progressPanel.remove(messageLabel);
//		progressPanel.add(messageLabel,BorderLayout.NORTH);
//	}	

//	/**
//		This method has to be implemented because the Timer, which works
//		together with the progressBar, calls this method every second,
//		so the progressBar can be updated or throws an event 
//		(actionEvent-parameter of this class' constructor)
//		after a given amount of time.
//		@param e ActionEvent created by the Timer or one of the buttons.
//	*/
//	public void actionPerformed(ActionEvent e) 
//	{
//		IExpression	exp	= agent.getExpressionbase().getExpression("turnplayer");
//		Player	player	= (Player)exp.getValue();
//		if(player!=null)
//			messageLabel.setText("Player's turn: "+player.getName());
//		else
//			messageLabel.setText("Welcome to Blackjack!");
//		
//		// if progressBar is NOT in indeterminate-Mode update the progressBar 
//		// and decrease the seconds to wait until the next Event is thrown to 
//		// the GUIUpdatePlan
//		if (progressBar.isIndeterminate() == false)
//		{
//			if(e.getSource() != progressBarTimer)
//				System.out.println("Wrong event: "+e);
//			progressBar.setString(seconds + " seconds");
//			progressBar.setValue(((Integer)restartWaitSpinner.getValue()).intValue() - seconds);
//			seconds--;
//		}
//
//		if ((e.getSource() == cardRequestTimer) || (e.getSource() == stepButton))
//		{
//			stepButton.setEnabled(false);
//			this.setText("*** Waiting / Thinking ***");
//			listener.actionPerformed(new ActionEvent(this, 0, "cardRequestPerformed"));
//			
//			if (cardRequestTimer != null)
//			{
//				cardRequestTimer.stop();
//				cardRequestTimer = null;
//			}
//		}		
//		
//		if (seconds < 0)
//		{
//			// time to wait is over, generate an ActionEvent and pass it to
//			// the listener (GUIUpdatePlan)
//
//			this.setIndeterminate(true);
//			
//			progressBar.setString("");
//
//			this.setText("*** Waiting / Thinking ***");
//			listener.actionPerformed(new ActionEvent(this, 0, actionCommand));
//		}		
//		
//		if (e.getSource() == statisticButton)
//		{
//			// User pressed the statistic-button so generate an statistic-event and inform the 
//			// listener (GUIUpdatePlan).	
//			listener.actionPerformed(new ActionEvent(this, 0, "statistic"));
//		}
//		
//	} // end of actionPerformed
}	
