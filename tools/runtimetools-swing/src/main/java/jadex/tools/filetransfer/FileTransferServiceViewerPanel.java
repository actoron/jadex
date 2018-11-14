package jadex.tools.filetransfer;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jadex.base.DefaultFileFilter;
import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.DefaultNodeFactory;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.commons.IAsyncFilter;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;

/**
 *  The deployment service viewer panel displays 
 *  the file tree in a scroll panel.
 */
public class FileTransferServiceViewerPanel	implements IAbstractViewerPanel
{
	//-------- attributes --------
	
	/** The outer panel. */
	protected JPanel	panel;
	
	/** The file tree panel. */
	protected FileTreePanel ftp;
	
	/** The service. */
	protected IFileTransferService service;
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	//-------- constructors --------

	/**
	 *  Create a new viewer panel.
	 */
	public FileTransferServiceViewerPanel(IExternalAccess exta, IControlCenter jcc, boolean remote, 
		IFileTransferService service, ISwingNodeHandler nodehandler, String title)
	{
		this.jcc = jcc;
		this.service = service;
		
		ftp = new FileTreePanel(exta, remote, true);
		RefreshAllAction	ra	= new RefreshAllAction(ftp, service);
		final DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(ftp.getModel(), true);
		ftp.setPopupBuilder(new PopupBuilder(new Object[]{ra, mic}));
		ftp.setMenuItemConstructor(mic);
		ftp.setNodeFactory(new DefaultNodeFactory()
		{
			public IAsyncFilter getFileFilter()
			{
				return new DefaultFileFilter(mic.isAll(), mic.getSelectedComponentTypes());
			}
		});
		ftp.addNodeHandler(new DefaultNodeHandler(ftp.getTree()));
		if(nodehandler!=null)
			ftp.addNodeHandler(nodehandler);
		ftp.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ftp.getTree().setDragEnabled(true);
		ftp.getTree().setDropMode(DropMode.ON);
		ftp.getTree().setTransferHandler(new TreeTransferHandler());
		
//		DragSourceListener[] lis = DragSource.getDefaultDragSource().getDragSourceListeners();
//		System.out.println("lis: "+SUtil.arrayToString(lis));
		
		// Initial state using refresh action to avoid duplicated code.
		ra.actionPerformed(null);
		
		panel = new JPanel(new BorderLayout());
		panel.add(ftp, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title+" ("+exta.getId().getPlatformName()+")"));
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
	public IFileTransferService getDeploymentService()
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
	public static void copy(final FileTransferServiceViewerPanel pan1, final FileTransferServiceViewerPanel pan2, final TreePath sp2, IControlCenter jcc) 
	{
		String sel1 = pan1.getSelectedPath();
		IExternalAccess exta1 = pan1.getFileTreePanel().getExternalAccess();
		copy(sel1, exta1, pan2, sp2, jcc);
	}
	
	/**
	 * 
	 */
	public static void copy(final String sel1, final IExternalAccess exta1, final FileTransferServiceViewerPanel pan2, final TreePath sp2, final IControlCenter jcc) 
	{
		IFileNode fn = ((IFileNode)sp2.getLastPathComponent());
		final String sel2 = fn.getFilePath();
		final IFileTransferService ds = pan2.getDeploymentService();
		
		if(sel1!=null && sel2!=null)
		{
			SRemoteGui.copy(sel1, exta1, sel2, ds)
				.addResultListener(new SwingIntermediateDefaultResultListener<String>(pan2.getComponent())
			{
				public void customIntermediateResultAvailable(String result)
				{
					jcc.setStatusText(result);
				}
				
				public void customFinished()
				{
					jcc.setStatusText("Copied: "+sel1+" to "+sel2);
					((ITreeNode)sp2.getLastPathComponent()).refresh(true);
				}
				
				public void customExceptionOccurred(Exception exception)
				{
					jcc.setStatusText("Copy error: "+sel1+" to: "+sel2+" exception: "+exception.getMessage());
					((ITreeNode)sp2.getLastPathComponent()).refresh(true);
				}
			});
		}
	}
	
//	/**
//	 * 
//	 */
//	protected IFuture<IExternalAccess> getJCCAccess(IExternalAccess acc, final IComponentIdentifier cid)
//	{
//		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
//		acc.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				cms.getExternalAccess(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
//			}
//		});
//		return ret;
//	}
	
	/**
	 *  Helper method that returns zips and jar not as directories.
	 *  How to get rid of that. file.isDirectory(zip) gives true :-(
	 */
	public static boolean isRealDirectory(IFileNode fn)
	{
		String name = fn.getFileName().toLowerCase();
		return !name.endsWith(".zip") && !name.endsWith(".jar") && fn.isDirectory(); 
	}
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		JFrame f = new JFrame();
//		f.setLayout(new BorderLayout());
//		final JTree t = new JTree();
//		
//		t.setDragEnabled(true);
//		t.setDropMode(DropMode.ON);
//		t.setTransferHandler(new TreeTransferHandler());
//
//		f.add(t, BorderLayout.CENTER);
//		JTextField tf = new JTextField();
//		tf.setDragEnabled(true);
//		f.add(tf, BorderLayout.SOUTH);
//		
//		f.pack();
//		f.setVisible(true);
//	}
	
	/**
	 *  Tree handler for drag and drop support.
	 */
	class TreeTransferHandler extends TransferHandler
	{
		protected DataFlavor flavor;

		protected DataFlavor[] flavors;

		/**
		 * 
		 */
		public TreeTransferHandler()
		{
//			DragSourceListener[] lis = DragSource.getDefaultDragSource().getDragSourceListeners();
//			System.out.println("listeners: "+SUtil.arrayToString(lis));
//			DragSource.getDefaultDragSource().removeDragSourceListener(lis[0]);
			
//			DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceListener()
//			{
//				public void dropActionChanged(DragSourceDragEvent dsde)
//				{
//				}
//				
//				public void dragOver(DragSourceDragEvent dsde)
//				{
//				}
//				
//				public void dragExit(DragSourceEvent dse)
//				{
//					System.out.println("dde");
//				}
//				
//				public void dragEnter(DragSourceDragEvent dsde)
//				{
//				}
//				
//				public void dragDropEnd(DragSourceDropEvent dsde)
//				{
//					System.out.println("dde");
//				}
//				
//				public String toString()
//				{
//					return "mylistener";
//				}
//			});
//			DragSource.getDefaultDragSource().addDragSourceMotionListener(dsml)
			
			this.flavors = new DataFlavor[2];
			try
			{
				String mimeType = DataFlavor.javaJVMLocalObjectMimeType+";class=\""
					+ TransferInfo.class.getName()+"\"";
				flavor = new DataFlavor(mimeType);
				flavors[0] = flavor;
				flavors[1] = DataFlavor.javaFileListFlavor;
			}
			catch(ClassNotFoundException e)
			{
				// should never happen
				e.printStackTrace();
			}
		}

		/**
		 *  Check if the 
		 */
		public boolean canImport(TransferHandler.TransferSupport support)
		{
//			System.out.println("canImp");
			boolean ret = false;
			
			if(support.isDrop())
			{
				support.setShowDropLocation(true);
				
				if(support.isDataFlavorSupported(flavor))
				{
					try
					{
						TransferInfo ti = (TransferInfo)support.getTransferable().getTransferData(flavor);
						JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
						IFileNode fn = (IFileNode)dl.getPath().getLastPathComponent();
						ret = !support.getComponent().equals(ti.getSource().getFileTreePanel().getTree()) && isRealDirectory(fn);
					}
					catch(Exception e)
					{
					}
				}
				else if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
					IFileNode fn = (IFileNode)dl.getPath().getLastPathComponent();
					ret = isRealDirectory(fn);
				}
			}
			
			return ret;
		}

		/**
		 *  Create transferable on drag start.
		 */
		protected Transferable createTransferable(JComponent c)
		{
			Transferable ret = null;
			
			JTree tree = (JTree)c;
			TreePath path = tree.getSelectionPath();
			Object o = path!=null ? path.getLastPathComponent() : null;
			if(o instanceof IFileNode && (((ITreeNode)o).isLeaf() || !isRealDirectory((IFileNode)o)))
			{
				ret = new NodesTransferable(new TransferInfo(FileTransferServiceViewerPanel.this));
			}
			
			return ret;
		}

		/**
		 *  Called after successful drop.
		 *  The target has added data to the transferable.
		 */
		protected void exportDone(JComponent source, Transferable data, int action)
		{
//			System.out.println("expDone");
			if(data!=null)
			{
				if(data.isDataFlavorSupported(flavor))
				{
					try
					{
		//				System.out.println("export done: "+data.getTransferData(flavor));
						
						TransferInfo ti = (TransferInfo)data.getTransferData(flavor);
						FileTransferServiceViewerPanel second = ti.getTarget();
	//					String sel1 = DeploymentServiceViewerPanel.this.getSelectedPath();
	//					IExternalAccess exta1 = DeploymentServiceViewerPanel.this.getFileTreePanel().getExternalAccess();
						copy(FileTransferServiceViewerPanel.this, second, ti.getSelection(), jcc);
					}
					catch(Exception e)
					{
					}
				}
				else if(data.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					System.out.println("jlf");
//					try
//					{
//		//				System.out.println("export done: "+data.getTransferData(flavor));
//						List<File> files = (List<File>)data.getTransferData(DataFlavor.javaFileListFlavor);
//						DeploymentServiceViewerPanel second = ti.getTarget();
//	//					String sel1 = DeploymentServiceViewerPanel.this.getSelectedPath();
//	//					IExternalAccess exta1 = DeploymentServiceViewerPanel.this.getFileTreePanel().getExternalAccess();
//						copy(DeploymentServiceViewerPanel.this, second, ti.getSelection(), jccaccess);
//	//					copy(DeploymentServiceViewerPanel.this, second, ti.getSelection(), jccaccess);
//					}
//					catch(Exception e)
//					{
//					}
				}
			}
		}
		
		/**
		 * 
		 */
		public void exportAsDrag(JComponent comp, InputEvent e, int action)
		{
//			System.out.println("expAsDrag");
			super.exportAsDrag(comp, e, action);
			
//			int srcActions = getSourceActions(comp);
//
//	        // only mouse events supported for drag operations
//	        if (!(e instanceof MouseEvent)
//	                // only support known actions
//	                || !(action == COPY || action == MOVE || action == LINK)
//	                // only support valid source actions
//	                || (srcActions & action) == 0) {
//
//	            action = NONE;
//	        }
//
//	        if (action != NONE && !GraphicsEnvironment.isHeadless()) {
//	            if (recognizer == null) {
//	                recognizer = new SwingDragGestureRecognizer(new DragHandler());
//	            }
//	            recognizer.gestured(comp, (MouseEvent)e, srcActions, action);
//	        } else {
//	            exportDone(comp, null, NONE);
//	        }
		}
		
//		/**
//		 * 
//		 */
//		public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException
//		{
//			System.out.println("expToClip");
//			super.exportToClipboard(comp, clip, action);
//		}

		/**
		 *  Get the allowed source actions.
		 */
		public int getSourceActions(JComponent c)
		{
			return COPY;
		}

		/**
		 *  Called when dropping on a possible target.
		 */
		public boolean importData(TransferHandler.TransferSupport support)
		{
			boolean ret = false;
		
//			System.out.println("impData: "+support);
			
			if(canImport(support))
			{
				try
				{
					Transferable t = support.getTransferable();
					
					if(t.isDataFlavorSupported(flavor))
					{
						Object to = t.getTransferData(flavor);
						
						if(to instanceof TransferInfo)
						{
							TransferInfo ti = (TransferInfo)to;
							
							JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
							TreePath dest = dl.getPath();
							Object n = dest.getLastPathComponent();
							
							if(n instanceof IFileNode && ((IFileNode)n).isDirectory())
							{
								ti.setSelection(dest);
								ti.setTarget(FileTransferServiceViewerPanel.this);
							}
						}
					}
					else if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					{
						List<File> files = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

//						System.out.println(files);
						
						JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
						TreePath dest = dl.getPath();
						Object n = dest.getLastPathComponent();
						
						if(n instanceof IFileNode && ((IFileNode)n).isDirectory())
						{
							for(File file: files)
							{
								if(file.exists())
								{
									String sel1 = file.getAbsolutePath();
									copy(sel1, jcc.getJCCAccess(), FileTransferServiceViewerPanel.this, dest, jcc);
								}
							}
						}
					}
				}
				catch(Exception e)
				{
				}
			}
			
			return ret;
		}

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return getClass().getName();
		}

		/**
		 * Transferable implementation class.
		 */
		public class NodesTransferable implements Transferable
		{
			protected TransferInfo	content;

			public NodesTransferable(TransferInfo content)
			{
				this.content = content;
			}

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException
			{
				if(!isDataFlavorSupported(flavor))
					throw new UnsupportedFlavorException(flavor);
				if(flavor.equals(TreeTransferHandler.this.flavor))
				{
					return content;
				}
				else if(flavor.equals(DataFlavor.javaFileListFlavor))
				{
					System.out.println("getTransferData: " + flavor);
//					Thread.dumpStack();
					return Collections.EMPTY_LIST;
				}
				return content;
			}

			public DataFlavor[] getTransferDataFlavors()
			{
				return flavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return SUtil.arrayToSet(flavors).contains(flavor);
			}
		}
		
		/**
		 *  The transferable content class.
		 */
		public class TransferInfo
		{
			/** The source panel. */
			protected FileTransferServiceViewerPanel source;
			
			/** The target panel. */
			protected FileTransferServiceViewerPanel target;
			
			/** The selected drop location (is not made selection in tree). */
			protected TreePath selection;
			
			/**
			 *  Create a new TransferInfo.
			 */
			public TransferInfo(FileTransferServiceViewerPanel source)
			{
				this.source = source;
			}

			/**
			 *  Get the source.
			 *  @return The source.
			 */
			public FileTransferServiceViewerPanel getSource()
			{
				return source;
			}

			/**
			 *  Set the source.
			 *  @param source The source to set.
			 */
			public void setSource(FileTransferServiceViewerPanel source)
			{
				this.source = source;
			}

			/**
			 *  Get the target.
			 *  @return The target.
			 */
			public FileTransferServiceViewerPanel getTarget()
			{
				return target;
			}

			/**
			 *  Set the target.
			 *  @param target The target to set.
			 */
			public void setTarget(FileTransferServiceViewerPanel target)
			{
				this.target = target;
			}

			/**
			 *  Get the selection.
			 *  @return The selection.
			 */
			public TreePath getSelection()
			{
				return selection;
			}

			/**
			 *  Set the selection.
			 *  @param selection The selection to set.
			 */
			public void setSelection(TreePath selection)
			{
				this.selection = selection;
			}
		}
	}
}


