package jadex.tools.filetransfer;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.RemoteFileNode;
import jadex.base.gui.filetree.RootNode;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.future.Future;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

/**
 *  The refresh all action.
 */
public class RefreshAllAction extends AbstractAction
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"overlay_refresh", SGUI.makeIcon(FileTreePanel.class, "/jadex/base/gui/images/overlay_refresh.png"),
	});
	
	//-------- attributes --------

	/** The file tree panel. */
	protected FileTreePanel ftp;
	
	/** The deployment service. */
	protected IFileTransferService	service;
	
	//-------- constructors --------

	/**
	 *  Create a new action.
	 */
	public RefreshAllAction(FileTreePanel ftp, IFileTransferService service)
	{
		super("Refresh all", icons.getIcon("overlay_refresh"));
		this.ftp	= ftp;
		this.service	= service;
	}
	
	//-------- methods --------

	/**
	 *  Called when the action is performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Future	entries	= new Future();
		if(!ftp.isRemote())
		{
			entries.setResult(File.listRoots());
		}
		else
		{
			service.getRoots().addResultListener(new SwingDelegationResultListener(entries));
		}

		entries.addResultListener(new SwingDefaultResultListener(ftp)
		{
			public void customResultAvailable(Object result)
			{
				Object[]	entries	= (Object[])result;
				RootNode	root	= (RootNode)ftp.getTree().getModel().getRoot();
				List	newchildren	= new ArrayList();
				for(int i=0; i<entries.length; i++)
				{
					// If found, reuse old node.
					for(int j=0; newchildren.size()==i && j<root.getCachedChildren().size(); j++)
					{
						ITreeNode	node	= (ITreeNode)root.getCachedChildren().get(j);
						if(node instanceof FileNode && node.getId().equals(entries[i])
							|| node instanceof RemoteFileNode && node.getId().equals(entries[i].toString()))
						{
							newchildren.add(node);
						}
					}
					
					// If not found create new node.
					if(newchildren.size()==i)
					{
						if(entries[i] instanceof File)
							ftp.addTopLevelNode((File)entries[i]);
						else
							ftp.addTopLevelNode((FileData)entries[i]);
						newchildren.add(root.getCachedChildren().get(root.getCachedChildren().size()-1));
					}
				}
				
				// Set children removes old nodes and brings remaining nodes in correct order.
				root.setChildren(newchildren);
				root.refresh(true);
			}
		});
	}
	
	/**
	 *  Get the action name.
	 */
	public static String getName()
	{
		return "Refresh all";
	}
}
