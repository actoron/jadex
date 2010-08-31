package jadex.bdi.examples.shop;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.commons.SGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 *  Frame for displaying of the customer gui.
 */
public class CustomerFrame extends JFrame
{
	/**
	 *  Create a new frame.
	 */
	public CustomerFrame(final IBDIExternalAccess agent)
	{
		add(new CustomerPanel(agent));
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killAgent();
			}
		});
		agent.addAgentListener(new IAgentListener() 
		{
			public void agentTerminating(AgentEvent ae) 
			{
				setVisible(false);
				dispose();
			}
			
			public void agentTerminated(AgentEvent ae) 
			{
			}
		});
	}
}
