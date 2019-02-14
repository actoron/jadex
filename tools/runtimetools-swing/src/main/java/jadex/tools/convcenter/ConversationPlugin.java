package jadex.tools.convcenter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.fipa.FipaMessage;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

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
		"conversation_sel", SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/new_conversation_sel.png"),
		"help",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/help.png"),
	});

	//-------- attributes --------
	
	/** The agent tree table. */
	protected ComponentTreePanel comptree;

	/** The conversation center panel. */
	protected ConversationPanel convcenter;

	/**
	 * @return "Conversation Center"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Conversation Center";
	}
	
	/**
	 *  ConvCenter should initialize non-lazy to catch all incoming messages.
	 */
	public boolean isLazy()
	{
		return false;
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
					IComponentIdentifier receiver = new ComponentIdentifier(rec.getName());
					FipaMessage	message	= convcenter.getMessagePanel().getMessage();
					Set<IComponentIdentifier>	recs	= message.getReceivers();
					if(recs!=null && recs.contains(receiver))
					{
						message.removeReceiver(receiver);
					}
					else
					{
						message.addReceiver(receiver);
					}
					convcenter.getMessagePanel().setMessage(message);
					
					comptree.getModel().fireNodeChanged(node);
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

		comptree = new ComponentTreePanel(getJCC().getPlatformAccess(), getJCC().getJCCAccess(), getJCC().getCMSHandler(), getJCC().getPropertyHandler(), getJCC().getIconCache());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);
		convcenter = new ConversationPanel(getJCC().getPlatformAccess(), getJCC().getJCCAccess(), getJCC().getCMSHandler(), getJCC().getIconCache(), comptree);
		comptree.addNodeHandler(new ShowRemoteControlCenterHandler(getJCC(), getView()));
		comptree.addNodeHandler(new ISwingNodeHandler()
		{
			public Action[] getPopupActions(ISwingTreeNode[] nodes)
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
			
			public byte[] getOverlay(ITreeNode node)
			{
				// TODO Auto-generated method stub
				return null;
			}

			public Icon getSwingOverlay(ISwingTreeNode node)
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
			
			public Action getDefaultAction(ISwingTreeNode node)
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

//		SHelp.setupHelp(split, "tools.conversationcenter");

		split.setDividerLocation(150);
		
		
//
//		final IMessageListener listener = new IRemoteMessageListener()
//		{
//			public IFuture messageReceived(IMessageAdapter msg)
//			{
//				convcenter.addMessage(msg);
//				return IFuture.DONE;
//			}
//			public IFuture messageSent(IMessageAdapter msg)
//			{
//				return IFuture.DONE;
//			}
//		};
		
		getJCC().getPlatformAccess().scheduleStep(new IComponentStep<Collection<Object>>()
		{
			@Classname("installListener")
			public ISubscriptionIntermediateFuture<Object> execute(final IInternalAccess ia)
			{
				final SubscriptionIntermediateFuture<Object>	ret	= new SubscriptionIntermediateFuture<Object>();
				SFuture.avoidCallTimeouts(ret, ia);
				
				final IMessageHandler	handler	= new IMessageHandler()
				{
					@Override
					public boolean isRemove()
					{
						return false;
					}
					
					@Override
					public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg)
					{
						return true;
					}
					
					@Override
					public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg)
					{
						ret.addIntermediateResultIfUndone(msg);
					}
				};
				
				ia.getFeature(IMessageFeature.class).addMessageHandler(handler);
				
				ret.setTerminationCommand(new TerminationCommand()
				{
					@Override
					public void terminated(Exception reason)
					{
						ia.getFeature(IMessageFeature.class).removeMessageHandler(handler);
					}
				});
				return ret;
			}
		}).addResultListener(new IIntermediateResultListener<Object>()
		{
			@Override
			public void intermediateResultAvailable(Object result)
			{
				convcenter.addMessage(result);
			}
			@Override
			public void resultAvailable(Collection<Object> result)
			{
			}
			@Override
			public void finished()
			{
			}
			@Override
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		return split;
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
	public IFuture setProperties(Properties props)
	{
		Properties ps = props.getSubproperty("convcenter");
		if(ps!=null)
			convcenter.setProperties(ps);
		return IFuture.DONE;
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		Properties props = new Properties();
		props.addSubproperties("convcenter", convcenter.getProperties());
		return new Future(props);
	}
	
	/**
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		comptree.dispose();
		return super.shutdown();
	}
}
