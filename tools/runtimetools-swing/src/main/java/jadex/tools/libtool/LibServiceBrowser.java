package jadex.tools.libtool;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jadex.base.SRemoteGui;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.AddRIDAction;
import jadex.base.gui.modeltree.AddRemotePathAction;
import jadex.base.gui.modeltree.ITreeAbstraction;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.platform.service.library.LibraryService;

/**
 *  The library plugin.
 */
public class LibServiceBrowser	extends	JPanel	implements IServiceViewerPanel
{
	//-------- constants --------

	protected static final String ROOTTEXT = "Platform resources";
	protected static final String SYSTEMTEXT = "System classpath";
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"help",	SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/help.gng"),
		"jar", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/jar.png"),
		"global", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/global.png"),
		"oglobal", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/overlay_global.png"),
		"folder", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/folder4.png"),
		"orem", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/overlay_removable.png")
	});
	
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The list. */
	protected JTree ridtree;
	
	/** The lib service. */
	protected ILibraryService libservice;
	
	/** The lib service. */
	protected ILibraryServiceListener listener;
	
	/** The thread pool. */
	protected IThreadPool tp;
	
	/** The dependencies. */
	protected Map<IResourceIdentifier, List<IResourceIdentifier>> deps;
	
	/** The removable links. */
	protected Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> remlinks;

	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		this.jcc = jcc;
		this.libservice	= (ILibraryService)service;
		
		// Create class paths view.
		final JPanel classview = new JPanel(new BorderLayout());
		
		ridtree = new JTree(new DefaultTreeModel(null));
		ToolTipManager.sharedInstance().registerComponent(ridtree);
		ridtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ridtree.setCellRenderer(new DefaultTreeCellRenderer() 
		{
			public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				assert SwingUtilities.isEventDispatchThread();
				
				if(value instanceof LazyNode)
				{
					// Change icons depending on node type.
					LazyNode node = (LazyNode)value;
					Icon icon = node.getIcon();
					String tooltip = node.getTooltipText();
					
					if(icon!=null)
					{
						setOpenIcon(icon);
						setClosedIcon(icon);
						setLeafIcon(icon);
					}
					else
					{
						setOpenIcon(getDefaultOpenIcon());
						setClosedIcon(getDefaultClosedIcon());
						setLeafIcon(getDefaultLeafIcon());
					}
					
					if(tooltip!=null)
					{
						setToolTipText(tooltip);
					}
				}

				JComponent comp = (JComponent)super.getTreeCellRendererComponent(tree,
					value, selected, expanded, leaf, row, hasFocus);
		
				return comp;
			}
        });
		ridtree.addMouseListener(new MouseAdapter()
        {
	    	public void mousePressed(MouseEvent e)
	        {
	        	popup(e);
	        }
	        
	    	public void mouseReleased(MouseEvent e)
	        {
	        	popup(e);
	        }
	        
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		popup(e);
	    	}
	    	
	    	protected void popup(MouseEvent e)
	    	{
	    		if(e.isPopupTrigger())
	            {
	    			int row = ridtree.getRowForLocation(e.getX(), e.getY());
					if(row != -1)
					{
						Object node = ridtree.getLastSelectedPathComponent();
						if(node instanceof LazyNode)
						{
							LazyNode ln = (LazyNode)node;
							Object o = ((LazyNode)node).getMyUserObject();
							IResourceIdentifier parid = (IResourceIdentifier)(ln.getParent()!=null? ((LazyNode)ln.getParent()).getMyUserObject(): null);
							final boolean rem = !jcc.getJCCAccess().getId().getRoot().equals(jcc.getPlatformAccess().getId().getRoot());
							JPopupMenu popup = new JPopupMenu();
						
							if(LibraryService.SYSTEMCPRID.equals(parid))
							{
								if(ln.isRemovable())
								{
									LibTreeAbstraction tar = new LibTreeAbstraction(rem)
									{
										public void action(Object obj)
										{
											LazyNode child = (LazyNode)obj;
											final Object o = child.getUserObject();
											if(o instanceof IResourceIdentifier)
											{
												URL url = SUtil.toURL(((IResourceIdentifier)o).getLocalIdentifier().getUri());
													
												libservice.removeTopLevelURL(url)
													.addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
												{
													public void resultAvailable(Void result)
													{
														jcc.setStatusText("Removed resource: "+o);
													}
													
													public void exceptionOccurred(Exception exception)
													{
														exception.printStackTrace();
														jcc.setStatusText("Error removin resource: "+o+" "+exception.getMessage());
													}
												}));
											}
										}
									};
									
									popup.add(new RemovePathAction(tar));
								}
							}
							// system classpath
							else if(LibraryService.SYSTEMCPRID.equals(o))
							{
								LibTreeAbstraction taa = new LibTreeAbstraction(rem)
								{
									public void action(Object obj)
									{
										if(obj instanceof File)
										{
											try
											{
												final URL url = ((File)obj).getCanonicalFile().toURI().toURL();
												jcc.setStatusText("Started adding: "+url);
												libservice.addTopLevelURL(url)
													.addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
												{
													public void resultAvailable(Void result)
													{
														jcc.setStatusText("Finished adding: "+url);
													}
													
													public void exceptionOccurred(Exception e)
													{
														jcc.setStatusText("Error adding: "+url+" "+e.getMessage());
													}
												}));
											}
											catch(Exception e)
											{
												jcc.setStatusText("Error adding: "+obj+" "+e.getMessage());
											}
										}
										else if(obj instanceof FileData)
										{
											final String filename = ((FileData)obj).getPath();
											SRemoteGui.addRemoteURL(null, filename, true, jcc.getPlatformAccess()).addResultListener(new IResultListener<Tuple2<URL,IResourceIdentifier>>()
											{
												public void resultAvailable(Tuple2<URL, IResourceIdentifier> result)
												{
													jcc.setStatusText("Finished adding: "+result.getSecondEntity());
												}
												
												public void exceptionOccurred(Exception exception)
												{
													jcc.setStatusText("Error adding: "+filename+" "+exception.getMessage());
												}
											});
										}
									}
								};
								
								if(!rem)
								{
									popup.add(new AddPathAction(taa));
								}
								else
								{
									popup.add(new AddRemotePathAction(taa));
								}
							}
							else if(o instanceof IResourceIdentifier || o==null)
							{
								final IResourceIdentifier selrid = (IResourceIdentifier)o;
								
								LibTreeAbstraction taa = new LibTreeAbstraction(rem)
								{
									public void action(Object obj)
									{
										try
										{
											IResourceIdentifier rid = null;
											if(obj instanceof File)
											{
												URL url = ((File)obj).getCanonicalFile().toURI().toURL();
												IComponentIdentifier cid = getExternalAccess().getId().getRoot();
												ILocalResourceIdentifier lid = new LocalResourceIdentifier(cid, url);
												rid = new ResourceIdentifier(lid, null);
											}
											else if(obj instanceof IResourceIdentifier)
											{
												rid = (IResourceIdentifier)obj;
											}
											else if(obj instanceof FileData)
											{
												final String filename = ((FileData)obj).getPath();
												SRemoteGui.addRemoteURL(selrid, filename, false, jcc.getPlatformAccess()).addResultListener(new IResultListener<Tuple2<URL,IResourceIdentifier>>()
												{
													public void resultAvailable(Tuple2<URL, IResourceIdentifier> result)
													{
														jcc.setStatusText("Finished adding: "+result.getSecondEntity());
													}
													
													public void exceptionOccurred(Exception exception)
													{
														jcc.setStatusText("Erro adding: "+filename+" "+exception.getMessage());
													}
												});
											}
											
											if(rid!=null)
											{
												final IResourceIdentifier frid = rid;
												jcc.setStatusText("Started adding: "+frid);
												libservice.addResourceIdentifier(selrid, rid, true)
													.addResultListener(new SwingResultListener<IResourceIdentifier>(new IResultListener<IResourceIdentifier>()
												{
													public void resultAvailable(IResourceIdentifier result)
													{
														jcc.setStatusText("Finished adding: "+frid);
													}
													
													public void exceptionOccurred(Exception e)
													{
														jcc.setStatusText("Error adding: "+frid+" "+e.getMessage());
													}
												}));
											}
											
										}
										catch(Exception e)
										{
											jcc.setStatusText("Error adding: "+obj+" err: "+e.getMessage());
//											e.printStackTrace();
										}
									}
								};
								if(!rem)
								{
									popup.add(new AddPathAction(taa));
								}
								else
								{
									popup.add(new AddRemotePathAction(taa));
								}
								popup.add(new AddRIDAction(taa));
								
								LibTreeAbstraction tar = new LibTreeAbstraction(rem)
								{
									public void action(Object obj)
									{
										LazyNode child = (LazyNode)obj;
										final Object o = child.getUserObject();
										if(o instanceof IResourceIdentifier)
										{
											LazyNode parent = (LazyNode)child.getParent();
											Object parid = parent.getUserObject();
											if(!(parid instanceof IResourceIdentifier))
												parid = null;
												
											libservice.removeResourceIdentifier((IResourceIdentifier)parid, (IResourceIdentifier)o)
												.addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
											{
												public void resultAvailable(Void result)
												{
													jcc.setStatusText("Removed resource: "+o);
												}
												
												public void exceptionOccurred(Exception exception)
												{
													exception.printStackTrace();
													jcc.setStatusText("Error removin resource: "+o+" "+exception.getMessage());
												}
											}));
										}
									}
								};
								
								if(o!=null && ln.isRemovable())
								{
									popup.add(new RemovePathAction(tar));
								}
							}

							if(popup.getSubElements().length>0)
								popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
	            }
	    	}
        });
		
		JScrollPane scroll = new JScrollPane(ridtree);
		
		JPanel buts = new JPanel(new GridBagLayout());
		JButton add = new JButton("Add ...");
		add.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
//		JButton fetch = new JButton("Refresh");		
//		fetch.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton remove = new JButton("Remove");
		remove.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		JButton ref = new JButton("Refresh");
		ref.putClientProperty(SGUI.AUTO_ADJUST, Boolean.TRUE);
		add.setToolTipText("Add a class path entry");
		remove.setToolTipText("Remove one or more selected entries from the classpath");
//		fetch.setToolTipText("Fetch all entries from current class path");
		buts.add(add, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(remove, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(ref, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
//		buts.add(fetch, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
//			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		add.addActionListener(new ActionListener()
		{
			JFileChooser cchooser;
			
			public void actionPerformed(ActionEvent e)
			{
				if(cchooser==null)
				{
					cchooser  = new JFileChooser(".");
					cchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					cchooser.setFileFilter(new FileFilter()
					{
						public boolean accept(File name)
						{
							return name.isDirectory() || name.getName().endsWith(".jar");
						}
						public String getDescription()
						{
							return "*.jar";
						}
					});
					cchooser.setMultiSelectionEnabled(true);
				}
				
				
				if(cchooser.showDialog(SGUI.getWindowParent(classview)
					, "Add")==JFileChooser.APPROVE_OPTION)
				{
					final File[] files = cchooser.getSelectedFiles();
					
					TreePath sel = ridtree.getSelectionPath();
					LazyNode seln = (LazyNode)(sel!=null? sel.getLastPathComponent(): null);
					Object uo = seln!=null? seln.getMyUserObject(): null;
					IResourceIdentifier parid = uo instanceof IResourceIdentifier? (IResourceIdentifier)uo: null;
					
					IResultListener<IResourceIdentifier> lis = new CounterResultListener<IResourceIdentifier>(files.length, new IResultListener<Void>()
					{
						public void resultAvailable(Void result) 
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									refresh(false);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									jcc.setStatusText("Error while adding path: "+SUtil.arrayToString(files));
								}
							});
						}
					});
					
					for(int i=0; i<files.length; i++)
					{
						try
						{
							URL url = files[i].getCanonicalFile().toURI().toURL();
							libservice.addURL(parid, url).addResultListener(lis);
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = ridtree.getSelectionPath();
				Object uo = getUserObject(path);
				
				if(uo instanceof IResourceIdentifier)
				{
					Object puo = getUserObject(path.getParentPath());
					IResourceIdentifier parid = puo instanceof IResourceIdentifier? (IResourceIdentifier)puo: null;
					
					libservice.removeResourceIdentifier(parid, (IResourceIdentifier)uo)
						.addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							refresh(false);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							jcc.displayError("Library error", "Could not remove url", exception);
						}
					}));
				}
			}
		});
		
		ref.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh(true);
			}
		});
		
		classview.add("Center", scroll);
		classview.add("South", buts);
		
		this.setLayout(new BorderLayout());
		this.add(classview, BorderLayout.CENTER);

		// Add a library service listener to be informed about library changes.
		this.listener	= new ILibraryServiceListener()
		{
			public IFuture<Void> resourceIdentifierAdded(final IResourceIdentifier parid, final IResourceIdentifier rid, final boolean rem)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						List<IResourceIdentifier> mydeps = deps.get(parid);
						if(mydeps==null)
						{
							mydeps = new ArrayList<IResourceIdentifier>();
							deps.put(parid, mydeps);
						}
						mydeps.add(rid);
						if(rem)
						{
							remlinks.add(new Tuple2<IResourceIdentifier, IResourceIdentifier>(parid, rid));
						}
						
						LazyNode root = (LazyNode)ridtree.getModel().getRoot();
						root.refresh();
						ridtree.invalidate();
						ridtree.doLayout();
						ridtree.repaint();
					}
				});
				return IFuture.DONE;
			}
			
			public IFuture<Void> resourceIdentifierRemoved(final IResourceIdentifier parid, final IResourceIdentifier rid)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						List<IResourceIdentifier> mydeps = deps.get(parid);
						if(mydeps!=null)
						{
							mydeps.remove(rid);
						}
						
						LazyNode root = (LazyNode)ridtree.getModel().getRoot();
						root.refresh();
						ridtree.invalidate();
						ridtree.doLayout();
						ridtree.repaint();
					}
				});
				return IFuture.DONE;
			}
		};
		libservice.addLibraryServiceListener(listener);
		
		// Todo: remove listener, when tool is closed.
		
		refresh(false);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get the dependencies.
	 *  @return The dependencies.
	 */
	public Map<IResourceIdentifier, List<IResourceIdentifier>> getDependencies()
	{
		return deps;
	}

	/**
	 *  Get the remlinks.
	 *  @return The remlinks.
	 */
	public Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> getRemlinks()
	{
		return remlinks;
	}

	/**
	 *  Refresh the tree.
	 */
	public void refresh(final boolean force)
	{
		jcc.setStatusText("Refreshing resource tree started...");
		libservice.getResourceIdentifiers().addResultListener(new SwingDefaultResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(LibServiceBrowser.this)
		{
			public void customResultAvailable(final Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> mydeps)
			{
				libservice.getRemovableLinks().addResultListener(new SwingDefaultResultListener<Set<Tuple2<IResourceIdentifier, IResourceIdentifier>>>(LibServiceBrowser.this)
				{
					public void customResultAvailable(Set<Tuple2<IResourceIdentifier, IResourceIdentifier>> reml)
					{
						jcc.setStatusText("Refreshing resource tree finished");

						LibServiceBrowser.this.deps = mydeps.getSecondEntity();
						LibServiceBrowser.this.remlinks = reml;
//						System.out.println("kukuku: "+reml);
						
						DefaultTreeModel mod = (DefaultTreeModel)ridtree.getModel();
						
						// Create new if first time
						if(!(mod.getRoot() instanceof LazyNode) || force)
						{
							// Add nonmanged urls
							ridtree.removeAll();
							LazyNode root = new LazyNode(ROOTTEXT);
							mod.setRoot(root);
						}
						else
						{
							((LazyNode)mod.getRoot()).refresh();
						}
					}
				});
			}
		});
	}	
	
	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		try
		{
			libservice.removeLibraryServiceListener(listener);
		}
		catch(Exception e)
		{
		}
		
		return IFuture.DONE;
	}

	
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return this;
	}
		
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "libservicebrowser";
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		return Future.getEmptyFuture();
	}
	
	/**
	 *  Get the thread pool.
	 */
	protected IFuture<IThreadPool> getThreadPool()
	{
		final Future<IThreadPool> ret = new  Future<IThreadPool>();
		
		if(tp==null)
		{
			jcc.getJCCAccess().searchService( new ServiceQuery<>( IDaemonThreadPoolService.class, ServiceScope.PLATFORM))
				.addResultListener(new SwingDefaultResultListener<IDaemonThreadPoolService>()
			{
				public void customResultAvailable(IDaemonThreadPoolService result)
				{
					tp = result;
					ret.setResult(tp);
				}
			});
		}
		else
		{
			ret.setResult(tp);
		}
		
		return ret;
	}
	
	/**
	 *  Get the tree user object for a tree path.
	 *  @param path The path.
	 *  @return The object. 
	 */
	protected Object getUserObject(TreePath path)
	{
		LazyNode seln = (LazyNode)(path!=null? path.getLastPathComponent(): null);
		Object uo = seln!=null? seln.getMyUserObject(): null;
		return uo;
	}
	
	/**
	 *  Node type for tree.
	 */
	class LazyNode extends DefaultMutableTreeNode
	{
		/** Flag if children have been created. */
		protected boolean childrencreated;
		
		/** The last child count. */
		protected int childcnt;
		
		/**
		 *  Create a new RidNode.
		 */
		public LazyNode(Object o)
		{
			super(o);
			this.childcnt = getChildCount();
		}

		/**
		 *  Test if the node is removable.
		 */
		protected boolean isRemovable()
		{
			boolean ret = false;
			LazyNode pa = (LazyNode)getParent();
			if(pa!=null)
			{
				Object uo = pa.getMyUserObject();
				if(uo==null || uo instanceof IResourceIdentifier)
				{
					Object myo = getMyUserObject();
					if(myo instanceof IResourceIdentifier)
					{
						IResourceIdentifier p = (IResourceIdentifier)uo;
						IResourceIdentifier ch = (IResourceIdentifier)myo;
						ret = remlinks.contains(new Tuple2<IResourceIdentifier, IResourceIdentifier>(p, ch));
					}
				}
			}
			return ret;
		}
		
		/**
		 *  Get the icon.
		 */
		public Icon getIcon()
		{
			Icon ret = null;
			Object o = getUserObject();
			
			List<Icon> ilist = new ArrayList<Icon>();
			
			if(LibraryService.SYSTEMCPRID.equals(o))
			{
				ilist.add(icons.getIcon("folder"));
			}
			else if(getParent()!=null && LibraryService.SYSTEMCPRID.equals(((LazyNode)getParent()).getMyUserObject()))
			{
				if(((IResourceIdentifier)o).getLocalIdentifier().getUri().toString().indexOf(".jar")!=-1)
				{
					ilist.add(icons.getIcon("jar"));
				}
				else
				{
					ilist.add(icons.getIcon("folder"));
				}
			}
			else if(o instanceof IResourceIdentifier)
			{
				if(((IResourceIdentifier)o).getGlobalIdentifier()!=null && !ResourceIdentifier.isHashGid((IResourceIdentifier)o))
				{
					ilist.add(icons.getIcon("global"));
				}
				else
				{
					ILocalResourceIdentifier lrid = ((IResourceIdentifier)o).getLocalIdentifier();
					
					if(lrid.getUri().toString().indexOf(".jar")!=-1)
					{
						ilist.add(icons.getIcon("jar"));
					}
					else
					{
						ilist.add(icons.getIcon("folder"));
					}
					
					if(!ResourceIdentifier.isLocal((IResourceIdentifier)o, jcc.getPlatformAccess().getId().getRoot()))
					{
						ilist.add(icons.getIcon("oglobal"));
					}
				}
			}
			else if(o instanceof String)
			{
				if(getChildCount()>0)
				{
					ilist.add(icons.getIcon("folder"));
				}
			}
			
//			System.out.println("getIcon: "+isRemovable()+" "+getUserObject());
			if(isRemovable())
			{
				ilist.add(icons.getIcon("orem"));
			}
			
			ret = new CombiIcon(ilist.toArray(new Icon[ilist.size()]));
			
			return ret;
		}
		
		/**
		 *  Get the tooltip.
		 */
		public String getTooltipText()
		{
			String ret = null;
			
			Object o = getUserObject();
			if(o instanceof IResourceIdentifier)
			{
				if(ResourceIdentifier.isHashGid((IResourceIdentifier)o))
				{
					ret = ((IResourceIdentifier)o).getGlobalIdentifier().getResourceId();
				}
			}
			
			return ret;
		}
		
		/**
		 *  Get the number of chilren (without creating them).
		 */
		public int getChildCount()
		{
			int ret = 0;
			
			if(childrencreated)
			{
				ret = super.getChildCount();
			}
			else
			{
				Object o = getMyUserObject();
				
				if(o==null || o instanceof IResourceIdentifier)
				{
					List<IResourceIdentifier> cs = deps.get(o);
					ret += cs==null? 0: cs.size();
				}
				else
				{
					return super.getChildCount();
				}
			}
			
			return ret;
		}
		
		/**
		 *  Get a child from an index.
		 *  @param index The index.
		 *  @return The child.
		 */
		public TreeNode getChildAt(int index)
		{
			createChildren();
			return super.getChildAt(index);
		}
		
		/**
		 *  Add a child.
		 *  @param child The child.
		 */
		public void add(MutableTreeNode child)
		{
			createChildren();
			super.add(child);
		}
		
		/**
		 *  Insert a child at an index.
		 *  @param index The index.
		 */
		public void insert(MutableTreeNode newChild, int childIndex)
		{
			createChildren();

			super.insert(newChild, childIndex);
		}
		
		/**
		 *  Create the child nodes.
		 */
		protected void createChildren()
		{
			if(!childrencreated)
			{
				childrencreated = true;
				
				Object o = getMyUserObject();
				
				if(o==null || o instanceof IResourceIdentifier)
				{
					List<IResourceIdentifier> cs = deps.get(o);
					if(cs!=null)
					{
						for(IResourceIdentifier rid: cs)
						{
							LazyNode n = new LazyNode(rid);
							insertChild(n);
						}
					}
					childrencreated = true;
				}
			}
		}
		
		/**
		 *  Insert a child node alphabetically.
		 */
		protected int insertChild(LazyNode n)
		{
			int ret = -1;
			
			boolean issyscp = LibraryService.SYSTEMCPRID.equals(getMyUserObject());
			
			int cnt = getChildCount();
			boolean inserted = false;
			boolean nhasch = n.getChildCount()>0 || (issyscp && ((IResourceIdentifier)n.getMyUserObject()).getLocalIdentifier().getUri().toString().indexOf(".jar")==-1);
			for(int i=0; i<cnt && !inserted; i++)
			{
				LazyNode tmp = (LazyNode)getChildAt(i);
				boolean tmphasch = tmp.getChildCount()>0 || (issyscp && ((IResourceIdentifier)tmp.getMyUserObject()).getLocalIdentifier().getUri().toString().indexOf(".jar")==-1);
				if((!tmphasch && nhasch) 
					|| (tmphasch==nhasch && n.toString().compareTo(tmp.toString())<0 )) 
				{
					insert(n, i);
					inserted = true;
					ret = i;
				}
			}
			if(!inserted)
			{
				ret = cnt;
				add(n);
			}
			
			return ret;
		}
		
		/**
		 *  Refresh the nodes.
		 */
		public void refresh()
		{
			if(childrencreated || childcnt!=getChildCount())
			{				
				childcnt = getChildCount();
				if(!childrencreated)
				{
					createChildren();
				}
				
				List<IResourceIdentifier> cs = deps.get(getMyUserObject());

				List<IResourceIdentifier> toadd = new ArrayList<IResourceIdentifier>(cs);
				List<LazyNode> todel = new ArrayList<LazyNode>();
				List<LazyNode> toref = new ArrayList<LazyNode>();
				
				int cnt = getChildCount();
				for(int i=0; i<cnt; i++)
				{
					LazyNode ch = (LazyNode)getChildAt(i);
					Object uo = ch.getMyUserObject();
				
					if(uo instanceof IResourceIdentifier)
					{
						toadd.remove(uo);
						if(!cs.contains(uo))
						{
							todel.add(ch);
						}
						else
						{
							toref.add(ch);
						}
					}
				}
				
				// Remove obsolete nodes
				if(!todel.isEmpty())
				{
					int[] remis = new int[todel.size()];
					LazyNode[] remns = new LazyNode[todel.size()];
					for(int i=0; i<todel.size(); i++)
					{
						LazyNode node = todel.get(i);
						remis[i] = getIndex(node);
						remns[i] = node;
						remove(node);
					}
					((DefaultTreeModel)ridtree.getModel()).nodesWereRemoved(this, remis, remns);
				}
				
				// Add new nodes
				if(!toadd.isEmpty())
				{
					int[] addis = new int[toadd.size()];
					for(int i=0; i<toadd.size(); i++)
					{
						IResourceIdentifier rid = toadd.get(i);
						LazyNode node = new LazyNode(rid);
						addis[i] = insertChild(node);
					}
					
					((DefaultTreeModel)ridtree.getModel()).nodesWereInserted(this, addis);
				}
				
				// Refresh child nodes
				for(LazyNode node: toref)
				{
					node.refresh();
				}
			}
		}
		
		/**
		 *  Get the user object.
		 *  (Returns null for root - instead of its text).
		 */
		public Object getMyUserObject()
		{
			Object ret = getUserObject();
			if(ROOTTEXT.equals(ret))
				ret = null;
			return ret;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			String ret = null;
			
			Object o = getUserObject();
			if(LibraryService.SYSTEMCPRID.equals(o))
			{
				ret = SYSTEMTEXT;
			}
			else if(o instanceof IResourceIdentifier)
			{
				IGlobalResourceIdentifier grid = ((IResourceIdentifier)o).getGlobalIdentifier();
				if(grid!=null && !ResourceIdentifier.isHashGid((IResourceIdentifier)o))
				{
					ret = grid.getResourceId();
				}
				else
				{
					ILocalResourceIdentifier lrid = ((IResourceIdentifier)o).getLocalIdentifier();
					ret = lrid.getUri().toString();
					
					if(!ResourceIdentifier.isLocal((IResourceIdentifier)o, jcc.getPlatformAccess().getId().getRoot()))
					{
						ret += " ("+((IResourceIdentifier)o).getLocalIdentifier().getComponentIdentifier()+")";
					}
				}
			}
			else
			{
				ret = super.toString();
			}
			
			return ret;
		}
	}
	
	/**
	 *  Abstract base class for tree abstraction used in generic add/remove path actions.
	 */
	protected abstract class LibTreeAbstraction implements ITreeAbstraction
	{
		protected boolean rem;
		
		public LibTreeAbstraction(boolean rem)
		{
			this.rem = rem;
		}
		
		public boolean isRemote()
		{
			return rem;
		}
		
		public JTree getTree()
		{
			return ridtree;
		}
		
		public IExternalAccess getExternalAccess()
		{
			return jcc.getPlatformAccess();
		}
		
		public IExternalAccess getGUIExternalAccess()
		{
			return jcc.getJCCAccess();
		}
	}
}

