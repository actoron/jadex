package jadex.bpmn.editor.gui.stylesheets;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;
import jadex.commons.gui.SGUI;

import java.awt.Color;

import com.mxgraph.util.mxConstants;

/**
 *  Simple grayscale style.
 *
 */
public class BpmnStylesheetCampaign extends BpmnStylesheetColor
{
	/** Style sheet name. */
	public static String NAME = "Color Gradient";
	
	public BpmnStylesheetCampaign()
	{
		styles.get(VPool.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#f7f7f7");
		styles.get(VLane.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#f7f7f7");
		styles.get(VActivity.class.getSimpleName() + "_" + MTask.TASK).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VOutParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		styles.get(VInParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		styles.get(VInParameter.class.getSimpleName()+ "_Connected").put(mxConstants.STYLE_FILLCOLOR, "#fff49c");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Event").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Event").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Event").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS).put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS).put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Unfolded").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Unfolded").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Unfolded").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VExternalSubProcess.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		
//		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
//		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
//		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
//		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_FILLCOLOR, "#fede6d");
		
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_FILLCOLOR, "#9ac7e4");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_FILLCOLOR, "#9ac7e4");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_FILLCOLOR, "#9ac7e4");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_FILLCOLOR, "#9ac7e4");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
		
//		for(String key : styles.keySet())
//		{
//			String lkey = key.toLowerCase();
//			if(lkey.contains("activity") || lkey.contains("task") || lkey.contains("subprocess"))
//			{
//				styles.get(key).put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
//				styles.get(key).put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
//			}
//		}
	}
}
