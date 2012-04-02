package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.CurrentVision;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.xml.bean.JavaReader;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *  Gui for external observers.
 */
public class ObserverGui	extends EnvironmentGui
{
	//-------- attributes --------

	/** The last time the highscore was refreshed. */
	protected long	refreshtime;

	/** The interval between refreshes of highscore (-1 for autorefresh off). */
	protected long	refreshinterval;

	//-------- constructors --------

	/**
	 *  Create a new gui plan.
	 */
	public ObserverGui(IBDIExternalAccess agent)
	{
		super(agent);
	}

	//-------- helper methods --------

	/**
	 *  Create the options panel.
	 */
	protected JPanel	createOptionsPanel(final IBDIExternalAccess agent)
	{
		JPanel	options	= new JPanel(new GridBagLayout());
		options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Observer Control"));
		final JTextField refreshintervaltf = new JTextField(""+refreshinterval, 5);
		refreshintervaltf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshinterval	= Math.max(5000L,
					Long.parseLong(refreshintervaltf.getText()));
				refreshintervaltf.setText(""+refreshinterval);
			}
		});
		JButton refresh	= new JButton("Refresh highscore");
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				refreshHighscore(agent);
			}
		});

		Insets insets = new Insets(2, 4, 4, 2);
		options.add(new JLabel("Autorefresh highscore [millis, -1 for off]"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(refreshintervaltf, new GridBagConstraints(1, 0, 3, 1, 1, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(refresh, new GridBagConstraints(0, 1, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));		

		return options;
	}

	/**
	 *  Refresh the highscore.
	 */
	protected void	refreshHighscore(IBDIExternalAccess agent)
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("highscore")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				String hs = (String)bia.getBeliefbase().getBelief("highscore").getFact();
				BufferedReader reader = null;
				try
				{
					reader = new BufferedReader(new InputStreamReader(SUtil.getResource(hs, ObserverGui.class.getClassLoader())));
					StringBuffer fileData = new StringBuffer(1000);
					char[] buf = new char[1024];
					int numRead=0;
					while((numRead=reader.read(buf)) != -1){
						fileData.append(buf, 0, numRead);
					}
					reader.close();
					Creature[]	hscreatures	= (Creature[]) JavaReader.objectFromXML(fileData.toString(), this.getClass().getClassLoader());
				
					highscore.update(hscreatures);
				}
				catch(Exception e)
				{
					System.out.print("Error loading highscore: ");
					e.printStackTrace();
				}
				finally
				{
					if(reader!=null)
					{
						try
						{
							reader.close();
						}
						catch(Exception e)
						{
						}
					}
				}
				return IFuture.DONE;
			}
		});
		
		// Read highscore list from resource.
//		agent.getBeliefbase().getBeliefFact("highscore").addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				BufferedReader reader = null;
//				try
//				{
//					reader = new BufferedReader(new InputStreamReader(SUtil.getResource((String)result, ObserverGui.class.getClassLoader())));
//					StringBuffer fileData = new StringBuffer(1000);
//					char[] buf = new char[1024];
//					int numRead=0;
//					while((numRead=reader.read(buf)) != -1){
//						fileData.append(buf, 0, numRead);
//					}
//					reader.close();
//					Creature[]	hscreatures	= (Creature[]) JavaReader.objectFromXML(fileData.toString(), this.getClass().getClassLoader());
//				
//					highscore.update(hscreatures);
//				}
//				catch(Exception e)
//				{
//					System.out.print("Error loading highscore: ");
//					e.printStackTrace();
//				}
//				finally
//				{
//					if(reader!=null)
//					{
//						try
//						{
//							reader.close();
//						}
//						catch(Exception e)
//						{
//						}
//					}
//				}
//			}
//		});
	}

	/**
	 *  Ensure that the gui is updated on changes in the environment.
	 */
	protected void	enableGuiUpdate(final IBDIExternalAccess agent)
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("update")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				bia.getBeliefbase().getBelief("vision").addBeliefListener(new IBeliefListener()
				{
					public void beliefChanged(AgentEvent ae)
					{
						final Vision vision = (Vision)ae.getValue(); 
						Creature me = (Creature)bia.getBeliefbase().getBelief("my_self").getFact();
						if(vision!=null)
						{
							// Update map and creature list from vision.
							map.update(new CurrentVision(me, vision));
							creatures.update(vision.getCreatures());
							observers.update(vision.getCreatures());
						}

						// Refresh highscore.
						long	time	= System.currentTimeMillis();
						if(refreshinterval>=0 && refreshtime+refreshinterval<=time)
						{
							refreshHighscore(agent);
							refreshtime	= time;
						}
					}
				});
				return IFuture.DONE;
			}
		});
		
//		agent.getBeliefbase().addBeliefListener("vision", new IBeliefListener()
//		{
//			public void beliefChanged(AgentEvent ae)
//			{
//				final Vision vision = (Vision)ae.getValue(); 
//				agent.getBeliefbase().getBeliefFact("my_self").addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object source, Object result)
//					{
//						Creature me = (Creature)result;
//						if(vision!=null)
//						{
//							// Update map and creature list from vision.
//							map.update(new CurrentVision(me, vision));
//							creatures.update(vision.getCreatures());
//							observers.update(vision.getCreatures());
//						}
//
//						// Refresh highscore.
//						long	time	= System.currentTimeMillis();
//						if(refreshinterval>=0 && refreshtime+refreshinterval<=time)
//						{
//							refreshHighscore(agent);
//							refreshtime	= time;
//						}
//					}
//				});
//			}
//		});
	}
}

