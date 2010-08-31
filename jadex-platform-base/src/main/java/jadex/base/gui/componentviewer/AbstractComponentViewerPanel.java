package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;

import javax.swing.JComponent;

/**
 *  Simple default viewer panel.
 */
public abstract class AbstractComponentViewerPanel implements IComponentViewerPanel
{
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IExternalAccess component;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = component;
		return new Future(null);
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		return new Future(null);
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return null;
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public abstract JComponent getComponent();

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}

	
	/**
	 *  Get the jcc.
	 *  @return the jcc.
	 */
	public IControlCenter getJCC()
	{
		return jcc;
	}
	
	/**
	 *  Get the component.
	 */
	public IExternalAccess getActiveComponent()
	{
		return component;
	}
}
