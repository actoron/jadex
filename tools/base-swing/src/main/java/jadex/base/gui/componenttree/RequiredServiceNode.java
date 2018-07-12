package jadex.base.gui.componenttree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.NFRPropertyInfo;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;

/**
 *  Node object representing a service container.
 */
public class RequiredServiceNode extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(RequiredServiceNode.class, "/jadex/base/gui/images/required_16.png"),
		"services", SGUI.makeIcon(RequiredServiceNode.class, "/jadex/base/gui/images/required_multiple_16.png")
	});
	
	//-------- attributes --------
	
	/** The service info. */
	private final RequiredServiceInfo info;
	
	/** The node id. */
	protected final String nid;

	/** The properties component (if any). */
	protected RequiredServiceProperties	propcomp;

	/** The external access. */
	protected IExternalAccess ea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public RequiredServiceNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, RequiredServiceInfo info, String nid, IExternalAccess ea)
	{
		super(parent, model, tree);
		this.info = info;
		this.nid = nid;
		this.ea = ea;
//		if(service==null || service.getId()==null)
//			System.out.println("service node: "+this);
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service info.
	 */
	public RequiredServiceInfo getServiceInfo()
	{
		return info;
	}

	/**
	 *  Get the id used for lookup.
	 */
	public Object getId()
	{
		return nid;
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
		return info.isMultiple()? icons.getIcon("services"): icons.getIcon("service");
	}

	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return SReflect.getUnqualifiedTypeName(info.getType().getTypeName());
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return info.getName()+" "+info.getDefaultBinding();
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return true;
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		NFPropertyContainerNode cn = null;
		List<NFPropertyContainerNode> childs = new ArrayList<NFPropertyContainerNode>();
		
		if(info.getNFRProperties()!=null && info.getNFRProperties().size()>0)
		{
			Map<MethodInfo, List<NFRPropertyInfo>> props = new HashMap<MethodInfo, List<NFRPropertyInfo>>();
			for(NFRPropertyInfo pi: info.getNFRProperties())
			{
				List<NFRPropertyInfo> tmp = props.get(pi.getMethodInfo());
				if(tmp==null)
				{
					tmp = new ArrayList<NFRPropertyInfo>();
					props.put(pi.getMethodInfo(), tmp);
				}
				tmp.add(pi);
			}

			Set<String> doublenames = new HashSet<String>();
			Set<String> tmp = new HashSet<String>();
			for(MethodInfo mi: props.keySet())
			{
				if(tmp.contains(mi.getName()))
				{
					doublenames.add(mi.getName());
				}
				else
				{
					tmp.add(mi.getName());
				}
			}
			
			for(MethodInfo mi: props.keySet())
			{
				if(mi==null)
				{
					String name = "Service properties";
					cn = (NFPropertyContainerNode)model.getNode(NFPropertyContainerNode.getId(getId(), name));
					if(cn==null)
						cn = new NFPropertyContainerNode(null, name, RequiredServiceNode.this, (AsyncSwingTreeModel)model, tree, ea, null, null, info);
				}
				else
				{
					String name = doublenames.contains(mi.getName())? mi.getNameWithParameters(): mi.getName();
					NFPropertyContainerNode pcn = (NFPropertyContainerNode)model.getNode(NFPropertyContainerNode.getId(getId(), name));
					if(pcn==null)
						pcn = new NFPropertyContainerNode(name, mi.toString(), RequiredServiceNode.this, (AsyncSwingTreeModel)model, tree, ea, null, mi, info);
					
					childs.add(pcn);
				}
			}
			
			Collections.sort(childs, new java.util.Comparator<ISwingTreeNode>()
			{
				public int compare(ISwingTreeNode t1, ISwingTreeNode t2)
				{
					String si1 = t1.toString();
					String si2 = t2.toString();
					return si1.compareTo(si2);
				}
			});
			
			if(cn!=null)
				childs.add(0, cn);
			
			setChildren(childs);
		}
			
//			ea.scheduleStep(new IComponentStep<ReqInfo>()
//			{
//				public IFuture<ReqInfo> execute(IInternalAccess ia)
//				{
//					final Future<ReqInfo> ret = new Future<ReqInfo>();
//					final IService res = ia.getServiceContainer().getLastRequiredService(fname);
//					if(res!=null && ia.getServiceContainer().hasRequiredServicePropertyProvider(res.getId()))
//					{
//						final IServiceIdentifier sid = res.getId();
//						final INFMixedPropertyProvider pp = ia.getServiceContainer().getRequiredServicePropertyProvider(res.getId());
//						pp.getNFPropertyMetaInfos().addResultListener(new ExceptionDelegationResultListener<Map<String,INFPropertyMetaInfo>, ReqInfo>(ret)
//						{
//							public void customResultAvailable(final Map<String,INFPropertyMetaInfo> result1)
//							{
//								pp.getMethodNFPropertyMetaInfos().addResultListener(new ExceptionDelegationResultListener<Map<MethodInfo,Map<String,INFPropertyMetaInfo>>, ReqInfo>(ret)
//								{
//									public void customResultAvailable(Map<MethodInfo, Map<String, INFPropertyMetaInfo>> result2)
//									{
//										ret.setResult(new ReqInfo(sid, result1, result2));
//									}
//								});
//							}
//						});
//					}
//					else
//					{
//						ret.setResult(null);
//					}
//					
//					return ret;
//				}
//			}).addResultListener(new IResultListener<ReqInfo>()
//			{
//				public void resultAvailable(ReqInfo reqinfo)
//				{
//					if(reqinfo!=null)
//					{
//						NFPropertyContainerNode cn = null;
//						if(reqinfo.getServiceProperties()!=null && reqinfo.getServiceProperties().size()>0=
//						{
//							String name = "Service properties";
//							cn = (NFPropertyContainerNode)model.getNode(NFPropertyContainerNode.getId(getId(), name));
//							if(cn==null)
//								cn = new NFPropertyContainerNode(null, name, ProvidedServiceInfoNode.this, (AsyncSwingTreeModel)model, tree, ea, sid, null);
//						}
//
//					}
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("ex: "+getId());
//				}
//			});
//		}
//		else
//		{
//			ea.scheduleStep(new IComponentStep<Collection<IService>>()
//			{
//				public IFuture<Collection<IService>> execute(IInternalAccess ia)
//				{
//					Collection<IService> res = ia.getServiceContainer().getLastRequiredServices(fname);
//					return new Future<Collection<IService>>(res);
//				}
//			}).addResultListener(new IResultListener<Collection<IService>>()
//			{
//				public void resultAvailable(Collection<IService> result)
//				{
//					
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("ex: "+getId());
//				}
//			});
//		}
	}

	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		if(propcomp==null)
		{
			propcomp	= new RequiredServiceProperties();
		}
		propcomp.setService(info);
		return propcomp;
	}
	
	/**
	 * 
	 */
	protected static class ReqInfo
	{
		public IServiceIdentifier sid;
		
		public Map<String,INFPropertyMetaInfo> serviceprops;
		
		public Map<MethodInfo, Map<String, INFPropertyMetaInfo>> methodprops;

		/**
		 *  Create a new ReqInfo. 
		 */
		public ReqInfo(IServiceIdentifier sid, Map<String, INFPropertyMetaInfo> serviceprops, 
			Map<MethodInfo, Map<String, INFPropertyMetaInfo>> methodprops)
		{
			this.sid = sid;
			this.serviceprops = serviceprops;
			this.methodprops = methodprops;
		}

		/**
		 *  Get the sid.
		 *  @return The sid.
		 */
		public IServiceIdentifier getServiceIdentifier()
		{
			return sid;
		}

		/**
		 *  Set the sid.
		 *  @param sid The sid to set.
		 */
		public void setServiceIdentifier(IServiceIdentifier sid)
		{
			this.sid = sid;
		}

		/**
		 *  Get the serviceProperties.
		 *  @return The serviceProperties.
		 */
		public Map<String, INFPropertyMetaInfo> getServiceProperties()
		{
			return serviceprops;
		}

		/**
		 *  Set the serviceProperties.
		 *  @param serviceprops The serviceProperties to set.
		 */
		public void setServiceProperties(Map<String, INFPropertyMetaInfo> serviceprops)
		{
			this.serviceprops = serviceprops;
		}

		/**
		 *  Get the methodProperties.
		 *  @return The methodProperties.
		 */
		public Map<MethodInfo, Map<String, INFPropertyMetaInfo>> getMethodProperties()
		{
			return methodprops;
		}

		/**
		 *  Set the methodProperties.
		 *  @param methodprops The methodProperties to set.
		 */
		public void setMethodProperties(Map<MethodInfo, Map<String, INFPropertyMetaInfo>> methodprops)
		{
			this.methodprops = methodprops;
		}
	}
}
