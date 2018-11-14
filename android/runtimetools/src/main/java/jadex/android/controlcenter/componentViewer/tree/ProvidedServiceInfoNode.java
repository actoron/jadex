package jadex.android.controlcenter.componentViewer.tree;

import jadex.android.controlcenter.componentViewer.properties.PropertyItem;
import jadex.android.controlcenter.componentViewer.properties.ServicePropertyActivity;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.commons.SReflect;

import java.util.ArrayList;

/**
 *  Node object representing a service.
 */
public class ProvidedServiceInfoNode	extends AbstractTreeNode implements IAndroidTreeNode
{
	//-------- constants --------
	
	//-------- attributes --------
	
	/** The service. */
	private final ProvidedServiceInfo	service;
	
	/** The service id. */
	protected IServiceIdentifier sid;

	/** The external access. */
	protected IExternalAccess ea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProvidedServiceInfoNode(ITreeNode parent, AsyncTreeModel model, 
		ProvidedServiceInfo service, IServiceIdentifier sid, IExternalAccess ea)
	{
		super(parent, model);
		this.service	= service;
		this.sid = sid;
		this.ea = ea;
//		if(service==null || service.getType().getTypeName()==null)
//			System.out.println("service node: "+this);
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
		return getId(parent, service);
	}

	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[]	getIcon()
	{
		return null;
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

	//-------- helper methods --------
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(ITreeNode parent, ProvidedServiceInfo service)
	{
		IComponentIdentifier	provider	= (IComponentIdentifier)parent.getParent().getId();
		return ""+provider+":service:"+service.getName();
	}

	@Override
	public Class getPropertiesActivityClass()
	{
		return ServicePropertyActivity.class;
	}

	@Override
	public PropertyItem[] getProperties()
	{
		ArrayList<PropertyItem> props = new ArrayList<PropertyItem>();
		props.add(new PropertyItem("Name", service.getName()));
		props.add(new PropertyItem("Type", service.getType().getTypeName()));
		
//		if(service.getType().getType(null)==null)
//		{
//			ea.getServiceProvider().searchService( new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM))
//				.addResultListener(new SwingDefaultResultListener<ILibraryService>()
//			{
//				public void customResultAvailable(ILibraryService ls)
//				{
//					ls.getClassLoader(sid.getResourceIdentifier())
//						.addResultListener(new SwingDefaultResultListener<ClassLoader>()
//					{
//						public void customResultAvailable(ClassLoader cl)
//						{
//							Class type = service.getType().getType(cl);
//							internalSetService(type);
//						}
//					});
//				}
//			});
//		}
//		else
//		{
//			internalSetService(service.getType().getType(null));
//		}
//		
//		props.add(new PropertyItem("Methods", service.getName()));
		
		return props.toArray(new PropertyItem[props.size()]);
	}

}
