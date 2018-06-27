package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.model.MParameter;

public class VOutParameter extends VNamedNode
{
	/** The parameter. */
	protected MParameter parameter;
	
	public VOutParameter(mxGraph graph, MParameter param)
	{
		super(graph, VOutParameter.class.getSimpleName());
		parameter = param;
		getGeometry().setWidth(BpmnStylesheetColor.PARAMETER_PORT_SIZE);
		getGeometry().setHeight(BpmnStylesheetColor.PARAMETER_PORT_SIZE);
	}
	
	public String getStyle()
	{
		return super.getStyle();
	}
	
	public Object getValue()
	{
		return parameter.getName();
	}
	
	public void setValue(Object value)
	{
	}
	
	public MParameter getParameter()
	{
		return parameter;
	}
}
