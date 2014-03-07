package jadex.bpmn.editor.model.visual;

import jadex.bpmn.model.MSubProcess;

import com.mxgraph.view.mxGraph;

public class VSubProcess extends VActivity
{
	public VSubProcess(mxGraph graph)
	{
		super(graph);
	}
	
	/**
	 *  Get the style.
	 */
	public String getStyle()
	{
		String ret = super.getStyle();
		if (MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)getBpmnElement()).getSubprocessType()))
		{
			ret += "_Event";
		}
		
		return ret;
	}
}
