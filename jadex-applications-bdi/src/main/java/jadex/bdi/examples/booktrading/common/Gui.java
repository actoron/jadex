package jadex.bdi.examples.booktrading.common;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The gui allows to add and delete buy or sell orders and shows open and
 *  finished orders.
 */
public class Gui extends JFrame
{
	//-------- constructors --------

	/**
	 *  Shows the gui, and updates it when beliefs change.
	 */
	public Gui(final IBDIExternalAccess agent)//, final boolean buy)
	{
		super((GuiPanel.isBuyer(agent)? "Buyer: ": "Seller: ")+agent.getComponentIdentifier().getName());
		
		GuiPanel gp = new GuiPanel(agent);
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				bia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								dispose();
							}
						});
					}
				});
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
//						dispose();
//					}
//				});
//			}
//			
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});
//		gp.refresh();
		add(gp, BorderLayout.CENTER);
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
	}
}