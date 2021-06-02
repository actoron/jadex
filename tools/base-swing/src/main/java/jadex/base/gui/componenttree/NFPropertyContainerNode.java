package jadex.base.gui.componenttree;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Node object representing a nf properties container
 */
public class NFPropertyContainerNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The node name (used as id suffix and displayed in the tree). */
	public static final String	NAME	= "Non-functional Properties";
	
	/** The service container icon. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/nonfunc.png"),
	});
	
	/** The name. */
	protected String name;
	
	/** The tooltip. */
	protected String tooltip;
	
	
	/** The external access of the nfproperty provider. */
	protected IExternalAccess ea;
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The method info. */
	protected MethodInfo mi;
	
	/** The required service info. */
	protected RequiredServiceInfo rinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a new nf property container node.
	 *  - component (sid=null, mi=null)
	 *  - service (sid!=null, mi=null)
	 *  - method (sid!=null, mi!=null)
	 */
	public NFPropertyContainerNode(String name, String tooltip, ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree,
		IExternalAccess ea, IServiceIdentifier sid, MethodInfo mi, RequiredServiceInfo rinfo)
	{
		super(parent, model, tree);
		this.name = name;
		this.tooltip = tooltip;
		this.ea = ea;
		this.sid = sid;
		this.mi = mi;
		this.rinfo = rinfo;
		model.registerNode(this);
		
//		PropertyUpdateHandler puh = (PropertyUpdateHandler)tree.getClientProperty(PropertyUpdateHandler.class);
//		if(puh!=null)
//		{
//			String id = sid+":"+mi+":"+name;
//			puh.addPropertyCommand(ea.getComponentIdentifier(), id, new ICommand<IMonitoringEvent>()
//			{
//				public void execute(IMonitoringEvent ev)
//				{
//					System.out.println("received: "+ev);
//				}
//			});
//		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object getId()
	{
		return getId(getParent().getId(), name);
//		return getParent().getId()+NAME+(name==null? "": name);
	}
	
	/**
	 * 
	 * @param parentid
	 * @param name
	 * @return
	 */
	public static String getId(Object parentid, String name)
	{
		return parentid.toString()+NAME+(name==null? "": name);
	}
	
	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		Icon	ret	= icons.getIcon("service-container");
		return ret;
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
//		System.out.println("start search childs: "+getId());
		getNFPropertyInfos().addResultListener(new SwingResultListener<Collection<INFPropertyMetaInfo>>(
			new IResultListener<Collection<INFPropertyMetaInfo>>()
		{
			public void resultAvailable(Collection<INFPropertyMetaInfo> result)
			{
//				System.out.println("found childs: "+getId()+" "+result.size());
				List<NFPropertyNode> children = new ArrayList<NFPropertyNode>();
				for(INFPropertyMetaInfo p: result)
				{
					NFPropertyNode nfpn = (NFPropertyNode)model.getNode(NFPropertyNode.getId(NFPropertyContainerNode.this.getId(), p.getName()));
					if(nfpn==null)
					{
						nfpn = new NFPropertyNode(NFPropertyContainerNode.this, 
							getModel(), getTree(), p, ea, sid, mi, rinfo);
					}
					children.add(nfpn);
				}
				
				Collections.sort(children, new java.util.Comparator<ISwingTreeNode>()
				{
					public int compare(ISwingTreeNode t1, ISwingTreeNode t2)
					{
						String si1 = ((NFPropertyNode)t1).getMetaInfo().getName();
						String si2 = ((NFPropertyNode)t2).getMetaInfo().getName();
						return si1.compareTo(si2);
					}
				});
				
				setChildren(children);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex on: "+getId());
			}
		}));
	}
	
	/**
	 *  Remove property from provider.
	 */
	protected IIntermediateFuture<INFPropertyMetaInfo> getNFPropertyInfos()
	{
		final IntermediateFuture<INFPropertyMetaInfo> ret = new IntermediateFuture<INFPropertyMetaInfo>();
		
		if(rinfo!=null)
		{
			final MethodInfo fmi = mi;
			ea.scheduleStep(new IComponentStep<Collection<INFPropertyMetaInfo>>()
			{
				public IFuture<Collection<INFPropertyMetaInfo>> execute(IInternalAccess ia)
				{
					final IntermediateFuture<INFPropertyMetaInfo> ret = new IntermediateFuture<INFPropertyMetaInfo>();
					if(fmi!=null)
					{
						INFMixedPropertyProvider pp = ia.getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(null);
						pp.getMethodNFPropertyMetaInfos(fmi).addResultListener(
							new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
						{
							public void customResultAvailable(Map<String,INFPropertyMetaInfo> mis)
							{
								for(INFPropertyMetaInfo mi: mis.values())
								{
									ret.addIntermediateResult(mi);
								}
								ret.setFinished();
							}
							public void exceptionOccurred(Exception e)
							{
								e.printStackTrace();
							}
						});
					}
					else
					{
						INFMixedPropertyProvider pp = ia.getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(null);
						pp.getNFPropertyMetaInfos().addResultListener(
							new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
						{
							public void customResultAvailable(Map<String,INFPropertyMetaInfo> mis)
							{
								for(INFPropertyMetaInfo mi: mis.values())
								{
									ret.addIntermediateResult(mi);
								}
								ret.setFinished();
							}
							
							public void exceptionOccurred(Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					return ret;
				}
			}).addResultListener(new DelegationResultListener<Collection<INFPropertyMetaInfo>>(ret));
		}
		else
		{
			if(sid!=null)
			{
				IFuture<IService> fut = ea.searchService( new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sid));
				fut.addResultListener(new SwingResultListener<IService>(new IResultListener<IService>()
				{
					public void resultAvailable(IService ser) 
					{
						if(mi!=null)
						{
							ea.getMethodNFPropertyMetaInfos(ser.getServiceId(), mi)
//							((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getMethodNFPropertyMetaInfos(mi)
								.addResultListener(new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
							{
								public void customResultAvailable(Map<String, INFPropertyMetaInfo> mis)
								{
									for(INFPropertyMetaInfo mi: mis.values())
									{
										ret.addIntermediateResult(mi);
									}
									ret.setFinished();
								}
							});
						}
						else
						{
							ea.getNFPropertyMetaInfos(ser.getServiceId())
//							((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getNFPropertyMetaInfos()
								.addResultListener(new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
							{
								public void customResultAvailable(Map<String, INFPropertyMetaInfo> mis)
								{
									for(INFPropertyMetaInfo mi: mis.values())
									{
										ret.addIntermediateResult(mi);
									}
									ret.setFinished();
								}
							});
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				}));
			}
			else if(ea!=null)
			{
//				((INFPropertyProvider)ea.getExternalComponentFeature(INFPropertyComponentFeature.class)).getNFPropertyMetaInfos().addResultListener(
//					new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
//				{
//					public void customResultAvailable(Map<String, INFPropertyMetaInfo> mis)
//					{
//						for(INFPropertyMetaInfo mi: mis.values())
//						{
//							ret.addIntermediateResult(mi);
//						}
//						ret.setFinished();
//					}
//				});
				
				ea.getNFPropertyMetaInfos().addResultListener(
					new ExceptionDelegationResultListener<Map<String, INFPropertyMetaInfo>, Collection<INFPropertyMetaInfo>>(ret)
				{
					public void customResultAvailable(Map<String, INFPropertyMetaInfo> mis)
					{
						for(INFPropertyMetaInfo mi: mis.values())
						{
							ret.addIntermediateResult(mi);
						}
						ret.setFinished();
					}
				});
			}
			else
			{
				ret.setException(new RuntimeException("Provider not set."));
			}
		}
		return ret;
	}
	
	/**
	 *  Set the children.
	 */
	protected void setChildren(List<? extends ITreeNode> children)
	{
		super.setChildren(children);
	}

	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return name!=null? name: NAME;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return tooltip!=null? tooltip: null;
	}
}
