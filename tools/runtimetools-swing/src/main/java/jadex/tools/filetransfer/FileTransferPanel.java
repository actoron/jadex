package jadex.tools.filetransfer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.tree.TreePath;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.RemoteFileNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

/**
 *  Panel for showing a file transfer view composed of two
 *  panels with a file tree.
 */
public class FileTransferPanel extends JPanel implements IPropertiesProvider
{
	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter jcc;
	
	/** The split panel. */
	protected JSplitPane splitpanel;
	
	/** The first panel. */
	protected FileTransferServiceSelectorPanel p1;

	/** The second panel. */
	protected FileTransferServiceSelectorPanel p2;

	//-------- constructors --------
	
	/**
	 *  Create a new deployer panel.
	 */
	public FileTransferPanel(final IControlCenter jcc)
	{
		this.jcc = jcc;
		
		splitpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setLayout(new BorderLayout());
		add(splitpanel, BorderLayout.CENTER);

		// Local view on the left
		DeployerNodeHandler nh1 = new DeployerNodeHandler();
		DeployerNodeHandler nh2 = new DeployerNodeHandler();
		p1 = new FileTransferServiceSelectorPanel(jcc, jcc.getJCCAccess(), nh1, "Local Platform");
		p2 = new FileTransferServiceSelectorPanel(jcc, jcc.getPlatformAccess(), nh2, "Remote Platform");
		nh1.setFirstPanel(p1);
		nh1.setSecondPanel(p2);
		nh2.setFirstPanel(p2);
		nh2.setSecondPanel(p1);
		
		p1.setMinimumSize(new Dimension(1, 1));
		p2.setMinimumSize(new Dimension(1, 1));
		splitpanel.add(p1);
		splitpanel.add(p2);
		
		splitpanel.setOneTouchExpandable(true);
	
		p1.refreshCombo();
		p2.refreshCombo();
	}
	
	/**
	 *  Get the properties.
	 */
	public IFuture<Properties> getProperties()
	{
		final Future<Properties> ret = new Future<Properties>();

		final Properties props = new Properties();
		props.addProperty(new Property("split_location", ""+splitpanel.getDividerLocation()));
		
		// Only save properties of local panels.
		FileTransferServiceViewerPanel	dsvp1	= (FileTransferServiceViewerPanel)p1.getCurrentPanel();
		FileTransferServiceViewerPanel	dsvp2	= (FileTransferServiceViewerPanel)p2.getCurrentPanel();
		IFuture<Properties>	p1props	= dsvp1!=null && !dsvp1.getFileTreePanel().isRemote()
			? p1.getProperties() : new Future<Properties>((Properties)null);
		final IFuture<Properties>	p2props	= dsvp2!=null && !dsvp2.getFileTreePanel().isRemote()
			? p2.getProperties() : new Future<Properties>((Properties)null);
		
		p1props.addResultListener(new SwingDelegationResultListener<Properties>(ret)
		{
			public void customResultAvailable(Properties result)
			{
				if(result!=null)
					props.addSubproperties("first", result);
				p2props.addResultListener(new SwingDelegationResultListener<Properties>(ret)
				{
					public void customResultAvailable(Properties result)
					{
						if(result!=null)
							props.addSubproperties("second", result);
						ret.setResult(props);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Set the properties.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		Properties firstprops = props.getSubproperty("first");
		if(firstprops!=null)
			p1.setProperties(firstprops);
		Properties secondprops = props.getSubproperty("second");
		if(secondprops!=null)
			p2.setProperties(secondprops);

		splitpanel.setDividerLocation(props.getIntProperty("split_location"));
	
		return IFuture.DONE;
	}
	
	//-------- helper classes --------
	
	/**
	 *  The deployer node handler that combines
	 *  both file trees via commands.
	 */
	class DeployerNodeHandler implements ISwingNodeHandler
	{
		//-------- attributes --------
		
		/** The first panel. */
		protected FileTransferServiceSelectorPanel first;
		
		/** The second panel. */
		protected FileTransferServiceSelectorPanel second;

		AbstractAction copy = new AbstractAction("Copy file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null && second!=null)
				{
					FileTransferServiceViewerPanel.copy((FileTransferServiceViewerPanel)first.getCurrentPanel(), 
						(FileTransferServiceViewerPanel)second.getCurrentPanel(), second.getSelectedTreePath(), jcc);
				}
			}
			
			public boolean isEnabled()
			{
				return first!=null && second!=null && first.getSelectedPath()!=null && second.getSelectedPath()!=null;
			}
		};
		
		AbstractAction del = new AbstractAction("Delete file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null)
				{
					final TreePath tp = first.getSelectedTreePath();
					final String sel = first.getSelectedPath();
					if(sel!=null)
					{
						IFileTransferService ds = first.getDeploymentService();
						ds.deleteFile(sel).addResultListener(new SwingDefaultResultListener<Void>()
						{
							public void customResultAvailable(Void result)
							{
								first.refreshTreePaths(new TreePath[]{tp.getParentPath()});
								jcc.setStatusText("Deleted: "+sel);
							}
							public void customExceptionOccurred(Exception exception) 
							{
								jcc.setStatusText("Could not delete: "+sel);
							}
						});
					}
				}
			}
			
			public boolean isEnabled()
			{
				boolean ret = false;
				if(first!=null && first.getSelectedTreePath()!=null)
				{
					Object node = first.getSelectedTreePath().getLastPathComponent();
					if(node instanceof FileNode)
					{
						File file = ((FileNode)node).getFile();
						ret = !file.isDirectory() || (file instanceof JarAsDirectory && ((JarAsDirectory)file).isRoot());
					}
					else if(node instanceof RemoteFileNode)
					{
						ret = !((RemoteFileNode)node).getRemoteFile().isDirectory();
					}
				}
				return ret;
			}
		};
		
		AbstractAction rename = new AbstractAction("Rename file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null)
				{
					final TreePath tp = first.getSelectedTreePath();
					final String sel = first.getSelectedPath();
					
					if(sel!=null)
					{
						String name = JOptionPane.showInputDialog("New name: ");
						if(name==null || name.length()==0)
						{
							jcc.setStatusText("Cannot rename to empty name.");
						}
						else
						{
							IFileTransferService ds = first.getDeploymentService();
							ds.renameFile(sel, name).addResultListener(new SwingDefaultResultListener<String>()
							{
								public void customResultAvailable(String result)
								{
									first.refreshTreePaths(new TreePath[]{tp.getParentPath()});
									jcc.setStatusText("Renamed: "+sel);
								}
								public void customExceptionOccurred(Exception exception) 
								{
									jcc.setStatusText("Could not rename: "+sel+" reason: "+exception.getMessage());
								}
							});
						}
					}
				}
			}
			
			public boolean isEnabled()
			{
				return first!=null && first.getSelectedTreePath()!=null;
			}
		};
		
		AbstractAction open = new AbstractAction("Open file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null)
				{
					final TreePath tp = first.getSelectedTreePath();
					final String sel = first.getSelectedPath();
					
					if(sel!=null)
					{
						IFileTransferService ds = first.getDeploymentService();
						ds.openFile(sel).addResultListener(new SwingDefaultResultListener<Void>()
						{
							public void customResultAvailable(Void result)
							{
								first.refreshTreePaths(new TreePath[]{tp.getParentPath()});
								jcc.setStatusText("Opened: "+sel);
							}
							public void customExceptionOccurred(Exception exception) 
							{
								jcc.setStatusText("Could not open: "+sel+" reason: "+exception.getMessage());
							}
						});
					}
				}
			}
			
			public boolean isEnabled()
			{
				return first!=null && first.getSelectedTreePath()!=null;
			}
		};
	
		//-------- methods --------

		
		/**
		 *  Get the overlay for a node if any.
		 */
		public Icon	getSwingOverlay(ISwingTreeNode node)
		{
			return null;
		}

		/**
		 *  Get the overlay for a node as byte[] if any.
		 */
		public byte[] getOverlay(ITreeNode node)
		{
			return null;
		}

		/**
		 *  Get the popup actions available for all of the given nodes, if any.
		 */
		public Action[]	getPopupActions(ISwingTreeNode[] nodes)
		{
			return new Action[]{copy, del, rename, open};
		}

		/**
		 *  Get the default action to be performed after a double click.
		 */
		public Action getDefaultAction(ISwingTreeNode node)
		{
			return null;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setFirstPanel(FileTransferServiceSelectorPanel first)
		{
			 this.first = first;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setSecondPanel(FileTransferServiceSelectorPanel second)
		{
			 this.second = second;
		}
	}	
}

