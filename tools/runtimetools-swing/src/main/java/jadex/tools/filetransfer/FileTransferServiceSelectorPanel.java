package jadex.tools.filetransfer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.tools.generic.AbstractServiceSelectorPanel;

/**
 *  Panel for deployment service selection.
 */
public class FileTransferServiceSelectorPanel extends AbstractServiceSelectorPanel
{
	//-------- attributes --------
	
	/** The node handler. */
	protected ISwingNodeHandler nodehandler;
	
	/** The jcc (local). */
	protected IControlCenter jcc;
	
	/** The panel title. */
	protected String	title;
	
	//-------- constructors --------

	/**
	 *  Create a new selector panel.
	 */
	public FileTransferServiceSelectorPanel(IControlCenter jcc, IExternalAccess platformaccess, ISwingNodeHandler nodehandler, String title)
	{
		super(platformaccess, IFileTransferService.class);
		this.jcc = jcc;
		this.nodehandler = nodehandler;
		this.title	= title;
	}
	
	//-------- methods --------
	
	/**
	 *  Create the service panel.
	 */
	public IFuture<IAbstractViewerPanel> createPanel(final IService service)
	{
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		
		SServiceProvider.searchService(jcc.getJCCAccess(), new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)service.getServiceIdentifier().getProviderId())
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result) 
					{
						IExternalAccess component = (IExternalAccess)result; 
						boolean remote = !jcc.getJCCAccess().getComponentIdentifier().getPlatformName().equals(component.getComponentIdentifier().getPlatformName());
						FileTransferServiceViewerPanel dp = new FileTransferServiceViewerPanel(component, jcc, remote, (IFileTransferService)service, nodehandler, title);
						ret.setResult(dp);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the deployment service.
	 */
	public IFileTransferService getDeploymentService()
	{
		IFileTransferService ret = null;
		FileTransferServiceViewerPanel dvp = (FileTransferServiceViewerPanel)getCurrentPanel();
		if(dvp!=null)
		{
			ret = dvp.getDeploymentService();
		}
		return ret;
	}
	
	/**
	 *  Get the selected path.
	 */
	public String getSelectedPath()
	{
		String ret = null;
		FileTransferServiceViewerPanel dvp = (FileTransferServiceViewerPanel)getCurrentPanel();
		if(dvp!=null)
		{
			ret = dvp.getSelectedPath();
		}
		return ret;
	}
	
	/**
	 *  Get the selected path.
	 */
	public TreePath getSelectedTreePath()
	{
		TreePath ret = null;
		FileTransferServiceViewerPanel dvp = (FileTransferServiceViewerPanel)getCurrentPanel();
		if(dvp!=null)
		{
			ret = dvp.getFileTreePanel().getTree().getSelectionPath();
		}
		return ret;
	}
	
	/**
	 *  Refersh a subtree.
	 */
	public void refreshTreePaths(TreePath[] paths)
	{
		FileTransferServiceViewerPanel dvp = (FileTransferServiceViewerPanel)getCurrentPanel();
		if(dvp!=null)
		{
			if(paths==null)
				paths = dvp.getFileTreePanel().getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				((ITreeNode)paths[i].getLastPathComponent()).refresh(true);
			}
		}
	}

	/**
	 *  Get the selected path.
	 */
	public JTree getTree()
	{
		JTree ret = null;
		FileTransferServiceViewerPanel dvp = (FileTransferServiceViewerPanel)getCurrentPanel();
		if(dvp!=null)
		{
			ret = dvp.getFileTreePanel().getTree();
		}
		return ret;
	}
	
}

