package jadex.gpmn.editor.gui.controllers;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.gui.IControllerAccess;
import jadex.gpmn.editor.gui.IModelContainer;
import jadex.gpmn.editor.gui.IViewAccess;
import jadex.gpmn.editor.gui.SGuiHelper;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IPlan;
import jadex.gpmn.editor.model.gpmn.ModelConstants;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VNode;
import jadex.gpmn.editor.model.visual.VPlan;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge;
import jadex.gpmn.editor.model.visual.VVirtualActivationEdge.VVEdgeMarker;

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
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraphView;

/**
 *  Controller for handling mouse inputs to the graph.
 *
 */
public class MouseController extends MouseAdapter
{
	/** Access to the models. */
	protected IModelContainer modelcontainer;
	
	/** Access to the controllers. */
	protected IControllerAccess controlleraccess;
	
	/** Access to view. */
	protected IViewAccess viewaccess;
	
	/** Timer for animated zoom operations. */
	protected Timer zoomtimer;
	
	/** Target scale for zoom operation. */
	protected double targetscale;
	
	/**
	 *  Creates a new mouse controller.
	 * 
	 *  @param container Access to the models.
	 */
	public MouseController(IModelContainer container, IControllerAccess controlleraccess, IViewAccess viewaccess)
	{
		this.modelcontainer = container;
		this.controlleraccess = controlleraccess;
		this.viewaccess = viewaccess;
	}
	
	public void mousePressed(MouseEvent e)
	{
		if (MouseEvent.BUTTON1 == e.getButton())
		{
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			String mode = viewaccess.getEditMode();
			if (IViewAccess.CONTROL_POINT_MODE.equals(mode))
			{
				if (cell == modelcontainer.getGraph().getSelectionCell() &&
					modelcontainer.getGraph().getSelectionCount() == 1 &&
					modelcontainer.getGraph().getSelectionCell() instanceof VEdge)
				{
					mxPoint p = modelcontainer.getGraphComponent().getPointForEvent(e);
					mxGeometry geo = ((VEdge) cell).getGeometry();
					List<mxPoint> points = (List<mxPoint>) geo.getPoints();
					
					int i = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(cell), p.getX(), p.getY());;
					
					if (points == null)
					{
						points = new ArrayList<mxPoint>();
						geo.setPoints(points);
					}
					
					/*while (i < points.size())
					{
						
						mxPoint tmp = points.get(i);
						if (tmp.getX() <= p.getX() && tmp.getY() <= p.getY())
						{
							break;
						}
					}*/
					
					points.add(i, p);
					
					SGuiHelper.refreshCellView(modelcontainer.getGraph(), (VEdge) cell);
					
					viewaccess.getToolGroup().setSelected(viewaccess.getSelectTool().getModel(), true);
				}
			}
		}
	}
	
	/**
	 *  Called when the mouse is clicked.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (MouseEvent.BUTTON1 == e.getButton())
		{
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			
			String mode = viewaccess.getEditMode();
			
			if (IViewAccess.NODE_CREATION_MODES.contains(mode) &&
				cell == null && mode != null &&
				modelcontainer.getGraph().getSelectionModel().getCell() == null)
			{
				Point p = modelcontainer.getGraphComponent().getPointForEvent(e).getPoint();
				
				VNode node = createNode(mode, p);
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().addCell(node);
				modelcontainer.getGraph().getModel().endUpdate();
			}
			else if (cell instanceof VVEdgeMarker)
			{
				VVirtualActivationEdge virtedge = (VVirtualActivationEdge) ((VVEdgeMarker) cell).getParent();
				controlleraccess.getFoldController().unfoldActivationPlan(virtedge);
			}
		}
		else if (MouseEvent.BUTTON2 == e.getButton())
		{
			setTargetScale(1.0);
		}
		else if (MouseEvent.BUTTON3 == e.getButton())
		{
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			
			String mode = viewaccess.getEditMode();
			
			if (IViewAccess.CONTROL_POINT_MODE.equals(mode))
			{
				if (cell== modelcontainer.getGraph().getSelectionCell() &&
					modelcontainer.getGraph().getSelectionCount() == 1 &&
					modelcontainer.getGraph().getSelectionCell() instanceof VEdge)
				{
					mxGeometry geo = ((VEdge) cell).getGeometry();
					List<mxPoint> points = (List<mxPoint>) geo.getPoints();
					mxPoint p = modelcontainer.getGraphComponent().getPointForEvent(e);
					
					if (points != null && points.size() > 0)
					{
						int index = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(cell), p.getX(), p.getY());
						
						// FIXME: Use modulus on index, hack? Possible bug in findNearestSegment()
						mxPoint cp = points.get(index % points.size()); 
						double dx = p.getX() - cp.getX();
						double dy = p.getY() - cp.getY();
						double d2 = (dx * dx) + (dy * dy);
						
						//TODO: Better (scale-dependent) threshold
						if (d2 < 25)
						{
							points.remove(index);
						}
						
						SGuiHelper.refreshCellView(modelcontainer.getGraph(), (VEdge) cell);
					}
				}
			}
			viewaccess.getToolGroup().setSelected(viewaccess.getSelectTool().getModel(), true);
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
		scale = scale - units * 0.1 * scale;
		scale = scale < 0.2? 0.2: scale;;
		setTargetScale(scale > 4.0? 4.0: scale);
	}
	
	protected void setTargetScale(double scale)
	{
		targetscale = scale;
		
		if (zoomtimer == null)
		{
			zoomtimer = new Timer(GuiConstants.ANIMATION_FRAME_TIME, new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					mxGraphComponent gc = modelcontainer.getGraphComponent();
					mxGraphView view = gc.getGraph().getView();
					double scale = view.getScale();
					double oldscale = scale;
					
					mxPoint center = new mxPoint(0.5, 0.5);
					Point mp = modelcontainer.getGraphComponent().getMousePosition();
					if (mp != null)
					{
						center = new mxPoint(mp);
						center.setX(center.getX() / gc.getSize().width);
						center.setY(center.getY() / gc.getSize().height);
						center.setX(0.5 - ((0.5 - center.getX()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
						center.setY(0.5 - ((0.5 - center.getY()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
					}
					
					if (Math.min(scale, targetscale) / Math.max(scale, targetscale) > GuiConstants.ZOOM_ANIMATION_FINAL_RATIO)
					{
						// lock in on final scale
						scale = targetscale;
						zoomtimer.stop();
						zoomtimer = null;
					}
					else
					{
						// take a step
						scale = scale + ((targetscale - scale) * GuiConstants.ZOOM_ANIMATION_STEP_RATIO);
					}
					
					double rat = scale / oldscale;
					view.scaleAndTranslate(scale, 0, 0);
					gc.refresh();
					
					JScrollBar vbar = gc.getVerticalScrollBar();
					JScrollBar hbar = gc.getHorizontalScrollBar();
					int px = (int) Math.round(hbar.getModel().getValue() * rat + hbar.getModel().getExtent() * (rat - 1.0) * center.getX());
					int py = (int) Math.round(vbar.getModel().getValue() * rat + vbar.getModel().getExtent() * (rat - 1.0) * center.getY());
					
					hbar.getModel().setValue(Math.max(0, px));
					vbar.getModel().setValue(Math.max(0, py));
				}
			});
			zoomtimer.start();
		}
	}
	
	/**
	 * Creates the right node for the edit mode.
	 * 
	 * @param editmode The current edit mode.
	 * @param p The target position.
	 * @return The new node.
	 */
	protected VNode createNode(String editmode, Point p)
	{
		VNode ret = null;
		if (editmode.startsWith("Goal"))
		{
			IGoal bgoal = (IGoal) modelcontainer.getGpmnModel().createNode(IGoal.class);
			ret = new VGoal(bgoal,
							new mxPoint(p.getX() - (GuiConstants.DEFAULT_GOAL_WIDTH >>> 1),
										p.getY() - (GuiConstants.DEFAULT_GOAL_HEIGHT >>> 1)));
			if (IViewAccess.ACHIEVE_GOAL_MODE.equals(editmode))
				bgoal.setGoalType(ModelConstants.ACHIEVE_GOAL_TYPE);
			else if (IViewAccess.PERFORM_GOAL_MODE.equals(editmode))
				bgoal.setGoalType(ModelConstants.PERFORM_GOAL_TYPE);
			else if (IViewAccess.MAINTAIN_GOAL_MODE.equals(editmode))
				bgoal.setGoalType(ModelConstants.MAINTAIN_GOAL_TYPE);
			else if (IViewAccess.QUERY_GOAL_MODE.equals(editmode))
				bgoal.setGoalType(ModelConstants.QUERY_GOAL_TYPE);
		}
		else if (editmode.startsWith("Plan"))
		{
			IPlan mplan = null;
			
			if (IViewAccess.BPMN_PLAN_MODE.equals(editmode))
				mplan = (IBpmnPlan) modelcontainer.getGpmnModel().createNode(IBpmnPlan.class);
			else if (IViewAccess.ACTIVATION_PLAN_MODE.equals(editmode))
				mplan = (IActivationPlan) modelcontainer.getGpmnModel().createNode(IActivationPlan.class);
			
			ret = new VPlan(mplan, p.getX() - (GuiConstants.DEFAULT_PLAN_WIDTH >>> 1),
								   p.getY() - (GuiConstants.DEFAULT_PLAN_HEIGHT >>> 1));
		}
		
		return ret;
	}
}
