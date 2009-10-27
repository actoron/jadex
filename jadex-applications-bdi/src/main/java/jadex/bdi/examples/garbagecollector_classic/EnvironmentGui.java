package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentExecutionService;
import jadex.commons.SGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The gui plan.
 */
public class EnvironmentGui	extends JFrame
{
	//-------- constructors --------

	/**
	 *  Create a new gui.
	 */
	public EnvironmentGui(final IExternalAccess agent)
	{
		super("Garbage Collector Environment");

		MapPanel	map = new MapPanel((Environment)agent.getBeliefbase().getBelief("env").getFact());
		getContentPane().add("Center", map);

		setSize(400, 400);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// On exit kill all known agents.
				Environment	env	= (Environment)agent.getBeliefbase().getBelief("env").getFact();
				WorldObject[]	wobs	= env.getWorldObjects();
				for(int i=0; i<wobs.length; i++)
				{
					if(wobs[i].getType().equals(Environment.BURNER)
						|| wobs[i].getType().equals(Environment.COLLECTOR))
					{
						try
						{
							IGoal kill = agent.createGoal("ams_destroy_agent");
							IComponentExecutionService ces = (IComponentExecutionService)agent.getServiceContainer().getService(IComponentExecutionService.class);
							kill.getParameter("agentidentifier").setValue(ces.createComponentIdentifier(wobs[i].getName(), true, null));
//							kill.getParameter("agentidentifier").setValue(new AgentIdentifier(wobs[i].getName(), true));
							agent.dispatchTopLevelGoalAndWait(kill);
						}
						catch(GoalFailureException gfe) {}
					}
				}
				
				// Finally shutdown environment agent.
				agent.killAgent();
			}
		});
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						EnvironmentGui.this.dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});		
	}
}

