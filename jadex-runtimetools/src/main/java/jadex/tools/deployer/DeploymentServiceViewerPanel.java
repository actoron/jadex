package jadex.tools.deployer;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.filetree.DefaultFileFilter;
import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileData;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.service.deployment.IDeploymentService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.gui.PopupBuilder;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;

/**
 *  The deployment service viewer panel displays 
 *  the file tree in a scroll panel.
 */
public class DeploymentServiceViewerPanel	implements IAbstractViewerPanel
{
	//-------- attributes --------

	/** The outer panel. */
	protected JPanel	panel;
	
	/** The file tree panel. */
	protected FileTreePanel ftp;
	
	/** The service. */
	protected IDeploymentService service;
	
	//-------- constructors --------

	/**
	 *  Create a new viewer panel.
	 */
	public DeploymentServiceViewerPanel(IExternalAccess exta, boolean remote, 
		IDeploymentService service, INodeHandler nodehandler, String title)
	{
		this.service = service;
		
		ftp = new FileTreePanel(exta, remote, true);
		DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(ftp.getModel());
		ftp.setPopupBuilder(new PopupBuilder(new Object[]{mic}));
		ftp.setMenuItemConstructor(mic);
		DefaultFileFilter ff = new DefaultFileFilter(mic);
		ftp.setFileFilter(ff);
		ftp.addNodeHandler(new DefaultNodeHandler(ftp.getTree()));
		if(nodehandler!=null)
			ftp.addNodeHandler(nodehandler);
		ftp.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		if(!remote)
		{
			File[] roots = File.listRoots();
			for(int i=0; i<roots.length; i++)
			{
				ftp.addTopLevelNode(roots[i]);
			}
		}
		else
		{
			service.getRoots().addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					FileData[] roots = (FileData[])result;
					for(int i=0; i<roots.length; i++)
					{
						ftp.addTopLevelNode(roots[i]);
					}
				}
			});
		}
		
		panel	= new JPanel(new BorderLayout());
		panel.add(ftp, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title+" ("+exta.getComponentIdentifier().getPlatformName()+")"));
	}
	
	/**
	 *  Set the properties
	 *  @param props
	 *  @return
	 */
	public IFuture setProperties(Properties props)
	{
		return ftp.setProperties(props);
	}
	
	/**
	 *  Get the properties.
	 *  @return
	 */
	public IFuture getProperties()
	{
		return ftp.getProperties();
	}

	/**
	 *  Shutdown the panel.
	 */ 
	public IFuture shutdown()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Get the panel id.
	 */
	public String getId()
	{
		return ""+ftp.hashCode();
	}

	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	/**
	 *  Get the selected path.
	 *  @return The selected path as string.
	 */
	public String getSelectedPath()
	{
		String[] sels = ftp.getSelectionPaths();
		return sels.length>0? sels[0]: null;
	}

	/**
	 *  Get the service.
	 *  @return the service.
	 */
	public IDeploymentService getDeploymentService()
	{
		return service;
	}
	
	/**
	 *  Get the tree panel.
	 *  @return The panel.
	 */
	public FileTreePanel getFileTreePanel()
	{
		return ftp;
	}
}
