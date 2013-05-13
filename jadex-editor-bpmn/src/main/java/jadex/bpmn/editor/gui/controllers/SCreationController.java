package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.editor.model.visual.VNode;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MMessagingEdge;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

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
	public static final VPool createPool(ModelContainer modelcontainer, Point2D targetpoint)
	{
		VPool vpool = new VPool(modelcontainer.getGraph());
		
		double x = targetpoint.getX();
		double y = targetpoint.getY();
		if (modelcontainer.getGraph().isGridEnabled())
		{
			x -= targetpoint.getX() % modelcontainer.getGraph().getGridSize();
			y -= targetpoint.getY() % modelcontainer.getGraph().getGridSize();
		}
		
		vpool.setGeometry(new mxGeometry(x, y, BpmnStylesheetColor.DEFAULT_POOL_WIDTH, BpmnStylesheetColor.DEFAULT_POOL_HEIGHT));
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
	public static VActivity createActivity(ModelContainer modelcontainer, String mode, Object targetcell, Point2D tp, boolean xcenter)
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
			UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"\"", null);
			MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
			mactivity.addProperty(mprop);
			vactivity = new VExternalSubProcess(modelcontainer.getGraph());
			vactivity.setCollapsed(true);
		}
		else
		{
			vactivity = new VActivity(modelcontainer.getGraph());
		}
		vactivity.setBpmnElement(mactivity);
		
		Dimension ds = BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.containsKey(mactivity.getActivityType()) ?
				   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(mactivity.getActivityType()) :
				   BpmnStylesheetColor.DEFAULT_ACTIVITY_SIZES.get(vactivity.getStyle());
		
		Point p = new Point();
		
		if (!mode.endsWith(ModelContainer.BOUNDARY_EVENT))
		{
			p = adjustPoint(modelcontainer.getGraph(), targetcell, new mxPoint(tp.getX(), tp.getY())).getPoint();
			
			if (xcenter)
			{
				p.x -= ds.width * 0.5;
			}
			p.y -= ds.height * 0.5;
			
			if (modelcontainer.getGraph().isGridEnabled())
			{
				p.x = p.x - (p.x % modelcontainer.getGraph().getGridSize());
				p.y = p.y - (p.y % modelcontainer.getGraph().getGridSize());
			}
		}
		
		vactivity.setGeometry(new mxGeometry(p.getX(),
											 p.getY(),
											 ds.width,
											 ds.height));
		
		if (BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(vactivity.getStyle()) ||
			BpmnStylesheetColor.COLLAPSED_SIZES.containsKey(mactivity.getActivityType()))
		{
			Dimension ads = (Dimension) (BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) != null?
				BpmnStylesheetColor.COLLAPSED_SIZES.get(vactivity.getStyle()) :
				BpmnStylesheetColor.COLLAPSED_SIZES.get(mactivity.getActivityType()));
			vactivity.getGeometry().setAlternateBounds(
				new mxGeometry(p.getX(),
					 		   p.getY(),
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
		
		modelcontainer.getGraph().getModel().beginUpdate();
		modelcontainer.getGraph().addCell(vactivity, (VNode) targetcell);
		modelcontainer.getGraph().getModel().endUpdate();
		
		modelcontainer.setDirty(true);
			
		if (!ModelContainer.EDIT_MODE_TASK.equals(mode))
		{
			modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
		}
		
		return vactivity;
	}
	
	/**
	 *  Creates a connection.
	 *  
	 *  @param src Source object.
	 *  @param tgt Target object.
	 *  @return Created edge.
	 */
	public static final mxICell createConnection(BpmnGraph graph, String mode, Object src, Object tgt, long timestamp)
	{
		ModelContainer modelcontainer = graph.getModelContainer();
		mxICell ret = null;
		mxICell source = (mxICell) src;
		mxICell target = (mxICell) tgt;
		if (ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(mode))
		{
			if (SValidation.getMessagingEdgeValidationError(source, target) == null)
			{
				MMessagingEdge medge = new MMessagingEdge();
				medge.setId(modelcontainer.getIdGenerator().generateId());
				MActivity sact = (MActivity) ((VActivity) source).getBpmnElement();
				MActivity tact = (MActivity) ((VActivity) target).getBpmnElement();
				medge.setSource(sact);
				medge.setTarget(tact);
				sact.addOutgoingMessagingEdge(medge);
				tact.addIncomingMessagingEdge(medge);
				
				VMessagingEdge vedge = new VMessagingEdge(graph);
				vedge.setSource(source);
				vedge.setTarget(target);
				vedge.setBpmnElement(medge);
				
				ret = vedge;
			}
			
			modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
		}
		else if (source instanceof VActivity && target instanceof VActivity)
		{
			if (src.equals(tgt) &&
				System.currentTimeMillis() - timestamp < 2000)
			{
				graph.refreshCellView((mxICell) src);
				return null;
			}
			MSequenceEdge medge = new MSequenceEdge();
			medge.setId(modelcontainer.getIdGenerator().generateId());
			
			if (((VActivity) source).getParent() instanceof VSubProcess)
			{
				((MSubProcess) ((VSubProcess) ((VActivity) source).getParent()).getBpmnElement()).addSequenceEdge(medge);
			}
			else
			{
				MActivity msrc = (MActivity) ((VActivity) source).getBpmnElement();
				msrc.getPool().addSequenceEdge(medge);
			}
			
			VSequenceEdge vedge = new VSequenceEdge(modelcontainer.getGraph(), VSequenceEdge.class.getSimpleName());
			vedge.setBpmnElement(medge);
			vedge.setSource(source);
			vedge.setTarget(target);
			
			VActivity vsrc = (VActivity) source;
			VActivity vtgt = (VActivity) target;
			
			List<VOutParameter> unconnectedsrc = new ArrayList<VOutParameter>();
			for (int i = 0; i < vsrc.getChildCount(); ++i)
			{
				if (vsrc.getChildAt(i) instanceof VOutParameter && vsrc.getChildAt(i).getEdgeCount() == 0)
				{
					unconnectedsrc.add((VOutParameter) vsrc.getChildAt(i));
				}
			}
			
			List<VInParameter> unconnectedtgt = new ArrayList<VInParameter>();
			for (int i = 0; i < vtgt.getChildCount(); ++i)
			{
				if (vtgt.getChildAt(i) instanceof VInParameter && vtgt.getChildAt(i).getEdgeCount() == 0)
				{
					unconnectedtgt.add((VInParameter) vtgt.getChildAt(i));
				}
			}
			
			for (VOutParameter voutparam : unconnectedsrc)
			{
				for (VInParameter vinparam : unconnectedtgt)
				{
					MParameter outparam = voutparam.getParameter();
					MParameter inparam = vinparam.getParameter();
					
					if (outparam.getName() != null && outparam.getName().equals(inparam.getName()) &&
						outparam.getClazz() != null && outparam.getClazz().getTypeName().equals(inparam.getClazz().getTypeName()))
					{
						VDataEdge vdataedge = createDataEdge(graph, voutparam, vinparam);
						graph.addCell(vdataedge);
						break;
					}
				}
			}
			
			modelcontainer.setDirty(true);
			ret = vedge;
		}
		else if (source instanceof VOutParameter && target instanceof VInParameter)
		{
			ret = createDataEdge(graph, (VOutParameter) source, (VInParameter) target);
		}
		
		return ret;
	}
	
	protected static final VDataEdge createDataEdge(BpmnGraph graph, VOutParameter source, VInParameter target)
	{
		MActivity sactivity = (MActivity) ((VActivity) source.getParent()).getBpmnElement();
		MActivity tactivity = (MActivity) ((VActivity) target.getParent()).getBpmnElement();
		
		MDataEdge dedge = new MDataEdge();
		dedge.setSource(sactivity);
		dedge.setSourceParameter(source.getParameter().getName());
		dedge.setTarget(tactivity);
		dedge.setTargetParameter(target.getParameter().getName());
		sactivity.addOutgoingDataEdge(dedge);
		tactivity.addIncomingDataEdge(dedge);
		
		VDataEdge vedge = new VDataEdge(graph);
		vedge.setBpmnElement(dedge);
		vedge.setSource(source);
		vedge.setTarget(target);
		
		return vedge;
	}
	
	/**
	 *  Adjusts a point for relative positioning.
	 *  
	 *  @param modelcontainer The model container.
	 *  @param parent The parent cell.
	 *  @param point The unadjusted targeted point.
	 *  @return The adjusted point.
	 */
	protected static final mxPoint adjustPoint(mxGraph graph, Object parent, mxPoint point)
	{
		mxPoint p = point;
		
		mxCellState pstate = graph.getView().getState(parent);
		if (pstate != null)
		{
			p.setX(p.getX() - pstate.getOrigin().getX());
			p.setY(p.getY() - pstate.getOrigin().getY());
		}
		
		return p;
	}
}
