package jadex.tools.componentviewer.executionservice;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.service.IService;
import jadex.commons.service.execution.IExecutionService;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.componentviewer.IServiceViewerPanel;

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
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture init(IControlCenter jcc, IService service)
	{
		this.exe = (IExecutionService)service;
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
		JList	list	= new JList(exe.getTasks());
		return new JScrollPane(list);
	}

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
}
