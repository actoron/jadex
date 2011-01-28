package jadex.tools.jcc;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.xml.annotation.XMLClassname;

import javax.swing.SwingUtilities;

/**
 * The Jadex control center.
 */
public class AgentControlCenter extends ControlCenter
{
	//-------- attributes --------
	
	/** The external access. */
	protected IBDIExternalAccess agent;

	//-------- constructors --------

	/**
	 * Create a control center.
	 */
	public AgentControlCenter(IBDIExternalAccess agent, String plugins_prop)
	{
		super(agent, plugins_prop);
		
		this.agent = agent;

		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("kill")
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new IComponentListener()
				{
					public void componentTerminating(ChangeEvent ae)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								if(!killed)
								{
									saveProject();
//									closeProject();
									closePlugins();
									killed = true;
								}
								window.setVisible(false);
								window.dispose();
							}
						});
					}

					public void componentTerminated(ChangeEvent ae)
					{
					}
				});
				return null;
			}
		});
	}

	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public IBDIExternalAccess getAgent()
	{
		return this.agent;
	}
}
