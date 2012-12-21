package jadex.bpmn.editor.gui.stylesheets;

import com.mxgraph.util.mxConstants;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MBpmnModel;

/**
 *  Simple grayscale style.
 *
 */
public class BpmnStylesheetSimpleGrayscale extends BpmnStylesheetColor
{
	public BpmnStylesheetSimpleGrayscale()
	{
		String white = "#ffffff";
		
		styles.get(VPool.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VLane.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VExternalSubProcess.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_FILLCOLOR, white);
	}
}
