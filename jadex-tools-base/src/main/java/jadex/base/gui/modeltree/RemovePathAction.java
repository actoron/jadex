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
			ITreeNode	node = (ITreeNode)selected[i].getLastPathComponent();
			treepanel.removeTopLevelNode(node);
			
			if(treepanel.getExternalAccess()!=null && node instanceof IFileNode)
			{
				final String	path	= ((IFileNode)node).getFilePath();
				treepanel.getExternalAccess().scheduleStep(new IComponentStep<Void>()
				{
					@Classname("removeURL")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						final Future	ret	= new Future();
						SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new DelegationResultListener<ILibraryService>(ret)
						{
							public void customResultAvailable(ILibraryService ls)
							{
								try
								{
									ls.removeURL(SUtil.toURL(path));
									ret.setResult(null);
								}
								catch(Exception ex)
								{
									ret.setException(ex);
								}
							}
						});
						return ret;
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
