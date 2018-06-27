package jadex.base.gui.componentviewer;

import javax.swing.JComponent;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	public IFuture<Void> init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = component;
		return IFuture.DONE;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return toString();
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public abstract JComponent getComponent();

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
//		System.out.println("Warning: setProperties not implemented "+getClass());
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
//		System.out.println("Warning: getProperties not implemented "+getClass());
		return new Future<Properties>((Properties)null);
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
