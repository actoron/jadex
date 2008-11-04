package jadex.tools.convcenter;

import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The gui for sending and viewing messages.
 */
public class FipaMessageGui extends JFrame
{
	//-------- attributes --------

	/** The conversation panel. */
	protected FipaConversationPanel	convpanel;
	
	//-------- constructors --------

	/**
	 *  Open the GUI.
	 */
	public FipaMessageGui(final IExternalAccess agent)
	{
		this.setTitle("FipaMessageDialog");

		// Init on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				convpanel	= new FipaConversationPanel(agent, (IAgentIdentifier)agent.getBeliefbase().getBelief("receiver").getFact());
				getContentPane().add(BorderLayout.CENTER, convpanel);
				pack();
				setVisible(true);
			}
		});

		// Kill agent on exit.
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killAgent();
			}
		});
	}

	//-------- constructors --------
	
	/**
	 *  Get the conversation panel.
	 */
	public FipaConversationPanel	getConversationPanel()
	{
		return convpanel;
	}

	//--------- static part ---------

	/**
	 *  Main method for testing gui layout.
	 */
	public static void main(String[] args)
	{
		new FipaMessageGui(null);
	}
}
