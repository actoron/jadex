package jadex.base.gui.componenttree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.gui.SGUI;

/**
 *  The platform tree nodes displays a Jadex platform with views
 *  that group components into predefined folders (proxy, application, system).
 */
public class PlatformTreeNode extends ComponentTreeNode
{
	/**
	 * The image icons.
	 */
	public static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"platform", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/platform.png")
	});
	
//	/** The folder for proxies. */
//	protected ViewTreeNode proxy;
//	
//	/** The folder for applications. */
//	protected ViewTreeNode application;
//	
//	/** The folder for applications. */
//	protected ViewTreeNode system;
	
	/** The real children. */
	protected List<? extends ITreeNode> realchildren; 
	
	/**
	 *  Create a new service container node.
	 */
	public PlatformTreeNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree, desc, cms, iconcache, access);
		
//		System.out.println(getId());
//		proxy = new ViewTreeNode("Platforms", this, model, tree, null);
//		application = new ViewTreeNode("Applications", this, model, tree, null);
//		system = new ViewTreeNode("System", this, model, tree, null);
	}
	
	/**
	 * 
	 */
	protected void setChildren(List<? extends ITreeNode> newchildren)
	{
//		System.out.println("setChildren: "+newchildren);
		this.realchildren = newchildren;
		
		List<ITreeNode> childs = new ArrayList<ITreeNode>();
		List<ITreeNode> chpr = new ArrayList<ITreeNode>();
		List<ITreeNode> chap = new ArrayList<ITreeNode>();
		List<ITreeNode> chsy = new ArrayList<ITreeNode>();
		
		for(ITreeNode node: newchildren)
		{
			if(node instanceof ProxyComponentTreeNode || node instanceof PseudoProxyComponentTreeNode)
			{
				chpr.add(node);
			}
			else if(node instanceof ComponentTreeNode)
			{
				ComponentTreeNode n = (ComponentTreeNode)node;
				if(n.getDescription().isSystemComponent())
				{
					chsy.add(node);
				}
				else
				{
					chap.add(node);
				}
			}
			else
			{
				childs.add(node);
			}
		}
		
		if(chpr.size()>0)
		{
			ViewTreeNode proxy = getChildPlatforms();
			proxy.setChildren(chpr);
			childs.add(proxy);
		}
		if(chap.size()>0)
		{
			ViewTreeNode application = getChildApplications();
			application.setChildren(chap);
			childs.add(application);
		}
		if(chsy.size()>0)
		{
			ViewTreeNode system = getChildSystem();
			system.setChildren(chsy);
			childs.add(system);
		}
		
//		System.out.println("childs: "+childs.size()+" "+childs);
		super.setChildren(childs);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected ViewTreeNode getChildPlatforms()
	{
		return getChild("Platforms");
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected ViewTreeNode getChildApplications()
	{
		return getChild("Applications");
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected ViewTreeNode getChildSystem()
	{
		return getChild("System");
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected ViewTreeNode getChild(String name)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		ViewTreeNode node = (ViewTreeNode)model.getNode(getId()+name);
		if(node==null)
		{
			node = new ViewTreeNode(name, this, (AsyncSwingTreeModel)model, tree, null);
		}
		return node;
	}
	
	/**
	 * 
	 */
	protected void addNode(ITreeNode node)
	{
//		List<ITreeNode> childs = new ArrayList<ITreeNode>(getCachedChildren());
//		int cnt = childs.size();
		
		if(node instanceof ProxyComponentTreeNode || node instanceof PseudoProxyComponentTreeNode)
		{
			ViewTreeNode proxy = getChildPlatforms();
			proxy.addChild(node);
			if(proxy.getChildCount()==1)
			{
//				childs.add(proxy);
				insertNode(proxy);
			}
		}
		else if(node instanceof ComponentTreeNode)
		{
			ComponentTreeNode n = (ComponentTreeNode)node;
			if(n.getDescription().isSystemComponent())
			{
				ViewTreeNode system = getChildSystem();
				system.addChild(node);
				if(system.getChildCount()==1)
					insertNode(system);
//					childs.add(system);
			}
			else
			{
				ViewTreeNode application = getChildApplications();
				application.addChild(node);
				if(application.getChildCount()==1)
					insertNode(application);
//					childs.add(application);
			}
		}
		else
		{
//			childs.add(node);
			insertNode(node);
		}
		
//		if(cnt!=childs.size())
//			setChildren(childs);
		
//		System.out.println("childs: "+getCachedChildren().size());
	}
	
	/**
	 * 
	 */
	protected void removeNode(ITreeNode node)
	{
//		List<ITreeNode> childs = new ArrayList<ITreeNode>(getCachedChildren());
//		int cnt = childs.size();
		
		if(node instanceof ProxyComponentTreeNode || node instanceof PseudoProxyComponentTreeNode)
		{
			ViewTreeNode proxy = getChildPlatforms();
			proxy.removeChild(node);
			if(proxy.getChildCount()==0)
//				childs.remove(proxy);
				super.removeChild(proxy);
		}
		else if(node instanceof ComponentTreeNode)
		{
			ComponentTreeNode n = (ComponentTreeNode)node;
			if(n.getDescription().isSystemComponent())
			{
				ViewTreeNode system = getChildSystem();
				system.removeChild(node);
				if(system.getChildCount()==0)
					super.removeChild(system);
//					childs.remove(system);
			}
			else
			{
				ViewTreeNode application = getChildApplications();
				application.removeChild(node);
				if(application.getChildCount()==0)
					super.removeChild(application);
//					childs.remove(application);
			}
		}
		else
		{
//			childs.remove(node);
			super.removeChild(node);
		}
		
//		if(cnt!=childs.size())
//			setChildren(childs);
	}
	
	/**
	 * 
	 */
	protected void insertNode(ITreeNode node)
	{
		boolean ins = false;
		for(int i=0; i<getChildCount() && !ins; i++)
		{
			ISwingTreeNode child = getChild(i);
			if(child instanceof ServiceContainerNode || child instanceof NFPropertyContainerNode)
				continue;
			if(child.toString().toLowerCase().compareTo(node.toString().toLowerCase())>=0)
			{
				super.addChild(i, node);
				ins = true;
			}
		}
		if(!ins)
		{
			super.addChild(node);
		}
	}
	
//	/**
//	 *  Create a new component node.
//	 */
//	public ISwingTreeNode	createComponentNode(final IComponentDescription desc)
//	{
//		ISwingTreeNode	node	= getModel().getNode(desc.getName());
//		if(node==null)
//		{
//			boolean proxy = "jadex.platform.service.remote.Proxy".equals(desc.getModelName())
//				// Only create proxy nodes for local proxy components to avoid infinite nesting.
//				&& ((IActiveComponentTreeNode)getModel().getRoot()).getComponentIdentifier().getName().equals(desc.getName().getPlatformName());
//			if(proxy)
//			{
//				node = new ProxyComponentTreeNode(this, getModel(), getTree(), desc, cms, iconcache, access);
//			}
//			else
//			{
//				node = new ComponentTreeNode(this, getModel(), getTree(), desc, cms, iconcache, access);
//			}
//		}
//		return node;
//	}
	
	/**
	 * 
	 */
	public void addChild(int index, ITreeNode node)
	{
		addNode(node);
//		if(realchildren==null)
//			realchildren = new ArrayList<ITreeNode>();
//		((ArrayList)realchildren).add(index, node);
//		setChildren(realchildren);
	}
	
	/**
	 * 
	 */
	public void addChild(ITreeNode node)
	{
		addNode(node);
//		if(realchildren==null)
//			realchildren = new ArrayList<ITreeNode>();
//		((ArrayList)realchildren).add(node);
//		setChildren(realchildren);
	}
	
	/**
	 * 
	 */
	public void removeChild(ITreeNode node)
	{
		removeNode(node);
//		if(realchildren!=null)
//		{
//			realchildren.remove(node);
//			setChildren(realchildren);
//		}
	}
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		return icons.getIcon("platform");
	}
}
