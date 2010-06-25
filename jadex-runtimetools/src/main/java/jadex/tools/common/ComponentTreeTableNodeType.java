package jadex.tools.common;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.commons.SGUI;
import jadex.commons.ThreadSuspendable;
import jadex.service.IServiceContainer;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  A custom tree table node type for representing
 *  component descriptions.
 */
public class ComponentTreeTableNodeType extends TreeTableNodeType
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"component",	SGUI.makeIcon(ComponentTreeTableNodeType.class, "/jadex/tools/common/images/new_agent.png"),
	});

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
			Collection coll = (Collection)container.getServices(IComponentFactory.class).get(new ThreadSuspendable());
			Iterator factories	= coll.iterator();
			while(ret==null && factories.hasNext())
				ret	= ((IComponentFactory)factories.next()).getComponentTypeIcon(type);
		}
		
		if(ret==null)
			ret	= icons.getIcon("component");
		
		return ret;
	}
}
