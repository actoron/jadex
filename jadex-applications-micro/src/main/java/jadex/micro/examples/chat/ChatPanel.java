package jadex.micro.examples.chat;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *  Panel for displaying the chat.
 */
public class ChatPanel extends JPanel
{
	//-------- constants --------
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	//-------- attributes --------
	
	/** The agent. */
	protected IExternalAccess agent;
	
	/** The text area. */
	protected JTextArea chatarea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat panel.
	 */
	public ChatPanel(final IExternalAccess agent)
	{
		this.agent = agent;
		
		chatarea = new JTextArea(10, 30);
		JScrollPane main = new JScrollPane(chatarea);
		
		JPanel south = new JPanel(new BorderLayout());
		final JTextField tf = new JTextField();
		JButton send = new JButton("Send");
		south.add(tf, BorderLayout.CENTER);
		south.add(send, BorderLayout.EAST);

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tell(tf.getText());
				tf.setText("");
			}
		};
		tf.addActionListener(al);
		send.addActionListener(al);
		
		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static ChatPanel createGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame(agent.getComponentIdentifier().getName());
		ChatPanel cp = new ChatPanel(agent);
		f.add(cp);
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
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@XMLClassname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						f.setVisible(false);
					}
				});
				return IFuture.DONE;
			}
		});
		
		return cp;
	}
	
	/**
	 *  Add a message to the text area.
	 */
	public void addMessage(String name, String text)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[").append(name).append("]: ").append(text).append(lf);
		chatarea.append(buf.toString());
	}
	
	/**
	 *  Tell something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public void tell(final String text)
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IIntermediateFuture<IChatService> fut = ia.getServiceContainer().getRequiredServices("chatservices");
				fut.addResultListener(new IIntermediateResultListener<IChatService>()
				{
					public void resultAvailable(Collection<IChatService> result)
					{
//						System.out.println("bulk");
						if(result!=null)
						{
							for(Iterator<IChatService> it=result.iterator(); it.hasNext(); )
							{
								IChatService cs = it.next();
								cs.message(text);
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("Chat service exception.");
						exception.printStackTrace();
					}
					
					public void intermediateResultAvailable(IChatService result)
					{
//						System.out.println("intermediate");
						result.message(text);
					}
					
					public void finished()
					{
//						System.out.println("end");
					}
				});
				return IFuture.DONE;
			}
		});
	}
}
