package jadex.tools.common;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.service.IServiceContainer;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;

import java.util.Iterator;

import javax.swing.Icon;

/**
 *  A custom tree table node type for representing
 *  component descriptions.
 */
public class ComponentTreeTableNodeType extends TreeTableNodeType
{
	//-------- attributes --------
	
	/** The service container. */
	protected IServiceContainer	container;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree table node type.
	 *  @param container	The service container.
	 */
	public ComponentTreeTableNodeType(IServiceContainer container)
	{
		super(ComponentTreeTable.NODE_COMPONENT, new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"});
		this.container	= container;
	}

	//-------- overridings --------
	
	/**
	 *  Get the icon.
	 *  @return The icon for the node.
	 */
	public Icon selectIcon(Object value)
	{
		Icon	ret	= null;
		IComponentDescription ad = (IComponentDescription)((DefaultTreeTableNode)value).getUserObject();
		String type	= ad.getType();
		if(type!=null)
		{
			Iterator	factories	= container.getServices(IComponentFactory.class).iterator();
			while(ret==null && factories.hasNext())
				ret	= ((IComponentFactory)factories.next()).getComponentTypeIcon(type);
		}
		
		return ret;
	}
}
