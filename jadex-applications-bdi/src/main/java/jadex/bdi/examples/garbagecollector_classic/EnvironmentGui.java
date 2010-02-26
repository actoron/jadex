package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentManagementService;
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
	public EnvironmentGui(final IBDIExternalAccess agent)
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
							IGoal kill = agent.createGoal("cms_destroy_component");
							IComponentManagementService ces = (IComponentManagementService)agent.getServiceContainer().getService(IComponentManagementService.class);
							kill.getParameter("componentidentifier").setValue(ces.createComponentIdentifier(wobs[i].getName(), true, null));
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

