package jadex.bpmn.tools.ui;

import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColorGradient;
import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.model.MBpmnModel;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.util.mxConstants;

/**
 * 
 */
public class BpmnStylesheetSelections extends BpmnStylesheetColorGradient
{
	/** Style sheet name. */
	public static String NAME = "Complex Grayscale";
	
	/**
	 *  Add selection elements colors.
	 */
	public BpmnStylesheetSelections()
	{
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS);
		addWaitingEntry(VExternalSubProcess.class.getSimpleName());
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END");
		
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS);
		addReadyEntry(VExternalSubProcess.class.getSimpleName());
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START");
		addReadyEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE");
		addReadyEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY");
		addReadyEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END");
	}
	
	/**
	 *  Add a selection entry for an element.
	 */
	protected void addWaitingEntry(String name)
	{
		Map<String, Object> vals = styles.get(name);
		Map<String, Object> cvals = new HashMap<String, Object>(vals);
		cvals.put(mxConstants.STYLE_FILLCOLOR, "#FF0000");
		styles.put(name+"_waiting", cvals);
	}
	
	/**
	 *  Add a selection entry for an element.
	 */
	protected void addReadyEntry(String name)
	{
		Map<String, Object> vals = styles.get(name);
		Map<String, Object> cvals = new HashMap<String, Object>(vals);
		cvals.put(mxConstants.STYLE_FILLCOLOR, "#00FF00");
		styles.put(name+"_ready", cvals);
	}
}
