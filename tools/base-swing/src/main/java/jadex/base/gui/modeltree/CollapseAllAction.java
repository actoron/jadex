package jadex.base.gui.modeltree;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Collapse all paths.
 */
public class CollapseAllAction extends ToolTipAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"collapseall",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/collapse.png"),
	});
	
	//-------- attributes --------
	
	/** The tree. */
	protected FileTreePanel treepanel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action
	 */
	public CollapseAllAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action 
	 */
	public CollapseAllAction(String name, Icon icon, String desc, FileTreePanel treepanel)
	{
		super(name, icon, desc);
		this.treepanel = treepanel;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		ISwingTreeNode rm = (ISwingTreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm==null && !treepanel.isRemote();
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		final ISwingTreeNode root = (ISwingTreeNode)treepanel.getTree().getModel().getRoot();
		root.getChildren().addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				if(result!=null)
				{
					List childs = (List)result;
					for(int i=0; i<childs.size(); i++)
					{
						ISwingTreeNode child = (ISwingTreeNode)childs.get(i);
						TreePath tp = new TreePath(new Object[]{root, child});
						treepanel.getTree().collapsePath(tp);
					}
				}
			}
		});
	}		
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public static Icon getIcon()
	{
		return icons.getIcon("collapseall");
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public static String getName()
	{
		return "Collapse all.";
	}
	
	/**
	 *  Get the tooltip text.
	 *  @return The tooltip text.
	 */
	public static String getTooltipText()
	{
		return "Collapse all opended path.";
	}
}
