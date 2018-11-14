package jadex.gpmn.editor.gui.stylesheets;

import java.util.Map;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 *  Style sheet defining the visual aspects of the GPMN model.
 *
 */
public class GpmnStylesheetGrayscale extends mxStylesheet
{
	/**
	 *  Creates the style sheet.
	 */
	public GpmnStylesheetGrayscale()
	{
		Map<String, Map<String, Object>> defaultstyles = (new GpmnStylesheetColor()).getStyles();
		
		// Goal Styles
		Map<String, Object> style = (Map<String, Object>) defaultstyles.get(ModelConstants.ACHIEVE_GOAL_TYPE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dadada");
		
		style = (Map<String, Object>) defaultstyles.get(ModelConstants.PERFORM_GOAL_TYPE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dfdfdf");
		putCellStyle(ModelConstants.PERFORM_GOAL_TYPE, style);
		
		style = (Map<String, Object>) defaultstyles.get(ModelConstants.MAINTAIN_GOAL_TYPE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dadada");
		
		style = (Map<String, Object>) defaultstyles.get(ModelConstants.QUERY_GOAL_TYPE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dadada");
		
		// Plan Styles
		style = (Map<String, Object>) defaultstyles.get(GuiConstants.REF_PLAN_STYLE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dadada");
		
		style = (Map<String, Object>) defaultstyles.get(GuiConstants.ACTIVATION_PLAN_STYLE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#dadada");
		
		for (Map.Entry<String, Map<String, Object>> entry : defaultstyles.entrySet())
		{
			putCellStyle(entry.getKey(), entry.getValue());
		}
	}
}
