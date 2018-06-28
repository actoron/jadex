package jadex.base.gui.filetree;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.commons.gui.SGUI;

/**
 *  The refresh action.
 */
public class RefreshAction extends AbstractAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"refresh", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/refresh_component.png"),
	});
	
	//-------- attributes --------

	/** The tree. */
	protected JTree tree;

	//-------- constructors --------

	/**
	 *  Create a new action.
	 */
	public RefreshAction(JTree tree)
	{
		this(getName(), icons.getIcon("refresh"), tree);
	}
	
	/**
	 *  Create a new action.
	 */
	public RefreshAction(String name, Icon icon, JTree tree)
	{
		super(name, icon);
		this.tree = tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the action is performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		TreePath[] paths = tree.getSelectionPaths();
		for(int i=0; paths!=null && i<paths.length; i++)
		{
			((ISwingTreeNode)paths[i].getLastPathComponent()).refresh(false);
		}
	}
	
	/**
	 *  Get the action name.
	 */
	public static String getName()
	{
		return "Refresh";
	}
}
