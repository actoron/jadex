package jadex.tools.libtool;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.modeltree.AddPathAction;
import jadex.base.gui.modeltree.AddRIDAction;
import jadex.base.gui.modeltree.AddRemotePathAction;
import jadex.base.gui.modeltree.ITreeAbstraction;
import jadex.base.gui.modeltree.ModelTreePanel;
import jadex.base.gui.modeltree.RemovePathAction;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.annotations.Classname;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/**
 *  The library plugin.
 */
public class LibServiceBrowser	extends	JPanel	implements IServiceViewerPanel
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"help",	SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/help.gng"),
		"jar", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/jar.png"),
		"global", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/global.png"),
		"oglobal", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/overlay_global.png"),
		"folder", SGUI.makeIcon(LibServiceBrowser.class, "/jadex/base/gui/images/folder4.png")
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
		
		ridtree = new JTree(new Node("Root resource"));
		
		ridtree.setCellRenderer(new DefaultTreeCellRenderer() 
		{
			public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				assert SwingUtilities.isEventDispatchThread();
				
				// Change icons depending on node type.
				Node node = (Node)value;
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
				
				JComponent comp = (JComponent)super.getTreeCellRendererComponent(tree,
					node.toString(), selected, expanded, leaf, row, hasFocus);

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
						final Object node = ridtree.getLastSelectedPathComponent();
						if(node instanceof Node)
						{
							Object o = ((Node)node).getUserObject();
							if(o instanceof IResourceIdentifier)
							{
								final boolean rem = !jcc.getJCCAccess().getComponentIdentifier().getRoot().equals(jcc.getPlatformAccess().getComponentIdentifier().getRoot());
								final IResourceIdentifier selrid = (IResourceIdentifier)o;
								
								JPopupMenu popup = new JPopupMenu();
								LibTreeAbstraction taa = new LibTreeAbstraction(rem)
								{
									public void action(Object obj)
									{
										try
										{
											IResourceIdentifier rid = null;
											if(obj instanceof File)
											{
												URL url = ((File)obj).toURI().toURL();
												IComponentIdentifier cid = getExternalAccess().getComponentIdentifier().getRoot();
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
												addRemoteURL(selrid, filename).addResultListener(new IResultListener<Tuple2<URL,IResourceIdentifier>>()
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
													.addResultListener(new SwingDefaultResultListener<IResourceIdentifier>()
												{
													public void customResultAvailable(IResourceIdentifier result)
													{
														jcc.setStatusText("Finished adding: "+frid);
													}
												});
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
										Node child = (Node)obj;
										final Object o = child.getUserObject();
										if(o instanceof IResourceIdentifier)
										{
											Node parent = (Node)child.getParent();
											Object parid = parent.getUserObject();
											if(!(parid instanceof IResourceIdentifier))
												parid = null;
												
//											parent.remove(child);
											
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
								popup.add(new RemovePathAction(tar));
								
								popup.show(e.getComponent(), e.getX(), e.getY());
							}
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
		final JFileChooser cchooser = new JFileChooser(".");
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
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cchooser.showDialog(SGUI.getWindowParent(classview)
					, "Add")==JFileChooser.APPROVE_OPTION)
				{
					final File[] files = cchooser.getSelectedFiles();
					for(int i=0; i<files.length; i++)
					{
						try
						{
							URL url = files[i].toURI().toURL();
							libservice.addURL(null, url).addResultListener(new SwingDefaultResultListener<IResourceIdentifier>()
							{
								public void customResultAvailable(IResourceIdentifier result)
								{
									refresh();
								}
								public void customExceptionOccurred(Exception exception)
								{
									jcc.setStatusText("Adding url failed: "+exception.getMessage());
								}
							});
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
//		remove.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				final int[] sel = classpaths.getSelectedRows();
//				final String[] entries = classpaths.getEntries();				
//				for(int i=0; i<sel.length; i++)
//				{
//					classpaths.removeEntry(entries[sel[i]]);
//					try
//					{
//						libservice.removeURLCompletely(new URL(entries[sel[i]]))
//							.addResultListener(new SwingDefaultResultListener<Void>()
//						{
//							public void customResultAvailable(Void result)
//							{
//								refresh();
//							}
//						});
//					}
//					catch(Exception ex)
//					{
//						jcc.displayError("Library error", "Could not remove url", ex);
////						System.out.println(entries[sel[i]]);
////						ex.printStackTrace();
//					}	
//				}
//			}
//		});
		
		ref.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		
//		classpaths.getModel().addTableModelListener(new TableModelListener()
//		{
//			public void tableChanged(TableModelEvent e)
//			{
//				if(e.getType()== TableModelEvent.DELETE && (e instanceof EditableListEvent))
//				{
//					final EditableListEvent ele = (EditableListEvent)e;
//					final int start = e.getFirstRow();
//					final int end = e.getLastRow();
//
//					for(int i=0; i<=end-start; i++)
//					{
//						if(ele.getData(i)!=null && ((String)ele.getData(i)).length()>0)
//						{
//							try
//							{
//								libservice.removeURLCompletely(new URL(ele.getData(i).toString()));
//							}
//							catch(MalformedURLException ex)
//							{
//								jcc.displayError("Library error", "Could not remove url", ex);
////								System.out.println(ele.getData(i));
////								ex.printStackTrace();
//							}	
//						}
//					}
//				}
//			}
//		});
		
		classview.add("Center", scroll);
		classview.add("South", buts);
		
		final JPanel otherview = new JPanel(new BorderLayout());
		final DefaultListModel dlm = new DefaultListModel();
		libservice.getNonManagedURLs().addResultListener(new SwingDefaultResultListener(LibServiceBrowser.this)
		{
			public void customResultAvailable(Object result)
			{
				List entries = (List)result;
				for(int i=0; i<entries.size(); i++)
				{
					dlm.addElement(entries.get(i));
				}
			}
		});
		
		final JList otherlist = new JList(dlm);
		JPanel obuts = new JPanel(new BorderLayout());
		JButton refresh = new JButton("Refresh");
		obuts.add("East", refresh);
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				libservice.getNonManagedURLs().addResultListener(new SwingDefaultResultListener(LibServiceBrowser.this)
				{
					public void customResultAvailable(Object result)
					{
						List entries = (List)result;
						DefaultListModel dlm = (DefaultListModel)otherlist.getModel();
						dlm.removeAllElements();
						for(int i=0; i<entries.size(); i++)
						{
							dlm.addElement((String)entries.get(i));
						}
					}
				});
			}
		});
		otherview.add("Center", new JScrollPane(otherlist));
		otherview.add("South", obuts);
		
		this.setLayout(new BorderLayout());
		this.add(classview, BorderLayout.CENTER);
//		this.add("Managed Classpath Entries", classview);
//		this.add("System Classpath Entries", otherview);

		// Add a library service listener to be informed about library changes.
//		this.listener	= new ILibraryServiceListener()
//		{
//			public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier rid)
//			{
//				// todo: make synchronized
//				if(!classpaths.containsEntry(rid.getLocalIdentifier().getUrl().toString()))
//					classpaths.addEntry(rid.getLocalIdentifier().getUrl().toString());
//				return IFuture.DONE;
//			}
//			public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier rid)
//			{
//				// todo: make synchronized
//				if(classpaths.containsEntry(rid.getLocalIdentifier().getUrl().toString()))
//					classpaths.removeEntry(rid.getLocalIdentifier().getUrl().toString());
//				return IFuture.DONE;
//			}
//		};
//		libservice.addLibraryServiceListener(listener);
		
		// Todo: remove listener, when tool is closed.
		
		refresh();
		
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public void refresh()
	{
		libservice.getNonManagedURLs().addResultListener(new SwingDefaultResultListener<List<URL>>(LibServiceBrowser.this)
		{
			public void customResultAvailable(final List<URL> nonmanurls)
			{
				libservice.getResourceIdentifiers().addResultListener(new SwingDefaultResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(LibServiceBrowser.this)
				{
					public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> result)
					{
						ridtree.removeAll();
						DefaultTreeModel mod = (DefaultTreeModel)ridtree.getModel();
						Map<IResourceIdentifier, List<IResourceIdentifier>> deps = result.getSecondEntity();
						
						Map<IResourceIdentifier, Node> nodes = new HashMap<IResourceIdentifier, Node>();
						for(IResourceIdentifier rid: deps.keySet())
						{
							Node node = new Node(rid);
							nodes.put(rid, node);
						}
						
						for(Node node: nodes.values())
						{
							List<IResourceIdentifier> mydeps = deps.get((IResourceIdentifier)node.getUserObject());
							for(IResourceIdentifier rid: mydeps)
							{
								Node child = nodes.get(rid);
								node.add(child);
							}
						}
						
						Node root = nodes.get(result.getFirstEntity());
						if(nonmanurls!=null && !nonmanurls.isEmpty())
						{
							Node cont = new Node("System classpaths");
							for(URL url: nonmanurls)
							{
								Node child = new Node(url);
								cont.add(child);
							}
							root.add(cont);
						}
						
						root.setUserObject("Platform resources");
						mod.setRoot(root);
//						ridtree.setRootVisible(false);
					}
				});
			}
		});
	}
	
	/**
	 * 
	 */
	protected IFuture<Tuple2<URL, IResourceIdentifier>> addRemoteURL(final IResourceIdentifier parid, final String filename)
	{
		final Future<Tuple2<URL, IResourceIdentifier>> ret = new Future<Tuple2<URL, IResourceIdentifier>>();
		
		jcc.getPlatformAccess().scheduleStep(new IComponentStep<Tuple2<URL, IResourceIdentifier>>()
		{
			@Classname("addurl")
			public IFuture<Tuple2<URL, IResourceIdentifier>> execute(IInternalAccess ia)
			{
				final URL	url	= SUtil.toURL(filename);
				final Future<Tuple2<URL, IResourceIdentifier>>	ret	= new Future<Tuple2<URL, IResourceIdentifier>>();
				IFuture<ILibraryService>	libfut	= SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				libfut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Tuple2<URL, IResourceIdentifier>>(ret)
				{
					public void customResultAvailable(final ILibraryService ls)
					{
						// todo: workspace=true?
						ls.addURL(parid, url).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
						{
							public void customResultAvailable(IResourceIdentifier rid)
							{
								ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
							}
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
								super.exceptionOccurred(exception);
							}
						});
					}
				});
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener<Tuple2<URL, IResourceIdentifier>>(ret));
//		}).addResultListener(new SwingDefaultResultListener<Tuple2<URL, IResourceIdentifier>>()
//		{
//			public void customResultAvailable(Tuple2<URL, IResourceIdentifier> tup) 
//			{
//				// Todo: remove entries on remove.
//	//			System.out.println("adding root: "+tup.getFirstEntity()+" "+tup.getSecondEntity());
//				addRootEntry(tup.getFirstEntity(), filepath, tup.getSecondEntity());
//				ModelTreePanel.super.addNode(node);
//			}
//			
//			public void customExceptionOccurred(final Exception exception)
//			{
//				jcc.getJCCAccess().scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ia.getLogger().warning(exception.toString());
//						return IFuture.DONE;
//					}
//				});					
//			}
//		});
		
		return ret;
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
			SServiceProvider.getServiceUpwards(jcc.getJCCAccess().getServiceProvider(), IDaemonThreadPoolService.class)
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
	 * 
	 */
	class Node extends DefaultMutableTreeNode
	{
		/**
		 *  Create a new RidNode.
		 */
		public Node(Object o)
		{
			super(o);
		}
		
		/**
		 *  Get the icon.
		 */
		public Icon getIcon()
		{
			Icon ret = null;
			Object o = getUserObject();
			if(o instanceof URL)
			{
				if(((URL)o).getFile().indexOf(".jar")!=-1)
				{
					ret = icons.getIcon("jar");
				}
				else
				{
					ret = icons.getIcon("folder");
				}
			}
			else if(o instanceof IResourceIdentifier)
			{
				if(((IResourceIdentifier)o).getGlobalIdentifier()!=null)
				{
					ret = icons.getIcon("global");
				}
				else
				{
					Icon[] ics = new Icon[2];
					ILocalResourceIdentifier lrid = ((IResourceIdentifier)o).getLocalIdentifier();
					
					if(lrid.getUrl().getFile().indexOf(".jar")!=-1)
					{
						ics[0] = icons.getIcon("jar");
					}
					else
					{
						ics[0] = ret = icons.getIcon("folder");
					}
					ics[1] = icons.getIcon("oglobal");
					
					ret = new CombiIcon(ics);
				}
			}
			else if(o instanceof String)
			{
				if(getChildCount()>0)
				{
					ret = icons.getIcon("folder");
				}
			}
			
			return ret;
		}
		
		/**
		 *  Get the tooltip.
		 */
		public String getTooltipText()
		{
			return null;
		}
		
		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			String ret = null;
			
			Object o = getUserObject();
			if(o instanceof IResourceIdentifier)
			{
				IGlobalResourceIdentifier grid = ((IResourceIdentifier)o).getGlobalIdentifier();
				if(grid!=null)
				{
					ret = grid.getResourceId();
				}
				else
				{
					ILocalResourceIdentifier lrid = ((IResourceIdentifier)o).getLocalIdentifier();
					ret = lrid.getUrl().toString();
					
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
	 * 
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

