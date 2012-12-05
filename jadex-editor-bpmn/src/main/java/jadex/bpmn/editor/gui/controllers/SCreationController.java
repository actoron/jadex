package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VNode;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
	 *  Creates a new pool.
	 *  
	 *  @param modelcontainer The model container.
	 *  @param targetpoint The targeted point for the pool.
	 *  @return The created pool.
	 */
	public static final VPool createPool(ModelContainer modelcontainer, Point targetpoint)
	{
		VPool vpool = new VPool(modelcontainer.getGraph());
		vpool.setGeometry(new mxGeometry(targetpoint.getX(), targetpoint.getY(), BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
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
		
		return vpool;
	}
	
	/**
	 *  Creates a new lane.
	 *  
	 *  @param modelcontainer The model container.
	 *  @param targetcell The parent cell.
	 *  @return The created lane.
	 */
	public static final VLane createLane(ModelContainer modelcontainer, Object targetcell)
	{
		// Special treatment for lanes, only add to pools, do not add to pools with stuff in it.
		VPool vpool = null;
		if (targetcell instanceof VPool)
		{
			vpool = (VPool) targetcell;
		}
		else
		{
			vpool = (VPool) ((VLane) targetcell).getParent();
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
		return vlane;
	}
	
	/**
	 *  Creates an activity.
	 *  
	 *  @param modelcontainer The model container.
	 *  @param mode The activity edit mode.
	 *  @param targetcell The parent cell.
	 *  @param targetpoint The targeted point for the activity.
	 *  @return The created activity.
	 */
	public static VActivity createActivity(ModelContainer modelcontainer, String mode, Object targetcell, Point targetpoint)
	{
		if (mode.endsWith(ModelContainer.BOUNDARY_EVENT))
		{
			if (!(targetcell instanceof VActivity) ||
			   !(MBpmnModel.TASK.equals(((MActivity) ((VActivity) targetcell).getBpmnElement()).getActivityType()) ||
				 MBpmnModel.SUBPROCESS.equals(((MActivity) ((VActivity) targetcell).getBpmnElement()).getActivityType())))
			{
				modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
				return null;
			}
		}
		else if (targetcell instanceof VPool)
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
		
		if (modelcontainer.getGraph().isGridEnabled())
		{
			p.x = p.x - (p.x % modelcontainer.getGraph().getGridSize());
			p.y = p.y - (p.y % modelcontainer.getGraph().getGridSize());
		}
		
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
		
		if (mode.endsWith(ModelContainer.BOUNDARY_EVENT))
		{
			mactivity.setEventHandler(true);
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
	 *  @param modelcontainer The model container.
	 *  @param parent The parent cell.
	 *  @param point The unadjusted targeted point.
	 *  @return The adjusted point.
	 */
	protected static final Point adjustPoint(ModelContainer modelcontainer, Object parent, Point point)
	{
		mxPoint p = new mxPoint(point);
		
		mxCellState pstate = modelcontainer.getGraph().getView().getState(parent);
		if (pstate != null)
		{
			p.setX(p.getX() - pstate.getOrigin().getX());
			p.setY(p.getY() - pstate.getOrigin().getY());
		}
		
		return p.getPoint();
	}
}
