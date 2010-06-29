package jadex.tools.convcenter;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IEAParameter;
import jadex.bdi.runtime.IEAParameterSet;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.MessageType;
import jadex.bridge.MessageType.ParameterSpecification;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ISuspendable;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.library.ILibraryService;
import jadex.tools.common.FipaMessagePanel;
import jadex.tools.common.GuiProperties;
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
	public FipaConversationPanel(final IBDIExternalAccess agent, final IComponentIdentifier default_receiver)
	{
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		setOneTouchExpandable(true);

		this.agent	= agent;
		this.receiver	= default_receiver;
		this.regmsgs = new ArrayList();
		
		// Right side starts with initial send panel only.
		agent.getEventbase().createMessageEvent("fipamsg").addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IEAMessageEvent	msg	= (IEAMessageEvent)result;
				msg.setParameterValue(SFipa.SENDER, agent.getComponentIdentifier());
				if(default_receiver!=null)
					msg.addParameterSetValue(SFipa.RECEIVERS, default_receiver);
				sendpanel = new FipaMessagePanel(msg, agent);
		
				JButton send = new JButton("Send");
				send.setToolTipText("Send the specified message");
				send.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
				send.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						// Hack! For handling conversations.
						// If no replies are sent sentmessages are 
						cloneMessage(sendpanel.getMessage()).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
						{
							public void customResultAvailable(Object source, Object result) 
							{
								final IEAMessageEvent msgevent = (IEAMessageEvent)result;
		
								MessageType mt = msgevent.getMessageType();
								String ri = mt.getReceiverIdentifier();
								ParameterSpecification ris = mt.getParameter(ri);
								
								// Check if receiver is specified
								if(ris.isSet())
								{
									msgevent.getParameterSetValues(ri).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
									{
										public void customResultAvailable(Object source, Object result)
										{
											Object[]	values = (Object[])result;
											if(values.length==0)
											{
												noReceiverSpecified();
											}
											else
											{
												sendMessage(agent, msgevent);
											}
										}
									});
								}
								else
								{
									msgevent.getParameterValue(ri).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
									{
										public void customResultAvailable(Object source, Object result)
										{
											if(result==null)
											{
												noReceiverSpecified();
											}
											else
											{
												sendMessage(agent, msgevent);
											}
										}
									});
								}
							}
						});
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
		
				HelpBroker hb = GuiProperties.setupHelp(FipaConversationPanel.this,  "tools.conversationcenter");
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
		
				sentmsgs = new JList(new DefaultListModel());
				sentmsgs.setCellRenderer(new MessageListCellRenderer());
				sentmsgs.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent e)
					{
						if(e.getClickCount()==2 && sentmsgs.locationToIndex(e.getPoint())!=-1)
						{
							final IEAMessageEvent msg	= (IEAMessageEvent)sentmsgs.getModel()
								.getElementAt(sentmsgs.locationToIndex(e.getPoint()));
							final JPanel	msgtab	= new JPanel(new BorderLayout());
							final FipaMessagePanel	msgpanel = new FipaMessagePanel(msg, agent);
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
									cloneMessage(msg).addResultListener(new SwingDefaultResultListener()
									{
										public void customResultAvailable(Object source, final Object result) 
										{
											sendpanel.setMessage((IEAMessageEvent)result);
											tabs.setSelectedComponent(sendtab);
										}
									});
									
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
									cloneMessage(msg).addResultListener(new DefaultResultListener() 
									{
										public void resultAvailable(Object source, Object result) 
										{
											try
											{
												final IEAMessageEvent	clone = (IEAMessageEvent)result;
												agent.getEventbase().sendMessage(clone);
												SwingUtilities.invokeLater(new Runnable() 
												{
													public void run() 
													{
														((DefaultListModel)sentmsgs.getModel()).addElement(clone);
													}
												});
											}
											catch(Exception e)
											{
												String text = SUtil.wrapText("Could not send message: "+e.getMessage());
												JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text,
													"Message Error", JOptionPane.INFORMATION_MESSAGE);
											}
										}
									});
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
							getMessageTitle(msg).addResultListener(new DefaultResultListener() 
							{
								public void resultAvailable(Object source, final Object result) 
								{
									SwingUtilities.invokeLater(new Runnable() 
									{
										public void run() 
										{
											tabs.addTab((String)result, icons.getIcon("sent_message"), scroll);
										}
									});
								}
							});
							
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
								final IEAMessageEvent	msg	= (IEAMessageEvent)receivedmsgs.getModel().getElementAt(idx);
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
										agent.getEventbase().createReply(msg, "fipamsg").addResultListener(new DefaultResultListener() 
										{
											public void resultAvailable(Object source, Object result) 
											{
												final IEAMessageEvent reply = (IEAMessageEvent)result;
												reply.setParameterValue(SFipa.SENDER, agent.getComponentIdentifier());
												SwingUtilities.invokeLater(new Runnable() 
												{
													public void run() 
													{
														sendpanel.setMessage(reply);
														tabs.setSelectedComponent(sendtab);
													}
												});
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
								getMessageTitle(msg).addResultListener(new DefaultResultListener() 
								{
									public void resultAvailable(Object source, final Object result) 
									{
										SwingUtilities.invokeLater(new Runnable() 
										{
											public void run() 
											{
												tabs.addTab((String)result, icons.getIcon("received_message"), scroll);
											}
										});
									}
								});
		//						tabs.addTab(getMessageTitle(msg), icons.getIcon("received_message"), scroll);
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
							IEAMessageEvent mevent = (IEAMessageEvent)regmsgs.get(i);
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
	protected IFuture getMessageTitle(final IEAMessageEvent mevent)
	{
		final Future ret = new Future();
		
		mevent.getParameterValue(SFipa.PERFORMATIVE).addResultListener(new SwingDefaultResultListener(this) 
		{
			public void customResultAvailable(Object source, Object result) 
			{
				final String perf = (String)result;
				mevent.getParameterValue(SFipa.CONTENT).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this) 
				{
					public void customResultAvailable(Object source, Object result) 
					{
						String cont = (String)result;
						
						StringBuffer title = new StringBuffer();
						if(perf!=null)
							title.append(perf);
						else
							title.append("unknown");
						
						title.append("(");
						if(cont!=null)
							title.append(cont);
						title.append(")");
						
						String res = title.toString();
						
						if(res.length()>25)
							res	= res.substring(0, 21) + "...)";

						ret.setResult(res);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a received message.
	 */
	public void	addMessage(final IEAMessageEvent msg)
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
				setSentMessages(new IEAMessageEvent[0]);
				setReceivedMessages(new IEAMessageEvent[0]);
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
		agent.getEventbase().createMessageEvent("fipamsg").addResultListener(new SwingDefaultResultListener(this) 
		{
			public void customResultAvailable(Object source, Object result) 
			{
				final IEAMessageEvent msg = (IEAMessageEvent)result;
				msg.setParameterValue(SFipa.SENDER, agent.getComponentIdentifier());
				if(receiver!=null)
					msg.addParameterSetValue(SFipa.RECEIVERS, receiver);
				SwingUtilities.invokeLater(new Runnable() 
				{
					public void run() 
					{
						sendpanel.setMessage(msg);
					}
				});
			}
		});
				
	}
	
	//-------- helper methods --------
	
	/**
	 *  Clone a message event.
	 */
	public IFuture cloneMessage(final IEAMessageEvent msg)
	{
		final Future ret = new Future();
		
		agent.createMessageEvent(msg.getType()).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result) 
			{
				final IEAMessageEvent clone = (IEAMessageEvent)result;
				
				msg.getParameters().addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result) 
					{
						IEAParameter[] params = (IEAParameter[])result;
						for(int i=0; i<params.length; i++)
						{
							final IEAParameter param = params[i];
							msg.getParameterValue(params[i].getName()).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result) 
								{
									clone.setParameterValue(param.getName(), result);
								}
							});
		//					System.out.println("Value: "+params[i].getName()+" "+val);
						}
					}
				});
				
				msg.getParameterSets().addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result) 
					{
						IEAParameterSet[] paramsets = (IEAParameterSet[])result;
						for(int i=0; i<paramsets.length; i++)
						{
							final boolean last = i==paramsets.length-1;
							final IEAParameterSet paramset = paramsets[i];
							msg.getParameterSetValues(paramsets[i].getName()).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result) 
								{
									Object[] vals = (Object[])result;
									for(int j=0; j<vals.length; j++)
									{
										clone.addParameterSetValue(paramset.getName(), vals[j]);
									}
									
									if(last)
										ret.setResult(clone);
								}
							});
		//					System.out.println("Value: "+params[i].getName()+" "+val);
						}
					}
				});
			}
		});

		return ret;
		
//		IMessageEvent clone = agent.createMessageEvent(msg.getType());
//
//		IParameter[]	params	= msg.getParameters();
//		for(int i=0; i<params.length; i++)
//		{
//			Object val = msg.getParameter(params[i].getName()).getValue();
//			clone.getParameter(params[i].getName()).setValue(val);
////			System.out.println("Value: "+params[i].getName()+" "+val);
//		}
//
//		IParameterSet[]	paramsets	= msg.getParameterSets();
//		for(int i=0; i<paramsets.length; i++)
//		{
//			Object[]	vals	= msg.getParameterSet(paramsets[i].getName()).getValues();
//			clone.getParameterSet(paramsets[i].getName()).removeValues();
//			for(int j=0; j<vals.length; j++)
//			{
//				clone.getParameterSet(paramsets[i].getName()).addValue(vals[j]);
//			}
//		}
//
//		return clone;
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
	public IEAMessageEvent[] getSentMessages()
	{
		DefaultListModel model	= (DefaultListModel)sentmsgs.getModel();
		IEAMessageEvent[]	ret	= new IEAMessageEvent[model.getSize()];
		model.copyInto(ret);
		return ret;
	}

	/**
	 *  Set the list of sent messages.
	 */
	public void	setSentMessages(final IEAMessageEvent[] msgs)
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
	public void	setReceivedMessages(final IEAMessageEvent[] msgs)
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
		
		if(msg!=null)
		{
			decodeMessage(msg).addResultListener(new SwingDefaultResultListener(this)
			{
				public void customResultAvailable(Object source, Object result)
				{
					IEAMessageEvent	message	= (IEAMessageEvent)result;
					
					if(message!=null)
					{
						// Update sender.
						message.setParameterValue(SFipa.SENDER, agent.getComponentIdentifier());
						getMessagePanel().setMessage(message);
					}
					else
					{
						resetMessage();
					}
				}
			});
			
//			try
//			{
//			}
//			catch(Exception e)
//			{
//				final String text = SUtil.wrapText("Could not decode stored message: "+e.getMessage());
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text, "Message problem", JOptionPane.INFORMATION_MESSAGE);
//					}
//				});
//			}
		}
		
		// Load list of sent messages.
		final List sentmsgs	= new ArrayList();
		Property[]	sents	= props.getProperties(ConversationPlugin.SENT_MESSAGE);
		for(int i=0; i<sents.length; i++)
		{
			final boolean last = i == sents.length-1;
			decodeMessage(sents[i].getValue()).addResultListener(new SwingDefaultResultListener(this)
			{
				public void customResultAvailable(Object source, Object result)
				{
					IEAMessageEvent message = (IEAMessageEvent)result;
					// Update sender.
					message.setParameterValue(SFipa.SENDER, agent.getComponentIdentifier());
					sentmsgs.add(0, message);	// Re-revert order
					
					if(last)
						setSentMessages((IEAMessageEvent[])sentmsgs.toArray(new IEAMessageEvent[sentmsgs.size()]));
				}
			});
		}
			
//			try
//			{
//			}
//			catch(Exception e)
//			{
//				final String text = SUtil.wrapText("Could not decode stored message: "+e.getMessage());
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						JOptionPane.showMessageDialog(SGUI.getWindowParent(FipaConversationPanel.this), text, "Message problem", JOptionPane.INFORMATION_MESSAGE);
//					}
//				});
//			}			
	}

	/**
	 *  Fill in message values from string.
	 */
	public IFuture decodeMessage(final String msg)
	{
		final Future ret = new Future();
		
		agent.getServiceContainer().getService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ILibraryService ls = (ILibraryService)result;
				ClassLoader cl = ls.getClassLoader();
				
				final Map map = (Map)JavaReader.objectFromXML(msg, cl);
				agent.createMessageEvent((String)map.get(ConversationPlugin.ENCODED_MESSAGE_TYPE)).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IEAMessageEvent	message	= (IEAMessageEvent)result;	
						
						String[] params	= message.getMessageType().getParameterNames();
						for(int i=0; i<params.length; i++)
						{
							message.setParameterValue(params[i], map.get(params[i]));
						}
						String[] paramsets	= message.getMessageType().getParameterSetNames();
						for(int i=0; i<paramsets.length; i++)
						{
							if(map.get(paramsets[i])!=null)
							{
								message.removeParameterSetValues(paramsets[i]);
								Object[] vals = (Object[])map.get(paramsets[i]);
								for(int j=0; j<vals.length; j++)
									message.addParameterSetValue(paramsets[i], vals[j]);
							}
						}
						
						// todo: Hack! scheduled actions might not have been executed
						ret.setResult(message);
					}
				});
			}
		});

		return ret;
	}

	/** 
	 *  Get properties to be saved.
	 */
	public Properties	getProperties()
	{
		if(!SwingUtilities.isEventDispatchThread())
			throw new RuntimeException("Can only save properties from swing thread");
		ISuspendable	sus	= new ThreadSuspendable(this);
			
		Properties	props	= new Properties();
		// Save message displayed in message panel.
		IEAMessageEvent	message	= getMessagePanel().getMessage();
		String msg = (String)encodeMessage(message).get(sus);
		props.addProperty(new Property(ConversationPlugin.LAST_MESSAGE, msg));
		
		// Save list of sent messages (limit to 5 messages).
		IEAMessageEvent[]	msgs = getSentMessages();
		Set	saved	= new HashSet();	// Used to avoid duplicates;
		for(int i=msgs.length-1; i>=0 && saved.size()<5; i--)	// Backward loop to save newest messages.
		{
			msg	= (String)encodeMessage(msgs[i]).get(sus);
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
	public IFuture encodeMessage(IEAMessageEvent message)
	{
		final Future ret = new Future();
		
		final Map map = new HashMap();
		map.put(ConversationPlugin.ENCODED_MESSAGE_TYPE, message.getType());
		String[] params	= message.getMessageType().getParameterNames();
		for(int i=0; i<params.length; i++)
		{
			final String name = params[i];
			message.getParameterValue(params[i]).addResultListener(new SwingDefaultResultListener(this)
			{
				public void customResultAvailable(Object source, Object result)
				{
					map.put(name, result);
				}
			});
		}
		String[] paramsets	= message.getMessageType().getParameterSetNames();
		for(int i=0; i<paramsets.length; i++)
		{
			final boolean last = i==paramsets.length-1;
			final String name = paramsets[i];
			message.getParameterSetValues(params[i]).addResultListener(new SwingDefaultResultListener(this)
			{
				public void customResultAvailable(Object source, Object result)
				{
					map.put(name, result);
					
					if(last)
					{
						agent.getServiceContainer().getService(ILibraryService.class).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								ClassLoader cl = ((ILibraryService)result).getClassLoader();
								String	msg	= JavaWriter.objectToXML(map, cl);
								ret.setResult(msg);
							}
						});
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Send a message.
	 * @param agent
	 * @param msgevent
	 */
	protected void sendMessage(final IBDIExternalAccess agent, final IEAMessageEvent msgevent)
	{
		// Register message for conversations / replies.
		msgevent.getParameterValue(SFipa.CONVERSATION_ID).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				if(result!=null)
				{
					registerMessage(agent, msgevent);
					agent.getEventbase().sendMessage(msgevent);
					((DefaultListModel)sentmsgs.getModel()).addElement(msgevent);
				}
				else
				{
					msgevent.getParameterValue(SFipa.REPLY_WITH).addResultListener(new SwingDefaultResultListener(FipaConversationPanel.this)
					{
						public void customResultAvailable(Object source, Object result)
						{
							if(result!=null)
							{
								registerMessage(agent, msgevent);
							}
							agent.getEventbase().sendMessage(msgevent);
							((DefaultListModel)sentmsgs.getModel()).addElement(msgevent);
						}
					});
				}
			}
		});
	}

	/**
	 *  Register a message for conversations / replies.
	 * @param agent
	 * @param msgevent
	 */
	protected void registerMessage(final IBDIExternalAccess agent,
			final IEAMessageEvent msgevent)
	{
		agent.getEventbase().registerMessageEvent(msgevent);
		regmsgs.add(msgevent);
		
		msgevent.addMessageEventListener(new IMessageEventListener()
		{
			public void messageEventReceived(AgentEvent ae)
			{
				agent.getEventbase().deregisterMessageEvent(msgevent);
				msgevent.removeMessageEventListener(this);
			}
			public void messageEventSent(AgentEvent ae)
			{
			}
		});
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
			if(value instanceof IEAMessageEvent)
			{
				IEAMessageEvent msg = (IEAMessageEvent)value;
				String perf = "n/a";
				try
				{
					perf = (String)msg.getParameterValue(SFipa.PERFORMATIVE).get(new ThreadSuspendable(this));
				}
				catch(Exception e)
				{
				}
				String cont;
				try
				{
					cont =""+msg.getParameterValue(SFipa.CONTENT);
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
