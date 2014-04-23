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
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK);
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS);
		addSelectionEntry(VExternalSubProcess.class.getSimpleName());
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE);
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL);
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE);
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START");
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE");
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY");
		addSelectionEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END");
	}
	
	/**
	 *  Add a selection entry for an element.
	 */
	protected void addSelectionEntry(String name)
	{
		Map<String, Object> vals = styles.get(name);
		Map<String, Object> cvals = new HashMap<String, Object>(vals);
		cvals.put(mxConstants.STYLE_FILLCOLOR, "#FF0000");
		styles.put(name+"_sel", cvals);
	}
}
