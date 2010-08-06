package jadex.tools.common.componenttree;

import jadex.base.service.remote.ProxyAgent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.ICommand;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.tools.common.CombiIcon;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 * 
 */
public class ProxyComponentTreeNode extends ComponentTreeNode
{
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
	 * /
	protected void	searchChildren()
	{
		final List	children	= new ArrayList();

		cms.getExternalAccess(desc.getName()).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IMicroExternalAccess agent = (IMicroExternalAccess)result;
				agent.scheduleStep(new ICommand()
				{
					public void execute(Object agent)
					{
						ProxyAgent pa = (ProxyAgent)agent;
						IComponentIdentifier cid = pa.getRemotePlatformIdentifier();
					
						
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
		
		
		final IComponentIdentifier[] achildren = cms.getChildren(desc.getName());
		if(achildren!=null && achildren.length > 0)
		{
			for(int i=0; i<achildren.length; i++)
			{
				final int index = i;
				cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentDescription	desc	= (IComponentDescription)result;
						IComponentTreeNode	node	= getModel().getNode(desc.getName());
						if(node==null)
						{
							createComponentNode(desc).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									children.add(result);
									
									// Last child? -> inform listeners
									if(index == achildren.length - 1)
									{
										ready[0]	= true;
										if(ready[0] &&  ready[1])
										{
											setChildren(children);
										}
									}
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									exception.printStackTrace();
								}
							});
						}
						else
						{
							children.add(node);
	
							// Last child? -> inform listeners
							if(index == achildren.length - 1)
							{
								ready[0]	= true;
								if(ready[0] &&  ready[1])
								{
									setChildren(children);
								}
							}
						}
					}
				});
			}
		}
		else
		{
			ready[0]	= true;
			if(ready[0] &&  ready[1])
			{
				setChildren(children);
			}
		}
		
		// Search services and only add container node when services are found.
//		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				IExternalAccess	ea	= (IExternalAccess)result;
//				SServiceProvider.getDeclaredServices(ea.getServiceProvider()).addResultListener(new SwingDefaultResultListener(ui)
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						List	services	= (List)result;
//						if(services!=null && !services.isEmpty())
//						{
//							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
//							if(scn==null)
//								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel());
//							children.add(0, scn);
//							List	subchildren	= new ArrayList();
//							for(int i=0; i<services.size(); i++)
//							{
//								IService service	= (IService)services.get(i);
//								ServiceNode	sn	= (ServiceNode)getModel().getNode(service.getServiceIdentifier());
//								if(sn==null)
//									sn	= new ServiceNode(scn, getModel(), service);
//								subchildren.add(sn);
//							}
//							scn.setChildren(subchildren);							
//						}
//
//						ready[1]	= true;
//						if(ready[0] &&  ready[1])
//						{
//							setChildren(children);
//						}
//					}
//				});
//			}
//		});*/
//	}
}
