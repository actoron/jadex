package jadex.base.gui.componentviewer;

import jadex.commons.IFuture;
import jadex.commons.Properties;

import javax.swing.JComponent;

/**
 * 
 */
public interface IAbstractViewerPanel
{
	/** The property for the viewer panel class. */
	public static final String	PROPERTY_VIEWERCLASS	= "componentviewer.viewerclass";

	
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
	public void setProperties(Properties ps);

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties();
}
