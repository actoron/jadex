package jadex.bdi.examples.shop;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.SGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
		super(agent.getComponentIdentifier().getName());
		add(new CustomerPanel(agent));
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
//				agent.killAgent();
				agent.killComponent();
			}
		});
		agent.scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "dispose"; 
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				bia.addComponentListener(new IComponentListener() 
				{
					public void componentTerminating(ChangeEvent ae) 
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								setVisible(false);
								dispose();
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
		
//		agent.addAgentListener(new IAgentListener() 
//		{
//			public void agentTerminating(AgentEvent ae) 
//			{
//				setVisible(false);
//				dispose();
//			}
//			
//			public void agentTerminated(AgentEvent ae) 
//			{
//			}
//		});
	}
}
