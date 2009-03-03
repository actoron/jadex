package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.planlib.simsupport.observer.capability.ObserverCenter;
import jadex.bdi.planlib.simsupport.observer.capability.plugin.IObserverCenterPlugin;
import jadex.bdi.runtime.IExternalAccess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *  The gui for the cleaner world example.
 *  Shows the world from the viewpoint of the environment agent.
 */
/*  @requires belief environment
 *  @requires belief roundtime
 */
public class EnvironmentControlPlugin	implements IObserverCenterPlugin
{
	//-------- attributes --------
	
	/** The round counter label. */
	protected JLabel	roundcnt;
	
	/** The panel displaying active creatures. */
	protected CreaturePanel	creatures;

	/** The panel displaying current observers. */
	protected CreaturePanel	observers;

	/** The panel displaying alltime highscores. */
	protected CreaturePanel	highscore;
	
	/** The ObserverCenter */
	protected ObserverCenter observerCenter;
	
	/** The frame for the observer view */
	protected JPanel view;
	
	//-------- constructors --------

	
	/**
	 *  Create a new gui plan.
	 *  @param agent The agent created this Plugin
	 *  @require belief roundtime
	 *  @require belief environment
	 */
	public EnvironmentControlPlugin(final IExternalAccess agent)
	{
		
		boolean reqBeliefsFound = 
			agent.getBeliefbase().containsBelief("environment") &&
			agent.getBeliefbase().containsBelief("roundtime");
		
		if (reqBeliefsFound)
		{
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
			// since 1.5 !
			//tp.setPreferredSize(new Dimension(243, 0));
			tp.setSize(new Dimension(243, 0));
			
			view = new JPanel(new BorderLayout());
			view.add(BorderLayout.CENTER, tp);
			view.add(BorderLayout.SOUTH, options);
	
			enableGuiUpdate(agent);
		}
		else
		{	
			JTextPane text = new JTextPane();
			StringBuffer buf = new StringBuffer();
			buf.append("Required beliefs not found in capability!\n\n");
			buf.append("Need:\n");
			buf.append("   Environment environment\n");
			buf.append("   Integer roundtime\n");
			buf.append("\n\nMaybe the problem is the provided scope:\n");
			buf.append(agent.toString());
			buf.append("\n\nBe sure to instantiate the plugin in the scope of the agent, not in the scope of the capability");
			text.setText(buf.toString());
			text.setEditable(false);
			
			view = new JPanel(new BorderLayout());
			view.add(text);
			view.setMinimumSize(new Dimension(200,100));
		}

	}

	//-------- helper methods --------
	
	/**
	 *  Create a panel for the options area.
	 */
	protected JPanel createOptionsPanel(final IExternalAccess agent)
	{
		final Environment	env	= (Environment)agent.getBeliefbase().getBelief("environment").getFact();

		JPanel	options	= new JPanel(new GridBagLayout());
		options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Environment Control"));
		this.roundcnt = new JLabel("0");
		final JTextField roundtimetf = new JTextField(""+agent.getBeliefbase().getBelief("roundtime").getFact(), 5);
		roundtimetf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Long val = new Long(roundtimetf.getText());
				agent.getBeliefbase().getBelief("roundtime").setFact(val);
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
		return options;
	}

	/**
	 *  Ensure that the gui is updated on changes in the environment.
	 */
	protected void	enableGuiUpdate(IExternalAccess agent)
	{
		final Environment	env	= (Environment)agent.getBeliefbase().getBelief("environment").getFact();
		env.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				roundcnt.setText(""+env.getWorldAge());

				Vision	vision	= new Vision();
				vision.setObjects(env.getAllObjects());

				creatures.update(env.getCreatures());
				observers.update(env.getCreatures());
				highscore.update(env.getHighscore());
			}
		});
	}

	public String getIconPath()
	{
		// TODO: create icon and replace path
		return null;
	}

	public String getName()
	{
		return "World Settings";
	}

	public Component getView()
	{
		return view;
	}

	public void refresh()
	{
		// ignore, get updates from agent
	}

	public void shutdown()
	{
		// ignore
	}

	public void start(ObserverCenter main)
	{
		observerCenter = main;
		// TODO: implement some markup methods :-)
	}
}

