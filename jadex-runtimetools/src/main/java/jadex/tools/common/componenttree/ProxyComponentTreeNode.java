package jadex.tools.common.componenttree;

import jadex.base.service.remote.ProxyAgent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.ICommand;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.service.IService;
import jadex.tools.common.CombiIcon;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 * 
 */
public class ProxyComponentTreeNode extends ComponentTreeNode
{
	/** The remote component identifier.*/
	protected IComponentIdentifier cid;
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"overlay_proxy", SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/tools/common/images/overlay_proxy.png"),
	});
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProxyComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, IComponentDescription desc,
		IComponentManagementService cms, Component ui, ComponentIconCache iconcache)
	{
		super(parent, model, desc, cms, ui, iconcache);
	}
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		Icon ret = null;
		Icon base = super.getIcon();
		if(base!=null)
		{
			ret = new CombiIcon(new Icon[]{base, icons.getIcon("overlay_proxy")});
		}
		return ret;
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		final List children = new ArrayList();
		final boolean ready[] = new boolean[2];

		cms.getExternalAccess(desc.getName()).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IMicroExternalAccess exta = (IMicroExternalAccess)result;
				exta.scheduleStep(new ICommand()
				{
					public void execute(Object agent)
					{
						ProxyAgent pa = (ProxyAgent)agent;
						cid = pa.getRemotePlatformIdentifier();
						pa.getVirtualChildren(cid).addResultListener(pa.createResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IComponentDescription[] descs = (IComponentDescription[])
									((Collection)result).toArray(new IComponentDescription[((Collection)result).size()]);
								for(int i=0; i<descs.length; i++)
								{
									IComponentTreeNode node = getModel().getNode(descs[i].getName());
									if(node==null)
									{
										node = new VirtualComponentTreeNode(ProxyComponentTreeNode.this, getModel(), descs[i], cms, ui, iconcache, exta);
									}
									children.add(node);
								}
								
								ready[0] = true;
								if(ready[0] && ready[1])
								{
									setChildren(children);
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								exception.printStackTrace();
							}
						}));
					}
				});
				
				exta.scheduleStep(new ICommand()
				{
					public void execute(Object agent)
					{
						ProxyAgent pa = (ProxyAgent)agent;
						cid = pa.getRemotePlatformIdentifier();
						pa.getRemoteServices(cid).addResultListener(
							pa.createResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								List services = (List)result;
								if(services!=null && !services.isEmpty())
								{
									ServiceContainerNode scn = (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
									if(scn==null)
										scn	= new ServiceContainerNode(ProxyComponentTreeNode.this, getModel());
									children.add(0, scn);
									List subchildren = new ArrayList();
									for(int i=0; i<services.size(); i++)
									{
										IService service = (IService)services.get(i);
										ServiceNode	sn = (ServiceNode)getModel().getNode(service.getServiceIdentifier());
										if(sn==null)
											sn = new ServiceNode(scn, getModel(), service);
										subchildren.add(sn);
									}
									scn.setChildren(subchildren);							
								}

								ready[1] = true;
								if(ready[0] &&  ready[1])
								{
									setChildren(children);
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								exception.printStackTrace();
							}
						}));
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return cid==null? desc.getName().getLocalName(): desc.getName().getLocalName()+"("+cid+")";
	}
}
