package jadex.tools.deployer;

import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.JarAsDirectory;
import jadex.base.gui.filetree.RemoteFileNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.tools.jcc.JCCAgent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.TreePath;

/**
 *  Panel for showing a file transfer view composed of two
 *  panels with a file tree.
 */
public class DeployerPanel extends JPanel implements IPropertiesProvider
{
	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter jcc;
	
	/** The split panel. */
	protected JSplitPane splitpanel;
	
	/** The first panel. */
	protected DeployerServiceSelectorPanel p1;

	/** The second panel. */
	protected DeployerServiceSelectorPanel p2;

	//-------- constructors --------
	
	/**
	 *  Create a new deloyer panel.
	 */
	public DeployerPanel(final IControlCenter jcc)
	{
		this.jcc = jcc;
		
		splitpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setLayout(new BorderLayout());
		add(splitpanel, BorderLayout.CENTER);

		// Local view on the left
		DeployerNodeHandler nh1 = new DeployerNodeHandler();
		DeployerNodeHandler nh2 = new DeployerNodeHandler();
		p1 = new DeployerServiceSelectorPanel(jcc.getJCCAccess(), jcc.getJCCAccess(), nh1, "Local Platform");
		p2 = new DeployerServiceSelectorPanel(jcc.getJCCAccess(), jcc.getPlatformAccess(), nh2, "Remote Platform");
		nh1.setFirstPanel(p1);
		nh1.setSecondPanel(p2);
		nh2.setFirstPanel(p2);
		nh2.setSecondPanel(p1);
		
		splitpanel.add(new JScrollPane(p1));
		splitpanel.add(new JScrollPane(p2));
		
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
		DeploymentServiceViewerPanel	dsvp1	= (DeploymentServiceViewerPanel)p1.getCurrentPanel();
		DeploymentServiceViewerPanel	dsvp2	= (DeploymentServiceViewerPanel)p2.getCurrentPanel();
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
	class DeployerNodeHandler implements INodeHandler
	{
		//-------- attributes --------
		
		/** The first panel. */
		protected DeployerServiceSelectorPanel first;
		
		/** The second panel. */
		protected DeployerServiceSelectorPanel second;

		AbstractAction copy = new AbstractAction("Copy file")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(first!=null && second!=null)
				{
					final String sel_1 = first.getSelectedPath();
					final String sel_2 = second.getSelectedPath();
					final IDeploymentService ds = second.getDeploymentService();
					
					if(sel_1!=null && sel_2!=null)
					{
						final IComponentIdentifier lcid = jcc.getJCCAccess().getComponentIdentifier();
						
						first.getPlatformAccess().scheduleStep(new IComponentStep<Void>()
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
													final File source = new File(sel_1);
													final FileInputStream fis = new FileInputStream(source);
													ServiceOutputConnection soc = new ServiceOutputConnection();
													soc.writeFromInputStream(fis, first.getPlatformAccess());//.addResultListener(new IIntermediateResultListener<Long>()
//													{
//														public void intermediateResultAvailable(Long result) 
//														{
//															System.out.println("wro ira: "+result);
//														}
//														public void finished()
//														{
//															System.out.println("wro fin");
//														}
//														public void resultAvailable(Collection<Long> result)
//														{
//															System.out.println("wro ra: "+result);
//														}
//														public void exceptionOccurred(Exception exception)
//														{
//															System.out.println("wro ex: "+exception);
//														}
//													});
													ITerminableIntermediateFuture<Long> fut = ds.uploadFile(soc.getInputConnection(), sel_2, source.getName());
													fut.addResultListener(ia.createResultListener(new IIntermediateResultListener<Long>()
													{
														public void intermediateResultAvailable(final Long result)
														{
//															System.out.println("rec: "+result);
															jccacc.scheduleStep(new IComponentStep<Void>()
															{
																public IFuture<Void> execute(IInternalAccess ia)
																{
																	double done = ((int)((result/(double)source.length())*10000))/100.0;
//																	System.out.println("done: "+done);
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
																	((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copied: "+sel_1+" to "+sel_2);
																	ret.setResult(null);
																	return IFuture.DONE;
																}
															});
//															second.refreshTreePaths(null);
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
																	((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copy error: "+sel_1+" to: "+sel_2+" exception: "+exception.getMessage());
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
													jcc.setStatusText("Copy error: "+sel_1+" "+ex.getMessage());
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
								second.refreshTreePaths(null);
							}
							public void exceptionOccurred(Exception exception)
							{
								second.refreshTreePaths(null);
							}
						}));
					}
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
						IDeploymentService ds = first.getDeploymentService();
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
							IDeploymentService ds = first.getDeploymentService();
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
						IDeploymentService ds = first.getDeploymentService();
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
		public Icon	getOverlay(ITreeNode node)
		{
			return null;
		}

		/**
		 *  Get the popup actions available for all of the given nodes, if any.
		 */
		public Action[]	getPopupActions(ITreeNode[] nodes)
		{
			return new Action[]{copy, del, rename, open};
		}

		/**
		 *  Get the default action to be performed after a double click.
		 */
		public Action getDefaultAction(ITreeNode node)
		{
			return null;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setFirstPanel(DeployerServiceSelectorPanel first)
		{
			 this.first = first;
		}
		
		/**
		 *  Set the first panel.
		 */
		public void setSecondPanel(DeployerServiceSelectorPanel second)
		{
			 this.second = second;
		}
	}
}
