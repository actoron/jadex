package jadex.micro.examples.chat;

import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.SGUI;
import jadex.micro.IMicroExternalAccess;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 */
public class ChatPanel extends JPanel
{
	/** The agent. */
	protected IMicroExternalAccess agent;
	
	/**
	 * 
	 */
	public ChatPanel(final IMicroExternalAccess agent)
	{
		this.agent = agent;
		
		JPanel main = new JPanel(new BorderLayout());
		final JTextArea ta = new JTextArea();
		main.add(ta, BorderLayout.CENTER);
		
		agent.scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				ChatAgent ca = (ChatAgent)args;
				ca.getChatService().addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						String[] text = (String[])event.getValue();
						ta.setText(ta.getText()+"\n"+"["+text[0]+"]:"+text[1]);
					}
				});
			}
		});
		
		JPanel south = new JPanel(new BorderLayout());
		final JTextField tf = new JTextField();
		JButton send = new JButton("Send");
		south.add(tf, BorderLayout.CENTER);
		south.add(send, BorderLayout.EAST);

		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new ICommand()
				{
					public void execute(Object args)
					{
						ChatAgent ca = (ChatAgent)args;
						ca.getChatService().tell(""+agent.getComponentIdentifier(), tf.getText());
						tf.setText("");
					}
				});
			}
		});
		
		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static void createGui(final IMicroExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new ChatPanel(agent));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		// todo: micro listener
	}
}
