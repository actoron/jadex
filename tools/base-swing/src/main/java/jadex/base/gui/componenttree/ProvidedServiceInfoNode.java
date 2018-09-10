package jadex.base.gui.componenttree;

import java.util.ArrayList;
import java.util.Collections;
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
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Node object representing a service.
 */
public class ProvidedServiceInfoNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/provided_16.png"),
		"overlay_system", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/overlay_system.png")
	});
	
	//-------- attributes --------
	
	/** The service. */
	private final ProvidedServiceInfo	service;
	
	/** The service id. */
	protected IServiceIdentifier sid;

	/** The properties component (if any). */
	protected ProvidedServiceInfoProperties	propcomp;
	
	/** The external access. */
	protected IExternalAccess ea;
	
	/** The platform external access. */
	protected IExternalAccess platformea;

	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProvidedServiceInfoNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, 
		ProvidedServiceInfo service, IServiceIdentifier sid, IExternalAccess ea, IExternalAccess platformea)
	{
		super(parent, model, tree);
		this.service	= service;
		this.sid = sid;
		this.ea = ea;
		this.platformea = platformea;
//		if(service==null || service.getType().getTypeName()==null)
//			System.out.println("service node: "+this);
//		System.out.println("ea: "+ea.getComponentIdentifier());
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service.
	 */
	public ProvidedServiceInfo	getServiceInfo()
	{
		return service;
	}
	
	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
//		return sid;
		return getId(getParent(), service);
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
		Icon icon = icons.getIcon("service");
		if(service.isSystemService())
			icon = icon!=null ? new CombiIcon(new Icon[]{icon, icons.getIcon("overlay_system")}) : icons.getIcon("overlay_system");
		return icon;
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		IFuture<IService> fut = ea.searchService( new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sid));
		fut.addResultListener(new IResultListener<IService>()
		{
			public void resultAvailable(final IService ser)
			{
//				((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getNFPropertyMetaInfos()
				ea.getNFPropertyMetaInfos(ser.getId())
					.addResultListener(new SwingResultListener<Map<String,INFPropertyMetaInfo>>(new IResultListener<Map<String,INFPropertyMetaInfo>>()
//					.addResultListener(new SwingResultListener<Map<String,INFPropertyMetaInfo>>(new IResultListener<Map<String,INFPropertyMetaInfo>>()
				{
					public void resultAvailable(Map<String,INFPropertyMetaInfo> result)
					{
						NFPropertyContainerNode cn = null;
						if(result!=null && result.size()>0)
						{
							String name = "Service properties";
							cn = (NFPropertyContainerNode)model.getNode(NFPropertyContainerNode.getId(getId(), name));
							if(cn==null)
								cn = new NFPropertyContainerNode(null, name, ProvidedServiceInfoNode.this, (AsyncSwingTreeModel)model, tree, ea, sid, null, null);
						}
						
						final NFPropertyContainerNode sercon = cn;
						
						ea.getMethodNFPropertyMetaInfos(ser.getId())
//						((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).getMethodNFPropertyMetaInfos()
//						ser.getMethodNFPropertyMetaInfos()
							.addResultListener(new SwingResultListener<Map<MethodInfo,Map<String,INFPropertyMetaInfo>>>(new IResultListener<Map<MethodInfo,Map<String,INFPropertyMetaInfo>>>()
						{
							public void resultAvailable(Map<MethodInfo,Map<String,INFPropertyMetaInfo>> result)
							{
								List<NFPropertyContainerNode> childs = new ArrayList<NFPropertyContainerNode>();
								if(result!=null && result.size()>0)
								{
									Set<String> doublenames = new HashSet<String>();
									Set<String> tmp = new HashSet<String>();
									for(MethodInfo mi: result.keySet())
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
									
									for(MethodInfo mi: result.keySet())
									{
										String name = doublenames.contains(mi.getName())? mi.getNameWithParameters(): mi.getName();
										NFPropertyContainerNode cn = (NFPropertyContainerNode)model.getNode(NFPropertyContainerNode.getId(getId(), name));
										if(cn==null)
											cn = new NFPropertyContainerNode(name, mi.toString(), ProvidedServiceInfoNode.this, (AsyncSwingTreeModel)model, tree, ea, sid, mi, null);
										
										childs.add(cn);
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
								
								if(sercon!=null)
									childs.add(0, sercon);
								
								setChildren(childs);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						System.out.println("ex on: "+getId());
//						exception.printStackTrace();
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				System.out.println("ex on: "+getId());
//				exception.printStackTrace();
			}
		});
	}
	
	/**
	 * 
	 */
	protected IFuture<Class<?>> getServiceType()
	{
		final Future<Class<?>> ret = new Future<Class<?>>();
		
		if(service.getType().getType0()==null)
		{
			platformea.searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
				.addResultListener(new SwingDefaultResultListener<ILibraryService>()
			{
				public void customResultAvailable(ILibraryService ls)
				{
					ls.getClassLoader(sid.getResourceIdentifier())
						.addResultListener(new SwingDefaultResultListener<ClassLoader>()
					{
						public void customResultAvailable(ClassLoader cl)
						{
							Class<?> type = service.getType().getType(cl);
	//						System.out.println("Found: "+service.getType().getTypeName()+" "+cl+" "+type);
							ret.setResult(type);
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(service.getType().getType0());
		}
		
		return ret;
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return SReflect.getUnqualifiedTypeName(service.getType().getTypeName());
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(service.getName());
		buf.append(" :").append(service.getType().getTypeName()); 
		return buf.toString();
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return true;
	}

	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		if(propcomp==null)
		{
			propcomp	= new ProvidedServiceInfoProperties();
		}
		propcomp.setService(service, sid, platformea);
		
		return propcomp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(ISwingTreeNode parent, ProvidedServiceInfo service)
	{
		IComponentIdentifier	provider	= (IComponentIdentifier)parent.getParent().getId();
		return ""+provider+":service:"+service.getName();
	}
}
