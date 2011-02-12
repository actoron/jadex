package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  The default node handler offers two refresh actions.
 *  The node handler is responsible for popup menu on nodes.
 */
public class DefaultNodeHandler implements INodeHandler
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"overlay_refresh", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
		"overlay_refreshtree", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
	});
	
	//-------- attributes --------

	/** The refresh action. */
	protected AbstractAction refresh;
	
	/** The refresh tree action. */
	protected AbstractAction refreshtree;

	//-------- constructors --------

	/**
	 *  Create a new node handler.
	 */
	public DefaultNodeHandler(JTree tree)
	{
		this.refresh = new RefreshAction(tree);
		this.refreshtree = new RefreshSubtreeAction(tree);
	}
	
	//-------- methods --------

	/**
	 *  Get the overlay.
	 * 	@param node The node.
	 * 	@return The icon.
	 */
	public Icon getOverlay(ITreeNode node)
	{
		return null;
	}

	/**
	 *  Get the popup actions.
	 *  @param nodes The nodes.
	 *  @return The actions.
	 */
	public Action[] getPopupActions(ITreeNode[] nodes)
	{
		List ret = new ArrayList();
		Icon	base	= nodes[0].getIcon();
		
		Action	prefresh	= new AbstractAction((String)refresh.getValue(Action.NAME),
			base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refresh")}) : (Icon)refresh.getValue(Action.SMALL_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh.actionPerformed(e);
			}
		};
		ret.add(prefresh);
		Action	prefreshtree	= new AbstractAction((String)refreshtree.getValue(Action.NAME),
			base!=null ? new CombiIcon(new Icon[]{base, icons.getIcon("overlay_refreshtree")}) : (Icon)refreshtree.getValue(Action.SMALL_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshtree.actionPerformed(e);
			}
		};
		ret.add(prefreshtree);
	
		return (Action[])ret.toArray(new Action[0]);
	}

	/**
	 *  Get the default action.
	 *  @param node The node.
	 *  @return The action.
	 */
	public Action getDefaultAction(final ITreeNode node)
	{
		Action	ret	= null;
//		if(node.hasProperties())
//		{
//			ret	= showprops;
//		}
		return ret;
	}
}
