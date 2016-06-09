package jadex.bpmn.editor.gui.controllers;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.SHelper;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VDataEdge;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VMessagingEdge;
import jadex.bpmn.editor.model.visual.VNamedNode;
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
import jadex.bpmn.model.MTask;
import jadex.bpmn.model.io.IdGenerator;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

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
		modelcontainer.setDirty(true);
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
//			if (!(targetcell instanceof VActivity) ||
//			   !(MBpmnModel.TASK.equals(((MActivity) ((VActivity) targetcell).getBpmnElement()).getActivityType()) ||
//				 MBpmnModel.SUBPROCESS.equals(((MActivity) ((VActivity) targetcell).getBpmnElement()).getActivityType())))
			if (!(targetcell instanceof VActivity) ||
			   !(((VActivity) targetcell).getBpmnElement() instanceof MTask ||
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
		
		if (targetcell instanceof VSubProcess &&
			MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)((VSubProcess) targetcell).getBpmnElement()).getSubprocessType()))
		{
			if (mode.startsWith("EventStart"))
			{
				if (ModelContainer.EDIT_MODE_EVENT_START_EMPTY.equals(mode))
				{
					return null;
				}
//				if (!ModelContainer.EDIT_MODE_EVENT_START_RULE.equals(mode))
//				{
//					List<MActivity> acts = ((MSubProcess)((VSubProcess) targetcell).getBpmnElement()).getActivities();
//					if (acts != null)
//					{
//						for (MActivity act : acts)
//						{
//							if (MBpmnModel.EVENT_START_RULE.equals(act.getActivityType()))
//							{
//								return null;
//							}
//						}
//					}
//				}
//				else
//				{
//					return null;
//				}
			}
		}
		
		MActivity mactivity = null;
		if (mode.startsWith(ModelContainer.EDIT_MODE_SUBPROCESS))
		{
			mactivity = new MSubProcess();
			mactivity.setClazz(new ClassInfo(""));
		}
		else if (ModelContainer.EDIT_MODE_TASK.equals(mode))
		{
			mactivity = new MTask();
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
			mactivity.setName("Ext. Sub-Process");
			UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"\"", null);
			MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
			mactivity.addProperty(mprop);
			vactivity = new VExternalSubProcess(modelcontainer.getGraph());
//			vactivity.setCollapsed(true);
		}
		else if (ModelContainer.EDIT_MODE_EVENT_SUBPROCESS.equals(mode))
		{
			
			if (!(targetcell instanceof VSubProcess))
			{
			mactivity.setActivityType(MBpmnModel.SUBPROCESS);
			vactivity = new VSubProcess(modelcontainer.getGraph());
			((MSubProcess) mactivity).setSubprocessType(MSubProcess.SUBPROCESSTYPE_EVENT);
			}
			else
			{
				return null;
			}
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
		if (ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(mode) ||
			SValidation.areMessageEventsConnectable(source, target))
		{
			if (SValidation.getMessagingEdgeValidationError(source, target) == null)
			{
				if (SValidation.areMessageEventsConnectable(source, target) &&
					SValidation.convertMessageEventsForConnection(source, target))
				{
					mxICell tmp = target;
					target = source;
					source = tmp;
				}
				
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
			
			//TODO: No longer necessary, cleanup?
//			if (((VActivity) source).getParent() instanceof VSubProcess)
//			{
//				((MSubProcess) ((VSubProcess) ((VActivity) source).getParent()).getBpmnElement()).addSequenceEdge(medge);
//			}
//			else
//			{
//				MActivity msrc = (MActivity) ((VActivity) source).getBpmnElement();
//				msrc.getPool().addSequenceEdge(medge);
//			}
			
			VSequenceEdge vedge = new VSequenceEdge(modelcontainer.getGraph());
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
					
					if (modelcontainer.getSettings().isNameTypeDataAutoConnect() &&
						outparam.getName() != null && outparam.getName().equals(inparam.getName()) &&
						outparam.getClazz() != null && outparam.getClazz().getTypeName().equals(inparam.getClazz().getTypeName()))
					{
						VDataEdge vdataedge = createDataEdge(graph, modelcontainer.getIdGenerator(), voutparam, vinparam, false);
						graph.addCell(vdataedge, vdataedge.getEdgeParent());
						break;
					}
				}
			}
			
			modelcontainer.setDirty(true);
			ret = vedge;
		}
		else if (((source instanceof VOutParameter && target instanceof VInParameter) ||
				 ((source instanceof VOutParameter || target instanceof VInParameter) &&
				 (SHelper.isVisualEvent(source) || SHelper.isVisualEvent(target)) ||
				 (SHelper.isVisualEvent(source) || SHelper.isVisualEvent(target)))) &&
				 source != null && target != null)
		{
			ret = createDataEdge(graph, modelcontainer.getIdGenerator(), (VNamedNode) source, (VNamedNode) target, modelcontainer.getSettings().isDirectSequenceAutoConnect());
		}
		
		return ret;
	}
	
	/**
	 *  Creates a control point.
	 */
	public static final void createControlPoint(VEdge vedge, mxPoint mxp, ModelContainer modelcontainer)
	{
		boolean gridstate = modelcontainer.getGraph().isGridEnabled();
		modelcontainer.getGraph().setGridEnabled(false);
//		mxp = modelcontainer.getGraphComponent().getPointForEvent(e, false);
//		p = new Point2D.Double(mxp.getX(), mxp.getY());
		
//		VEdge vedge = (VEdge) cell;
		mxGeometry geo = vedge.getGeometry();
		List<mxPoint> points = (List<mxPoint>) geo.getPoints();
//		mxPoint amxp = (new mxPoint(mxp));
		mxICell parent = null;
//		if (vedge.getSource() != null && vedge.getSource().getParent() != null)
//		{
		parent = vedge.getEdgeParent();
		mxCellState pstate = modelcontainer.getGraph().getView().getState(parent, true);
//			parent = vedge.getSource().getParent();
//			while (parent != null &&
//				   !(parent instanceof VSubProcess) &&
//				   !(parent instanceof VLane) &&
//				   !(parent instanceof VPool))
//			{
//				parent = parent.getParent();
//			}
			
		if (parent != null)
		{
			
			if (pstate != null)
			{
				mxp.setX(mxp.getX() - pstate.getOrigin().getX());
				mxp.setY(mxp.getY() - pstate.getOrigin().getY());
			}
		}
//		}
		
		if (points == null)
		{
			points = new ArrayList<mxPoint>();
			geo.setPoints(points);
		}
		
//		double scale = modelcontainer.getGraph().getView().getScale();
//		mxPoint amxp = (new mxPoint(p.getX() * scale, p.getY() * scale));
		
		if (points.size() == 0)
		{
			points.add(mxp);
		}
		else
		{
			List<mxPoint> newpoints = new ArrayList<mxPoint>(points);
			mxPoint p = new mxPoint(vedge.getSource().getGeometry().getCenterX(), vedge.getSource().getGeometry().getCenterY());
			mxICell pp = vedge.getSource();
			if (pp instanceof VOutParameter)
			{
				pp = pp.getParent();
				p.setX(p.getX() + pp.getGeometry().getX());
				p.setY(p.getY() + pp.getGeometry().getY());
			}
			
			newpoints.add(0, p);
			p = new mxPoint(vedge.getTarget().getGeometry().getCenterX(), vedge.getTarget().getGeometry().getCenterY());
			pp = vedge.getTarget();
			if (pp instanceof VInParameter)
			{
				pp = pp.getParent();
				p.setX(p.getX() + pp.getGeometry().getX());
				p.setY(p.getY() + pp.getGeometry().getY());
			}
			
			newpoints.add(p);
			
//			newpoints.add(new mxPoint(vedge.getTarget().getGeometry().getCenterX(), vedge.getTarget().getGeometry().getCenterY()));
//			modelcontainer.getGraph().getView().getState(vedge, true).
//			if (pstate != null)
//			{
//				newpoints.get(0).setX(newpoints.get(0).getX() + pstate.getOrigin().getX());
//				newpoints.get(0).setY(newpoints.get(0).getY() + pstate.getOrigin().getY());
//				newpoints.get(newpoints.size() - 1).setX(newpoints.get(newpoints.size() - 1).getX() + pstate.getOrigin().getX());
//				newpoints.get(newpoints.size() - 1).setY(newpoints.get(newpoints.size() - 1).getY() + pstate.getOrigin().getY());
//			}
//			int ind = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(cell), amxp.getX(), amxp.getY());
//			int ind = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(vedge), mxp.getX(), mxp.getY());
			double dist2 = Double.MAX_VALUE;
			int ind = -1;
			for (int i = 0; i < newpoints.size() - 1; ++i)
			{
				double d2 = Line2D.ptSegDistSq(newpoints.get(i).getX(), newpoints.get(i).getY(),
						  newpoints.get(i + 1).getX(), newpoints.get(i + 1).getY(), mxp.getX(), mxp.getY());
				if (dist2 > d2)
				{
					dist2 = d2;
					ind = i + 1;
				}
			}
			newpoints.add(ind, mxp);
			newpoints.remove(0);
			newpoints.remove(newpoints.size() - 1);
			points.clear();
			points.addAll(newpoints);
		}
		
		modelcontainer.getGraph().refreshCellView(vedge);
		modelcontainer.getGraph().setSelectionCell(vedge);
		modelcontainer.setDirty(true);
		
		modelcontainer.getGraph().setGridEnabled(gridstate);
		
		modelcontainer.setEditMode(ModelContainer.EDIT_MODE_SELECTION);
	}
	
	/**
	 *  Deletes a control point.
	 */
	public static final void deleteControlPoint(VEdge vedge, mxPoint mxp, ModelContainer modelcontainer)
	{
		boolean gridstate = modelcontainer.getGraph().isGridEnabled();
		modelcontainer.getGraph().setGridEnabled(false);
		
		mxGeometry geo = vedge.getGeometry();
		List<mxPoint> points = (List<mxPoint>) geo.getPoints();
		
		mxICell parent = vedge.getEdgeParent();
			
		if (parent != null)
		{
			mxCellState pstate = modelcontainer.getGraph().getView().getState(parent, true);
			if (pstate != null)
			{
				mxp.setX(mxp.getX() - pstate.getOrigin().getX());
				mxp.setY(mxp.getY() - pstate.getOrigin().getY());
			}
		}
		
		if (points == null)
		{
			points = new ArrayList<mxPoint>();
			geo.setPoints(points);
		}
		
		if (points.size() > 0)
		{
			double dist2 = Double.MAX_VALUE;
			int ind = -1;
			for (int i = 0; i < points.size(); ++i)
			{
				double diffx = points.get(i).getX() - mxp.getX();
				double diffy = points.get(i).getY() - mxp.getY();
				double d2p = Math.abs(diffx * diffx + diffy * diffy);
				if (dist2 > d2p)
				{
					dist2 = d2p;
					ind = i;
				}
			}
//			int ind = mxUtils.findNearestSegment(modelcontainer.getGraph().getView().getState(vedge), mxp.getX(), mxp.getY());
			points.remove(ind);
		}
		
		modelcontainer.getGraph().refreshCellView(vedge);
		modelcontainer.getGraph().setSelectionCell(vedge);
		modelcontainer.setDirty(true);
		
		modelcontainer.getGraph().setGridEnabled(gridstate);
	}
	
	/**
	 *  Creates a data edge.
	 * 
	 * 	@param graph The graph.
	 * 	@param idgenerator The ID generator.
	 * 	@param source Edge source.
	 * 	@param target Edge target.
	 * 	@return The edge.
	 */
	protected static final VDataEdge createDataEdge(final BpmnGraph graph, IdGenerator idgenerator, VNamedNode src, VNamedNode tgt, boolean autoseqedge)
	{
		VActivity tmpvact = null;
		String tparamname = null;
		if (tgt instanceof VInParameter)
		{
			tmpvact = (VActivity) tgt.getParent();
			tparamname = ((VInParameter) tgt).getParameter().getName();
		}
		else
		{
			tmpvact = (VActivity) tgt;
		}
		final VActivity vtactivity = tmpvact;
		
		String sparamname = null;
		if (src instanceof VOutParameter)
		{
			tmpvact = (VActivity) src.getParent();
			sparamname = ((VOutParameter) src).getParameter().getName();
		}
		else
		{
			tmpvact = (VActivity) src;
		}
		VActivity vsactivity = tmpvact;
		
		MActivity sactivity = (MActivity) vsactivity.getBpmnElement();
		MActivity tactivity = (MActivity) vtactivity.getBpmnElement();
		
		MDataEdge dedge = new MDataEdge();
		dedge.setId(idgenerator.generateId());
		dedge.setSource(sactivity);
		dedge.setSourceParameter(sparamname);
		dedge.setTarget(tactivity);
		dedge.setTargetParameter(tparamname);
		
		VDataEdge vedge = new VDataEdge(graph);
		vedge.setBpmnElement(dedge);
		vedge.setSource(src);
		vedge.setTarget(tgt);
		
		graph.delayedRefreshCellView(vtactivity);
		
		if (autoseqedge)
		{
			Set<VNode> rnodes = getReverseSequenceFlowNodes(vtactivity, null, vsactivity);
			if (!rnodes.contains(vsactivity))
			{
				MSequenceEdge medge = new MSequenceEdge();
				medge.setId(idgenerator.generateId());
				VSequenceEdge seqedge = new VSequenceEdge(graph);
				seqedge.setBpmnElement(medge);
				seqedge.setSource(vsactivity);
				seqedge.setTarget(vtactivity);
				graph.addCell(seqedge, seqedge.getEdgeParent());
			}
		}
		
		return vedge;
	}
	
	protected static final Set<VNode> getReverseSequenceFlowNodes(VNode start, Set<VNode> visited, VNode match)
	{
		if (visited == null)
		{
			visited = new HashSet<VNode>();
		}
		
		if (!visited.contains(start))
		{
			visited.add(start);
			
			for (int i = 0; i < start.getEdgeCount(); ++i)
			{
				if (start.getEdgeAt(i) instanceof VSequenceEdge &&
					start.equals(((VSequenceEdge) start.getEdgeAt(i)).getTarget()))
				{
					if (match != null && visited.contains(match))
					{
						return visited;
					}
					
					getReverseSequenceFlowNodes((VNode) ((VSequenceEdge) start.getEdgeAt(i)).getSource(), visited, match);
				}
			}
		}
		
		return visited;
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
