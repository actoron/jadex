package jadex.tools.convcenter;

import jadex.base.fipa.SFipa;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.componenttree.IComponentTreeNode;
import jadex.base.gui.componenttree.INodeHandler;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.tools.help.SHelp;
import jadex.tools.jcc.AgentControlCenter;
import jadex.tools.starter.StarterPlugin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

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

//	/** String used to store the message type in encoded messages. */
//	public static final String	ENCODED_MESSAGE_TYPE	= "convcenter-encoded-message-type";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"conversation",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/new_conversation.png"),
		"message",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/message_small.png"),
		"message_overlay",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/overlay_message.png"),
		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/new_conversation_sel.png"),
		"help",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/help.gif"),
	});

	//-------- attributes --------
	
	/** The agent tree table. */
	protected ComponentTreePanel comptree;

	/** The conversation center panel. */
	protected FipaConversationPanel convcenter;

	/**
	 * @return "Conversation Center"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Conversation Center";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("conversation_sel"): icons.getIcon("conversation");
	}


	final AbstractAction SEND_MESSAGE = new AbstractAction("Send Message", icons.getIcon("conversation"))
	{
		public void actionPerformed(ActionEvent e)
		{
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode)
				{
					final IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					final IComponentIdentifier rec = node.getDescription().getName();
					// Use clone, as added component id might be modified by user.
					SServiceProvider.getServiceUpwards(jcc.getServiceProvider(), IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(comptree)
					{
						public void customResultAvailable(Object source, Object result)
						{
							IComponentManagementService cms  = (IComponentManagementService)result;
							IComponentIdentifier receiver = cms.createComponentIdentifier(rec.getName(), false, rec.getAddresses());
							Map	message	= convcenter.getMessagePanel().getMessage();
							IComponentIdentifier[]	recs	= (IComponentIdentifier[])message.get(SFipa.RECEIVERS);
							List	lrecs	= recs!=null ? new ArrayList(Arrays.asList(recs)) : new ArrayList();
							if(lrecs.contains(receiver))
							{
								lrecs.remove(receiver);
							}
							else
							{
								lrecs.add(receiver);
							}
							message.put(SFipa.RECEIVERS, (IComponentIdentifier[])lrecs.toArray(new IComponentIdentifier[lrecs.size()]));					
							convcenter.getMessagePanel().setMessage(message);
							
							comptree.getModel().fireNodeChanged(node);
						}
					});
				}
			}
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

		comptree = new ComponentTreePanel(getJCC().getServiceProvider());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);
		convcenter = new FipaConversationPanel(((AgentControlCenter)getJCC()).getAgent(), comptree);
		comptree.addNodeHandler(new INodeHandler()
		{
			public Action[] getPopupActions(IComponentTreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	allcomp	= true;
				for(int i=0; allcomp && i<nodes.length; i++)
				{
					allcomp	= nodes[i] instanceof IActiveComponentTreeNode;
				}
				
				if(allcomp)
				{
					Action	a	= new AbstractAction((String)SEND_MESSAGE.getValue(Action.NAME), icons.getIcon("message"))
					{
						public void actionPerformed(ActionEvent e)
						{
							SEND_MESSAGE.actionPerformed(e);
						}
					};
					ret	= new Action[]{a};
				}
				
				return ret;
			}
			
			public Icon getOverlay(IComponentTreeNode node)
			{
				Icon	ret	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentIdentifier	id	= ((IActiveComponentTreeNode)node).getDescription().getName();
					IComponentIdentifier[]	recs	= (IComponentIdentifier[])convcenter.getMessagePanel().getReceivers();
					if(recs!=null && Arrays.asList(recs).contains(id))
					{
						ret	= icons.getIcon("message_overlay");
					}
				}
				return ret;
			}
			
			public Action getDefaultAction(IComponentTreeNode node)
			{
				Action	a	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					a	= SEND_MESSAGE;
				}
				return a;
			}
		});

		split.add(convcenter);

		SHelp.setupHelp(split, "tools.conversationcenter");

		split.setDividerLocation(150);

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
		
		((AgentControlCenter)getJCC()).getAgent().scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
				scope.getEventbase().addMessageEventListener("fipamsg", lis);
				scope.getEventbase().addMessageEventListener("component_inform", lis);
				return null;
			}
		});
		
		
//		((AgentControlCenter)jcc).getAgent().getEventbase().addMessageEventListener("component_inform", lis);

		return split;
	}

	/**
	 * @param me
	 * @return true if the message event is not from tool_management ontology
	 */
	public void processMessage(final IMessageEvent message)
	{
		((AgentControlCenter)getJCC()).getAgent().scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess	scope	= (IBDIInternalAccess)ia;
				Map	msg	= convcenter.createMessageMap(scope, message);
				String onto	= (String)msg.get(SFipa.ONTOLOGY);
				if(onto==null || !onto.startsWith("jadex.tools"))
				{
					convcenter.addMessage(msg);										
				}						
				return null;
			}
		});
	}
	
	/**
	 *  The actions.
	 */
	public JComponent[] createToolBar()
	{
		List components = new ArrayList();
		JButton	b = new JButton(SEND_MESSAGE);
		b.setBorder(null);
		b.setToolTipText(b.getText());
		b.setText(null);
		b.setEnabled(true);
		components.add(b);
		return (JComponent[])components.toArray((new JComponent[components.size()]));
	}

	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
		Properties ps = props.getSubproperty("convcenter");
		if(ps!=null)
			convcenter.setProperties(ps);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		Properties props = new Properties();
		addSubproperties(props, "convcenter", convcenter.getProperties());
		return props;
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.base.gui.plugin.AbstractJCCPlugin#getHelpID()
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
	
	/**
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy()
	{
		return false;
	}

}
