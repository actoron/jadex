package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

/**
 * Interface for the factory creating GPMN models.
 *
 */
public interface IGpmnModelFactory
{
	/**
	 *  Creates a new GPMN model.
	 *  @return GPMN model.
	 */
	public IGpmnModel createModel();
}
