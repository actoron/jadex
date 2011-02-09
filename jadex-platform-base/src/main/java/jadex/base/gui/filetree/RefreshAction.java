package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.SGUI;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class RefreshAction extends AbstractAction
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"refresh", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/refresh_component.png"),
	});
	
	/** The tree. */
	protected JTree tree;
	
	/**
	 * 
	 */
	public RefreshAction(JTree tree)
	{
		this("Refresh", icons.getIcon("refresh"), tree);
	}
	
	/**
	 * 
	 */
	public RefreshAction(String name, Icon icon, JTree tree)
	{
		super(name, icon);
		this.tree = tree;
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		TreePath[] paths = tree.getSelectionPaths();
		for(int i=0; paths!=null && i<paths.length; i++)
		{
			((ITreeNode)paths[i].getLastPathComponent()).refresh(false);
		}
	}
}
