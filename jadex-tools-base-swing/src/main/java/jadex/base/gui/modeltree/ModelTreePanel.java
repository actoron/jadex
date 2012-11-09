package jadex.base.gui.modeltree;

import jadex.base.gui.RememberOptionMessage;
import jadex.base.gui.asynctree.AsyncTreeCellRenderer;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeFactory;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.RIDNode;
import jadex.base.gui.filetree.RootNode;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.IRemoteFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.io.File;
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
	protected Map<URL, IResourceIdentifier>	rootentries;
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
		this.rootentries	= new LinkedHashMap<URL, IResourceIdentifier>();
		this.rootpathentries	= new LinkedHashMap<String, IResourceIdentifier>();
		
		final ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		setNodeFactory(new DefaultNodeFactory()
		{
			public IRemoteFilter getFileFilter()
			{
				return new ModelFileFilter(mic, getRootEntries(), exta);
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
				ModelTreePanel.this.removeTopLevelNode((ITreeNode)node);
				
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
						
						getExternalAccess().scheduleStep(new IComponentStep<Void>()
						{
							@Classname("removeURL")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								final Future	ret	= new Future();
								SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(new DelegationResultListener<ILibraryService>(ret)
								{
									public void customResultAvailable(ILibraryService ls)
									{
										try
										{
											ls.removeURL(null, SUtil.toURL(path));
											ret.setResult(null);
										}
										catch(Exception ex)
										{
											ret.setException(ex);
										}
									}
								});
								return ret;
							}
						});
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
				ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
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
			public Icon getOverlay(ITreeNode node)
			{
				Icon	overlay	= null;
				if(getModel().getRoot().equals(node.getParent()) && node instanceof IFileNode)
				{
//					URL	url	= SUtil.toURL(((IFileNode)node).getFilePath());
//					IResourceIdentifier	rid	= rootentries.get(url);
					IResourceIdentifier rid = getRootEntry(((IFileNode)node).getFilePath());
					if(rid!=null && rid.getGlobalIdentifier()!=null)
					{
						overlay	= ModelTreePanel.this.icons.getIcon("gid");
					}
				}
				return overlay;
			}
		};
		
		dnh.addAction(rpa, null);
		addNodeHandler(dnh);
		
		tree.setCellRenderer(new AsyncTreeCellRenderer()
		{
			protected String getLabel(ITreeNode node)
			{
				String	ret	= null;
				if(getModel().getRoot().equals(node.getParent()) && node instanceof IFileNode)
				{
//					URL	url	= SUtil.toURL(((IFileNode)node).getFilePath());
//					IResourceIdentifier	rid	= rootentries.get(url);
					IResourceIdentifier rid = getRootEntry(((IFileNode)node).getFilePath());
					ret	= rid!=null && rid.getGlobalIdentifier()!=null
						? rid.getGlobalIdentifier().toString() : null;
					if(ret!=null && ret.indexOf(':')!=-1)
						ret	= ret.substring(ret.indexOf(':')+1);
				}
				
				return ret!=null ? ret : node.toString();
			}
		});
		
		final String lid = exta.getServiceProvider().getId().toString() + localexta.getServiceProvider().getId().toString() + "_" + LISTENER_COUNTER++;
		SServiceProvider.getService(exta.getServiceProvider(), IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
		
		SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
								URL url = rid.getLocalIdentifier().getUrl();
								try
								{
									// Comparison of file/urls is hard.
									// Fetches filenames of all root entries in model tree
									// and sends them with filename to remove to component
									// that is local to those filenames and can create and
									// compare them.
									
									final String toremove = url.toURI().toString();
									
									final RootNode root = (RootNode)getModel().getRoot();
									final List children = root.getCachedChildren();
									final List filenames = new ArrayList();
									for(int i=0; i<children.size(); i++)
									{
										IFileNode child = (IFileNode)children.get(i);
										String filename = child.getFilePath();
										filenames.add(filename);
									}
									
									exta.scheduleStep(new IComponentStep<Integer>()
									{
										@Classname("findchild")
										public IFuture<Integer> execute(IInternalAccess ia)
										{
											int ret = SUtil.indexOfFilename(toremove, filenames);
											return new Future<Integer>(new Integer(ret));
										}
									}).addResultListener(new DefaultResultListener<Integer>()
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
														removeTopLevelNode((ITreeNode)children.get(res));
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
	protected void	addNode(final ITreeNode node)
	{
		if(node instanceof IFileNode && node.getParent().equals(getTree().getModel().getRoot()))
		{
			if(node instanceof RIDNode)
			{
				final IResourceIdentifier rid = ((RIDNode)node).getResourceIdentifier();
				
				exta.scheduleStep(new IComponentStep<IResourceIdentifier>()
				{
					@Classname("addrid")
					public IFuture<IResourceIdentifier> execute(IInternalAccess ia)
					{
						final Future<IResourceIdentifier>	ret	= new Future<IResourceIdentifier>();
						IFuture<ILibraryService>	libfut	= SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
						libfut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IResourceIdentifier>(ret)
						{
							public void customResultAvailable(final ILibraryService ls)
							{
								// todo: workspace=false?
								ls.addResourceIdentifier(null, rid, true).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
							}
						});
						
						return ret;
					}
				}).addResultListener(new SwingDefaultResultListener<IResourceIdentifier>()
				{
					public void customResultAvailable(IResourceIdentifier rid) 
					{
						// Todo: remove entries on remove.
						try
						{
							System.out.println("adding root: "+rid);
							File f = new File(rid.getLocalIdentifier().getUrl().toURI());
//							addRootEntry(f.toURI().toURL(), f.getAbsolutePath(), rid);
							RIDNode rn = (RIDNode)node;
							rn.setFile(f);
							addRootEntry(f.toURI().toURL(), rn.getFilePath() , rid);
							
							ModelTreePanel.super.addNode(node);
						}
						catch(Exception e)
						{
//							e.printStackTrace();
							customExceptionOccurred(e);
						}
					}
					
					public void customExceptionOccurred(final Exception exception)
					{
						localexta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ia.getLogger().warning(exception.toString());
								return IFuture.DONE;
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
				
				exta.scheduleStep(new IComponentStep<Tuple2<URL, IResourceIdentifier>>()
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
								ls.getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, Tuple2<URL, IResourceIdentifier>>(ret)
								{
									public void customResultAvailable(List<IResourceIdentifier> rids)
									{
//										System.out.println("rids are: "+rids);
										
										// this ugly piece of code checks if test-classes are added
										// in this case it searched if the original package was also added
										// and if yes it is added as dependency to the test-package
										// this makes the necessary classes available for the test case
										
										String suftc = "test-classes";
										String s2 = url.toString();
										if(s2.endsWith(suftc))
											s2 = s2 + "/";
										suftc = "test-classes/";
										
										IResourceIdentifier tmp = null;
										if(s2.endsWith(suftc) && url.getProtocol().equals("file"))
										{
											String st2 = s2.substring(0, s2.lastIndexOf(suftc));
											for(IResourceIdentifier rid: rids)
											{
												if(rid.getLocalIdentifier()!=null)
												{
													URL u1 = rid.getLocalIdentifier().getUrl();
													String s1 = u1.toString();
													String sufc = "classes";
													if(s1.endsWith(sufc))
														s1 = s1 + "/";
													sufc = "classes/";
													
													if(s1.endsWith(sufc) && u1.getProtocol().equals("file"))
													{
														String st1 = s1.substring(0, s1.lastIndexOf(sufc));
														if(st1.equals(st2))
														{
															tmp = rid;
	//														System.out.println("url: "+u1.getPath());
															break;
														}
													}
												}
											}
										}
										final IResourceIdentifier deprid = tmp;
										
										// todo: workspace=true?
										ls.addURL(null, url).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
										{
											public void customResultAvailable(IResourceIdentifier rid)
											{
												if(deprid!=null)
												{
													ls.addResourceIdentifier(rid, deprid, true).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
													{
														public void customResultAvailable(IResourceIdentifier rid)
														{
															ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
														}
													});
												}
												else
												{
													ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
												}
											}
											public void exceptionOccurred(Exception exception)
											{
												exception.printStackTrace();
												super.exceptionOccurred(exception);
											}
										});
									}
								});
							}
						});
						
						return ret;
					}
				}).addResultListener(new SwingDefaultResultListener<Tuple2<URL, IResourceIdentifier>>()
				{
					public void customResultAvailable(Tuple2<URL, IResourceIdentifier> tup) 
					{
						// Todo: remove entries on remove.
	//					System.out.println("adding root: "+tup.getFirstEntity()+" "+tup.getSecondEntity());
						addRootEntry(tup.getFirstEntity(), filepath, tup.getSecondEntity());
						ModelTreePanel.super.addNode(node);
					}
					
					public void customExceptionOccurred(final Exception exception)
					{
						localexta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ia.getLogger().warning(exception.toString());
								return IFuture.DONE;
							}
						});					
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
		ITreeNode node = factory.createNode(root, model, tree, rid, 
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
			SServiceProvider.getService(exta.getServiceProvider(), IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IResultListener()
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
			SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
	public Map<URL, IResourceIdentifier>	getRootEntries()
	{
		return rootentries;
	}
	
	/**
	 * 
	 */
	public void addRootEntry(URL url, String path, IResourceIdentifier rid)
	{
//		System.out.println("putting: "+url+" "+path+" "+rid);
		rootentries.put(url, rid);
		rootpathentries.put(path, rid);
	}
	
	/**
	 * 
	 */
	public IResourceIdentifier getRootEntry(URL url)
	{
		return rootentries.get(url);
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
		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
		
//		ret.addResultListener(new IResultListener<IResourceIdentifier>()
//		{
//			public void resultAvailable(IResourceIdentifier result)
//			{
//				System.out.println("try loading with:"+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//			}
//		});
		
//		System.out.println("rid a:"+filename);
//		System.out.println("platform access: "+jcc.getPlatformAccess().getComponentIdentifier().getName());
//		System.out.println("local access: "+jcc.getJCCAccess().getComponentIdentifier().getName());
		
		exta.scheduleStep(new IComponentStep<IResourceIdentifier>()
		{
			@Classname("createRid")
			public IFuture<IResourceIdentifier> execute(IInternalAccess ia)
			{
				final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
				
				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<ILibraryService, IResourceIdentifier>(ret)
				{
					public void customResultAvailable(ILibraryService ls)
					{
						// Must be done on remote site as SUtil.toURL() uses new File()
						final URL url = SUtil.toURL(filename);
//						System.out.println("url: "+filename);
						ls.getResourceIdentifier(url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
					}
				}));
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
		
//		
//		ret.addResultListener(new DefaultResultListener<IResourceIdentifier>()
//		{
//			public void resultAvailable(IResourceIdentifier result)
//			{
//				System.out.println("rid b:"+result);
//			}
//		});
		
		return ret;
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
