package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.impl.GpmnModel;

/**
 *  Factory for the GPMN intermediate model.
 *
 */
public class GpmnIntermediateModelFactory implements IGpmnModelFactory
{
	/**
	 *  Creates a new GPMN model.
	 *  @return GPMN model.
	 */
	public IGpmnModel createModel()
	{
		return new GpmnModel();
	}
}
