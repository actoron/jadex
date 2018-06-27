package jadex.base.gui.componentviewer;

import javax.swing.JComponent;

import jadex.commons.IPropertiesProvider;
import jadex.commons.future.IFuture;

/**
 *  Abstract viewer panel for components and services.
 */
public interface IAbstractViewerPanel extends IPropertiesProvider
{
	//-------- constants ---------
	
	/** The property for the viewer panel class. */
	public static final String	PROPERTY_VIEWERCLASS	= "componentviewer.viewerclass";

	//-------- attributes --------
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown();

	/**
	 *  The id used for mapping properties.
	 */
	public String getId();

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent();
}
