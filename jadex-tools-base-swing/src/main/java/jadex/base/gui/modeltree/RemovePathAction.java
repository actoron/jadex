package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.transformation.annotations.Classname;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

/**
 *  Action for removing a path. 
 */
public class RemovePathAction extends ToolTipAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"removepath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/remove_folder424.png"),
	});

	//-------- attributes --------	

	/** The tree. */
	protected ITreeAbstraction treepanel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action.
	 */
	public RemovePathAction(ITreeAbstraction treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action. 
	 */
	public RemovePathAction(String name, Icon icon, String desc, ITreeAbstraction treepanel)
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
		Object rm = treepanel.getTree().getLastSelectedPathComponent();
		return rm!=null; //&& rm.getParent().equals(treepanel.getTree().getModel().getRoot());
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		// Can be used from toolbar without considering enabled state.
		if(!isEnabled())
		{
			JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel.getTree()), "Only root folders can be removed.");
			return;
		}
		
		TreePath[]	selected	= treepanel.getTree().getSelectionPaths();
		for(int i=0; selected!=null && i<selected.length; i++)
		{
			treepanel.action(selected[i].getLastPathComponent());
		}
	}
	
	/**
	 *  Get the name.
	 *  @reurn The name.
	 */
	public static String getName()
	{
		return "Remove Path";
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public static Icon getIcon()
	{
		return icons.getIcon("removepath");
	}
	
	/**
	 *  Get the tooltip text.
	 *  @return The tooltip text.
	 */
	public static String getTooltipText()
	{
		return "Remove a directory / jar from the project structure";
	}
}
