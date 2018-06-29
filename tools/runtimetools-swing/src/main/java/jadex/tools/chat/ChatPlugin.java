package jadex.tools.chat;

import javax.swing.Icon;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

/**
 *  The chat plugin is used to wrap the chat panel as JCC plugin.
 */
public class ChatPlugin extends AbstractServicePlugin
{
	//-------- constants --------

	/** The plugin name. */
	public static final String	PLUGIN_NAME	= "Chat";
	
	static
	{
		icons.put("chat", SGUI.makeIcon(ChatPlugin.class, "images/chat.png"));
		icons.put("chat_sel", SGUI.makeIcon(ChatPlugin.class, "images/chat.png"));
		icons.put("chat_small", SGUI.makeIcon(ChatPlugin.class, "images/chat_small.png"));
		icons.put("chat_small_star", SGUI.makeIcon(ChatPlugin.class, "images/chat_small_star.png"));
		icons.put("star", SGUI.makeIcon(ChatPlugin.class, "images/star.png"));
	}
	
	/**
	 *  Get the icon for the chat.
	 */
	public static Icon	getStatusIcon(boolean star)
	{
		return star ? icons.getIcon("chat_small_star") : icons.getIcon("chat_small");
	}

	/**
	 *  Get the icon for a changed tab.
	 */
	public static Icon	getTabIcon()
	{
		return icons.getIcon("star");
	}

	//-------- methods --------
	
//	/**
//	 *  Overridden to activate chat on JCC startup.
//	 */
//	public boolean isLazy()
//	{
//		return false;
//	}
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class<?> getServiceType()
	{
		return IChatGuiService.class;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service)
	{
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		final ChatPanel panel = new ChatPanel();
		panel.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(panel);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("chat_sel"): icons.getIcon("chat");
	}

	/**
	 *  Get the plugin name.
	 */
	public String getName()
	{
		return PLUGIN_NAME;
	}
}
