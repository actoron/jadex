package jadex.base.gui.modeltree;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;

import java.awt.event.ActionEvent;
import java.io.File;

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
		"removepath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_removefolder.png"),
	});

	//-------- attributes --------	

	/** The tree. */
	protected FileTreePanel treepanel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action.
	 */
	public RemovePathAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action. 
	 */
	public RemovePathAction(String name, Icon icon, String desc, FileTreePanel treepanel)
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
		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm!=null && rm.getParent().equals(treepanel.getTree().getModel().getRoot());
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		// Can be used from toolbar without considering enabled state.
		if(!isEnabled())
		{
			JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel), "Only root folders can be removed.");
			return;
		}
		
		TreePath[]	selected	= treepanel.getTree().getSelectionPaths();
		for(int i=0; selected!=null && i<selected.length; i++)
		{
			final ITreeNode	node = (ITreeNode)selected[i].getLastPathComponent();
			treepanel.removeTopLevelNode(node);
			
			// todo: jars
			if(treepanel.getExternalAccess()!=null && node instanceof FileNode)
			{
				SServiceProvider.getService(treepanel.getExternalAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingDefaultResultListener(treepanel)
				{
					public void customResultAvailable(Object result)
					{
						ILibraryService ls = (ILibraryService)result;
						File file = ((FileNode)node).getFile();
						file = new File(file.getParentFile(), file.getName());
						try
						{
							ls.removeURL(file.toURI().toURL());
						}
						catch(Exception ex)
						{
	//						ex.printStackTrace();
						}
	//					resetCrawler();
	//					((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), node, index);
					}
				});
			}
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
