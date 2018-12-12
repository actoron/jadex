package jadex.tools.generic;

import javax.swing.Icon;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;

/**
 *  Abstract plugin for wrapping service views to plugin view.
 */
public abstract class AbstractServicePlugin extends AbstractGenericPlugin<IService>
{
	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public abstract Class<?> getServiceType();
	
	/**
	 *  Create the component/service panel.
	 */
	public abstract IFuture<IAbstractViewerPanel> createServicePanel(IService service);
	
	/**
	 *  Get the tool icon.
	 */
	public abstract Icon getToolIcon(boolean selected);
	
	/**
	 *  Create the selector panel.
	 */
	public AbstractSelectorPanel<IService> createSelectorPanel()
	{
		return new AbstractServiceSelectorPanel(getJCC().getPlatformAccess(), getServiceType())
		{
			public IFuture<IAbstractViewerPanel> createPanel(IService service)
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
