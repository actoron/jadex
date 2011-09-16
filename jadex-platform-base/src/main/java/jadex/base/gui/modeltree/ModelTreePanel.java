package jadex.base.gui.modeltree;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.RootNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.bridge.service.library.LibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.xml.annotation.XMLClassname;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 *  Tree for component models.
 */
public class ModelTreePanel extends FileTreePanel
{
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
	public ModelTreePanel(final IExternalAccess exta, IExternalAccess localexta, boolean remote)
	{
		super(exta, remote, false);
		this.localexta = localexta;
		actions = new HashMap();
		
		ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		ModelFileFilter ff = new ModelFileFilter(mic, exta);
		ModelIconCache ic = new ModelIconCache(exta, getTree());
		
		setFileFilter(ff);
		setMenuItemConstructor(mic);
		actions.put(AddPathAction.getName(), remote ? new AddRemotePathAction(this) : new AddPathAction(this));
		actions.put(RemovePathAction.getName(), new RemovePathAction(this));
		setPopupBuilder(new PopupBuilder(new Object[]{actions.get(AddPathAction.getName()), 
			actions.get(AddRemotePathAction.getName()), mic}));
		setMenuItemConstructor(mic);
		setIconCache(ic);
		DefaultNodeHandler dnh = new DefaultNodeHandler(getTree());
		dnh.addAction(new RemovePathAction(this), null);
		addNodeHandler(dnh);
		
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
					public IFuture urlRemoved(final URL url)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
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
									
									exta.scheduleStep(new IComponentStep()
									{
										@XMLClassname("findchild")
										public Object execute(IInternalAccess ia)
										{
											int ret = LibraryService.indexOfFilename(toremove, filenames);
											return new Integer(ret);
										}
									}).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											final int res = ((Integer)result).intValue();
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
					
					public IFuture urlAdded(URL url)
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
	
	//-------- methods --------
	
	/**
	 *  Add a root node to the tree panel. 
	 */
	protected void	addNode(final ITreeNode node)
	{
		if(node instanceof IFileNode && node.getParent().equals(getTree().getModel().getRoot()))
		{
			SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					final ILibraryService ls = (ILibraryService)result;
					
					ls.getAllURLs().addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							final List urls = (List)result;
							
							// Hack!!! add protocol if not present to allow comparison with library service URLs.
							final String filepath = ((IFileNode)node).getFilePath();
							final String filename = filepath.startsWith("file:") || filepath.startsWith("jar:file:")
								? filepath : "file:"+filepath;
							exta.scheduleStep(new IComponentStep()
							{
								@XMLClassname("fileexists")
								public Object execute(IInternalAccess ia)
								{
									List urlstrings	= new ArrayList();
									List exceptions = new ArrayList();
									for(int i=0; i<urls.size(); i++)
									{
										try
										{
											urlstrings.add(((URL)urls.get(i)).toURI().toString());
										}
										catch(Exception e)
										{
											exceptions.add(e);
//											e.printStackTrace();
										}
									}
									boolean res = LibraryService.indexOfFilename(filename, urlstrings)!=-1;
									
//									boolean res = ((Boolean)result).booleanValue();
									if(!res)
									{
//										System.out.println("Need to add path: "+filename);
										try
										{
											ls.addURL(LibraryService.toURL(filepath));
										}
										catch(Exception e)
										{
											exceptions.add(e);
//											e.printStackTrace();
										}
									}
									
									return exceptions;//new Boolean(LibraryService.indexOfFilename(filename, urlstrings)!=-1);
								}
							}).addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(final Object result) 
								{
									ModelTreePanel.super.addNode(node);
									
									if(result instanceof List)
									{
										localexta.scheduleStep(new IComponentStep()
										{
											public Object execute(IInternalAccess ia)
											{
												List exs = (List)result;
												
												for(int i=0; i<exs.size(); i++)
												{
													ia.getLogger().warning(exs.get(i).toString());
												}
												return null;
											}
										});
									}
								};
							});
						}
					});
				}
			});
		}
		else
		{
			super.addNode(node);
		}
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
}
