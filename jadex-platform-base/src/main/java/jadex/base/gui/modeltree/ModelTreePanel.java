package jadex.base.gui.modeltree;

import jadex.base.gui.IPropertiesProvider;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.NodePath;
import jadex.base.gui.filetree.RootNode;
import jadex.base.gui.filetree.TreeProperties;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.tree.TreePath;

/**
 *  Tree for component models.
 */
public class ModelTreePanel extends FileTreePanel
{
	//-------- attributes --------
	
	/** The actions. */
	protected Map actions;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model tree panel.
	 */
	public ModelTreePanel(IExternalAccess exta, boolean remote)
	{
		super(exta, remote);
		actions = new HashMap();
		
		ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		ModelFileFilter ff = new ModelFileFilter(mic, exta);
		ModelIconCache ic = new ModelIconCache(exta, getTree());
		
		setFileFilter(ff);
		setMenuItemConstructor(mic);
		actions.put(AddPathAction.getName(), new AddPathAction(this));
		actions.put(AddRemotePathAction.getName(), new AddRemotePathAction(this));
		actions.put(RemovePathAction.getName(), new RemovePathAction(this));
		setPopupBuilder(new PopupBuilder(new Object[]{actions.get(AddPathAction.class), 
			actions.get(AddRemotePathAction.class), mic}));
		setIconCache(ic);
		addNodeHandler(new DefaultNodeHandler(getTree()));
	}
	
	//-------- methods --------
	
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
	 *  Write current state into properties.
	 */
	public IFuture getProperties()
	{
		final Future ret = new Future();
		final Properties props = new Properties();
		if(remote)
			return new Future(props);
		
		// Save tree properties.
		final TreeProperties	mep	= new TreeProperties();
		RootNode root = (RootNode)getTree().getModel().getRoot();
		String[] paths	= root.getPathEntries();
		for(int i=0; i<paths.length; i++)
			paths[i]	= SUtil.convertPathToRelative(paths[i]);
		mep.setRootPathEntries(paths);
		mep.setSelectedNode(getTree().getSelectionPath()==null ? null
			: NodePath.createNodePath((FileNode)getTree().getSelectionPath().getLastPathComponent()));
		List	expanded	= new ArrayList();
		Enumeration exp = getTree().getExpandedDescendants(new TreePath(root));
		if(exp!=null)
		{
			while(exp.hasMoreElements())
			{
				TreePath	path	= (TreePath)exp.nextElement();
				if(path.getLastPathComponent() instanceof FileNode)
				{
					expanded.add(NodePath.createNodePath((FileNode)path.getLastPathComponent()));
				}
			}
		}
		mep.setExpandedNodes((NodePath[])expanded.toArray(new NodePath[expanded.size()]));
		// todo: remove ThreadSuspendable()
		SServiceProvider.getService(exta.getServiceProvider(), 
			ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ClassLoader cl = ((ILibraryService)result).getClassLoader();
				String	treesave	= JavaWriter.objectToXML(mep, cl);	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
				props.addProperty(new Property("tree", treesave));
						
				// Save the last loaded file.
//				File sf = filechooser.getSelectedFile();
//				if(sf!=null)
//				{
//					String	lastpath	= SUtil.convertPathToRelative(sf.getAbsolutePath());
//					props.addProperty(new Property("lastpath", lastpath));
//				}

				// Save refresh/checking flags.
//				props.addProperty(new Property("refresh", Boolean.toString(refresh)));
				
				// Save the state of file filters
				if(mic instanceof IPropertiesProvider)
				{
					((IPropertiesProvider)mic).getProperties()
						.addResultListener(new SwingDelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							Properties	filterprops	= (Properties)result;
							props.addSubproperties(filterprops);
							ret.setResult(props);
						}
					});
				}
				else
				{
					ret.setResult(props);
				}
//				Properties	filterprops	= new Properties(null, "filter", null);
////				filtercon.isAll();
////				filterprops.addProperty(new Property("all", ""+filtercon.isAll()));
//				List ctypes = filtercon.getSelectedComponentTypes();
//				for(int i=0; i<ctypes.size(); i++)
//				{
//					String ctype = (String)ctypes.get(i);
//					filterprops.addProperty(new Property(ctype, "true"));
//				}
//				props.addSubproperties(filterprops);
//				ret.setResult(props);
			}
		});
		
		return ret;
	}

	/**
	 *  Update tool from given properties.
	 */
	public IFuture setProperties(final Properties props)
	{
		final Future ret = new Future();
		
		if(remote)
		{
			ret.setResult(null);
			return ret;
		}
//		refresh	= false;	// stops crawler task, if any
		
		SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService)result;
				
				// Load root node.
				String	treexml	= props.getStringProperty("tree");
				if(treexml==null)
				{
					ret.setResult(null);
				}
				else
				{
					try
					{
						// todo: hack!
						ClassLoader cl = ls.getClassLoader();
						TreeProperties	mep	= (TreeProperties)JavaReader.objectFromXML(treexml, cl); 	// Doesn't support inner classes: ModelExplorer$ModelExplorerProperties
//						ModelExplorerProperties	mep	= (ModelExplorerProperties)Nuggets.objectFromXML(treexml, cl);
						RootNode root = (RootNode)getTree().getModel().getRoot();
						root.removeAll();
						String[] entries = mep.getRootPathEntries();
						for(int i=0; i<entries.length; i++)
						{
							ITreeNode node = factory.createNode(root, model, tree, new File(entries[i]), iconcache, filefilter, exta, factory);
							root.addChild(node);
						}

						ITreeNode[] childs = root.getChildren();
						for(int i=0; i<childs.length; i++)
						{
							// Todo: support non-file (e.g. url nodes).
							File file = ((FileNode)childs[i]).getFile();
							
							// Hack!!! Build new file object. This strips trailing "/" from jar file nodes.
							file = new File(file.getParentFile(), file.getName());
							try
							{
								ls.addURL(file.toURI().toURL());
							}
							catch(MalformedURLException ex)
							{
								ex.printStackTrace();
							}
						}
						
						// Select the last selected model in the tree.
						expansionhandler.setSelectedPath(mep.getSelectedNode());

						// Load the expanded tree nodes.
						expansionhandler.setExpandedPaths(mep.getExpandedNodes());

						root.refresh(true);
						
						// Load last selected model.
//						String lastpath = props.getStringProperty("lastpath");
//						if(lastpath!=null)
//						{
//							try
//							{
//								File mo_file = new File(lastpath);
//								filechooser.setCurrentDirectory(mo_file.getParentFile());
//								filechooser.setSelectedFile(mo_file);
//							}
//							catch(Exception e)
//							{
//							}
//						}				
								
						// Load refresh/checking flag (defaults to true).
//						refresh	= !"false".equals(props.getStringProperty("refresh"));
//						if(refreshmenu!=null)
//							refreshmenu.setState(this.refresh);
//						resetCrawler();
						
						// Load the filter settings
						Properties	filterprops	= props.getSubproperty("mic");
						if(mic instanceof IPropertiesProvider)
							((IPropertiesProvider)mic).setProperties(filterprops)
							.addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result) 
							{
								ret.setResult(null);
							};
						});
						else
						{
							ret.setResult(null);
						}
						
//						if(filterprops!=null)
//						{
//							Property[] mps = filterprops.getProperties();
//							Set selected = new HashSet();
//							for(int i=0; i<mps.length; i++)
//							{
//								if(Boolean.parseBoolean(mps[i].getValue())) 
//									selected.add(mps[i].getType());
//							}
//							filtercon.setSelectedComponentTypes(selected);
//						}
					}
					catch(Exception e)
					{
						ret.setException(e);
						System.err.println("Cannot load project tree: "+e.getClass().getName());
//						e.printStackTrace();
					}
				}
			}	
		});
		
		return ret;
	}
}
