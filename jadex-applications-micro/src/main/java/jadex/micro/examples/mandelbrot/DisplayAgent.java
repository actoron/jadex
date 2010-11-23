package jadex.micro.examples.mandelbrot;

import jadex.commons.SGUI;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Agent offering a display service.
 */
public class DisplayAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The gui . */
	protected DisplayPanel	panel;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		// Hack!!! Swing code not on swing thread!?
		DisplayAgent.this.panel	= new DisplayPanel();

		addService(new DisplayService(this));
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame	frame	= new JFrame(getAgentName());
				frame.getContentPane().add(BorderLayout.CENTER, panel);
				frame.setSize(500, 500);
				frame.setLocation(SGUI.calculateMiddlePosition(frame));
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the display panel.
	 */
	public DisplayPanel	getPanel()
	{
		return this.panel;
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a display service.", null, null,
			null, null, null,
			new Class[]{}, new Class[]{IDisplayService.class});
	}
}
