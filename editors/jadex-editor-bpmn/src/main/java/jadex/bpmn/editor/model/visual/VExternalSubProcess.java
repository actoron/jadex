package jadex.bpmn.editor.model.visual;

import com.mxgraph.view.mxGraph;

import jadex.bpmn.model.MSubProcess;

/**
 *  Visual representation of an external subprocess.
 */
public class VExternalSubProcess extends VActivity
{
	/**
	 *  Creates the subprocess.
	 *  @param graph The graph.
	 */
	public VExternalSubProcess(mxGraph graph)
	{
		super(graph);
//		setCollapsed(true);
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
			return ((MSubProcess) getBpmnElement()).getName();
//			if (isCollapsed())
//			{
//				return ((MSubProcess) getBpmnElement()).getName();
//			}
//			else
//			{
//				MSubProcess msp = (MSubProcess) getBpmnElement();
//				String ret;
//				if(msp.hasPropertyValue("file"))
//				{
//					UnparsedExpression mp = (UnparsedExpression)msp.getPropertyValue("file");
//					ret = mp.getValue();
//				}
//				else
//				{
//					ret = msp.getPropertyValue("filename").getValue();
//					ret = ret.substring(1, ret.length() - 2);
//				}
//				return ret != null? ret : "";
//			}
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
			((MSubProcess) getBpmnElement()).setName((String) value);
//			if (isCollapsed())
//			{
//				((MSubProcess) getBpmnElement()).setName((String) value);
//			}
//			else
//			{
//				MSubProcess msp = (MSubProcess) getBpmnElement();
//				if (msp.hasPropertyValue("file"))
//				{
//					UnparsedExpression exp = new UnparsedExpression("file", String.class, (String) value, null);
//					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
//					msp.addProperty(mprop);
//				}
//				else
//				{
//					UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"" + value + "\"", null);
//					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
//					msp.addProperty(mprop);
//				}
//			}
		}
	}
}
