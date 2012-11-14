package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraphComponent;
import jadex.bpmn.editor.gui.BpmnGraphComponent.BpmnGraphControl;
import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VNode;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;

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
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraphView;

/**
 *  Controller for handling mouse inputs to the graph.
 *
 */
public class MouseController extends MouseAdapter
{
	/** Access to the models. */
	protected ModelContainer modelcontainer;
	
	/** Access to the controllers. */
	//protected IControllerAccess controlleraccess;
	
	/** Access to view. */
	//protected IViewAccess viewaccess;
	
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
			String mode = modelcontainer.getEditMode();
			
			Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
			if (cell == null && ModelContainer.EDIT_MODE_POOL.equals(mode))
			{
				Point p = modelcontainer.getGraphComponent().getPointForEvent(e).getPoint();
				VPool vpool = new VPool(modelcontainer.getGraph());
				vpool.setGeometry(new mxGeometry(p.getX(), p.getY(), BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
				MPool mpool = new MPool();
				mpool.setId(modelcontainer.getIdGenerator().generateId());
				mpool.setName("Pool");
				modelcontainer.getBpmnModel().addPool(mpool);
				vpool.setBpmnElement(mpool);
				
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().addCell(vpool);
				modelcontainer.getGraph().getModel().endUpdate();
				modelcontainer.setDirty(true);
			}
			else if (ModelContainer.EDIT_MODE_LANE.equals(mode) &&
					(cell instanceof VPool || cell instanceof VLane))
			{
				// Special treatment for lanes, only add to pools, do not add to pools with stuff in it.
				VPool vpool = null;
				if (cell instanceof VPool)
				{
					vpool = (VPool) cell;
				}
				else
				{
					vpool = (VPool) ((VLane) cell).getParent();
				}
				
				VLane vlane = new VLane(modelcontainer.getGraph());
				vlane.setGeometry(new mxGeometry(0, 0, BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
				MLane mlane = new MLane();
				mlane.setName("Lane");
				mlane.setId(modelcontainer.getIdGenerator().generateId());
				((MPool) vpool.getBpmnElement()).addLane(mlane);
				vlane.setBpmnElement(mlane);
				
				boolean moveelements = (!vpool.hasLanes()) && vpool.getChildCount() > 0;
				
				modelcontainer.getGraph().getModel().beginUpdate();
				
				modelcontainer.getGraph().addCell(vlane, vpool);
				if (moveelements)
				{
					// Move pool elements to new lane.
					List<VElement> movablechildren = new ArrayList<VElement>();
					for (int i = 0; i < vpool.getChildCount(); ++i)
					{
						if (!(vpool.getChildAt(i) instanceof VLane))
						{
							movablechildren.add((VElement) vpool.getChildAt(i));
						}
					}
					
					MPool mpool = (MPool) vpool.getBpmnElement();
					VElement[] movele = movablechildren.toArray(new VElement[movablechildren.size()]);
					for (int i = 0; i < movele.length; ++i)
					{
						MIdElement melement = movele[i].getBpmnElement();
						if (melement instanceof MActivity)
						{
							mpool.removeActivity((MActivity) melement);
							mlane.addActivity((MActivity) melement);
						}
						
						modelcontainer.getGraph().moveCells(new Object[] { movele[i] }, 0, 0, false, vlane, null);
					}
					
					/*modelcontainer.getGraph().removeCells(movele);
					modelcontainer.getGraph().addCells(movele, vlane);*/
				}
				
				modelcontainer.getGraph().getModel().endUpdate();
				//modelcontainer.getGraph().getStacklayout().execute(modelcontainer.getGraph().getDefaultParent());
				modelcontainer.getGraphComponent().refresh();
				
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
			}
			else if (ModelContainer.ACTIVITY_MODES.contains(mode) || mode.contains("Event"))
			{
				
				if (cell instanceof VPool)
				{
					if (((VPool) cell).hasLanes())
					{
						modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
						return;
					}
				}
				else if (!(cell instanceof VLane))
				{
					modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
					return;
				}
				
				MActivity mactivity = null;
				if (ModelContainer.EDIT_MODE_SUBPROCESS.equals(mode))
				{
					mactivity = new MSubProcess();
					mactivity.setClazz(new ClassInfo(""));
				}
				else
				{
					mactivity = new MActivity();
				}
				mactivity.setId(modelcontainer.getIdGenerator().generateId());
				mactivity.setActivityType(ModelContainer.ACTIVITY_MODES_TO_TYPES.containsKey(mode) ? ModelContainer.ACTIVITY_MODES_TO_TYPES.get(mode) : mode);
				
				VActivity vactivity = null;
				if (ModelContainer.EDIT_MODE_SUBPROCESS.equals(mode))
				{
					vactivity = new VSubProcess(modelcontainer.getGraph());
				}
				else
				{
					vactivity = new VActivity(modelcontainer.getGraph());
				}
				vactivity.setBpmnElement(mactivity);
				
				Point p = getPointForEvent(cell, e);
				
				Dimension ds = BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.containsKey(mode) ?
							   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(mode) :
							   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(vactivity.getStyle());
				vactivity.setGeometry(new mxGeometry(p.getX() - ds.width * 0.5,
													 p.getY() - ds.height * 0.5,
													 ds.width,
													 ds.height));
				
				if (ModelContainer.EDIT_MODE_TASK.equals(mode))
				{
					vactivity.setValue("Task");
					mactivity.setClazz(new ClassInfo(""));
				}
				else if (mode.endsWith(ModelContainer.THROWING_EVENT))
				{
					mactivity.setThrowing(true);
				}
				
				
				if (cell instanceof VPool)
				{
					MPool mpool = (MPool) ((VNode) cell).getBpmnElement();
					mactivity.setPool(mpool);
				}
				else
				{
					//((MLane) ((VNode) cell).getBpmnElement()).addActivity(mactivity);
					MPool mpool = (MPool) ((VLane) cell).getPool().getBpmnElement();
					mactivity.setPool(mpool);
				}
				
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().addCell(vactivity, (VNode) cell);
				modelcontainer.getGraph().getModel().endUpdate();
			}
			else if (cell == null)
			{
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				return;
			}
		}
		else if (MouseEvent.BUTTON2 == e.getButton())
		{
			setTargetScale(1.0);
		}
	}
	
	/**
	 *  Called when the mouse is pressed.
	 */
	public void mousePressed(MouseEvent e)
	{
		if (MouseEvent.BUTTON3 == e.getButton())
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
					
					/*if (Math.min(scale, targetscale) / Math.max(scale, targetscale) > GuiConstants.ZOOM_ANIMATION_FINAL_RATIO)
					{
						// lock in on final scale
						scale = targetscale;
						zoomtimer.stop();
						zoomtimer = null;
						System.out.println(System.currentTimeMillis() - ts);
					}
					else
					{
						// take a step
						//double ds = ((targetscale - scale) * GuiConstants.ZOOM_ANIMATION_STEP_RATIO);
						double ds = (targetscale - scale) 
						scale = scale + ds;
					}*/
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
						// take a step
						//double ds = ((targetscale - scale) * GuiConstants.ZOOM_ANIMATION_STEP_RATIO);
						scale = (targetscale - zoomdist) + (zoomdist * (Math.log(zoomstep++) / Math.log(steps)));
					}
					
					double rat = scale / oldscale;
					//gc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					//gc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
					JScrollBar vbar = gc.getVerticalScrollBar();
					JScrollBar hbar = gc.getHorizontalScrollBar();
					
					Dimension grapharea = new Dimension(gc.getGraphControl().getMinimumSize());
					//System.out.println("In: " + grapharea + " " + rat);
					
					grapharea.width = (int) Math.round(grapharea.width * rat);
					grapharea.height = (int) Math.round(grapharea.height * rat);
					
					//System.out.println("Out: " + grapharea + " " + rat);
					//view.setEventsEnabled(false);
					//view.scaleAndTranslate(scale, 0, 0);
					view.setScale(scale);
					//gc.refresh();
					//System.out.println(gc.getBounds());
					
					//gc.setMinimumSize(grapharea);
					//gc.setPreferredSize(grapharea);
					((BpmnGraphControl) gc.getGraphControl()).doSetPreferredSize(grapharea);
					((BpmnGraphControl) gc.getGraphControl()).doSetMinimumSize(grapharea);
					gc.getGraphControl().revalidate();
					//System.out.println("PS: " + gc.getGraphControl().getMinimumSize() + " " + rat);
					//gc.setPreferredSize(grapharea);
					
					//gc.extendComponent(grapharea);
					
					int px = (int) Math.round(hbar.getModel().getValue() * rat + hbar.getModel().getExtent() * (rat - 1.0) * center.getX());
					int py = (int) Math.round(vbar.getModel().getValue() * rat + vbar.getModel().getExtent() * (rat - 1.0) * center.getY());
					
					
					
					//vbar.getModel().setMaximum(grapharea.width);
					//hbar.getModel().setMaximum(grapharea.height);
					
					/*view.setEventsEnabled(true);
					view.revalidate();
					//view.fireEvent(new mxEventObject(mxEvent.SCALE, "scale", scale, "previousScale", oldscale));
					view.fireEvent(new mxEventObject(mxEvent.SCALE_AND_TRANSLATE, "scale",
							scale, "previousScale", oldscale, "translate", translate,
							"previousTranslate", previousTranslate));*/
					
					//gc.getGraphControl().setPreferredSize(new Dimension(grapharea.width, grapharea.height));
					//gc.getGraphControl().setMinimumSize(new Dimension(grapharea.width, grapharea.height));
					hbar.getModel().setValue(Math.max(0, px));
					vbar.getModel().setValue(Math.max(0, py));
					
					
					
					//gc.extendComponent(grapharea);
					
					
				}
			});
			zoomtimer.start();
		}
	}
	
	protected Point getPointForEvent(Object parent, MouseEvent e)
	{
		mxPoint p = modelcontainer.getGraphComponent().getPointForEvent(e);
		
		mxCellState pstate = modelcontainer.getGraph().getView().getState(parent);
		if (pstate != null)
		{
			//double scale = modelcontainer.getGraph().getView().getScale();
			//p.setX(p.getX() * scale - (pstate.getOrigin().getX() * scale));
			//p.setY(p.getY() / scale - (pstate.getOrigin().getY() / scale));
			p.setX(p.getX() - pstate.getOrigin().getX());
			p.setY(p.getY() - pstate.getOrigin().getY());
		}
		
		return p.getPoint();
	}
}
