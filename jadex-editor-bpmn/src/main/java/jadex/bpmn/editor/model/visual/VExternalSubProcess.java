package jadex.bpmn.editor.model.visual;

import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MSubProcess;

import com.mxgraph.view.mxGraph;

public class VExternalSubProcess extends VActivity
{
	public VExternalSubProcess(mxGraph graph)
	{
		super(graph);
		setCollapsed(true);
	}
	
	/**
	 *  Gets the style.
	 */
	public String getStyle()
	{
		return VExternalSubProcess.class.getSimpleName();
	}
	
	/**
	 *  Gets the value.
	 *  
	 *  @return The value.
	 */
	public Object getValue()
	{
		if (getBpmnElement() != null)
		{
			if (isCollapsed())
			{
				return ((MSubProcess) getBpmnElement()).getName();
			}
			else
			{
				String ret = (String) ((MSubProcess) getBpmnElement()).getPropertyValue("file");
				return ret != null? ret : "";
			}
		}
		
		return super.getValue();
	}
	
	/**
	 *  Sets the value.
	 *  
	 *  @param value The value.
	 */
	public void setValue(Object value)
	{
		if (getBpmnElement() != null)
		{
			if (isCollapsed())
			{
				((MSubProcess) getBpmnElement()).setName((String) value);
			}
			else
			{
				((MSubProcess) getBpmnElement()).setPropertyValue("file", value);
			}
		}
	}
	
	/**
	 *  Sets the BPMN element.
	 *  
	 *  @param bpmnelement The BPMN element.
	 */
	public void setBpmnElement(MIdElement bpmnelement)
	{
		this.bpmnelement = bpmnelement;
	}
}
