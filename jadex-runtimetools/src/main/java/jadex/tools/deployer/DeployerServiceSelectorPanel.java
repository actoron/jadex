package jadex.tools.deployer;

import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.RefreshSubtreeAction;
import jadex.base.service.deployment.IDeploymentService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.SServiceProvider;
import jadex.tools.generic.AbstractServiceSelectorPanel;

/**
 *  Panel for deployment service selection.
 */
public class DeployerServiceSelectorPanel extends AbstractServiceSelectorPanel
{
	/** The node handler. */
	protected INodeHandler nodehandler;
	
	/**
	 *  Create a new selector panel.
	 */
	public DeployerServiceSelectorPanel(IExternalAccess exta, Class servicetype, INodeHandler nodehandler)
	{
		super(exta, servicetype);
		this.nodehandler = nodehandler;
	}
	
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(final IService service)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(exta.getServiceProvider(), IComponentManagementService.class)
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
						boolean remote = !exta.getComponentIdentifier().getPlatformName().equals(component.getComponentIdentifier().getPlatformName());
						DeploymentServiceViewerPanel dp = new DeploymentServiceViewerPanel(component, remote, (IDeploymentService)service, nodehandler);
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
}

