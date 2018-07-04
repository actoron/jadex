package jadex.base.gui.modeltree;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.base.ModelFileFilter;
import jadex.base.SRemoteGui;
import jadex.base.gui.RememberOptionMessage;
import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeFactory;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.RIDNode;
import jadex.base.gui.filetree.RootNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.IAsyncFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Tree for component models.
 */
public class ModelTreePanel extends FileTreePanel
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"gid", SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/overlay_global.png")
	});
	
	protected static int LISTENER_COUNTER = 0;
	
	//-------- attributes --------
	
	/** The actions. */
	protected Map actions;
	
	/** Kernel listener */
	protected IMultiKernelListener kernellistener;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	/** The local external access. */
	protected IExternalAccess localexta;
	
	/** The root entries. */
	protected Map<URI, IResourceIdentifier>	rootentries;
	// todo: remove 
	protected Map<String, IResourceIdentifier>	rootpathentries; 
	
	/** Flag if path should be automatically deleted from library service. */
	protected Boolean autodelete;
	
//	/** The jcc. */
//	protected IControlCenter jcc;
	
	//-------- constructors --------
	
//	public ModelTreePanel(IControlCenter jcc)
//	{
//		this(jcc.getPlatformAccess(), jcc.getJCCAccess(), 
//			!SUtil.equals(jcc.getPlatformAccess().getComponentIdentifier().getPlatformName(), 
//			jcc.getJCCAccess().getComponentIdentifier().getPlatformName()));
//		this.jcc = jcc;
//	}

	
	/**
	 *  Create a new model tree panel.
	 */
	public ModelTreePanel(final IExternalAccess exta, final IExternalAccess localexta, boolean remote)
	{
		super(exta, remote, false);
		this.localexta = localexta;
		actions = new HashMap<URL, IResourceIdentifier>();
		this.rootentries	= new LinkedHashMap<URI, IResourceIdentifier>();
		this.rootpathentries	= new LinkedHashMap<String, IResourceIdentifier>();
		
		final ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		setNodeFactory(new DefaultNodeFactory()
		{
			public IAsyncFilter getFileFilter()
			{
				// Hack!!! Have to use URL for communication backwards compatibility as model file filter is transferred remotely.
				Map<URL, IResourceIdentifier>	rids	= new HashMap();
				for(Map.Entry<URI, IResourceIdentifier> entry: getRootEntries().entrySet())
				{
					rids.put(SUtil.toURL0(entry.getKey()), entry.getValue());
				}
				return new ModelFileFilter(mic.isAll(), mic.getSelectedComponentTypes(), rids, exta);
			}
		});
		ModelIconCache ic = new ModelIconCache(exta, getTree());
		setMenuItemConstructor(mic);
		actions.put(CollapseAllAction.getName(), new CollapseAllAction(this));
		ITreeAbstraction taa = new ModelTreeAbstraction()
		{
			public void action(Object obj)
			{
				if(ModelTreePanel.this.getModel().getNode(obj)==null &&
					ModelTreePanel.this.getModel().getNode(obj.toString())==null)
				{
//					treepanel.addTopLevelNode(result);
					ModelTreePanel.this.addTopLevelNodeMeta(obj);
				}
				else
				{
					// Todo: already added to library service (remove?)
					String	msg	= SUtil.wrapText("Path can not be added twice:\n"+obj);
					JOptionPane.showMessageDialog(SGUI.getWindowParent(getTree()),
						msg, "Duplicate path", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		ITreeAbstraction tar = new ModelTreeAbstraction()
		{
			public void action(Object node) 
			{
				ModelTreePanel.this.removeTopLevelNode((ISwingTreeNode)node);
				
				if(getExternalAccess()!=null && node instanceof IFileNode)
				{
					final String	path	= ((IFileNode)node).getFilePath();
					
					int choice;
					RememberOptionMessage msg = null;
					if(autodelete==null)
					{
						msg = new RememberOptionMessage("You deleted a resource from the model tree.\n "
							+ "Do you also want to remove the resource from the classpath?\n");
						choice = JOptionPane.showConfirmDialog(SGUI.getWindowParent(ModelTreePanel.this), msg, "Classpath Question",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					}
					else if(!autodelete.booleanValue())
					{
						choice = JOptionPane.NO_OPTION;
					}
					else // if(jccexit.equals(JCC_EXIT_SHUTDOWN))
					{
						choice = JOptionPane.YES_OPTION;
					}
			
					if(JOptionPane.YES_OPTION == choice)
					{
						// Save settings if wanted
						if(msg!=null && msg.isRemember())
							autodelete = true;
						SRemoteGui.removeURL(exta, path);
					}
					else if(JOptionPane.NO_OPTION == choice)
					{
						// Save settings if wanted
						if(msg!=null && msg.isRemember())
							autodelete = false;
					}
					// else CANCEL
				}
			}
		};
		
		Action rpa = new RemovePathAction(tar)
		{
			public boolean isEnabled()
			{
				ISwingTreeNode rm = (ISwingTreeNode)treepanel.getTree().getLastSelectedPathComponent();
				return rm!=null && rm.getParent().equals(treepanel.getTree().getModel().getRoot());
			}
		};
		
		actions.put(AddPathAction.getName(), remote ? new AddRemotePathAction(taa) : new AddPathAction(taa));
		actions.put(AddRIDAction.getName(), new AddRIDAction(taa));
		actions.put(RemovePathAction.getName(), rpa);
		setPopupBuilder(new PopupBuilder(new Object[]{actions.get(AddPathAction.getName()), actions.get(AddRIDAction.getName()),
			actions.get(AddRemotePathAction.getName()), mic}));
		setMenuItemConstructor(mic);
		setIconCache(ic);
		DefaultNodeHandler dnh = new DefaultNodeHandler(getTree())
		{
			public Icon getSwingOverlay(ISwingTreeNode node)
			{
				Icon	overlay	= null;
				if(getModel().getRoot().equals(node.getParent()) && node instanceof IFileNode)
				{
//					URL	url	= SUtil.toURL(((IFileNode)node).getFilePath());
//					IResourceIdentifier	rid	= rootentries.get(url);
					IResourceIdentifier rid = getRootEntry(((IFileNode)node).getFilePath());
					if(rid!=null && rid.getGlobalIdentifier()!=null && !ResourceIdentifier.isHashGid(rid))
					{
						overlay	= ModelTreePanel.icons.getIcon("gid");
					}
				}
				return overlay;
			}
		};
		
		dnh.addAction(rpa, null);
		addNodeHandler(dnh);
		
		tree.setCellRenderer(new AsyncTreeCellRenderer()
		{
			// Hack!!! why not use RID node???
			protected String getLabel(ITreeNode node)
			{
				String	ret	= null;
				if(getModel().getRoot().equals(node.getParent()) && node instanceof IFileNode)
				{
//					URL	url	= SUtil.toURL(((IFileNode)node).getFilePath());
//					IResourceIdentifier	rid	= rootentries.get(url);
					IResourceIdentifier rid = getRootEntry(((IFileNode)node).getFilePath());
					if(rid!=null)
					{
						IGlobalResourceIdentifier grid = rid.getGlobalIdentifier();
						if(grid!=null && !grid.getResourceId().startsWith("::"))
						{
							ret = grid.getResourceId();
							if(ret!=null && ret.indexOf(':')!=-1)
							{
								ret	= ret.substring(ret.indexOf(':')+1);
							}
						}
//						else
//						{
//							ILocalResourceIdentifier lrid = rid.getLocalIdentifier();
//							ret = lrid.getUri().getPath();
//							if(ret.indexOf('/')!=-1)
//							{
//								ret	= ret.substring(ret.lastIndexOf('/')+1);
//							}
//						}
					}
				}
				
				return ret!=null ? ret : node.toString();
			}
		});
		
		final String lid = exta.getComponentIdentifier().toString() + localexta.getComponentIdentifier().toString() + "_" + LISTENER_COUNTER++;
		SServiceProvider.searchService(exta, new ServiceQuery<>( IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				kernellistener = new TreePanelKernelListener(lid, getTree(), ((ModelFileFilterMenuItemConstructor)getMenuItemConstructor()));
				((IMultiKernelNotifierService)result).addKernelListener(kernellistener);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Ignore, no multi-kernel
			}
		});
		
		SServiceProvider.searchService(exta, new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService)result;
				libservicelistener = new ILibraryServiceListener()
				{
					public IFuture resourceIdentifierRemoved(final IResourceIdentifier parid, final IResourceIdentifier rid)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								URI uri = rid.getLocalIdentifier().getUri();
								try
								{
									// Comparison of file/urls is hard.
									// Fetches filenames of all root entries in model tree
									// and sends them with filename to remove to component
									// that is local to those filenames and can create and
									// compare them.
									
									final String toremove = uri.toString();
									
									final RootNode root = (RootNode)getModel().getRoot();
									final List children = root.getCachedChildren();
									final List filenames = new ArrayList();
									for(int i=0; i<children.size(); i++)
									{
										IFileNode child = (IFileNode)children.get(i);
										String filename = child.getFilePath();
										filenames.add(filename);
									}
									
									SRemoteGui.findChild(exta, toremove, filenames)
										.addResultListener(new DefaultResultListener<Integer>()
									{
										public void resultAvailable(Integer result)
										{
											final int res = result.intValue();
											if(res!=-1)
											{
												SwingUtilities.invokeLater(new Runnable()
												{
													public void run()
													{
														removeTopLevelNode((ISwingTreeNode)children.get(res));
													}
												});
											}
										}
									});
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						});
						return IFuture.DONE;
					}
					
					public IFuture resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean rem)
					{
						return IFuture.DONE;
					}
				};
				ls.addLibraryServiceListener(libservicelistener);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Ignore, no library service
			}
		});
	}
	
	/**
	 * 
	 * @param obj
	 */
	public void addTopLevelNodeMeta(Object obj)
	{
		if(obj instanceof File)
		{
			addTopLevelNode((File)obj);
		}
		else if(obj instanceof FileData)
		{
			addTopLevelNode((FileData)obj);
		}
		else if(obj instanceof IResourceIdentifier)
		{
			addTopLevelNode((IResourceIdentifier)obj);
		}
		else
		{
			throw new RuntimeException("Unknown node type: "+obj);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Add a root node to the tree panel. 
	 */
	protected void	addNode(final ISwingTreeNode node)
	{
		if(node instanceof IFileNode && node.getParent().equals(getTree().getModel().getRoot()))
		{
			if(node instanceof RIDNode)
			{
				final IResourceIdentifier rid = ((RIDNode)node).getResourceIdentifier();
				
				SServiceProvider.searchService(exta, new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new SwingDefaultResultListener<ILibraryService>()
				{
					public void customResultAvailable(final ILibraryService ls)
					{
						// todo: workspace=false?
						ls.addResourceIdentifier(null, rid, true).addResultListener(new SwingDefaultResultListener<IResourceIdentifier>()
						{
							public void customResultAvailable(IResourceIdentifier rid) 
							{
								// Todo: remove entries on remove.
								try
								{
									// Hack!!! Shouldn't use file when remote!?
									System.out.println("adding root: "+rid);
									File f = new File(rid.getLocalIdentifier().getUri());
//									addRootEntry(f.toURI().toURL(), f.getAbsolutePath(), rid);
									RIDNode rn = (RIDNode)node;
									rn.setFile(f);
									addRootEntry(f.toURI(), rn.getFilePath() , rid);
									
									ModelTreePanel.super.addNode(node);
								}
								catch(Exception e)
								{
//									e.printStackTrace();
									customExceptionOccurred(e);
								}
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								SRemoteGui.logWarning(exception.toString(), localexta);
							}
						});
					}
				});
			}
			else
			{
				// Convert file path or jar url to file url as used in lib service. 
				final String filepath = ((IFileNode)node).getFilePath();
				final String filename = filepath.startsWith("jar:")
					? filepath.endsWith("!/") ? filepath.substring(4, filepath.length()-2)
						: filepath.endsWith("!") ? filepath.substring(4, filepath.length()-1) : filepath.substring(4)
					: filepath.startsWith("file:") ? filepath : "file:"+filepath;
				
				SRemoteGui.addURL(exta, filename)
					.addResultListener(new SwingDefaultResultListener<Tuple2<URL, IResourceIdentifier>>()
				{
					public void customResultAvailable(Tuple2<URL, IResourceIdentifier> tup) 
					{
						// Todo: remove entries on remove.
	//					System.out.println("adding root: "+tup.getFirstEntity()+" "+tup.getSecondEntity());
						addRootEntry(SUtil.toURI0(tup.getFirstEntity()), filepath, tup.getSecondEntity());
						ModelTreePanel.super.addNode(node);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						SRemoteGui.logWarning(exception.toString(), localexta);
					}
				});
			}
		}
		else
		{
			super.addNode(node);
		}
	}
	
	/**
	 *  Add a top level node.
	 */
	public void addTopLevelNode(IResourceIdentifier rid)
	{
		final RootNode root = (RootNode)getModel().getRoot();
		ISwingTreeNode node = factory.createNode(root, model, tree, rid, 
			iconcache, exta, factory);
		addNode(node);
	}
	
	/**
	 *  Get the action.
	 *  @param name The action name.
	 *  @return The action.
	 */
	public Action getAction(String name)
	{
		return (Action)actions.get(name);
	}
	
	/**
	 *  Dispose the panel.
	 */
	public void dispose()
	{
		if(kernellistener!=null)
		{
			SServiceProvider.searchService(exta, new ServiceQuery<>( IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					((IMultiKernelNotifierService)result).removeKernelListener(kernellistener);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Ignore, no multi-kernel
				}
			});
		}
		if(libservicelistener!=null)
		{
			SServiceProvider.searchService(exta, new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					ILibraryService ls = (ILibraryService)result;
					ls.removeLibraryServiceListener(libservicelistener);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Ignore, no library service
				}
			});
		}
		super.dispose();
	}
	
	/**
	 *  Get the root entries of the tree.
	 */
	public Map<URI, IResourceIdentifier>	getRootEntries()
	{
		return rootentries;
	}
	
	/**
	 * 
	 */
	public void addRootEntry(URI uri, String path, IResourceIdentifier rid)
	{
//		System.out.println("putting: "+url+" "+path+" "+rid);
		rootentries.put(uri, rid);
		rootpathentries.put(path, rid);
	}
	
	/**
	 * 
	 */
	public IResourceIdentifier getRootEntry(String path)
	{
		return rootpathentries.get(path);
	}
	
	/**
	 *  Create a resource identifier.
	 */
	public static IFuture<IResourceIdentifier> createResourceIdentifier(IExternalAccess exta, final String filename)
	{
		return SRemoteGui.createResourceIdentifier(exta, filename, null);
	}
	
	protected abstract class ModelTreeAbstraction implements ITreeAbstraction
	{
		public boolean isRemote()
		{
			return ModelTreePanel.this.isRemote();
		}
		
		public JTree getTree()
		{
			return ModelTreePanel.this.getTree();
		}
		
		public IExternalAccess getExternalAccess()
		{
			return ModelTreePanel.this.getExternalAccess();
		}
		
		public IExternalAccess getGUIExternalAccess()
		{
			return localexta;
		}
		
		public abstract void action(Object obj);
	};
}
