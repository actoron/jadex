package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SGUI;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;

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
		
		agent.getBeliefbase().getBeliefFact("env").addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				MapPanel map = new MapPanel((Environment)result);
				getContentPane().add("Center", map);
				
				setSize(400, 400);
				setLocation(SGUI.calculateMiddlePosition(EnvironmentGui.this));
				setVisible(true);
			}
		});
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// On exit kill all known agents.
				agent.getBeliefbase().getBeliefFact("env").addResultListener(new SwingDefaultResultListener(EnvironmentGui.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						Environment	env	= (Environment)result;
						final WorldObject[]	wobs = env.getWorldObjects();
						for(int i=0; i<wobs.length; i++)
						{
							final int num = i;
							if(wobs[i].getType().equals(Environment.BURNER)
								|| wobs[i].getType().equals(Environment.COLLECTOR))
							{
								agent.scheduleStep(new IComponentStep()
								{
									public Object execute(IInternalAccess ia)
									{
										final IBDIInternalAccess bia = (IBDIInternalAccess)ia;
										final IGoal kill = bia.getGoalbase().createGoal("cms_destroy_component");
										SServiceProvider.getServiceUpwards(agent.getServiceProvider(), IComponentManagementService.class)
											.addResultListener(bia.createResultListener(new DefaultResultListener()
										{
											public void resultAvailable(Object source, Object result)
											{
												try
												{
													IComponentManagementService ces = (IComponentManagementService)result;
													kill.getParameter("componentidentifier").setValue(ces.createComponentIdentifier(wobs[num].getName(), true, null));
													bia.getGoalbase().dispatchTopLevelGoal(kill);
												}
												catch(GoalFailureException gfe) 
												{
												}
											}
										}));
										return null;
									}
								});
								
//								agent.createGoal("cms_destroy_component").addResultListener(new DefaultResultListener()
//								{
//									public void resultAvailable(Object source, Object result)
//									{
//										final IEAGoal kill = (IEAGoal)result;
//										SServiceProvider.getServiceUpwards(agent.getServiceProvider(), IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(EnvironmentGui.this)
//										{
//											public void customResultAvailable(Object source, Object result)
//											{
//												try
//												{
//													IComponentManagementService ces = (IComponentManagementService)result;
//													kill.setParameterValue("componentidentifier", ces.createComponentIdentifier(wobs[num].getName(), true, null));
//													agent.dispatchTopLevelGoalAndWait(kill);
//												}
//												catch(GoalFailureException gfe) 
//												{
//												}
//											}
//										});
//									}
//								});
							}
						}
					}
				});
				
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

