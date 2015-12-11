package jadex.bpmn.editor.gui.controllers;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraphView;

import jadex.bpmn.editor.gui.BpmnGraphComponent;
import jadex.bpmn.editor.gui.BpmnGraphComponent.BpmnGraphControl;
import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.contextmenus.EventContextMenu;
import jadex.bpmn.editor.gui.contextmenus.GatewayContextMenu;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.model.MActivity;

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
		if (MouseEvent.BUTTON1 == e.getButton() && e.getClickCount() == 1)
		{
			mxPoint mxp = modelcontainer.getGraphComponent().getPointForEvent(e);
			Point2D p = new Point2D.Double(mxp.getX(), mxp.getY());
			String mode = modelcontainer.getEditMode();
			
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			if (e.getClickCount() == 1 &&
				(cell instanceof VInParameter ||
				 cell instanceof VOutParameter))
			{
				modelcontainer.getGraph().setSelectionCell(cell);
			}
			else if (ModelContainer.EDIT_MODE_ADD_CONTROL_POINT.equals(mode))
			{
				if (cell == modelcontainer.getGraph().getSelectionCell() &&
					modelcontainer.getGraph().getSelectionCount() == 1 &&
					modelcontainer.getGraph().getSelectionCell() instanceof VEdge)
				{
					SCreationController.createControlPoint((VEdge) cell, mxp, modelcontainer);
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
						vactivity.refreshParameterObjectGeometry();
						
						// TODO: Fix this selection hack.
						Object[] scells = modelcontainer.getGraph().getSelectionCells();
						modelcontainer.getGraph().refreshCellView(vactivity);
						modelcontainer.getGraph().setSelectionCells(scells);
						modelcontainer.setDirty(true);
					}
				}
			}
			
			mxPoint mxp = modelcontainer.getGraphComponent().getPointForEvent(e);
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			if (cell instanceof VEdge)
			{
				SCreationController.deleteControlPoint((VEdge) cell, mxp, modelcontainer);
			}
		}
		else if (MouseEvent.BUTTON3 == e.getButton() &&
				 modelcontainer.getGraph().getSelectionCount() == 1 &&
				 modelcontainer.getGraph().getSelectionCell() instanceof VActivity &&
				 modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY()) == modelcontainer.getGraph().getSelectionCell())
		{
			VActivity activity = (VActivity) modelcontainer.getGraph().getSelectionCell();
			if (activity.getMActivity().getActivityType() != null)
			{
				if (activity.getMActivity().getActivityType().startsWith("Gateway"))
				{
					GatewayContextMenu gcm = new GatewayContextMenu(activity, modelcontainer);
					int x = e.getX() - modelcontainer.getGraphComponent().getHorizontalScrollBar().getModel().getValue();
					int y = e.getY() - modelcontainer.getGraphComponent().getVerticalScrollBar().getModel().getValue();
					gcm.show(modelcontainer.getGraphComponent(), x, y);
				}
				else if (activity.getMActivity().getActivityType().startsWith("Event") &&
						 (activity.getMActivity().getIncomingMessagingEdges() == null || activity.getMActivity().getIncomingMessagingEdges().size() == 0) &&
						 (activity.getMActivity().getOutgoingMessagingEdges() == null || activity.getMActivity().getOutgoingMessagingEdges().size() == 0))
				{
					EventContextMenu ecm = new EventContextMenu(activity, modelcontainer);
					int x = e.getX() - modelcontainer.getGraphComponent().getHorizontalScrollBar().getModel().getValue();
					int y = e.getY() - modelcontainer.getGraphComponent().getVerticalScrollBar().getModel().getValue();
					ecm.show(modelcontainer.getGraphComponent(), x, y);
				}
			}
		}
//		else if (MouseEvent.BUTTON3 == e.getButton() &&
//				  e.getClickCount() == 1 &&
//				  modelcontainer.getGraph().getSelectionCells() != null &&
//				  modelcontainer.getGraph().getSelectionCells().length > 0 &&
//				  modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY()) != null &&
//				  Arrays.asList(modelcontainer.getGraph().getSelectionCells()).contains(modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY())))
//		{
//			Object[] cells = modelcontainer.getGraph().getSelectionCells();
//			CellContextMenu ccm = new CellContextMenu(modelcontainer.getGraph(), cells);
//			ccm.show(modelcontainer.getGraphComponent(), e.getX(), e.getY());
//		}
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
		if(modelcontainer.getSettings()!=null && modelcontainer.getSettings().isSmoothZoom())
		{
			modelcontainer.getGraphComponent().stopEditing(true);
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
		else
		{
			double oldscale = modelcontainer.getGraph().getView().getScale();
			
			mxPoint center = null;
			Point mp = modelcontainer.getGraphComponent().getMousePosition();
			if (mp != null)
			{
				center = new mxPoint(mp);
				center.setX(center.getX() / modelcontainer.getGraphComponent().getSize().width);
				center.setY(center.getY() / modelcontainer.getGraphComponent().getSize().height);
				center.setX(0.5 - ((0.5 - center.getX()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
				center.setY(0.5 - ((0.5 - center.getY()) * GuiConstants.ZOOM_MOUSE_DIRECTION_FACTOR));
			}
			
			setScale(modelcontainer, oldscale, scale, center);
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
