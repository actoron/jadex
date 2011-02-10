package jadex.tools.generic;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Plugin that allows to look at viewable components.
 */
public abstract class AbstractComponentPlugin extends AbstractGenericPlugin
{	
	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public abstract String getModelName();
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createComponentPanel(IExternalAccess component);
	
	/**
	 *  Create the selector panel.
	 */
	public AbstractSelectorPanel createSelectorPanel()
	{
		return new AbstractComponentSelectorPanel(getJCC().getExternalAccess(), getModelName())
		{
			public IFuture createComponentPanel(IExternalAccess component)
			{
				return AbstractComponentPlugin.this.createComponentPanel(component);
			}
		};
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return getModelName();
	}
}
