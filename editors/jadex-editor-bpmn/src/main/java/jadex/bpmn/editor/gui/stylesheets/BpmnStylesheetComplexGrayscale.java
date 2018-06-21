package jadex.bpmn.editor.gui.stylesheets;

import java.awt.Color;

import com.mxgraph.util.mxConstants;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MTask;
import jadex.commons.gui.SGUI;

/**
 *  Complex grayscale style.
 */
public class BpmnStylesheetComplexGrayscale extends BpmnStylesheetColor
{
	/** Style sheet name. */
	public static String NAME = "Complex Grayscale";
	
	/**
	 *  Modify colors.
	 */
	public BpmnStylesheetComplexGrayscale()
	{
		styles.get(VPool.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#F0F0F0");
		styles.get(VLane.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#F3F3F3");
		styles.get(VActivity.class.getSimpleName() + "_" + MTask.TASK).put(mxConstants.STYLE_FILLCOLOR, "#AAAAAA");
		styles.get(VOutParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		styles.get(VInParameter.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		styles.get(VInParameter.class.getSimpleName()+ "_Connected").put(mxConstants.STYLE_FILLCOLOR, "#CCCCCC");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS + "_Event").put(mxConstants.STYLE_FILLCOLOR, "#BBBBBB");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS).put(mxConstants.STYLE_FILLCOLOR, "#AAAAAA");
		styles.get(VExternalSubProcess.class.getSimpleName()).put(mxConstants.STYLE_FILLCOLOR, SGUI.colorToHTML(Color.LIGHT_GRAY));
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, SGUI.colorToHTML(Color.WHITE));
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL).put(mxConstants.STYLE_FILLCOLOR, SGUI.colorToHTML(Color.WHITE));
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE).put(mxConstants.STYLE_FILLCOLOR, SGUI.colorToHTML(Color.WHITE));
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START").put(mxConstants.STYLE_FILLCOLOR, "#F8F8F8");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE").put(mxConstants.STYLE_FILLCOLOR, "#DDDDDD");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_BOUNDARY").put(mxConstants.STYLE_FILLCOLOR, "#DDDDDD");
		styles.get(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END").put(mxConstants.STYLE_FILLCOLOR, "#555555");
	}
}
