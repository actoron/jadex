package jadex.tools.generic;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComboBox;

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
		return new AbstractServiceSelectorPanel(getJCC().getExternalAccess(), getServiceType())
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
