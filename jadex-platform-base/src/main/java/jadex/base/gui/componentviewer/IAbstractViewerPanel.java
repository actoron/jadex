package jadex.base.gui.componentviewer;

import jadex.commons.Properties;
import jadex.commons.future.IFuture;

import javax.swing.JComponent;

/**
 *  Abstract viewer panel for components and services.
 */
public interface IAbstractViewerPanel
{
	//-------- constants ---------
	
	/** The property for the viewer panel class. */
	public static final String	PROPERTY_VIEWERCLASS	= "componentviewer.viewerclass";

	//-------- attributes --------
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown();

	/**
	 *  The id used for mapping properties.
	 */
	public String getId();

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent();

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture setProperties(Properties ps);

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture getProperties();
}
