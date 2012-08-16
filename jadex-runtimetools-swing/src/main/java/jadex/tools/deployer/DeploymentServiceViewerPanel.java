package jadex.tools.deployer;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.filetree.DefaultFileFilter;
import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.DefaultNodeFactory;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.commons.IRemoteFilter;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PopupBuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
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
		RefreshAllAction	ra	= new RefreshAllAction(ftp, service);
		final DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(ftp.getModel(), true);
		ftp.setPopupBuilder(new PopupBuilder(new Object[]{ra, mic}));
		ftp.setMenuItemConstructor(mic);
		ftp.setNodeFactory(new DefaultNodeFactory()
		{
			public IRemoteFilter getFileFilter()
			{
				return new DefaultFileFilter(mic);
			}
		});
		ftp.addNodeHandler(new DefaultNodeHandler(ftp.getTree()));
		if(nodehandler!=null)
			ftp.addNodeHandler(nodehandler);
		ftp.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ftp.getTree().setDragEnabled(true);
		ftp.getTree().setDropMode(DropMode.ON);
		ftp.getTree().setTransferHandler(new TreeTransferHandler());
		
		// Initial state using refresh action to avoid duplicated code.
		ra.actionPerformed(null);
		
		panel	= new JPanel(new BorderLayout());
		panel.add(ftp, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title+" ("+exta.getComponentIdentifier().getPlatformName()+")"));
	}
	
	/**
	 *  Set the properties
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		return ftp.setProperties(props);
	}
	
	/**
	 *  Get the properties.
	 */
	public IFuture<Properties> getProperties()
	{
		return ftp.getProperties();
	}

	/**
	 *  Shutdown the panel.
	 */ 
	public IFuture<Void> shutdown()
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

	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setLayout(new BorderLayout());
		final JTree t = new JTree();
		
		t.setDragEnabled(true);
		t.setDropMode(DropMode.ON);
		t.setTransferHandler(new TreeTransferHandler());

		f.add(t, BorderLayout.CENTER);
		JTextField tf = new JTextField();
		tf.setDragEnabled(true);
		f.add(tf, BorderLayout.SOUTH);
		
		f.pack();
		f.setVisible(true);
	}
}

/**
 * 
 */
class TreeTransferHandler extends TransferHandler
{
	protected DataFlavor				nodesFlavor;

	protected DataFlavor[]				flavors	= new DataFlavor[1];

	protected DefaultMutableTreeNode[]	nodesToRemove;

	public TreeTransferHandler()
	{
		try
		{
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName()
					+ "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		if(!support.isDrop())
		{
			return false;
		}
		support.setShowDropLocation(true);
		if(!support.isDataFlavorSupported(nodesFlavor))
		{
			return false;
		}
		
		// Do not allow a drop on the drag source selections.
		JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
		JTree tree = (JTree)support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for(int i = 0; i < selRows.length; i++)
		{
			if(selRows[i] == dropRow)
			{
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 */
	protected Transferable createTransferable(JComponent c)
	{
		Transferable ret = null;
		
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		Object o = path.getLastPathComponent();
//		if(o instanceof IFileNode)
//		{
//			((IFileNode)o).
//		}
		if(path != null && ((ITreeNode)path.getLastPathComponent()).isLeaf())
		{
			ret = new NodesTransferable(new String[]{path.getLastPathComponent().toString()});
		}
		
		return ret;
	}

	/**
	 * 
	 */
	protected void exportDone(JComponent source, Transferable data, int action)
	{
		try
		{
			System.out.println("done: "+((String[])data.getTransferData(nodesFlavor))[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		if((action & MOVE) == MOVE)
//		{
//			JTree tree = (JTree)source;
//			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
//			// Remove nodes saved in nodesToRemove in createTransferable.
//			for(int i = 0; i < nodesToRemove.length; i++)
//			{
//				model.removeNodeFromParent(nodesToRemove[i]);
//			}
//		}
	}

	/**
	 * 
	 */
	public int getSourceActions(JComponent c)
	{
		return COPY;
	}

	/**
	 * 
	 */
	public boolean importData(TransferHandler.TransferSupport support)
	{
		boolean ret = false;
	
		if(canImport(support))
		{
			// Extract transfer data.
			DefaultMutableTreeNode[] nodes = null;
			try
			{
				Transferable t = support.getTransferable();
				Object to = t.getTransferData(nodesFlavor);
				
//				((NodesTransferable)to).setContent(new String[]{"bla"});
				
				
//				// Get drop location info.
				JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
				int idx = dl.getChildIndex();
				TreePath dest = dl.getPath();
				Object node = dest.getLastPathComponent();

				if(node instanceof IFileNode)
				{
					IFileNode n = (IFileNode)node;
					((String[])to)[0] = n.getFilePath();
					System.out.println("to: "+to);
				}
				
//				JTree tree = (JTree)support.getComponent();
//				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
//				// Configure for drop mode.
//				int index = childIndex; // DropMode.INSERT
//				if(childIndex == -1)
//				{ // DropMode.ON
//					index = parent.getChildCount();
//				}
//				// Add data to model.
//				for(int i = 0; i < nodes.length; i++)
//				{
//					model.insertNodeInto(nodes[i], parent, index++);
//				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ret = false;
			}
		}
		
		return ret;
	}

	/**
	 * 
	 */
	public String toString()
	{
		return getClass().getName();
	}

	/**
	 * 
	 */
	public class NodesTransferable implements Transferable
	{
		protected String[] content;

		public NodesTransferable(String[] content)
		{
			this.content = content;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
		{
			if(!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return content;
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return nodesFlavor.equals(flavor);
		}
	}
}
