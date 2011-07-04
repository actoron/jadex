package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.RootNode;
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
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.xml.annotation.XMLClassname;

import java.io.File;
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
	
	//-------- constructors --------
	
	/**
	 *  Create a new model tree panel.
	 */
	public ModelTreePanel(final IExternalAccess exta, IExternalAccess localexta, boolean remote)
	{
		super(exta, remote, false);
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
										String filename = child.getFileName();
										filenames.add(filename);
									}
									
									exta.scheduleStep(new IComponentStep()
									{
										@XMLClassname("findchild")
										public Object execute(IInternalAccess ia)
										{
											int ret = -1;
											try
											{
												File target = new File(LibraryService.toURL(toremove).toURI());
												for(int i=0; i<filenames.size() && ret==-1; i++)
												{
													File test = new File(LibraryService.toURL((String)filenames.get(i)).toURI());
													if(target.getCanonicalPath().equals(test.getCanonicalPath()))
														ret = i;
												}
											}
											catch(Exception e)
											{
//												e.printStackTrace();
											}
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
														root.removeChild((ITreeNode)children.get(res));
													}
												});
											}
										}
									});
								}
								catch(Exception e)
								{
//									e.printStackTrace();
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
			final String filename = ((IFileNode)node).getFileName();
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
							
							exta.scheduleStep(new IComponentStep()
							{
								@XMLClassname("fileexists")
								public Object execute(IInternalAccess ia)
								{
									boolean ret = false;
									try
									{
										File target = new File(LibraryService.toURL(filename).toURI());
										for(int i=0; i<urls.size() && !ret; i++)
										{
											File test = new File(((URL)urls.get(i)).toURI());
											if(target.getCanonicalPath().equals(test.getCanonicalPath()))
												ret = true;
										}
									}
									catch(Exception e)
									{
//										e.printStackTrace();
									}
									return new Boolean(ret);
								}
							}).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									boolean res = ((Boolean)result).booleanValue();
									if(!res)
									{
//										System.out.println("Need to add path: "+filename);
										try
										{
											ls.addURL(LibraryService.toURL(filename));
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
									}
										
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											ModelTreePanel.super.addNode(node);
										}
									});
								}
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
