package jadex.tools.deployer;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.tools.generic.AbstractServiceSelectorPanel;

import javax.swing.tree.TreePath;

/**
 *  Panel for deployment service selection.
 */
public class DeployerServiceSelectorPanel extends AbstractServiceSelectorPanel
{
	//-------- attributes --------
	
	/** The node handler. */
	protected INodeHandler nodehandler;
	
	/** The jcc access (local). */
	protected IExternalAccess jccaccess;
	
	/** The panel title. */
	protected String	title;
	
	//-------- constructors --------

	/**
	 *  Create a new selector panel.
	 */
	public DeployerServiceSelectorPanel(IExternalAccess jccaccess, IExternalAccess platformaccess, INodeHandler nodehandler, String title)
	{
		super(platformaccess, IDeploymentService.class);
		this.jccaccess = jccaccess;
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
		
		SServiceProvider.getService(jccaccess.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
						boolean remote = !jccaccess.getComponentIdentifier().getPlatformName().equals(component.getComponentIdentifier().getPlatformName());
						DeploymentServiceViewerPanel dp = new DeploymentServiceViewerPanel(component, remote, (IDeploymentService)service, nodehandler, title);
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
	public IDeploymentService getDeploymentService()
	{
		IDeploymentService ret = null;
		DeploymentServiceViewerPanel dvp = (DeploymentServiceViewerPanel)getCurrentPanel();
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
		DeploymentServiceViewerPanel dvp = (DeploymentServiceViewerPanel)getCurrentPanel();
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
		DeploymentServiceViewerPanel dvp = (DeploymentServiceViewerPanel)getCurrentPanel();
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
		DeploymentServiceViewerPanel dvp = (DeploymentServiceViewerPanel)getCurrentPanel();
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
	 *  Get the jccaccess.
	 *  @return The jccaccess.
	 */
	public IExternalAccess getJCCAccess()
	{
		return jccaccess;
	}
	
}

