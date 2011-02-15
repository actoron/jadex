package jadex.base.gui.componentviewer.executionservice;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;
import jadex.commons.service.execution.IExecutionService;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *  Show details about a execution service.
 */
public class ExecutionServiceViewer	implements IServiceViewerPanel
{
	//-------- attributes --------
	
	/** The execution service. */
	protected IExecutionService	exe;
	
	/** The panel. */
	protected JScrollPane panel;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture init(IControlCenter jcc, IService service)
	{
		this.exe = (IExecutionService)service;
		JList	list = new JList(exe.getTasks());
		panel = new JScrollPane(list);
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
		return "executionserviceviewer";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture setProperties(Properties ps)
	{
		return new Future(null);
	}
	
	/**
	 *  Reset the properties.
	 */
	public IFuture resetProperties()
	{
		return new Future(null);
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture getProperties()
	{
		return new Future(null);
	}
}
