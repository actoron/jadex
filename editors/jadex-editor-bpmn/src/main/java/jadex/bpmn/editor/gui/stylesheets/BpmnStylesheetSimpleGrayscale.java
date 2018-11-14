package jadex.bpmn.editor.gui.stylesheets;

import com.mxgraph.util.mxConstants;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;

/**
 *  Simple grayscale style.
 *
 */
public class BpmnStylesheetSimpleGrayscale extends BpmnStylesheetColor
{
	/** Style sheet name. */
	public static String NAME = "Simple Grayscale";
	
	public BpmnStylesheetSimpleGrayscale()
	{
		String white = "#ffffff";
		
		styles.get(VPool.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VLane.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MTask.TASK).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VOutParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VInParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VInParameter.class.getSimpleName()+ "_Connected").put(mxConstants.STYLE_FILLCOLOR, white);
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Event").put(mxConstants.STYLE_FILLCOLOR, white);
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
