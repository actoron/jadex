package jadex.base.gui.filetree;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

/**
 *  The default node handler offers two refresh actions.
 *  The node handler is responsible for popup menu on nodes.
 */
public class DefaultNodeHandler implements ISwingNodeHandler
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		RefreshAction.getName(), SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		RefreshSubtreeAction.getName(), SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
	});
	
	//-------- attributes --------

	/** The actions. */
	protected List<Action> actions;
	
	/** The overlay icons. */
	protected UIDefaults overlays;

	//-------- constructors --------

	/**
	 *  Create a new node handler.
	 */
	public DefaultNodeHandler(JTree tree)
	{
		overlays = new UIDefaults();
		overlays.putAll(icons);
		actions = new ArrayList<Action>();
		actions.add(new RefreshAction(tree));
		actions.add(new RefreshSubtreeAction(tree));
	}
	
	/**
	 *  Create a new node handler.
	 */
	public DefaultNodeHandler(JTree tree, List actions, UIDefaults overlays)
	{
		this.actions = actions!=null? actions: new ArrayList();
		this.overlays = overlays!=null? overlays: new UIDefaults();
	}
	
	//-------- methods --------

	
	
	/**
	 *  Get the overlay.
	 * 	@param node The node.
	 * 	@return The icon.
	 */
	public Icon getSwingOverlay(ISwingTreeNode node)
	{
		return null;
	}

	@Override
	public byte[] getOverlay(ITreeNode node)
	{
		return null;
	}

	/**
	 *  Get the popup actions.
	 *  @param nodes The nodes.
	 *  @return The actions.
	 */
	public Action[] getPopupActions(ISwingTreeNode[] nodes)
	{
		List ret = new ArrayList();
		Icon	base	= nodes[0].getSwingIcon();
		
		for(int i=0; i<actions.size(); i++)
		{
			final Action baseaction = (Action)actions.get(i);
			if(baseaction.isEnabled())
			{
				String name = (String)baseaction.getValue(Action.NAME);
				Icon overlay = null;
				try
				{
					overlay = overlays.getIcon(name);
				}
				catch(Exception e)
				{
				}
				Action action = new AbstractAction(name, base!=null && overlay!=null? 
					new CombiIcon(new Icon[]{base, overlay}) 
					: (Icon)baseaction.getValue(Action.SMALL_ICON))
				{
					public void actionPerformed(ActionEvent e)
					{
						baseaction.actionPerformed(e);
					}
				};
				ret.add(action);
			}
		}
		
		return (Action[])ret.toArray(new Action[0]);
	}

	/**
	 *  Get the default action.
	 *  @param node The node.
	 *  @return The action.
	 */
	public Action getDefaultAction(final ISwingTreeNode node)
	{
		Action	ret	= null;
//		if(node.hasProperties())
//		{
//			ret	= showprops;
//		}
		return ret;
	}
	
	/**
	 *  Add action.
	 */
	public void addAction(Action action, Icon overlay)
	{
		if(action==null)
			throw new IllegalArgumentException("Action must not be null.");
		actions.add(action);
		if(overlay!=null)
			overlays.put(action.getValue(Action.NAME), overlay);
	}
	
	/**
	 *  Get an action with a name.
	 */
	public Action getAction(String name)
	{
		for(Action action: actions)
		{
			if(action.getValue(Action.NAME).equals(name))
			{
				return action;
			}
		}
		
		return null;
	}
}
