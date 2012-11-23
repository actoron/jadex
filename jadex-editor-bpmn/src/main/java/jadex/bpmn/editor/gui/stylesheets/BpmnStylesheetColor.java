package jadex.bpmn.editor.gui.stylesheets;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MBpmnModel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxMarkerRegistry;
import com.mxgraph.shape.mxSwimlaneShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

/**
 *  Style sheet defining the visual aspects of the BPMN model.
 *  This is the current default.
 *
 */
public class BpmnStylesheetColor extends mxStylesheet
{
	/** The default font. */
	public static final String FONT = "Bitstream Vera Sans";
	
	/** BPMN Pool Color */
	public static final String POOL_COLOR = "#cfdcf1";
	
	/** BPMN Lane Color */
	public static final String LANE_COLOR = "#cfdcf1";
	
	/** BPMN Task Color */
	public static final String TASK_COLOR = "#61a7e3";
	
	/** BPMN Sub-Process Color */
	public static final String SUBPROCESS_COLOR = "#b0c9f1";
	
	/** BPMN External Sub-Process Color */
	public static final String EXTERNAL_SUBPROCESS_COLOR = "#b0f1d3";
	
	/** BPMN Gateway Color */
	public static final String GATEWAY_COLOR = "#ff81ac";
	
	/** BPMN Start Event Color */
	public static final String START_EVENT_COLOR = "#c5ea6b";
	
	/** BPMN Intermediate Event Color */
	public static final String INTERMEDIATE_EVENT_COLOR = "#efab53";
	
	/** BPMN End Event Color */
	public static final String END_EVENT_COLOR = "#e76363";
	
	/** Default Pool Width */
	public static final int DEFAULT_POOL_WIDTH = 1000;
	
	/** Default Pool Height */
	public static final int DEFAULT_POOL_HEIGHT = 300;
	
	/** Default Activity Sizes */
	public static final Map<String, Dimension> DEFAULT_ACTIVITY_SIZES = new HashMap<String, Dimension>();
	static
	{
		DEFAULT_ACTIVITY_SIZES.put(MBpmnModel.TASK, new Dimension(160, 100));
		DEFAULT_ACTIVITY_SIZES.put(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS, new Dimension(480, 200));
		DEFAULT_ACTIVITY_SIZES.put(VExternalSubProcess.class.getSimpleName(), new Dimension(160, 100));
		DEFAULT_ACTIVITY_SIZES.put(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, new Dimension(60, 60));
		DEFAULT_ACTIVITY_SIZES.put(MBpmnModel.GATEWAY_PARALLEL, new Dimension(60, 60));
		DEFAULT_ACTIVITY_SIZES.put(MBpmnModel.GATEWAY_DATABASED_INCLUSIVE, new Dimension(60, 60));
		DEFAULT_ACTIVITY_SIZES.put(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START", new Dimension(40, 40));
		DEFAULT_ACTIVITY_SIZES.put(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE", new Dimension(40, 40));
		DEFAULT_ACTIVITY_SIZES.put(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END", new Dimension(40, 40));
	}
	
	/** Collapsed Sizes */
	public static final Map<String, Dimension> COLLAPSED_SIZES = new HashMap<String, Dimension>();
	static
	{
		COLLAPSED_SIZES.put(ModelContainer.EDIT_MODE_SUBPROCESS, new Dimension(160, 100));
		COLLAPSED_SIZES.put(ModelContainer.EDIT_MODE_EXTERNAL_SUBPROCESS, COLLAPSED_SIZES.get(ModelContainer.EDIT_MODE_SUBPROCESS));
	}
	
	static
	{
		//mxGraphics2DCanvas.putShape(VPool.class.getSimpleName(), new PoolLaneShape(true));
		//mxGraphics2DCanvas.putShape(VLane.class.getSimpleName(), new PoolLaneShape(false));
		mxGraphics2DCanvas.putShape(mxConstants.SHAPE_SWIMLANE, new mxSwimlaneShape()
		{
			/**
			 *  Paints the shape.
			 */
			public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
			{
				int start = (int) Math.round(mxUtils.getInt(state.getStyle(),
						mxConstants.STYLE_STARTSIZE, mxConstants.DEFAULT_STARTSIZE)
						* canvas.getScale());

				Rectangle tmp = state.getRectangle();

				if (mxUtils
						.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL, true))
				{
					if (configureGraphics(canvas, state, true))
					{
						canvas.fillShape(new Rectangle(tmp.x, tmp.y, tmp.width, Math
								.min(tmp.height, start)));
						canvas.fillShape(new Rectangle(tmp.x, tmp.y + start, tmp.width,
								tmp.height - start));
					}

					if (configureGraphics(canvas, state, false))
					{
						if (state.getCell() instanceof VPool)
						{
							canvas.getGraphics().drawRect(tmp.x, tmp.y, tmp.width,
									Math.min(tmp.height, start));
							canvas.getGraphics().drawRect(tmp.x, tmp.y + start, tmp.width,
									tmp.height - start);
						}
						else
						{
							canvas.getGraphics().drawRect(tmp.x, tmp.y, tmp.width, tmp.height);
						}
					}
				}
				else
				{
					if (configureGraphics(canvas, state, true))
					{
						canvas.fillShape(new Rectangle(tmp.x, tmp.y, Math.min(
								tmp.width, start), tmp.height));
						canvas.fillShape(new Rectangle(tmp.x + start, tmp.y,
								tmp.width - start, tmp.height));
					}

					if (configureGraphics(canvas, state, false))
					{
						if (state.getCell() instanceof VPool)
						{
							canvas.getGraphics().drawRect(tmp.x, tmp.y,
									Math.min(tmp.width, start), tmp.height);
							canvas.getGraphics().drawRect(tmp.x + start, tmp.y,
									tmp.width - start, tmp.height);
						}
						else
						{
							canvas.getGraphics().drawRect(tmp.x, tmp.y, tmp.width, tmp.height);
						}
					}
				}
			}
		});
		
		mxGraphics2DCanvas.putShape(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, new GatewayShape(GatewayShape.GATEWAY_SHAPE_TYPE_XOR));
		mxGraphics2DCanvas.putShape(MBpmnModel.GATEWAY_PARALLEL, new GatewayShape(GatewayShape.GATEWAY_SHAPE_TYPE_AND));
		mxGraphics2DCanvas.putShape(MBpmnModel.GATEWAY_DATABASED_INCLUSIVE, new GatewayShape(GatewayShape.GATEWAY_SHAPE_TYPE_OR));
		mxGraphics2DCanvas.putShape(EventShape.class.getSimpleName(), new EventShape());
		mxMarkerRegistry.registerMarker(StrokeMarker.class.getSimpleName(), new StrokeMarker());
		
		try
		{
			InputStream fontis = BpmnStylesheetColor.class.getClassLoader().getResourceAsStream("jadex/bpmn/editor/gui/fonts/Vera.ttf");
			Font vera = Font.createFont(Font.TRUETYPE_FONT, fontis);
			fontis.close();
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(vera);
			fontis = BpmnStylesheetColor.class.getClassLoader().getResourceAsStream("jadex/bpmn/editor/gui/fonts/VeraBd.ttf");
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
	public BpmnStylesheetColor()
	{
		Map<String, Object> style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
		style.put(mxConstants.STYLE_FILLCOLOR, POOL_COLOR);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSIZE, 16);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		style.put(mxConstants.STYLE_HORIZONTAL, Boolean.FALSE);
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.FALSE);
		putCellStyle(VPool.class.getSimpleName(), style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, LANE_COLOR);
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.TRUE);
		putCellStyle(VLane.class.getSimpleName(), style);
		
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_ROUNDED, Boolean.TRUE);
		style.put(mxConstants.STYLE_FILLCOLOR, TASK_COLOR);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSIZE, 16);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);
		style.put(mxConstants.STYLE_SHADOW, Boolean.TRUE);
		style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
		putCellStyle(VActivity.class.getSimpleName() + "_" + MBpmnModel.TASK, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, EXTERNAL_SUBPROCESS_COLOR);
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.TRUE);
		putCellStyle(VExternalSubProcess.class.getSimpleName(), style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, SUBPROCESS_COLOR);
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
		style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
		style.put(mxConstants.STYLE_SPACING, 10);
		putCellStyle(VActivity.class.getSimpleName() + "_" + MBpmnModel.SUBPROCESS, style);
		
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE);
		style.put(mxConstants.STYLE_ROUNDED, Boolean.FALSE);
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTSIZE, 16);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_FOLDABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RHOMBUS);
		style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
		style.put(mxConstants.STYLE_SHADOW, Boolean.TRUE);
		style.put(mxConstants.STYLE_SPACING_TOP, 4);
		style.put(mxConstants.STYLE_FILLCOLOR, GATEWAY_COLOR);
		style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
		putCellStyle(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_SHAPE, MBpmnModel.GATEWAY_PARALLEL);
		putCellStyle(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_PARALLEL, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_SHAPE, MBpmnModel.GATEWAY_DATABASED_INCLUSIVE);
		putCellStyle(VActivity.class.getSimpleName() + "_" + MBpmnModel.GATEWAY_DATABASED_INCLUSIVE, style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_SHAPE, EventShape.class.getSimpleName());
		style.put(mxConstants.STYLE_FILLCOLOR, START_EVENT_COLOR);
		putCellStyle(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_START", style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, INTERMEDIATE_EVENT_COLOR);
		putCellStyle(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_INTERMEDIATE", style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_FILLCOLOR, END_EVENT_COLOR);
		putCellStyle(VActivity.class.getSimpleName() + "_" + EventShape.class.getSimpleName() + "_END", style);
		
		style = new HashMap<String, Object>();
		style.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
		style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		style.put(mxConstants.STYLE_ROUNDED, Boolean.TRUE);
		style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		style.put(mxConstants.STYLE_FONTFAMILY, FONT);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_NOLABEL, Boolean.TRUE);
		style.put(mxConstants.STYLE_EDITABLE, Boolean.FALSE);
		style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SIDETOSIDE);
		//style.put(mxConstants.STYLE_LOOP, mxConstants.EDGESTYLE_LOOP);
		putCellStyle(VSequenceEdge.class.getSimpleName(), style);
		
		style = new HashMap<String, Object>(style);
		style.put(mxConstants.STYLE_STARTARROW, StrokeMarker.class.getSimpleName());
		style.put(mxConstants.STYLE_STARTSIZE, 18);
		putCellStyle(VSequenceEdge.class.getSimpleName() + "_DEFAULT", style);
	}
}
