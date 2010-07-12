package jadex.tools.common;

import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.commons.SGUI;
import jadex.commons.ThreadSuspendable;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;

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
	protected IServiceProvider provider;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree table node type.
	 *  @param container	The service container.
	 */
	public ComponentTreeTableNodeType(IServiceProvider provider)
	{
		super(ComponentTreeTable.NODE_COMPONENT, new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"});
		this.provider	= provider;
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
			IComponentFactory fac = (IComponentFactory)SServiceProvider.getService(provider, new ComponentFactorySelector(type)).get(new ThreadSuspendable());
			ret	= fac!=null ? fac.getComponentTypeIcon(type) : null;
		}
		
		if(ret==null)
			ret	= icons.getIcon("component");
		
		return ret;
	}
}
