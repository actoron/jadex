package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VNode;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;

import java.awt.Dimension;
import java.awt.Point;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

/**
 *  Methods for creating model objects.
 *
 */
public class SCreationController
{
	/**
	 *  Creates an activity.
	 */
	public static VActivity createActivity(ModelContainer modelcontainer, String mode, Object targetcell, Point targetpoint)
	{
		if (targetcell instanceof VPool)
		{
			if (((VPool) targetcell).hasLanes())
			{
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				return null;
			}
		}
		else if (!((targetcell instanceof VLane) ||
				((targetcell instanceof VSubProcess) && !((VSubProcess) targetcell).isCollapsed())))
		{
			modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
			return null;
		}
		
		MActivity mactivity = null;
		if (mode != null && mode.startsWith(ModelContainer.EDIT_MODE_SUBPROCESS))
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
		else if (ModelContainer.EDIT_MODE_EXTERNAL_SUBPROCESS.equals(mode))
		{
			mactivity.setName("External Sub-Process");
			mactivity.setPropertyValue("file", "");
			vactivity = new VExternalSubProcess(modelcontainer.getGraph());
			vactivity.setCollapsed(true);
		}
		else
		{
			vactivity = new VActivity(modelcontainer.getGraph());
		}
		vactivity.setBpmnElement(mactivity);
		
		Point p = adjustPoint(modelcontainer, targetcell, targetpoint);
		
		Dimension ds = BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.containsKey(mactivity.getActivityType()) ?
					   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(mactivity.getActivityType()) :
					   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(vactivity.getStyle());
		vactivity.setGeometry(new mxGeometry(p.getX() - ds.width * 0.5,
											 p.getY() - ds.height * 0.5,
											 ds.width,
											 ds.height));
		
		if (BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(vactivity.getStyle()) ||
			BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(mactivity.getActivityType()))
		{
			Dimension ads = (Dimension) (BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) != null?
				BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) :
				BpmnStylesheetColor.COLLAPSED_SIZES.get(mactivity.getActivityType()));
			vactivity.getGeometry().setAlternateBounds(
				new mxGeometry(p.getX() - ads.width * 0.5,
					 		   p.getY() - ads.height * 0.5,
					 		   ads.width,
					 		   ads.height));
		}
		
		if (ModelContainer.EDIT_MODE_TASK.equals(mode))
		{
			vactivity.setValue("Task");
			mactivity.setClazz(new ClassInfo(""));
		}
		else if (mode.endsWith(ModelContainer.THROWING_EVENT))
		{
			mactivity.setThrowing(true);
		}
		
		
//		if (cell instanceof VPool)
//		{
//			MPool mpool = (MPool) ((VNode) cell).getBpmnElement();
//			mactivity.setPool(mpool);
//		}
//		else
//		{
//			//((MLane) ((VNode) cell).getBpmnElement()).addActivity(mactivity);
//			MPool mpool = (MPool) ((VLane) cell).getPool().getBpmnElement();
//			mactivity.setPool(mpool);
//		}
		
		modelcontainer.getGraph().getModel().beginUpdate();
		modelcontainer.getGraph().addCell(vactivity, (VNode) targetcell);
		modelcontainer.getGraph().getModel().endUpdate();
			
		if (!ModelContainer.EDIT_MODE_TASK.equals(mode))
		{
			modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
		}
		
		return vactivity;
	}
	
	/**
	 *  Adjusts a point for relative positioning.
	 *  
	 */
	protected static final Point adjustPoint(ModelContainer modelcontainer, Object parent, Point point)
	{
		//mxPoint p = modelcontainer.getGraphComponent().getPointForEvent(e);
		mxPoint p = new mxPoint(point);
		
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
