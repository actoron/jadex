package jadex.bpmn.editor.model.visual;

import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.modelinfo.UnparsedExpression;

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
				MSubProcess msp = (MSubProcess) getBpmnElement();
				String ret;
				if (msp.hasParameter("file"))
				{
					UnparsedExpression fileexp = (UnparsedExpression) msp.getParameters().get("file");
					ret = fileexp.getValue();
				}
				else
				{
					ret = (String) msp.getParameters().get("filename");
				}
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
				MSubProcess msp = (MSubProcess) getBpmnElement();
				if (msp.hasParameter("file"))
				{
					UnparsedExpression exp = new UnparsedExpression("file", String.class, (String) value, null);
					msp.setPropertyValue("file", exp);
				}
				else
				{
					msp.setPropertyValue("filename", value);
				}
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
