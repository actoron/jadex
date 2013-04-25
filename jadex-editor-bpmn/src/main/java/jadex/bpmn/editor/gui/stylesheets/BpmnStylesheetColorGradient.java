package jadex.bpmn.editor.gui.stylesheets;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MBpmnModel;

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
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK).put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffff");
		styles.get(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK).put(mxConstants.STYLE_GRADIENT_DIRECTION, "radnorthwest");
	}
	
//	@Override
//	public Map<String, Object> getCellStyle(String name,
//			Map<String, Object> defaultStyle)
//	{
//		// TODO Auto-generated method stub
//		return new HashMap<String, Object>(super.getCellStyle(name, defaultStyle))
//		{
//			public Object get(Object arg0)
//			{
//				if (mxConstants.STYLE_GRADIENT_DIRECTION.equals(arg0))
//				{
//					System.out.println("grax");
//				}
//				return super.get(arg0);
//			};
//		};
//	}
}
