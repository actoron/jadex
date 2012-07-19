package jadex.tools.generic;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Plugin that allows to look at viewable components.
 */
public abstract class AbstractComponentPlugin extends AbstractGenericPlugin<IComponentIdentifier>
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
	public abstract IFuture<IAbstractViewerPanel> createComponentPanel(IExternalAccess component);
	
	/**
	 *  Create the selector panel.
	 */
	public AbstractSelectorPanel<IComponentIdentifier> createSelectorPanel()
	{
		return new AbstractComponentSelectorPanel(getJCC().getJCCAccess(), getJCC().getPlatformAccess(), getModelName())
		{
			public IFuture<IAbstractViewerPanel> createComponentPanel(IExternalAccess component)
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
		return getModelName()+ " Viewer";
	}
}
