/**
 * 
 */
package jadex.base.gui.componenttree;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
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
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

/**
 * Node for a non functional property.
 */
public class NFPropertyNode extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/nfprop.png"),
		"dynamic", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/overlay_dynamic.png")
	});
	
	//-------- attributes --------
	
	/** The property meta info. */
	protected INFPropertyMetaInfo propmi;
	
	/** The properties panel. */
	protected JComponent propcomp;
	
	
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
	 *  Create a new service container node.
	 */
	public NFPropertyNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, 
		INFPropertyMetaInfo propmi, IExternalAccess ea, IServiceIdentifier sid, MethodInfo mi, RequiredServiceInfo rinfo)
	{
		super(parent, model, tree);
		this.ea = ea;
		this.propmi = propmi;
		this.sid = sid;
		this.mi = mi;
		this.rinfo = rinfo;
		model.registerNode(this);
//		PropertyUpdateHandler puh = (PropertyUpdateHandler)tree.getClientProperty(PropertyUpdateHandler.class);
//		if(puh!=null)
//		{
//			puh.addPropertyCommand(ea.getComponentIdentifier(), propmi.getName(), new ICommand<IMonitoringEvent>()
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
	public Object	getId()
	{
//		return sid;
		return getId(getParent().getId(), propmi.getName());
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
		Icon ret = null;
		if(propmi.isDynamic())
		{
			ret = new CombiIcon(new Icon[]{icons.getIcon("service"), icons.getIcon("dynamic")});
		}
		else
		{
			ret = icons.getIcon("service");
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
		// no children
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return propmi.getName();
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(propmi.getName());
		buf.append(" :").append(propmi.getType()); 
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
			if(rinfo==null)
			{
				propcomp = new NFPropertyProperties();
				((NFPropertyProperties)propcomp).setProperty(propmi, ea, sid, mi);
			}
			else
			{
				propcomp = new NFRPropertyProperties();
				((NFRPropertyProperties)propcomp).setProperty(propmi, ea, mi, rinfo);
			}
		}
		
		return propcomp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the meta info.
	 */
	public INFPropertyMetaInfo getMetaInfo()
	{
		return propmi;
	}
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(Object parentid, String name)
	{
		return parentid+":nfproperty:"+name;
	}
	
	/**
	 *  Remove property from provider.
	 */
	protected IFuture<Void> removeProperty()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(ea!=null && propmi!=null)
		{
			if(sid!=null)
			{
				if(rinfo!=null)
				{
					final IServiceIdentifier fsid = sid;
					final MethodInfo fmi = mi;
					final String fname = propmi.getName();
					ea.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							final Future<Void> ret = new Future<Void>();
							INFMixedPropertyProvider pp = ia.getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(fsid);
							if(fmi==null)
							{
								pp.removeNFProperty(fname).addResultListener(new DelegationResultListener<Void>(ret));
							}
							else
							{
								pp.removeMethodNFProperty(fmi, fname).addResultListener(new DelegationResultListener<Void>(ret));
							}
							return ret;
						}
					});
				}
				else
				{
					IFuture<IService> fut = ea.searchService( new ServiceQuery<>( (Class<IService>)null).setServiceIdentifier(sid));
					fut.addResultListener(new SwingResultListener<IService>(new IResultListener<IService>()
					{
						public void resultAvailable(IService ser) 
						{
							if(mi!=null)
							{
//								((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).removeMethodNFProperty(mi, propmi.getName())
//									.addResultListener(new DelegationResultListener<Void>(ret));
								ea.removeMethodNFProperty(ser.getServiceId(), mi, propmi.getName())
									.addResultListener(new DelegationResultListener<Void>(ret));
							}
							else
							{
//								((INFMixedPropertyProvider)ser.getExternalComponentFeature(INFPropertyComponentFeature.class)).removeNFProperty(propmi.getName()).addResultListener(new DelegationResultListener<Void>(ret));
								ea.removeNFProperty(ser.getServiceId(), propmi.getName())
									.addResultListener(new DelegationResultListener<Void>(ret));
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					}));
				}
			}
			else
			{
//				((INFPropertyProvider)ea.getExternalComponentFeature(INFPropertyComponentFeature.class)).removeNFProperty(propmi.getName()).addResultListener(new DelegationResultListener<Void>(ret));
				ea.removeNFProperty(propmi.getName())
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		}
		else
		{
			ret.setException(new RuntimeException("Property not found."));
		}
		
		return ret;
	}

	/**
	 *  Get the meta info.
	 */
	public INFPropertyMetaInfo getPropertyMetaInfo()
	{
		return propmi;
	}
}
