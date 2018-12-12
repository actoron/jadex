package jadex.bpmn.model.task;

import javax.swing.JComponent;

import jadex.bpmn.model.IModelContainer;
import jadex.bpmn.model.MActivity;

/**
 * 
 */
public interface ITaskPropertyGui
{
	/**
	 *  Once called to init the component.
	 */
//	public void init(IModelInfo model, MActivity task, ClassLoader cl);
	public void init(IModelContainer container, MActivity task, ClassLoader cl);
	
	/**
	 *  Informs the panel that it should stop all its computation.
	 */
	public void shutdown();
	
	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent();
}
