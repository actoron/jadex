package jadex.tools.generic;

import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;

import javax.swing.Icon;

/**
 *  Abstract plugin for wrapping service views to plugin view.
 */
public abstract class AbstractServicePlugin extends AbstractGenericPlugin
{
	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public abstract Class getServiceType();
	
	/**
	 *  Create the component/service panel.
	 */
	public abstract IFuture createServicePanel(IService service);
	
	/**
	 *  Get the tool icon.
	 */
	public abstract Icon getToolIcon(boolean selected);
	
	/**
	 *  Create the selector panel.
	 */
	public AbstractSelectorPanel createSelectorPanel()
	{
		return new AbstractServiceSelectorPanel(getJCC().getPlatformAccess(), getServiceType())
		{
			public IFuture createServicePanel(IService service)
			{
				return AbstractServicePlugin.this.createServicePanel(service);
			}
		};
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		// dots not allowed in property names!
		return SReflect.getInnerClassName(getServiceType());
	}
}
