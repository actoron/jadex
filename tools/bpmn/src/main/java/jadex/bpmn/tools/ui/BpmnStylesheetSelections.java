package jadex.bpmn.tools.ui;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.util.mxConstants;

import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColorGradient;
import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;

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
		Map<String, Object> style = new HashMap<String, Object>();
//		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_SHAPE, BreakpointMarker.class.getSimpleName());
		style.put(mxConstants.STYLE_FILLCOLOR, "#FF0000");
		style.put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		style.put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSIZE, 16);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		style.put(mxConstants.STYLE_SHADOW, Boolean.FALSE);
//		style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		putCellStyle(BreakpointMarker.class.getSimpleName(), style);
		
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MTask.TASK);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Unfolded");
		addWaitingEntry(VExternalSubProcess.class.getSimpleName());
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE);
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY");
		addWaitingEntry(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END");
		
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MTask.TASK);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS);
		addReadyEntry(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Unfolded");
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
