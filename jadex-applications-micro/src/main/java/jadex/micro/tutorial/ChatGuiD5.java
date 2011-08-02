package jadex.micro.tutorial;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *  Basic chat user interface.
 */
public class ChatGuiD5 extends JFrame
{
	//-------- attributes --------
	
	/** The textfield with received messages. */
	protected JTextArea received;
	
	//-------- constructors --------
	
	/**
	 *  Create the user interface
	 */
	public ChatGuiD5(final IExternalAccess agent)
	{
		super(agent.getComponentIdentifier().getName());
		this.setLayout(new BorderLayout());
		
		received = new JTextArea(10, 20);
		final JTextField message = new JTextField();
		JButton send = new JButton("send");
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(message, BorderLayout.CENTER);
		panel.add(send, BorderLayout.EAST);
		
		getContentPane().add(received, BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String text = message.getText(); 
				agent.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						ia.getServiceContainer().getRequiredServices("chatservices")
							.addResultListener(new IIntermediateResultListener()
						{
							public void resultAvailable(Object result)
							{
								for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
								{
									IChatService cs = (IChatService)it.next();
									cs.message(agent.getComponentIdentifier().getName(), text);
								}
							}
							
							public void intermediateResultAvailable(Object result)
							{
								System.out.println("found: "+result);
								IChatService cs = (IChatService)result;
								cs.message(agent.getComponentIdentifier().getName(), text);
							}
							
							public void finished()
							{
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						});
						return null;
					}
				});
			}
		});
		pack();
		setVisible(true);
	}
	
	/**
	 *  Method to add a new text message.
	 *  @param text The text.
	 */
	public void addMessage(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				received.append(text+"\n");
			}
		})
	;}
}
