package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraphComponent;
import jadex.bpmn.editor.gui.BpmnGraphComponent.BpmnGraphControl;
import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.gui.stylesheets.SequenceEdgeStyleFunction;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MActivity;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraphView;

/**
 *  Controller for handling mouse inputs to the graph.
 *
 */
public class MouseController extends MouseAdapter
{
	/** Access to the models. */
	protected ModelContainer modelcontainer;
	
	/** Timer for animated zoom operations. */
	protected Timer zoomtimer;
	
	/** Target scale for zoom operation. */
	protected double targetscale;
	
	/** Initial zoom distance. */
	protected double zoomdist;
	
	/** Zoom step. */
	protected int zoomstep;
	
	/**
	 *  Creates a new mouse controller.
	 * 
	 *  @param container Access to the models.
	 */
	public MouseController(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	/**
	 *  Called when the mouse is clicked.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (MouseEvent.BUTTON1 == e.getButton())
		{
			mxPoint mxp = modelcontainer.getGraphComponent().getPointForEvent(e);
			Point p = mxp.getPoint();
			String mode = modelcontainer.getEditMode();
			
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			if (ModelContainer.EDIT_MODE_ADD_CONTROL_POINT.equals(mode))
			{
				if (cell == modelcontainer.getGraph().getSelectionCell() &&
					modelcontainer.getGraph().getSelectionCount() == 1 &&
					modelcontainer.getGraph().getSelectionCell() instanceof VEdge)
				{
					VEdge vedge = (VEdge) cell;
					mxGeometry geo = vedge.getGeometry();
					List<mxPoint> points = (List<mxPoint>) geo.getPoints();
					
//					double scale = modelcontainer.getGraph().getView().getScale();
//					mxp.setX(mxp.getX() * scale);
//					mxp.setY(mxp.getY() * scale);
					if (vedge.getSource() != null && vedge.getSource().getParent() != null)
					{
						mxp = SequenceEdgeStyleFunction.unAdjustPoint(modelcontainer.getGraph(), vedge.getSource().getParent(), mxp);
//						mxp = SCreationController.adjustPoint(modelcontainer.getGraph(), vedge.getSource().getParent(), mxp);
					}
					
					if (points == null)
					{
						points = new ArrayList<mxPoint>();
						geo.setPoints(points);
					}
					
					if (points.size() == 0)
					{
						points.add(mxp);
					}
					else
					{
						int i = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(cell), p.getX(), p.getY());;
						points.add(i, mxp);
					}
					
					modelcontainer.getGraph().refreshCellView((VEdge) cell);
					modelcontainer.setDirty(true);
					
					modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				}
			}
			else if (cell == null && ModelContainer.EDIT_MODE_POOL.equals(mode))
			{
				SCreationController.createPool(modelcontainer, p);
			}
			else if (ModelContainer.EDIT_MODE_LANE.equals(mode) &&
					(cell instanceof VPool || cell instanceof VLane))
			{
				SCreationController.createLane(modelcontainer, cell);
			}
			else if (ModelContainer.ACTIVITY_MODES.contains(mode) || mode.contains("Event"))
			{
				SCreationController.createActivity(modelcontainer, mode, cell, p, true);
				modelcontainer.getGraphComponent().doLayout();
			}
			else if (cell == null)
			{
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				return;
			}
		}
		else if (MouseEvent.BUTTON2 == e.getButton())
		{
			setTargetScale(GuiConstants.DEFAULT_ZOOM);
		}
		else if (MouseEvent.BUTTON3 == e.getButton() && e.getClickCount() == 2)
		{
			Object cells[] = modelcontainer.getGraph().getSelectionCells();
			if (cells != null)
			{
				for (Object obj : cells)
				{
					if (obj instanceof VActivity)
					{
						VActivity vactivity = (VActivity) obj;
						String at = ((MActivity) vactivity.getBpmnElement()).getActivityType();
						Dimension ds = BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.containsKey(at) ?
								   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(at) :
								   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(vactivity.getStyle());
						
						Dimension ads = null;
						if (BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(vactivity.getStyle()) ||
							BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(at))
						{
							ads = (BpmnStylesheetColor.COLLAPSED_SIZES.get(at) != null?
								   BpmnStylesheetColor.COLLAPSED_SIZES.get(at) :
								   BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()));
						}
						
						if (ads != null && vactivity.isCollapsed())
						{
							Dimension tmp = ds;
							ds = ads;
							ads = tmp;
						}
						
						vactivity.getGeometry().setWidth(ds.width);
						vactivity.getGeometry().setHeight(ds.height);
						if (ads != null)
						{
							mxRectangle alt = vactivity.getGeometry().getAlternateBounds();
							vactivity.getGeometry().setAlternateBounds(new mxRectangle(alt.getX(), alt.getY(),
																					   ads.width, ads.height));
						}
						
						modelcontainer.getGraph().refreshCellView(vactivity);
						modelcontainer.setDirty(true);
					}
				}
			}
		}
	}
	
	/**
	 *  Called when the mouse is pressed.
	 */
	public void mousePressed(MouseEvent e)
	{
		if (MouseEvent.BUTTON1 != e.getButton())
		{
			modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
		}
	}
	
	/**
	 *  Called when the mouse wheel is moved.
	 */
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		mxGraphComponent gc = modelcontainer.getGraphComponent();
		double scale = gc.getGraph().getView().getScale();
		
		if (zoomtimer != null)
		{
			scale = targetscale;
		}
		
		int units = e.getUnitsToScroll();
		scale = scale - units * 0.05 * scale;
		scale = scale < 0.2? 0.2: scale;;
		setTargetScale(scale > 4.0? 4.0: scale);
	}
	
	protected void setTargetScale(double scale)
	{
		targetscale = scale;
		zoomdist = targetscale - modelcontainer.getGraph().getView().getScale();
		zoomstep = 1;
		
		if (zoomtimer == null)
		{
			zoomtimer = new Timer(GuiConstants.ANIMATION_FRAME_TIME, new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					BpmnGraphComponent gc = (BpmnGraphComponent) modelcontainer.getGraphComponent();
					mxGraphView view = gc.getGraph().getView();
					double scale = view.getScale();
					double oldscale = scale;
					
					mxPoint center = null;
					Point mp = modelcontainer.getGraphComponent().getMousePosition();
					if (mp != null)
					{
						center = new mxPoint(mp);
						center.setX(center.getX() / gc.getSize().width);
						center.setY(center.getY() / gc.getSize().height);
						center.setX(0.5 - ((0.5 - center.getX()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
						center.setY(0.5 - ((0.5 - center.getY()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
					}
					
					double steps = GuiConstants.ANIMATION_FPS / 2.0;
					if (zoomstep > steps)
					{
						// lock in on final scale
						scale = targetscale;
						zoomtimer.stop();
						zoomtimer = null;
						//System.out.println(System.currentTimeMillis() - ts);
					}
					else
					{
						scale = (targetscale - zoomdist) + (zoomdist * (Math.log(zoomstep++) / Math.log(steps)));
					}
					
					setScale(modelcontainer, oldscale, scale, center);
					
					//gc.extendComponent(grapharea);
					
					
				}
			});
			zoomtimer.start();
		}
	}
	
	/**
	 *  Sets the scale around a center.
	 *  
	 *  @param modelcontainer Model container.
	 *  @param oldscale The old scale.
	 *  @param scale The new scale.
	 *  @param center The center point.
	 */
	public static final void setScale(ModelContainer modelcontainer, double oldscale, double scale, mxPoint center)
	{
		center = center != null? center: new mxPoint(0.5, 0.5);
		
		mxGraphComponent gc = modelcontainer.getGraphComponent();
		double rat = scale / oldscale;
		JScrollBar vbar = gc.getVerticalScrollBar();
		JScrollBar hbar = gc.getHorizontalScrollBar();
		
		Dimension grapharea = new Dimension(gc.getGraphControl().getMinimumSize());
		//System.out.println("In: " + grapharea + " " + rat);
		
		grapharea.width = (int) Math.round(grapharea.width * rat);
		grapharea.height = (int) Math.round(grapharea.height * rat);
		
		//System.out.println("Out: " + grapharea + " " + rat);
		//view.setEventsEnabled(false);
		//view.scaleAndTranslate(scale, 0, 0);
		modelcontainer.getGraph().getView().setScale(scale);
		//gc.refresh();
		//System.out.println(gc.getBounds());
		
		//gc.setMinimumSize(grapharea);
		//gc.setPreferredSize(grapharea);
		((BpmnGraphControl) gc.getGraphControl()).doSetPreferredSize(grapharea);
		((BpmnGraphControl) gc.getGraphControl()).doSetMinimumSize(grapharea);
		gc.getGraphControl().revalidate();
		
		int px = (int) Math.round(hbar.getModel().getValue() * rat + hbar.getModel().getExtent() * (rat - 1.0) * center.getX());
		int py = (int) Math.round(vbar.getModel().getValue() * rat + vbar.getModel().getExtent() * (rat - 1.0) * center.getY());
		
		hbar.getModel().setValue(Math.max(0, px));
		vbar.getModel().setValue(Math.max(0, py));
	}
}
