package jadex.tools.convcenter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IRemoteMessageListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageListener;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.Properties;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
		"help",	SGUI.makeIcon(ConversationPlugin.class, "/jadex/tools/common/images/help.gif"),
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
					IComponentIdentifier receiver = new BasicComponentIdentifier(rec.getName());
					Map	message	= convcenter.getMessagePanel().getMessage();
					MessageType	mt	= (MessageType)message.get(ConversationPanel.MESSAGE_TYPE);
					IComponentIdentifier[]	recs	= (IComponentIdentifier[])message.get(mt.getReceiverIdentifier());
					List	lrecs	= recs!=null ? new ArrayList(Arrays.asList(recs)) : new ArrayList();
					if(lrecs.contains(receiver))
					{
						lrecs.remove(receiver);
					}
					else
					{
						lrecs.add(receiver);
					}
					message.put(mt.getReceiverIdentifier(), (IComponentIdentifier[])lrecs.toArray(new IComponentIdentifier[lrecs.size()]));					
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
		convcenter = new ConversationPanel(getJCC().getPlatformAccess(), getJCC().getJCCAccess(), getJCC().getCMSHandler(), getJCC().getIconCache(), comptree, SFipa.FIPA_MESSAGE_TYPE);
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
		
		

		final IMessageListener listener = new IRemoteMessageListener()
		{
			public IFuture messageReceived(IMessageAdapter msg)
			{
				convcenter.addMessage(msg);
				return IFuture.DONE;
			}
			public IFuture messageSent(IMessageAdapter msg)
			{
				return IFuture.DONE;
			}
		};
		
		getJCC().getPlatformAccess().scheduleStep(new IComponentStep<Void>()
		{
			@Classname("installListener")
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				SServiceProvider.getService(ia, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener(ia.getLogger())
				{
					public void resultAvailable(Object result)
					{
						IMessageService	ms	= (IMessageService)result;
						ms.addMessageListener(listener, new IFilter()
						{
							public boolean filter(Object obj)
							{
								IMessageAdapter	msg	= (IMessageAdapter)obj;
								boolean	tojcc	= false;
								Object	rec	= msg.getValue(msg.getMessageType().getReceiverIdentifier());
								if(SReflect.isIterable(rec))
								{
									for(Iterator it=SReflect.getIterator(rec); !tojcc && it.hasNext(); )
									{
										tojcc	= ia.getComponentIdentifier().equals(it.next());
									}
								}
								else
								{
									tojcc	= ia.getComponentIdentifier().equals(rec);
								}
								
								return tojcc;
							}
						});
					}
				}));
				return IFuture.DONE;
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
