package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.CurrentVision;
import jadex.bdi.examples.hunterprey_classic.Prey;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *  The gui for the cleaner world example.
 *  Shows the world from the viewpoint of the environment agent.
 */
/*  @requires belief environment
 *  @requires belief roundtime
 */
public class EnvironmentGui	extends JFrame
{
	//-------- attributes --------
	
	/** The panel showing the map. */
	protected MapPanel	map;
	
	/** The round counter label. */
	protected JLabel	roundcnt;
	
	/** The panel displaying active creatures. */
	protected CreaturePanel	creatures;

	/** The panel displaying current observers. */
	protected CreaturePanel	observers;

	/** The panel displaying alltime highscores. */
	protected CreaturePanel	highscore;
	
	//-------- constructors --------

	/**
	 *  Create a new gui plan.
	 */
	public EnvironmentGui(final IExternalAccess agent)
	{
		super(agent.getComponentIdentifier().getName());
		
		// Map panel.
		this.map	= new MapPanel();
		map.setMinimumSize(new Dimension(300, 300));
		map.setPreferredSize(new Dimension(600, 600));

		JPanel options = createOptionsPanel(agent);

		this.creatures = new CreaturePanel();
		this.observers = new CreaturePanel(true);
		this.highscore = new CreaturePanel();

		JTabbedPane tp = new JTabbedPane();
		//tp.addTab("Control", options);
		tp.addTab("Living creatures", creatures);
		tp.addTab("Observers", observers);
		tp.addTab("Highscore", highscore);
		tp.setMinimumSize(new Dimension(0, 0));
		tp.setPreferredSize(new Dimension(243, 0));

		JPanel	east	= new JPanel(new BorderLayout());
		east.add(BorderLayout.CENTER, tp);
		east.add(BorderLayout.SOUTH, options);

		// Show the gui.
		JSplitPane	split	= new JSplitPane();
		split.setLeftComponent(map);
		split.setRightComponent(east);
		split.setResizeWeight(1);

		getContentPane().add(BorderLayout.CENTER, split);
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);

		enableGuiUpdate(agent);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
//				// todo: move to end goal
//				Environment en = (Environment)agent.getBeliefbase().getBelief("environment").getFact();
//				Creature[] creatures = en.getCreatures();
//				for(int i=0; i<creatures.length; i++)
//				{
//					try
//					{
////						System.out.println(creatures[i].getAID());
//						IGoal kg = agent.createGoal("cms_destroy_component");
//						kg.getParameter("componentidentifier").setValue(creatures[i].getAID());
//						agent.dispatchTopLevelGoalAndWait(kg);
//					}
//					catch(GoalFailureException gfe) 
//					{
//					}
//				}
//				agent.killAgent();
				
				dispose();
				
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("end")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
						IGoal goal = capa.getGoalbase().createGoal("end_agent");
						capa.getGoalbase().dispatchTopLevelGoal(goal);
						return IFuture.DONE;
					}
				});
//				agent.createGoal("end_agent").addResultListener(new SwingDefaultResultListener(EnvironmentGui.this)
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						agent.dispatchTopLevelGoal((IEAGoal)result);
//					}
//				});
				
//				IGoal eg = agent.createGoal("end_agent");
//				agent.dispatchTopLevelGoal(eg);
			}
		});
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								EnvironmentGui.this.dispose();
//							}
//						});
//					}
//				});
				
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						EnvironmentGui.this.dispose();
					}
				}));
				return IFuture.DONE;
			}
		});
		
//		agent.addAgentListener(new IAgentListener()
//		{
//			public void agentTerminating(AgentEvent ae)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						EnvironmentGui.this.dispose();
//					}
//				});
//			}
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});
	}

	//-------- helper methods --------
	
	/**
	 *  Create a panel for the options area.
	 */
	protected JPanel createOptionsPanel(final IExternalAccess agent)
	{
		final JPanel options = new JPanel(new GridBagLayout());

		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("env")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				final Environment env = (Environment)capa.getBeliefbase().getBelief("environment").getFact();
				
				options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Environment Control"));
				roundcnt = new JLabel("0");
				final JTextField roundtimetf = new JTextField(5);
				final Object rt = capa.getBeliefbase().getBelief("roundtime").getFact();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						roundtimetf.setText(""+rt);
					}
				});
				
				
				roundtimetf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final Long val = Long.valueOf(roundtimetf.getText());
						agent.scheduleStep(new IComponentStep<Void>()
						{
							@Classname("roundtime")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
								bia.getBeliefbase().getBelief("roundtime").setFact(val);
								return IFuture.DONE;
							}
						});
//						agent.getBeliefbase().setBeliefFact("roundtime", val);
						//roundtimesl.setValue((int)Math.log(val.intValue()));
					}
				});
				final JTextField saveintervaltf = new JTextField(""+env.getSaveInterval(), 5);
				saveintervaltf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						env.setSaveInterval(Long.parseLong(saveintervaltf.getText()));
					}
				});
				final JButton hs = new JButton("Save highscore");
				hs.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						//System.out.println("saving highscore: "+SUtil.arrayToString(env.getHighscore()));
						env.saveHighscore();
					}
				});
				final JTextField foodrate = new JTextField(""+env.getFoodrate(), 4);
				foodrate.addActionListener(new ActionListener()
				{
				    public void actionPerformed(ActionEvent ae)
				    {
				        env.setFoodrate(Integer.parseInt(foodrate.getText()));
				    }
				}); 
				
				Insets insets = new Insets(2, 4, 4, 2);
				options.add(new JLabel("Round number:"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(roundcnt, new GridBagConstraints(1, 0, 3, 1, 1, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Round time [millis]:"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				//options.add(roundtimesl, new GridBagConstraints(1, 1, 1, 1, 0, 0,
				//	GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(roundtimetf, new GridBagConstraints(1, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Autosave highscore [millis, -1 for off]"), new GridBagConstraints(0, 2, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(saveintervaltf, new GridBagConstraints(1, 2, 3, 1, 1, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Food rate [every n ticks]"), new GridBagConstraints(0, 3, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(foodrate, new GridBagConstraints(1, 3, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(hs, new GridBagConstraints(0, 4, 1, 1, 0, 0,
						GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				
				return IFuture.DONE;
			}
		});
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("roundcnt")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				final Environment env = (Environment)capa.getBeliefbase().getBelief("environment").getFact();
				
				options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Environment Control"));
				roundcnt = new JLabel("0");
				final JTextField roundtimetf = new JTextField(5);
				final Object rt = capa.getBeliefbase().getBelief("roundtime").getFact();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						roundtimetf.setText(""+rt);
					}
				});
				
				
				roundtimetf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final Long val = Long.valueOf(roundtimetf.getText());
						agent.scheduleStep(new IComponentStep<Void>()
						{
							@Classname("rt")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
								capa.getBeliefbase().getBelief("roundtime").setFact(val);
								return IFuture.DONE;
							}
						});
//						agent.getBeliefbase().setBeliefFact("roundtime", val);
						//roundtimesl.setValue((int)Math.log(val.intValue()));
					}
				});
				final JTextField saveintervaltf = new JTextField(""+env.getSaveInterval(), 5);
				saveintervaltf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						env.setSaveInterval(Long.parseLong(saveintervaltf.getText()));
					}
				});
				final JButton hs = new JButton("Save highscore");
				hs.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						//System.out.println("saving highscore: "+SUtil.arrayToString(env.getHighscore()));
						env.saveHighscore();
					}
				});
				final JTextField foodrate = new JTextField(""+env.getFoodrate(), 4);
				foodrate.addActionListener(new ActionListener()
				{
				    public void actionPerformed(ActionEvent ae)
				    {
				        env.setFoodrate(Integer.parseInt(foodrate.getText()));
				    }
				}); 
				
				Insets insets = new Insets(2, 4, 4, 2);
				options.add(new JLabel("Round number:"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(roundcnt, new GridBagConstraints(1, 0, 3, 1, 1, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Round time [millis]:"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				//options.add(roundtimesl, new GridBagConstraints(1, 1, 1, 1, 0, 0,
				//	GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(roundtimetf, new GridBagConstraints(1, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Autosave highscore [millis, -1 for off]"), new GridBagConstraints(0, 2, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(saveintervaltf, new GridBagConstraints(1, 2, 3, 1, 1, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(new JLabel("Food rate [every n ticks]"), new GridBagConstraints(0, 3, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(foodrate, new GridBagConstraints(1, 3, 1, 1, 0, 0,
					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				options.add(hs, new GridBagConstraints(0, 4, 1, 1, 0, 0,
						GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
				
				return IFuture.DONE;
			}
		});
		
//		agent.getBeliefbase().getBeliefFact("environment").addResultListener(new SwingDefaultResultListener(this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				final Environment env = (Environment)result;
//				
//				options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Environment Control"));
//				roundcnt = new JLabel("0");
//				final JTextField roundtimetf = new JTextField(5);
//				agent.getBeliefbase().getBeliefFact("roundtime").addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object source, Object result)
//					{
//						roundtimetf.setText(""+result);
//					}
//				});
//				roundtimetf.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						Long val = new Long(roundtimetf.getText());
//						agent.getBeliefbase().setBeliefFact("roundtime", val);
//						//roundtimesl.setValue((int)Math.log(val.intValue()));
//					}
//				});
//				final JTextField saveintervaltf = new JTextField(""+env.getSaveInterval(), 5);
//				saveintervaltf.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						env.setSaveInterval(Long.parseLong(saveintervaltf.getText()));
//					}
//				});
//				final JButton hs = new JButton("Save highscore");
//				hs.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent ae)
//					{
//						//System.out.println("saving highscore: "+SUtil.arrayToString(env.getHighscore()));
//						env.saveHighscore();
//					}
//				});
//				final JTextField foodrate = new JTextField(""+env.getFoodrate(), 4);
//				foodrate.addActionListener(new ActionListener()
//				{
//				    public void actionPerformed(ActionEvent ae)
//				    {
//				        env.setFoodrate(Integer.parseInt(foodrate.getText()));
//				    }
//				}); 
//				
//				Insets insets = new Insets(2, 4, 4, 2);
//				options.add(new JLabel("Round number:"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(roundcnt, new GridBagConstraints(1, 0, 3, 1, 1, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(new JLabel("Round time [millis]:"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				//options.add(roundtimesl, new GridBagConstraints(1, 1, 1, 1, 0, 0,
//				//	GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(roundtimetf, new GridBagConstraints(1, 1, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(new JLabel("Autosave highscore [millis, -1 for off]"), new GridBagConstraints(0, 2, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(saveintervaltf, new GridBagConstraints(1, 2, 3, 1, 1, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(new JLabel("Food rate [every n ticks]"), new GridBagConstraints(0, 3, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(foodrate, new GridBagConstraints(1, 3, 1, 1, 0, 0,
//					GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//				options.add(hs, new GridBagConstraints(0, 4, 1, 1, 0, 0,
//						GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
//			}
//		});

		return options;
	}

	/**
	 *  Ensure that the gui is updated on changes in the environment.
	 */
	protected void	enableGuiUpdate(IExternalAccess agent)
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dummy")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				final Environment env = (Environment)capa.getBeliefbase().getBelief("environment").getFact();
				env.addPropertyChangeListener(new PropertyChangeListener()
				{
					// Hack!!! Dummy creature required for world size.
					protected Creature	dummy	= new Prey();

					public void propertyChange(PropertyChangeEvent evt)
					{
						roundcnt.setText(""+env.getWorldAge());
			
						dummy.setWorldWidth(env.getWidth());
						dummy.setWorldHeight(env.getHeight());
			
						Vision	vision	= new Vision();
						vision.setObjects(env.getAllObjects());
			
						map.update(new CurrentVision(dummy, vision));
						creatures.update(env.getCreatures());
						observers.update(env.getCreatures());
						highscore.update(env.getHighscore());
					}
				});
				return IFuture.DONE;
			}
		});
		
//		agent.getBeliefbase().getBeliefFact("environment").addResultListener(new SwingDefaultResultListener(this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				final Environment env = (Environment)result;
//				env.addPropertyChangeListener(new PropertyChangeListener()
//				{
//					// Hack!!! Dummy creature required for world size.
//					protected Creature	dummy	= new Prey();
//
//					public void propertyChange(PropertyChangeEvent evt)
//					{
//						roundcnt.setText(""+env.getWorldAge());
//			
//						dummy.setWorldWidth(env.getWidth());
//						dummy.setWorldHeight(env.getHeight());
//			
//						Vision	vision	= new Vision();
//						vision.setObjects(env.getAllObjects());
//			
//						map.update(new CurrentVision(dummy, vision));
//						creatures.update(env.getCreatures());
//						observers.update(env.getCreatures());
//						highscore.update(env.getHighscore());
//					}
//				});
//			}
//		});
		
	}
}

