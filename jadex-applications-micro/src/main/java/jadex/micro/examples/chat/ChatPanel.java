package jadex.micro.examples.chat;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
				tell(""+agent.getComponentIdentifier(), tf.getText());
				tf.setText("");
			}
		};
		tf.addActionListener(al);
		send.addActionListener(al);
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("addlistener")
			public Object execute(IInternalAccess ia)
			{
				return ia.getServiceContainer().getProvidedServices(IChatService.class)[0];
//				final Future ret = new Future();
//				ia.getServiceContainer().getRequiredService("mychatservice").addResultListener(
//					ia.createResultListener(new DelegationResultListener(ret)));
//				return ret;
			}
		}).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IChatService cs = (IChatService)result;
				cs.addChangeListener(new IRemoteChangeListener()
				{
					public IFuture changeOccurred(final ChangeEvent event)
					{
						Future ret = new Future();
						if(!isVisible())
						{
							ret.setException(new RuntimeException("Gui closed."));
						}
						else
						{
							addMessage((String)event.getSource(), (String)event.getValue());
							ret.setResult(null);
						}
						return ret;
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
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("dispose")
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						f.setVisible(false);
					}
				});
				return null;
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
	public void tell(final String name, final String text)
	{
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ia.getServiceContainer().getRequiredServices("chatservices")
					.addResultListener(new IIntermediateResultListener<Object>()
				{
					public void resultAvailable(Collection result)
					{
//						System.out.println("bulk");
						if(result!=null)
						{
							for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
							{
								IChatService cs = (IChatService)it.next();
								cs.hear(name, text);
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("Chat service exception.");
						exception.printStackTrace();
					}
					
					public void intermediateResultAvailable(Object result)
					{
//						System.out.println("intermediate");
						((IChatService)result).hear(name, text);
					}
					
					public void finished()
					{
//						System.out.println("end");
					}
				});
				return null;
			}
		});
	}
}
