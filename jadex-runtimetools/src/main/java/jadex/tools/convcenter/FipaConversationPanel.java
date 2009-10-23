package jadex.tools.convcenter;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.service.library.ILibraryService;
import jadex.tools.common.FipaMessagePanel;
import jadex.tools.common.GuiProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import nuggets.Nuggets;

/**
 * A panel for sending and receiving messages.
 */
public class FipaConversationPanel extends JSplitPane
{
	//-------- static part --------
	
	/**
	 * The image icons.
	 */
	protected static UIDefaults icons = new UIDefaults(new Object[]
	{
		// Tab icons.
		"new_message", SGUI.makeIcon(FipaConversationPanel.class, "/jadex/tools/common/images/new_new_message.png"),
		"sent_message", SGUI.makeIcon(FipaConversationPanel.class, "/jadex/tools/common/images/new_sent_message.png"),
		"received_message", SGUI.makeIcon(FipaConversationPanel.class, "/jadex/tools/common/images/new_received_message.png")
	});
	
	//-------- attributes --------
	
	/** The agent to dispatch events to. */
	protected IExternalAccess	agent;

	/** The default receiver (if any). */
	protected IComponentIdentifier	receiver;

	/** The tabbed panel. */
	protected JTabbedPane	tabs;

	/** The send message panel. */
	protected FipaMessagePanel	sendpanel;

	/** The list of sent messages. */
	protected JList	sentmsgs;

	/** The list of received messages. */
	protected JList	receivedmsgs;
	
	/** Registered message events. */
	protected List regmsgs;
	
	//-------- constructors --------
	
	/**
	 *  Create the gui.
	 */
	public FipaConversationPanel(final IExternalAccess agent, IComponentIdentifier default_receiver)
	{
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		setOneTouchExpandable(true);

		this.agent	= agent;
		this.receiver	= default_receiver;
		this.regmsgs = new ArrayList();
		
		// Right side starts with initial send panel only.
		IMessageEvent	msg	= agent.getEventbase().createMessageEvent("fipamsg");
		msg.getParameter(SFipa.SENDER).setValue(agent.getAgentIdentifier());
		if(default_receiver!=null)
			msg.getParameterSet(SFipa.RECEIVERS).addValue(default_receiver);
		this.sendpanel	= new FipaMessagePanel(msg, agent);

		JButton send = new JButton("Send");
		send.setToolTipText("Send the specified message");
		send.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					// Hack! For handling conversations.
					// If no replies are sent sentmessages are 
					final IMessageEvent msgevent = cloneMessage(sendpanel.getMessage());
					if(msgevent.getParameter(SFipa.CONVERSATION_ID).getValue()!=null
						|| msgevent.getParameter(SFipa.REPLY_WITH).getValue()!=null)
					{
						agent.getEventbase().registerMessageEvent(msgevent);
						regmsgs.add(msgevent);
						
						/*msgevent.addMessageEventListener(new IMessageEventListener()
						{
							public void messageEventReceived(AgentEvent ae)
							{
								agent.getEventbase().deregisterMessageEvent(msgevent);
								msgevent.removeMessageEventListener(this);
							}
							public void messageEventSent(AgentEvent ae)
							{
							}
						});*/
					}
					agent.getEventbase().sendMessage(msgevent);
					((DefaultListModel)sentmsgs.getModel()).addElement(msgevent);
				}
				catch(Exception e)
				{
					String text = SUtil.wrapText("Could not send message: "+e.getMessage());
					JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text,
						"Message Error", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		JButton reset = new JButton("Reset");
		reset.setToolTipText("Reset all specified message values");
		reset.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		reset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				resetMessage();
			}
		});

		JPanel	sendcont	= new JPanel(new BorderLayout());
		sendcont.add(BorderLayout.CENTER, sendpanel);
		JPanel	south	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.add(send);
		south.add(reset);

		HelpBroker hb = GuiProperties.setupHelp(this,  "tools.conversationcenter");
		if (hb != null)
		{
			JButton help = new JButton("Help");
			help.setToolTipText("Open the Javahelp for the Conversation Center");
			help.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
			south.add(help);
		}
		sendcont.add(BorderLayout.SOUTH, south);
		final JScrollPane sendtab	= new JScrollPane(sendcont);
		sendtab.setBorder(null);

		
		// Left side contains lists of sent/received messages.
		JPanel	lists	= new JPanel(new GridBagLayout());
		GridBagConstraints	gbcons	= new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0);

		this.sentmsgs	= new JList(new DefaultListModel());
		sentmsgs.setCellRenderer(new MessageListCellRenderer());
		sentmsgs.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2 && sentmsgs.locationToIndex(e.getPoint())!=-1)
				{
					final IMessageEvent	msg	= (IMessageEvent)sentmsgs.getModel()
						.getElementAt(sentmsgs.locationToIndex(e.getPoint()));
					final JPanel	msgtab	= new JPanel(new BorderLayout());
					final FipaMessagePanel	msgpanel	= new FipaMessagePanel(msg, agent);
					msgpanel.setEditable(false);
					final JScrollPane	scroll	= new JScrollPane(msgtab);
					scroll.setBorder(null);

					JButton edit = new JButton("Edit");
					edit.setToolTipText("Edit this sent message");				
					edit.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
					edit.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent ae)
						{
							sendpanel.setMessage(cloneMessage(msg));
							tabs.setSelectedComponent(sendtab);
						}
					});

					JButton send = new JButton("Resend");
					send.setMargin(new Insets(2,2,2,2));
					send.setToolTipText("Send this message again");
					send.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);	
					send.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent ae)
						{
							try
							{
								IMessageEvent	clone	= cloneMessage(msg);
								agent.getEventbase().sendMessage(clone);
								((DefaultListModel)sentmsgs.getModel()).addElement(clone);
							}
							catch(Exception e)
							{
								String text = SUtil.wrapText("Could not send message: "+e.getMessage());
								JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text,
									"Message Error", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					});

					JButton reset = new JButton("Close");
					reset.setToolTipText("Close displayed message");
					reset.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
					reset.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent ae)
						{
							tabs.remove(scroll);
						}
					});

					msgtab.add(BorderLayout.CENTER, msgpanel);
					JPanel	south	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
					south.add(edit);
					south.add(send);
					south.add(reset);
					msgtab.add(BorderLayout.SOUTH, south);
					tabs.addTab(getMessageTitle(msg), icons.getIcon("sent_message"), scroll);
					tabs.setSelectedComponent(scroll);
					
					SGUI.adjustComponentSizes(FipaConversationPanel.this);
				}
			}
		});
		JPanel	cpane	= new JPanel(new BorderLayout());
		cpane.add(BorderLayout.CENTER, new JScrollPane(sentmsgs));
		cpane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Sent Messages "));
		lists.add(cpane, gbcons);

		gbcons.gridy++;
		this.receivedmsgs	= new JList(new DefaultListModel());
		receivedmsgs.setCellRenderer(new MessageListCellRenderer());
		receivedmsgs.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					int	idx	= receivedmsgs.locationToIndex(e.getPoint());
					if(idx!=-1)
					{
						final IMessageEvent	msg	= (IMessageEvent)receivedmsgs.getModel().getElementAt(idx);
						final JPanel msgtab	= new JPanel(new BorderLayout());
						final FipaMessagePanel	msgpanel = new FipaMessagePanel(msg, agent);
						msgpanel.setEditable(false);
						final JScrollPane	scroll	= new JScrollPane(msgtab);
						scroll.setBorder(null);
	
						JButton reply = new JButton("Reply");
						reply.setToolTipText("Set up a reply message");
						reply.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
						
						reply.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent ae)
							{
								IMessageEvent reply = agent.getEventbase().createReply(msg, "fipamsg");
//								IMessageEvent reply = msg.createReply("fipamsg");
								reply.getParameter(SFipa.SENDER).setValue(agent.getAgentIdentifier());
								sendpanel.setMessage(reply);
								tabs.setSelectedComponent(sendtab);
							}
						});
	
						JButton reset = new JButton("Close");
						reset.setToolTipText("Close this message view");
						reset.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
						reset.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent ae)
							{
								tabs.remove(scroll);
							}
						});
	
						msgtab.add(BorderLayout.CENTER, msgpanel);
						JPanel	south	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
						south.add(reply);
						south.add(reset);
						msgtab.add(BorderLayout.SOUTH, south);
						tabs.addTab(getMessageTitle(msg), icons.getIcon("received_message"), scroll);
						tabs.setSelectedComponent(scroll);
						SGUI.adjustComponentSizes(FipaConversationPanel.this);
					}
				}
			}
		});
		cpane	= new JPanel(new BorderLayout());
		cpane.add(BorderLayout.CENTER, new JScrollPane(receivedmsgs));
		cpane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Received Messages "));
		lists.add(cpane, gbcons);

		gbcons.gridy++;
		gbcons.weighty	= 0;
		cpane	= new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton	clear	= new JButton("Clear");
		clear.setToolTipText("Clear the lists of sent and received messages");
		clear.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for(int i=0; i<regmsgs.size(); i++)
				{
					IMessageEvent mevent = (IMessageEvent)regmsgs.get(i);
					agent.getEventbase().deregisterMessageEvent(mevent);
				}
				regmsgs.clear();
				
				((DefaultListModel)sentmsgs.getModel()).removeAllElements();
				((DefaultListModel)receivedmsgs.getModel()).removeAllElements();
				while(tabs.getComponentCount()>1)
					tabs.remove(1);
			}
		});
		cpane.add(clear);
		lists.add(cpane, gbcons);
		
		this.tabs	= new JTabbedPane();
		tabs.addTab("Send", icons.getIcon("new_message"), sendtab);
		
		// Initialize split panel
		this.add(lists);
		this.add(tabs);
		
		SGUI.adjustComponentSizes(this);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void	run()
			{
				// Hack!!! Doesn't work when called before panel is shown.
				setDividerLocation(0.35);
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the message as a title.
	 */
	protected String getMessageTitle(IMessageEvent mevent)
	{
		String perf = (String)mevent.getParameter(SFipa.PERFORMATIVE).getValue();
		String cont = (String)mevent.getParameter(SFipa.CONTENT).getValue();

		StringBuffer title = new StringBuffer();
		if(perf!=null)
			title.append(perf);
		else
			title.append("unknown");
		
		title.append("(");
		if(cont!=null)
			title.append(cont);
		title.append(")");
		
		String ret = title.toString();
		
		if(ret.length()>25)
			ret	= ret.substring(0, 21) + "...)";

		return ret;
	}
	
	/**
	 *  Add a received message.
	 */
	public void	addMessage(final IMessageEvent msg)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void	run()
			{
				((DefaultListModel)receivedmsgs.getModel()).addElement(msg);
			}
		});
	}

	/**
	 *  Reset the message panel to an initial state.
	 */
	public void	reset()
	{
		resetMessage();
		setSentMessages(new IMessageEvent[0]);
		setReceivedMessages(new IMessageEvent[0]);
		while(tabs.getTabCount()>1)
			tabs.removeTabAt(tabs.getTabCount()-1);
	}
	
	/**
	 *  Reset the message to send.
	 */
	public void	resetMessage()
	{
		IMessageEvent	msg	= agent.getEventbase().createMessageEvent("fipamsg");
		msg.getParameter(SFipa.SENDER).setValue(agent.getAgentIdentifier());
		if(receiver!=null)
			msg.getParameterSet(SFipa.RECEIVERS).addValue(receiver);
		sendpanel.setMessage(msg);		
	}
	
	//-------- helper methods --------
	
	/**
	 *  Clone a message event.
	 */
	public IMessageEvent	cloneMessage(IMessageEvent msg)
	{
		IMessageEvent	clone	= agent.createMessageEvent(msg.getType());

		IParameter[]	params	= msg.getParameters();
		for(int i=0; i<params.length; i++)
		{
			Object val = msg.getParameter(params[i].getName()).getValue();
			clone.getParameter(params[i].getName()).setValue(val);
//			System.out.println("Value: "+params[i].getName()+" "+val);
		}

		IParameterSet[]	paramsets	= msg.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			Object[]	vals	= msg.getParameterSet(paramsets[i].getName()).getValues();
			clone.getParameterSet(paramsets[i].getName()).removeValues();
			for(int j=0; j<vals.length; j++)
			{
				clone.getParameterSet(paramsets[i].getName()).addValue(vals[j]);
			}
		}

		return clone;
	}

	/**
	 *  Get the message panel.
	 */
	public FipaMessagePanel getMessagePanel()
	{
		return sendpanel;
	}

	/**
	 *  Get the list of sent messages.
	 */
	public IMessageEvent[]	getSentMessages()
	{
		DefaultListModel	model	= (DefaultListModel)sentmsgs.getModel();
		IMessageEvent[]	ret	= new IMessageEvent[model.getSize()];
		model.copyInto(ret);
		return ret;
	}

	/**
	 *  Set the list of sent messages.
	 */
	public void	setSentMessages(final IMessageEvent[] msgs)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void	run()
			{
				DefaultListModel	model	= (DefaultListModel)sentmsgs.getModel();
				model.removeAllElements();
				for(int i=0; i<msgs.length; i++)
				{
					model.addElement(msgs[i]);
				}
			}
		});
	}

	/**
	 *  Set the list of received messages.
	 */
	public void	setReceivedMessages(final IMessageEvent[] msgs)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void	run()
			{
				DefaultListModel	model	= (DefaultListModel)receivedmsgs.getModel();
				model.removeAllElements();
				for(int i=0; i<msgs.length; i++)
				{
					model.addElement(msgs[i]);
				}
			}
		});
	}

	/**
	 *  Initialize the plugin from the properties.
	 */
	public void setProperties(Properties props)
	{
		// Load last state of message panel.
		String	msg	= props.getStringProperty(ConversationPlugin.LAST_MESSAGE);
		IMessageEvent	message	= null;
		if(msg!=null)
		{
			try
			{
				message	= decodeMessage(msg);
				// Update sender.
				message.getParameter(SFipa.SENDER).setValue(agent.getAgentIdentifier());
			}
			catch(Exception e)
			{
				final String text = SUtil.wrapText("Could not decode stored message: "+e.getMessage());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text, "Message problem", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		
		if(message!=null)
		{
			getMessagePanel().setMessage(message);
		}
		else
		{
			resetMessage();
		}
		
		// Load list of sent messages.
		List	sentmsgs	= new ArrayList();
		Property[]	sents	= props.getProperties(ConversationPlugin.SENT_MESSAGE);
		for(int i=0; i<sents.length; i++)
		{
			try
			{
				message	= decodeMessage(sents[i].getValue());
				// Update sender.
				message.getParameter(SFipa.SENDER).setValue(agent.getAgentIdentifier());
				sentmsgs.add(0, message);	// Re-revert order
			}
			catch(Exception e)
			{
				final String text = SUtil.wrapText("Could not decode stored message: "+e.getMessage());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text, "Message problem", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}			
		}
		setSentMessages((IMessageEvent[])sentmsgs.toArray(new IMessageEvent[sentmsgs.size()]));
	}

	/**
	 *  Fill in message values from string.
	 */
	public IMessageEvent	decodeMessage(String msg)
	{
		ClassLoader cl = ((ILibraryService)agent.getServiceContainer().getService(ILibraryService.class)).getClassLoader();
		Map	map	= (Map)Nuggets.objectFromXML(msg, cl);
		IMessageEvent	message	= agent.createMessageEvent((String)map.get(ConversationPlugin.ENCODED_MESSAGE_TYPE));

		String[] params	= message.getMessageType().getParameterNames();
		for(int i=0; i<params.length; i++)
		{
			message.getParameter(params[i]).setValue(map.get(params[i]));
		}
		String[] paramsets	= message.getMessageType().getParameterSetNames();
		for(int i=0; i<paramsets.length; i++)
		{
			if(map.get(paramsets[i])!=null)
			{
				message.getParameterSet(paramsets[i]).removeValues();
				message.getParameterSet(paramsets[i]).addValues((Object[])map.get(paramsets[i]));
			}
		}
		return message;
	}

	/** 
	 *  Get properties to be saved.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		// Save message displayed in message panel.
		IMessageEvent	message	= getMessagePanel().getMessage();
		String msg = encodeMessage(message);
		props.addProperty(new Property(ConversationPlugin.LAST_MESSAGE, msg));
		
		// Save list of sent messages (limit to 5 messages).
		IMessageEvent[]	msgs	= getSentMessages();
		Set	saved	= new HashSet();	// Used to avoid duplicates;
		for(int i=msgs.length-1; i>=0 && saved.size()<5; i--)	// Backward loop to save newest messages.
		{
			msg	= encodeMessage(msgs[i]);
			if(!saved.contains(msg))
			{
				props.addProperty(new Property(ConversationPlugin.SENT_MESSAGE, msg));
				saved.add(msg);
			}
		}
		
		return props;
	}

	/**
	 *  Convert message to a string.
	 * @param message TODO
	 */
	public String encodeMessage(IMessageEvent message)
	{
//		try
		{
			Map	map	= new HashMap();
			map.put(ConversationPlugin.ENCODED_MESSAGE_TYPE, message.getType());
			IParameter[] params	= message.getParameters();
			for(int i=0; i<params.length; i++)
			{
				map.put(params[i].getName(), params[i].getValue());
			}
			IParameterSet[]	paramsets	= message.getParameterSets();
			for(int i=0; i<paramsets.length; i++)
			{
				map.put(paramsets[i].getName(), paramsets[i].getValues());
			}
			ClassLoader cl = ((ILibraryService)agent.getServiceContainer().getService(ILibraryService.class)).getClassLoader();
			String	msg	= Nuggets.objectToXML(map, cl);
			return msg;
		}
//		catch(AgentTerminatedException e)
//		{
			// Hack!!! If agent has died, external access no longer works.
			// Access message directly.
			
			// todo: fixme
			
//			IRMessageEvent	msg	= (IRMessageEvent)((ElementWrapper)message).unwrap();
//			
//			Map	map	= new HashMap();
//			map.put(ConversationPlugin.ENCODED_MESSAGE_TYPE, msg.getType());
//			IRParameter[] params = msg.getParameters();
//			for(int i=0; i<params.length; i++)
//			{
//				map.put(params[i].getName(), params[i].getValue());
//			}
//			IRParameterSet[] paramsets = msg.getParameterSets();
//			for(int i=0; i<paramsets.length; i++)
//			{
//				map.put(paramsets[i].getName(), paramsets[i].getValues());
//			}
//			return Nuggets.objectToXML(map);
//		}
	}
	
	/**
	 *  Display messages with performative and content instead of type.
	 *  This is because the type of messages is always "fipamsg" in the
	 *  conversation center.
	 */
	class MessageListCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, 
			int index, boolean sel, boolean hasfocus)
		{		
			if(value instanceof IMessageEvent)
			{
				IMessageEvent msg = (IMessageEvent)value;
				String perf = (String)msg.getParameter(SFipa.PERFORMATIVE).getValue();
				if(perf==null)
					perf = "n/a";
				String cont;
				try
				{
					cont =""+msg.getParameter(SFipa.CONTENT).getValue();
				}
				catch(ContentException e)
				{
					cont = "invalid content";
				}
				value = perf + "( "+cont+ " )";  
			}
			return super.getListCellRendererComponent(list, value, index, sel, hasfocus);
		}
	}
}
