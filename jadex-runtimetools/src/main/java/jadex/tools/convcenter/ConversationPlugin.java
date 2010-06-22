package jadex.tools.convcenter;

import jadex.base.DefaultResultListener;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IEAEventbase;
import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IEAParameterSet;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.IParameterSet;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.tools.common.ComponentTreeTable;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.jcc.AgentControlCenter;
import jadex.tools.starter.StarterPlugin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  The conversation plugin
 */
public class ConversationPlugin extends AbstractJCCPlugin
{
	//-------- constants --------
	
	/** The property storing the last state of the message panel. */ 
	public static final String	LAST_MESSAGE	= "lastmessage";

	/** The property storing sent messages (from 0..4). */ 
	public static final String	SENT_MESSAGE	= "sentmessage";

	/** String used to store the message type in encoded messages. */
	public static final String	ENCODED_MESSAGE_TYPE	= "encoded-message-type";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"conversation",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/new_conversation.png"),
		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_conversation_sel.png"),
		"help",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/help.gif"),
	});

	//-------- attributes --------
	
	/** The agent tree table. */
	protected ComponentTreeTable agents;

	/** The conversation center panel. */
	protected FipaConversationPanel convcenter;

	/**
	 * @return "Conversation Center"
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Conversation Center";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("conversation_sel"): icons.getIcon("conversation");
	}


	final AbstractAction SEND_MESSAGE = new AbstractAction("Send Message", icons.getIcon("conversation"))
	{
		public void actionPerformed(ActionEvent e)
		{
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			IComponentDescription desc = (IComponentDescription)node.getUserObject();
			// Use clone, as added component id might be modified by user.
			IComponentManagementService ces  = (IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class);
			IComponentIdentifier rec = desc.getName();
			final IComponentIdentifier receiver = ces.createComponentIdentifier(rec.getName(), false, rec.getAddresses());
			final IEAMessageEvent	message	= convcenter.getMessagePanel().getMessage();
			message.getParameterSet(SFipa.RECEIVERS).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result) 
				{
					final IEAParameterSet rcvs = (IEAParameterSet)result;
					
					rcvs.containsValue(receiver).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result) 
						{
							if(((Boolean)result).booleanValue())
							{
								rcvs.removeValue(receiver);
							}
							else
							{
								rcvs.addValue(receiver);
							}
							
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run() 
								{
									convcenter.getMessagePanel().setMessage(message);
								};
							});
						}
					});
				};
			});
			
//			if(rcvs.containsValue(receiver))
//			{
//				rcvs.removeValue(receiver);
//			}
//			else
//			{
//				rcvs.addValue(receiver);
//			}
		}
	};
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		split.setOneTouchExpandable(true);

		agents = new ComponentTreeTable(((AgentControlCenter)getJCC()).getServiceContainer());
		agents.setMinimumSize(new Dimension(0, 0));
		split.add(agents);
		agents.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agents.getNodeType(ComponentTreeTable.NODE_COMPONENT).addPopupAction(SEND_MESSAGE);

		split.add(convcenter = new FipaConversationPanel(((AgentControlCenter)getJCC()).getAgent(), null));

		GuiProperties.setupHelp(split, "tools.conversationcenter");

		agents.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		split.setDividerLocation(150);

		agents.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					SEND_MESSAGE.actionPerformed(null);
				}
			}
		});

		IComponentManagementService ces = (IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class);
		IFuture ret = ces.getComponentDescriptions();
		ret.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentDescription[] res = (IComponentDescription[])result;
				for(int i=0; i<res.length; i++)
					agentBorn(res[i]);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
		
		ces.addComponentListener(null, new IComponentListener()
		{
			public void componentRemoved(IComponentDescription desc, java.util.Map results)
			{
				agentDied(desc);
			}
			
			public void componentAdded(IComponentDescription desc)
			{
				agentBorn(desc);
			}

			public void componentChanged(IComponentDescription desc)
			{
			}
		});
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				agents.adjustColumnWidths();
//			}
//		});
		
//		jcc.addMessageListener(this);
		
		final IMessageEventListener lis = new IMessageEventListener()
		{
			public void messageEventSent(AgentEvent ae)
			{
//				System.out.println("messageEventSent");
			}
			
			public void messageEventReceived(AgentEvent ae)
			{
				processMessage((IMessageEvent)ae.getSource());
			}
		};
		
		((AgentControlCenter)jcc).getAgent().getEventbase().addMessageEventListener("fipamsg", lis);
		((AgentControlCenter)jcc).getAgent().getEventbase().addMessageEventListener("component_inform", lis);
		
		
//		((AgentControlCenter)jcc).getAgent().getEventbase().addMessageEventListener("component_inform", lis);

		return split;
	}

	/**
	 * @param me
	 * @return true if the message event is not from tool_management ontology
	 */
	public boolean processMessage(IMessageEvent message)
	{
		boolean	processed = false;
		try
		{
			String onto = message.hasParameter(SFipa.ONTOLOGY)? 
				(String)message.getParameter(SFipa.ONTOLOGY).getValue(): null;
			if(onto==null || !onto.startsWith("jadex.tools"))
			{
				convcenter.addMessage(message);
				processed	= true;
			}
		}
		catch(Exception e)
		{
		}
		return processed;
	}
	


	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
		Properties ps = props.getSubproperty("convcenter");
		if(ps!=null)
			convcenter.setProperties(ps);
		ps = props.getSubproperty("agents");
		if(ps!=null)
			agents.setProperties(ps);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		Properties props = new Properties();
		addSubproperties(props, "convcenter", convcenter.getProperties());
		addSubproperties(props, "agents", agents.getProperties());
		return props;
	}

	/**
	 * @param ad
	 */
	public void agentDied(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				agents.removeComponent(ad);
			}
		});
	}

	/**
	 * @param ad
	 */
	public void agentBorn(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				agents.addComponent(ad);
			}
		});
	}
	
	/**
	 * @param ad
	 */
	public void agentChanged(final IComponentDescription ad)
	{
		// nop?
		// Update components on awt thread.
		/*SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				agents.addAgent(ad);
			}
		});*/
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.tools.jcc.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.conversationcenter";
	}
	
	/**
	 *  Reset the conversation center to an initial state
	 */
	public void	reset()
	{
		convcenter.reset();
	}
}
