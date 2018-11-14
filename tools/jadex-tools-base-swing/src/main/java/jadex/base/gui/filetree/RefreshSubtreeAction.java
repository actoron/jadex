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
 *  The refresh subtree action.
 */
public class RefreshSubtreeAction extends AbstractAction
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"refresh_tree", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/refresh_tree.png"),
	});
	
	//-------- attributes --------

	/** The tree. */
	protected JTree tree;
	
	//-------- constructors --------

	/**
	 *  Create a new action.
	 */
	public RefreshSubtreeAction(JTree tree)
	{
		this("Refresh subtree", icons.getIcon("refresh_tree"), tree);
	}
	
	/**
	 *  Create a new action.
	 */
	public RefreshSubtreeAction(String name, Icon icon, JTree tree)
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
		TreePath[]	paths	= tree.getSelectionPaths();
		for(int i=0; paths!=null && i<paths.length; i++)
		{
			((ISwingTreeNode)paths[i].getLastPathComponent()).refresh(true);
		}
	}
	
	/**
	 *  Get the action name.
	 */
	public static String getName()
	{
		return "Refresh subtree";
	}
}
