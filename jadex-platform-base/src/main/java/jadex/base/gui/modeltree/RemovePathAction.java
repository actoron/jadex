package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.RootNode;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 * 
 */
public class RemovePathAction extends ToolTipAction
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"removepath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_removefolder.png"),
	});
	
	/** The tree. */
	protected FileTreePanel treepanel;
	
	/**
	 * 
	 */
	public RemovePathAction(FileTreePanel treepanel)
	{
		this("Remove Path", icons.getIcon("removepath"), "Remove a directory / jar from the project structure", treepanel);
	}
	
	/**
	 * 
	 */
	public RemovePathAction(String name, Icon icon, String desc, FileTreePanel treepanel)
	{
		super(name, icon, desc);
		this.treepanel = treepanel;
	}
	
	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm==null && !treepanel.isRemote();
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		final ITreeNode	node = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		((RootNode)treepanel.getTree().getModel().getRoot()).removeChild(node);
		
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
					catch(MalformedURLException ex)
					{
						ex.printStackTrace();
					}
//					resetCrawler();
//					((ModelExplorerTreeModel)getModel()).fireNodeRemoved(getRootNode(), node, index);
				}
			});
		}
	}
}
