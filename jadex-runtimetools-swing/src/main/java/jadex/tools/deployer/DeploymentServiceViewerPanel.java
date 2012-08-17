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
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.IRemoteFilter;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.Tuple3;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.future.SwingResultListener;
import jadex.tools.jcc.JCCAgent;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Collection;

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
	
	/** The jcc access. */
	protected IExternalAccess jccaccess;
	
	//-------- constructors --------

	/**
	 *  Create a new viewer panel.
	 */
	public DeploymentServiceViewerPanel(IExternalAccess exta, IExternalAccess jccaccess, boolean remote, 
		IDeploymentService service, INodeHandler nodehandler, String title)
	{
		this.jccaccess = jccaccess;
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
		
		panel = new JPanel(new BorderLayout());
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

//	/**
//	 * 
//	 */
//	public static void copy(final String sel1, final String sel2, final IExternalAccess exta1, final IExternalAccess exta2,  
//		 final JTree tree2, final IDeploymentService ds2, IExternalAccess jccaccess) 
//	{
//		if(sel1!=null && sel2!=null)
//		{
//			final IComponentIdentifier lcid = jccaccess.getComponentIdentifier();
////			final IDeploymentService ds = second.getDeploymentService();
//			
//			exta1.scheduleStep(new IComponentStep<Void>()
//			{
//				public IFuture<Void> execute(final IInternalAccess ia)
//				{
//					final Future<Void> ret = new Future<Void>();
//					
//					SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//					{
//						public void customResultAvailable(IComponentManagementService cms)
//						{
//							cms.getExternalAccess(lcid).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//							{
//								public void customResultAvailable(final IExternalAccess jccacc) 
//								{
//									try
//									{
//										final File source = new File(sel1);
//										final FileInputStream fis = new FileInputStream(source);
//										ServiceOutputConnection soc = new ServiceOutputConnection();
//										soc.writeFromInputStream(fis, exta1);//.addResultListener(new IIntermediateResultListener<Long>()
////										{
////											public void intermediateResultAvailable(Long result) 
////											{
////												System.out.println("wro ira: "+result);
////											}
////											public void finished()
////											{
////												System.out.println("wro fin");
////											}
////											public void resultAvailable(Collection<Long> result)
////											{
////												System.out.println("wro ra: "+result);
////											}
////											public void exceptionOccurred(Exception exception)
////											{
////												System.out.println("wro ex: "+exception);
////											}
////										});
//										ITerminableIntermediateFuture<Long> fut = ds2.uploadFile(soc.getInputConnection(), sel2, source.getName());
//										fut.addResultListener(ia.createResultListener(new IIntermediateResultListener<Long>()
//										{
//											public void intermediateResultAvailable(final Long result)
//											{
////												System.out.println("rec: "+result);
//												jccacc.scheduleStep(new IComponentStep<Void>()
//												{
//													public IFuture<Void> execute(IInternalAccess ia)
//													{
//														double done = ((int)((result/(double)source.length())*10000))/100.0;
////														System.out.println("done: "+done);
//														DecimalFormat fm = new DecimalFormat("#0.00");
//														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy "+fm.format(done)+"% done ("+SUtil.bytesToString(result)+" / "+SUtil.bytesToString(source.length())+")");
//														return IFuture.DONE;
//													}
//												});
//											}
//											
//											public void finished()
//											{
//												jccacc.scheduleStep(new IComponentStep<Void>()
//												{
//													public IFuture<Void> execute(IInternalAccess ia)
//													{
//														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copied: "+sel1+" to "+sel2);
//														ret.setResult(null);
//														return IFuture.DONE;
//													}
//												});
////												second.refreshTreePaths(null);
//											}
//											
//											public void resultAvailable(Collection<Long> result)
//											{
//												finished();
//											}
//											
//											public void exceptionOccurred(final Exception exception)
//											{
//												jccacc.scheduleStep(new IComponentStep<Void>()
//												{
//													public IFuture<Void> execute(IInternalAccess ia)
//													{
//														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy error: "+sel1+" to: "+sel2+" exception: "+exception.getMessage());
//														ret.setResult(null);
//														return IFuture.DONE;
//													}
//												});
//											}
//										}));
//									}
//									catch(final Exception ex)
//									{
//										ret.setResult(null);
//										jccacc.scheduleStep(new IComponentStep<Void>()
//										{
//											public IFuture<Void> execute(IInternalAccess ia)
//											{
//												((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy error: "+sel1+" "+ex.getMessage());
//												return IFuture.DONE;
//											}
//										});
////										jcc.setStatusText("Copy error: "+sel_1+" "+ex.getMessage());
//									}
//								}
//							}));
//						}
//					}));
//					
//					return ret;
//				}
//			}).addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
//			{
//				public void resultAvailable(Void result)
//				{
//					refreshTreePaths();
//				}
//				public void exceptionOccurred(Exception exception)
//				{
//					refreshTreePaths();
//				}
//				
//				protected void refreshTreePaths()
//				{
//					TreePath[] paths = tree2.getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						((ITreeNode)paths[i].getLastPathComponent()).refresh(true);
//					}
//				}
//			}));
//		}
//	}
	
	/**
	 * 
	 */
	public static void copy(final DeploymentServiceViewerPanel pan1, final DeploymentServiceViewerPanel pan2, final TreePath sp2, IExternalAccess jccaccess) 
	{
		final String sel1 = pan1.getSelectedPath();
		final String sel2 = ((IFileNode)sp2.getLastPathComponent()).getFilePath();
		final IDeploymentService ds = pan2.getDeploymentService();
		
		System.out.println("sel1: "+sel1+" sel2:"+sel2);
		
		if(sel1!=null && sel2!=null)
		{
			final IComponentIdentifier lcid = jccaccess.getComponentIdentifier();
			
			final IExternalAccess exta1 = pan1.getFileTreePanel().getExternalAccess();
			exta1.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					final Future<Void> ret = new Future<Void>();
					
					SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							cms.getExternalAccess(lcid).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
							{
								public void customResultAvailable(final IExternalAccess jccacc) 
								{
									try
									{
										final File source = new File(sel1);
										final FileInputStream fis = new FileInputStream(source);
										ServiceOutputConnection soc = new ServiceOutputConnection();
										soc.writeFromInputStream(fis, exta1);//.addResultListener(new IIntermediateResultListener<Long>()
//										{
//											public void intermediateResultAvailable(Long result) 
//											{
//												System.out.println("wro ira: "+result);
//											}
//											public void finished()
//											{
//												System.out.println("wro fin");
//											}
//											public void resultAvailable(Collection<Long> result)
//											{
//												System.out.println("wro ra: "+result);
//											}
//											public void exceptionOccurred(Exception exception)
//											{
//												System.out.println("wro ex: "+exception);
//											}
//										});
										ITerminableIntermediateFuture<Long> fut = ds.uploadFile(soc.getInputConnection(), sel2, source.getName());
										fut.addResultListener(ia.createResultListener(new IIntermediateResultListener<Long>()
										{
											public void intermediateResultAvailable(final Long result)
											{
//												System.out.println("rec: "+result);
												jccacc.scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														double done = ((int)((result/(double)source.length())*10000))/100.0;
//														System.out.println("done: "+done);
														DecimalFormat fm = new DecimalFormat("#0.00");
														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy "+fm.format(done)+"% done ("+SUtil.bytesToString(result)+" / "+SUtil.bytesToString(source.length())+")");
														return IFuture.DONE;
													}
												});
											}
											
											public void finished()
											{
												jccacc.scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copied: "+sel1+" to "+sel2);
														ret.setResult(null);
														return IFuture.DONE;
													}
												});
//												second.refreshTreePaths(null);
											}
											
											public void resultAvailable(Collection<Long> result)
											{
												finished();
											}
											
											public void exceptionOccurred(final Exception exception)
											{
												jccacc.scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy error: "+sel1+" to: "+sel2+" exception: "+exception.getMessage());
														ret.setResult(null);
														return IFuture.DONE;
													}
												});
											}
										}));
									}
									catch(Exception ex)
									{
										ret.setResult(null);
										final String extxt = ex.getMessage();
										jccacc.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy error: "+sel1+" "+extxt);
												return IFuture.DONE;
											}
										});
									}
								}
							}));
						}
					}));
					
					return ret;
				}
			}).addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					refreshTreePaths();
				}
				public void exceptionOccurred(Exception exception)
				{
					refreshTreePaths();
				}
				
				protected void refreshTreePaths()
				{
					System.out.println("ref: "+sp2.getLastPathComponent());
					((ITreeNode)sp2.getLastPathComponent()).refresh(true);
//					TreePath[] paths = pan2.getFileTreePanel().getTree().getSelectionPaths();
//					for(int i=0; paths!=null && i<paths.length; i++)
//					{
//						((ITreeNode)paths[i].getLastPathComponent()).refresh(true);
//					}
				}
			}));
		}
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
	 * 
	 */
	class TreeTransferHandler extends TransferHandler
	{
		protected DataFlavor flavor;

		protected DataFlavor[] flavors	= new DataFlavor[1];

		public TreeTransferHandler()
		{
			this.flavors = new DataFlavor[1];
			try
			{
				String mimeType = DataFlavor.javaJVMLocalObjectMimeType+";class=\""
					+ TransferInfo.class.getName()+"\"";
				flavor = new DataFlavor(mimeType);
				flavors[0] = flavor;
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
			if(!support.isDataFlavorSupported(flavor))
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
			if(path != null && ((ITreeNode)o).isLeaf() && o instanceof IFileNode)
			{
//				IFileNode fn = (IFileNode)o;
//				TransferInfo ti = new TransferInfo(fn.getFilePath());
				ret = new NodesTransferable(new TransferInfo());
			}
			
			return ret;
		}

		/**
		 * 
		 */
		protected void exportDone(JComponent source, Transferable data, int action)
		{
			if(data==null || !data.isDataFlavorSupported(flavor))
				return;
			
			try
			{
//				System.out.println("export done: "+data.getTransferData(flavor));
				
				Object o = data.getTransferData(flavor);
				if(o instanceof TransferInfo)
				{
					TransferInfo ti = (TransferInfo)o;
					DeploymentServiceViewerPanel second = ti.getTarget();
					copy(DeploymentServiceViewerPanel.this, second, ti.getSelection(), jccaccess);
				}
			}
			catch(Exception e)
			{
			}
			
//			if((action & MOVE) == MOVE)
//			{
//				JTree tree = (JTree)source;
//				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
//				// Remove nodes saved in nodesToRemove in createTransferable.
//				for(int i = 0; i < nodesToRemove.length; i++)
//				{
//					model.removeNodeFromParent(nodesToRemove[i]);
//				}
//			}
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
				try
				{
					Transferable t = support.getTransferable();
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
							ti.setTarget(DeploymentServiceViewerPanel.this);
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
			protected TransferInfo content;
			
			public NodesTransferable(TransferInfo content)
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
				return flavor.equals(flavor);
			}
		}
		
		/**
		 * 
		 */
		public class TransferInfo
		{
			protected DeploymentServiceViewerPanel target;

			protected TreePath selection;
			
			/**
			 *  Get the target.
			 *  @return The target.
			 */
			public DeploymentServiceViewerPanel getTarget()
			{
				return target;
			}

			/**
			 *  Set the target.
			 *  @param target The target to set.
			 */
			public void setTarget(DeploymentServiceViewerPanel target)
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

