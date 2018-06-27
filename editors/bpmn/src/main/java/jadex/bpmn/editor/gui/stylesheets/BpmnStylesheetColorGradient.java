package jadex.bpmn.editor.gui.stylesheets;

import com.mxgraph.util.mxConstants;

/**
 *  Simple grayscale style.
 *
 */
public class BpmnStylesheetColorGradient extends BpmnStylesheetColor
{
	/** Style sheet name. */
	public static String NAME = "Color Gradient";
	
	public BpmnStylesheetColorGradient()
	{
		for(String key : styles.keySet())
		{
			String lkey = key.toLowerCase();
			if(lkey.contains("activity") || lkey.contains("task") || lkey.contains("subprocess"))
			{
				styles.get(key).put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
				styles.get(key).put(mxConstants.STYLE_GRADIENT_DIRECTION, "northwest");
			}
		}
	}
}
