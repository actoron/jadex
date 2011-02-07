package jadex.tools.convcenter;

import jadex.base.fipa.FIPAMessageType;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.MessageType;
import jadex.bridge.MessageType.ParameterSpecification;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

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
	protected IBDIExternalAccess	agent;
	
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
	public FipaConversationPanel(final IBDIExternalAccess agent, Component comptree)
	{
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		setOneTouchExpandable(true);

		this.agent	= agent;
		this.regmsgs = new ArrayList();
		
		// Right side starts with initial send panel only.
		Map	msg	= new HashMap();
		msg.put(SFipa.SENDER, agent.getComponentIdentifier());
		sendpanel = new FipaMessagePanel(msg, agent, comptree);

		JButton send = new JButton("Send");
		send.setToolTipText("Send the specified message");
		send.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				// Hack! For handling conversations.
				// If no replies are sent sentmessages are 
				Map	msg	= sendpanel.getMessage();
				MessageType mt = new FIPAMessageType();	// (MessageType)msg.get(ConversationPlugin.ENCODED_MESSAGE_TYPE);
				String ri = mt.getReceiverIdentifier();
				ParameterSpecification ris = mt.getParameter(ri);
				
				// Check if receiver is specified
				if(ris.isSet())
				{
					Object	value = msg.get(ri);
					if(value==null || value instanceof Object[] && ((Object[])value).length==0)	// Hack!!! Even for set may use single cid???
					{
						noReceiverSpecified();
					}
					else
					{
						sendMessage(msg);
					}
				}
				else
				{
					if(msg.get(ri)==null)
					{
						noReceiverSpecified();
					}
					else
					{
						sendMessage(msg);
					}
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

//		HelpBroker hb = SHelp.setupHelp(FipaConversationPanel.this,  "tools.conversationcenter");
//		if(hb != null)
//		{
//			JButton help = new JButton("Help");
//			help.setToolTipText("Open the Javahelp for the Conversation Center");
//			help.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
//			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
//			south.add(help);
//		}
		sendcont.add(BorderLayout.SOUTH, south);
		final JScrollPane sendtab	= new JScrollPane(sendcont);
		sendtab.setBorder(null);

		
		// Left side contains lists of sent/received messages.
		JPanel	lists	= new JPanel(new GridBagLayout());
		GridBagConstraints	gbcons	= new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0);

		sentmsgs = new JList(new DefaultListModel());
		sentmsgs.setCellRenderer(new MessageListCellRenderer());
		sentmsgs.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2 && sentmsgs.locationToIndex(e.getPoint())!=-1)
				{
					final Map msg	= (Map)sentmsgs.getModel()
						.getElementAt(sentmsgs.locationToIndex(e.getPoint()));
					final JPanel	msgtab	= new JPanel(new BorderLayout());
					final FipaMessagePanel	msgpanel = new FipaMessagePanel(msg, agent, null);
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
							sendMessage(msg);
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
		receivedmsgs = new JList(new DefaultListModel());
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
						final Map	msg	= (Map)receivedmsgs.getModel().getElementAt(idx);
						final JPanel msgtab	= new JPanel(new BorderLayout());
						final FipaMessagePanel	msgpanel = new FipaMessagePanel(msg, agent, null);
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
								agent.scheduleStep(new IComponentStep()
								{
									@XMLClassname("reply")
									public Object execute(IInternalAccess ia)
									{
										IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
										IMessageEvent	me	= createMessageEvent(scope, msg);
										IMessageEvent	reply	= scope.getEventbase().createReply(me, "fipamsg");
										final Map	replymsg	= createMessageMap(scope, reply);
										SwingUtilities.invokeLater(new Runnable()
										{
											public void run()
											{
												replymsg.put(SFipa.SENDER, agent.getComponentIdentifier());
												sendpanel.setMessage(replymsg);
												tabs.setSelectedComponent(sendtab);
											}
										});
										return null;
									}
								});								
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
				final IMessageEvent[]	msgs	= (IMessageEvent[])regmsgs.toArray(new IMessageEvent[regmsgs.size()]);
				agent.scheduleStep(new IComponentStep()
				{
					@XMLClassname("deregister")
					public Object execute(IInternalAccess ia)
					{
						for(int i=0; i<msgs.length; i++)
						{
							IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
							scope.getEventbase().deregisterMessageEvent(msgs[i]);
						}
						return null;
					}
				});
				regmsgs.clear();
				
				((DefaultListModel)sentmsgs.getModel()).removeAllElements();
				((DefaultListModel)receivedmsgs.getModel()).removeAllElements();
				while(tabs.getComponentCount()>1)
					tabs.remove(1);
			}
		});
		cpane.add(clear);
		lists.add(cpane, gbcons);
		
		tabs	= new JTabbedPane();
		tabs.addTab("Send", icons.getIcon("new_message"), sendtab);
		
		// Initialize split panel
		add(lists);
		add(tabs);
		
		SGUI.adjustComponentSizes(FipaConversationPanel.this);
		
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
	 *  Show error when no receiver is specified.
	 */
	protected void	noReceiverSpecified()
	{
		String text = SUtil.wrapText("Cannot not send message, no receiver specified.");
		JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text,
			"Message Error", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 *  Get the message as a title.
	 */
	protected String getMessageTitle(Map msg)
	{
		StringBuffer title = new StringBuffer();
		if(msg.get(SFipa.PERFORMATIVE)!=null)
			title.append(msg.get(SFipa.PERFORMATIVE));
		else
			title.append("unknown");
		
		title.append("(");
		if(msg.get(SFipa.CONTENT)!=null)
			title.append(msg.get(SFipa.CONTENT));
		title.append(")");
		
		String res = title.toString();
		
		if(res.length()>25)
			res	= res.substring(0, 21) + "...)";

		return res;
	}
	
	/**
	 *  Add a received message.
	 */
	public void	addMessage(final Map msg)
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
		// Hack!!! Constructor chages thread context from swing to agent and back.
		// Make sure to call reset() afterwards.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				resetMessage();
				setSentMessages(new Map[0]);
				setReceivedMessages(new Map[0]);
				while(tabs.getTabCount()>1)
					tabs.removeTabAt(tabs.getTabCount()-1);
			}
		});
	}
	
	/**
	 *  Reset the message to send.
	 */
	public void	resetMessage()
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				Map	msg	= new HashMap();
				msg.put(SFipa.SENDER, agent.getComponentIdentifier());
				sendpanel.setMessage(msg);
			}
		});				
	}
	
	//-------- helper methods --------
	
	/**
	 *  Clone a message event.
	 */
	public Map	cloneMessage(Map msg)
	{	
		return new HashMap(msg);
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
	public Map[] getSentMessages()
	{
		DefaultListModel model	= (DefaultListModel)sentmsgs.getModel();
		Map[]	ret	= new Map[model.getSize()];
		model.copyInto(ret);
		return ret;
	}

	/**
	 *  Set the list of sent messages.
	 */
	public void	setSentMessages(final Map[] msgs)
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
	public void	setReceivedMessages(final Map[] msgs)
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
		try
		{
			// Load last state of message panel.
			String	msg	= props.getStringProperty(ConversationPlugin.LAST_MESSAGE);
			
			if(msg!=null)
			{
				final Map	message	= decodeMessage(msg);
				// Update sender.
				message.put(SFipa.SENDER, agent.getComponentIdentifier());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						getMessagePanel().setMessage(message);
					}
				});
			}
			else
			{
				resetMessage();
			}
			
			// Load list of sent messages.
			final List sentmsgs	= new ArrayList();
			Property[]	sents	= props.getProperties(ConversationPlugin.SENT_MESSAGE);
			for(int i=0; i<sents.length; i++)
			{
				final boolean last = i == sents.length-1;
				Map	message	= decodeMessage(sents[i].getValue());
				// Update sender.
				message.put(SFipa.SENDER, agent.getComponentIdentifier());
				sentmsgs.add(0, message);	// Re-revert order
				
				if(last)
					setSentMessages((Map[])sentmsgs.toArray(new Map[sentmsgs.size()]));
			}
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

	/**
	 *  Fill in message values from string.
	 */
	public Map decodeMessage(String msg)
	{
		Map map = (Map)JavaReader.objectFromXML(msg, null);	// Todo: classloader!?
		return map;
	}

	/** 
	 *  Get properties to be saved.
	 */
	public Properties	getProperties()
	{
		if(!SwingUtilities.isEventDispatchThread())
			throw new RuntimeException("Can only save properties from swing thread");
			
		Properties	props	= new Properties();
		// Save message displayed in message panel.
		Map	message	= getMessagePanel().getMessage();
		String msg = (String)encodeMessage(message);
		props.addProperty(new Property(ConversationPlugin.LAST_MESSAGE, msg));
		
		// Save list of sent messages (limit to 5 messages).
		Map[]	msgs = getSentMessages();
		Set	saved	= new HashSet();	// Used to avoid duplicates;
		for(int i=msgs.length-1; i>=0 && saved.size()<5; i--)	// Backward loop to save newest messages.
		{
			msg	= (String)encodeMessage(msgs[i]);
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
	 *  @param message The message.
	 */
	public String encodeMessage(Map message)
	{
		String	msg	= JavaWriter.objectToXML(message, null);	// Todo: classloader!?
		return msg;
	}
	
	/**
	 *  Send a message.
	 */
	protected void sendMessage(final Map msg)
	{
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("sendM")
			public Object execute(IInternalAccess ia)
			{
				final IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
				final IMessageEvent	me	= createMessageEvent(scope, msg);
				scope.getEventbase().sendMessage(me);
				
				// Register message for conversations / replies.
				if(msg.get(SFipa.CONVERSATION_ID)!=null || msg.get(SFipa.REPLY_WITH)!=null)
				{
					regmsgs.add(me);
					scope.getEventbase().registerMessageEvent(me);
					
					me.addMessageEventListener(new IMessageEventListener()
					{
						public void messageEventReceived(AgentEvent ae)
						{
							scope.getEventbase().deregisterMessageEvent(me);
							me.removeMessageEventListener(this);
						}
						public void messageEventSent(AgentEvent ae)
						{
						}
					});
				}
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Register message for conversations / replies.
						if(msg.get(SFipa.CONVERSATION_ID)!=null || msg.get(SFipa.REPLY_WITH)!=null)
						{
							regmsgs.add(me);
						}
						((DefaultListModel)sentmsgs.getModel()).addElement(cloneMessage(msg));
					}
				});
				return null;
			}
		});
	}
	
	/**
	 *  Create a message event from a message map.
	 */
	protected IMessageEvent	createMessageEvent(IBDIInternalAccess scope, Map msg)
	{
		IMessageEvent	me	= scope.getEventbase().createMessageEvent("fipamsg");
		MessageType	mt	= new FIPAMessageType();	// (MessageType)msg.get(ConversationPlugin.ENCODED_MESSAGE_TYPE);
				
		for(int i=0; i<mt.getParameters().length; i++)
		{
			String	name	= mt.getParameters()[i].getName();
			me.getParameter(name).setValue(msg.get(name));
		}
		for(int i=0; i<mt.getParameterSets().length; i++)
		{
			String	name	= mt.getParameterSets()[i].getName();
			if(msg.containsKey(name))
			{
				Object[]	values	= (Object[])msg.get(name);
				for(int j=0; j<values.length; j++)
				{
					me.getParameterSet(name).addValue(values[j]);
				}
			}
		}
		
		return me;
	}
	
	/**
	 *  Create a map from a message event.
	 *  @param scope	Used as marker to show that method requires running on component thread.
	 *  @param message	The message event to convert to a map.
	 */
	public Map	createMessageMap(IBDIInternalAccess scope, IMessageEvent message)
	{
		MessageType	mt	= message.getMessageType();
		Map	msg	= new HashMap();
		
		for(int i=0; i<mt.getParameters().length; i++)
		{
			String name	= mt.getParameters()[i].getName();
			msg.put(name, message.getParameter(name).getValue());
		}
		for(int i=0; i<mt.getParameterSets().length; i++)
		{
			String name	= mt.getParameterSets()[i].getName();
			msg.put(name, message.getParameterSet(name).getValues());
		}
		
		return msg;
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
			if(value instanceof Map)
			{
				Map msg = (Map)value;
				String perf = "n/a";
				try
				{
					perf = (String)msg.get(SFipa.PERFORMATIVE);
				}
				catch(Exception e)
				{
				}
				String cont;
				try
				{
					cont =""+msg.get(SFipa.CONTENT);
				}
				catch(ContentException e)
				{
					cont = "invalid content";
				}
				catch(Exception e)
				{
					cont = "n/a";
				}
				value = perf + "( "+cont+ " )";  
			}
			return super.getListCellRendererComponent(list, value, index, sel, hasfocus);
		}
	}
}
