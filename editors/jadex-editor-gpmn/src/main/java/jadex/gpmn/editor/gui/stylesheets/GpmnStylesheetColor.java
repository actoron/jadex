package jadex.gpmn.editor.gui.stylesheets;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 *  Style sheet defining the visual aspects of the GPMN model.
 *  This is the current default.
 *
 */
public class GpmnStylesheetColor extends mxStylesheet
{
	/** The default font. */
	public static final String FONT = "Bitstream Vera Sans";
	
	static
	{
		mxGraphics2DCanvas.putShape(VirtualActivationEdgeMarkerShape.class.getSimpleName(), new VirtualActivationEdgeMarkerShape());
		mxGraphics2DCanvas.putShape(GoalMarkerShape.class.getSimpleName(), new GoalMarkerShape());
		mxGraphics2DCanvas.putShape(PlanMarkerShape.class.getSimpleName(), new PlanMarkerShape());
		
		try
		{
			InputStream fontis = GpmnStylesheetColor.class.getClassLoader().getResourceAsStream("jadex/gpmn/editor/gui/fonts/Vera.ttf");
			Font vera = Font.createFont(Font.TRUETYPE_FONT, fontis);
			fontis.close();
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(vera);
			fontis = GpmnStylesheetColor.class.getClassLoader().getResourceAsStream("jadex/gpmn/editor/gui/fonts/VeraBd.ttf");
			vera = Font.createFont(Font.TRUETYPE_FONT, fontis);
			fontis.close();
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(vera);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Creates the style sheet.
	 */
	public GpmnStylesheetColor()
	{
		// Goal Styles
		Map<String, Object> style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.ACHIEVE_GOAL_COLOR);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		style.put(mxConstants.STYLE_SHADOW, Boolean.TRUE);
		style.put(mxConstants.STYLE_FONTSIZE, 10);
		//style.put(mxConstants.STYLE_GLASS, 1);
		//style.put(mxConstants.STYLE_GRADIENTCOLOR, "ghsdt");
		//style.put(mxConstants.STYLE_L);
		putCellStyle(ModelConstants.ACHIEVE_GOAL_TYPE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.PERFORM_GOAL_COLOR);
		putCellStyle(ModelConstants.PERFORM_GOAL_TYPE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.MAINTAIN_GOAL_COLOR);
		putCellStyle(ModelConstants.MAINTAIN_GOAL_TYPE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.QUERY_GOAL_COLOR);
		putCellStyle(ModelConstants.QUERY_GOAL_TYPE, style);
		
		// Goal Markers
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, GoalMarkerShape.class.getSimpleName());
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
		style.put(mxConstants.STYLE_MOVABLE, 0);
		style.put(mxConstants.STYLE_EDITABLE, 0);
		style.put(mxConstants.STYLE_RESIZABLE, 0);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_CENTER);
		style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_CENTER);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
		style.put(mxConstants.STYLE_FONTSIZE, 12);
		putCellStyle(GuiConstants.GOAL_TYPE_STYLE, style);
		
		// Plan Styles
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_ROUNDED, Boolean.TRUE);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.REF_PLAN_COLOR);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		style.put(mxConstants.STYLE_SHADOW, Boolean.TRUE);
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_FONTSIZE, 10);
		putCellStyle(GuiConstants.REF_PLAN_STYLE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, GuiConstants.ACTIVATION_PLAN_COLOR);
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.TRUE);
		putCellStyle(GuiConstants.ACTIVATION_PLAN_STYLE, style);
		
		// Plan Markers
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, PlanMarkerShape.class.getSimpleName());
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		style.put(mxConstants.STYLE_MOVABLE, 0);
		style.put(mxConstants.STYLE_EDITABLE, 0);
		style.put(mxConstants.STYLE_RESIZABLE, 0);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_CENTER);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
		style.put(mxConstants.STYLE_FONTSIZE, 12);
		putCellStyle(GuiConstants.PLAN_TYPE_STYLE, style);
		
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
		style.put(mxConstants.STYLE_OPACITY, 0);
		style.put(mxConstants.STYLE_MOVABLE, 0);
		style.put(mxConstants.STYLE_EDITABLE, 0);
		style.put(mxConstants.STYLE_RESIZABLE, 0);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
		style.put(mxConstants.STYLE_FONTSIZE, 10);
		putCellStyle(GuiConstants.PLAN_MODE_STYLE, style);
		
		// Edge Styles
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		style.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		style.put(mxConstants.STYLE_EDITABLE, Boolean.FALSE);
		//style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CURVE);
		//style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_);
		//mxEdgeStyle
		putCellStyle(GuiConstants.PLAN_EDGE_STYLE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL);
		//style.put(mxConstants.STYLE_STROKECOLOR, "#ff0000");
		putCellStyle(GuiConstants.SUPPRESSION_EDGE_STYLE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		putCellStyle(GuiConstants.ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_PARALLEL, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_NOLABEL, Boolean.FALSE);
		style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
		style.put(mxConstants.STYLE_EDITABLE, Boolean.TRUE);
		putCellStyle(GuiConstants.ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_SEQUENTIAL, style);
		
		style = new HashMap<String, Object>(getCellStyle(GuiConstants.ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_PARALLEL, style));
		style.put(mxConstants.STYLE_EDITABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		putCellStyle(GuiConstants.VIRTUAL_ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_PARALLEL, style);
		
		style = new HashMap<String, Object>(getCellStyle(GuiConstants.ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_SEQUENTIAL, style));
		style.put(mxConstants.STYLE_EDITABLE, Boolean.TRUE);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		putCellStyle(GuiConstants.VIRTUAL_ACTIVATION_EDGE_STYLE + ModelConstants.ACTIVATION_MODE_SEQUENTIAL, style);
		
		// Edge markers
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, VirtualActivationEdgeMarkerShape.class.getSimpleName());
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
		style.put(mxConstants.STYLE_MOVABLE, 0);
		style.put(mxConstants.STYLE_EDITABLE, 0);
		style.put(mxConstants.STYLE_RESIZABLE, 0);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKEWIDTH, "2");
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		putCellStyle(GuiConstants.VIRTUAL_ACTIVATION_EDGE_MARKER_STYLE, style);
	}
}
