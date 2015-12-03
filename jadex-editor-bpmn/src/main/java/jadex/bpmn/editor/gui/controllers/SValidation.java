package jadex.bpmn.editor.gui.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mxgraph.model.mxICell;

import jadex.bpmn.editor.gui.SHelper;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.MTask;

/**
 *  Class for validating operations.
 *
 */
public class SValidation
{
	public static String getMoveValidationError(Object[] cells, Object target)
	{
		boolean hasoutedge = false;
		Set<MActivity> acts = new HashSet<MActivity>();
		for (int i = 0; i < cells.length; ++i)
		{
			if (cells[i] instanceof VActivity)
			{
				acts.add((MActivity) ((VActivity) cells[i]).getBpmnElement());
			}
		}
		
		edgesearch:
		for (MActivity mactivity : acts)
		{
			List<MSequenceEdge> edges = mactivity.getOutgoingSequenceEdges();
			if (edges != null)
			{
				edgeloop:
				for (MSequenceEdge edge : edges)
				{
					if (!acts.contains(edge.getTarget()))
					{
						for (MActivity pact : acts)
						{
							// check if the source is a handler that is contained in a parent in the selected group.
							if (pact.getEventHandlers() != null && pact.getEventHandlers().contains(edge.getSource()))
							{
								continue edgeloop;
							}
						}
						hasoutedge = true;
						break edgesearch;
					}
				}
			}
			
			edges = mactivity.getIncomingSequenceEdges();
			if (edges != null)
			{
				edgeloop:
				for (MSequenceEdge edge : edges)
				{
					if (!acts.contains(edge.getSource()))
					{
						if (edge.getSource().isEventHandler())
						{
							for (MActivity pact : acts)
							{
								// check if the source is a handler that is contained in a parent in the selected group.
								if (pact.getEventHandlers() != null && pact.getEventHandlers().contains(edge.getSource()))
								{
									continue edgeloop;
								}
							}
						}
						hasoutedge = true;
						break edgesearch;
					}
				}
			}
		}
		
		for (int i = 0; i < cells.length; ++i)
		{
			if (cells[i] instanceof VActivity)
			{
				VActivity vactivity = (VActivity) cells[i];
				MActivity mactivity = (MActivity) vactivity.getBpmnElement();
				
				if (target instanceof VLane || target instanceof VPool)
				{
					VPool targetpool = null;
					if (target instanceof VLane)
					{
						targetpool = ((VLane) target).getPool();
					}
					else
					{
						targetpool = (VPool) target;
						if (targetpool.hasLanes())
						{
							return "Activity cannot be added to a pool containing lanes.";
						}
					}
					
					if (vactivity.getParent() instanceof VSubProcess && hasoutedge)
					{
						return "Activities transferred out of sub-processes cannot have sequence edges.";
					}
					
					// Only allow cross-pool activity transfers if no sequence edges are connected
					if (!targetpool.getBpmnElement().equals(mactivity.getPool()) && hasoutedge)
					{
						return "Activities transferred between pools cannot have sequence edges.";
					}
				}
				else if (target instanceof VSubProcess)
				{
					MSubProcess msubproc = (MSubProcess) ((VSubProcess) target).getBpmnElement();
					
					if (!msubproc.getPool().getId().equals(mactivity.getPool().getId()) && hasoutedge)
					{
						return "Activities transferred between pools cannot have sequence edges."; 
					}
					
					if (!(msubproc.getActivities() != null &&
						msubproc.getActivities().contains(mactivity)) &&
						hasoutedge)
					{
						return "Activities transferred into sub-processes cannot have sequence edges.";
					}
					
					if (MSubProcess.SUBPROCESSTYPE_EVENT.equals(msubproc.getSubprocessType()) &&
						MBpmnModel.EVENT_START_EMPTY.equals(mactivity.getActivityType()))
					{
						return "Empty start events not allowed in event subprocesses.";
					}
				}
				else
				{
					return "Activities must belong to pools, lanes or sub-processes.";
				}
			}
			else if (cells[i] instanceof VLane)
			{
				VLane vlane = (VLane) cells[i];
				if (vlane.getPool() != target)
				{
					return "Lanes can only be moved within pools.";
				}
			}
			else if (cells[i] instanceof VPool)
			{
				if (target != cells[i] && target != null)
				{
					return "Pools cannot have parents.";
				}
			}
		}
		return null;
	}
	
	/**
	 *  Validates a sequence edge connection.
	 *  
	 *  @param source The proposed source of the edge.
	 *  @param target The proposed target of the edge.
	 *  @return Error message explaining why the connection is invalid or null if valid.
	 */
	public static String getSequenceEdgeValidationError(Object source, Object target)
	{
		String error = null;
		if (!(source instanceof VActivity) || !(target instanceof VActivity))
		{
			error = "Sequence edges can only connect activities.";
		}
		else if (((MActivity) ((VActivity) target).getBpmnElement()).isEventHandler())
		{
			error = "Event handlers can only be sources for sequence edges.";
		}
		else if (((MActivity) ((VActivity) source).getBpmnElement()).getActivityType() != null &&
				 ((MActivity) ((VActivity) source).getBpmnElement()).getActivityType().startsWith("EventEnd"))
		{
			error = "End events cannot be the source of a sequence.";
		}
		else if (((MActivity) ((VActivity) target).getBpmnElement()).getActivityType() != null &&
				 ((MActivity) ((VActivity) target).getBpmnElement()).getActivityType().startsWith("EventStart"))
		{
			error = "Start events cannot be the target of a sequence.";
		}
		else if (SHelper.isEventSubProcess(target))
		{
			error = "Event subprocesses cannot be targeted with sequence edges.";
		}
		else
		{
			VActivity sa = (VActivity) source;
			VActivity ta = (VActivity) target;
			mxICell sp = sa;
			while (!(sp instanceof VPool))
			{
				sp = sp.getParent();
			}
			mxICell tp = ta;
			if (tp != null)
			{
				while (!(tp instanceof VPool))
				{
					tp = tp.getParent();
				}
			}
			if (!tp.equals(sp))
			{
				error = "No sequence edges allowed between pools.";
			}
			
			mxICell sparent = sa.getParent();
			mxICell tparent = ta.getParent();
			
			if (((MActivity) ((VActivity) source).getBpmnElement()).isEventHandler())
			{
				if (sparent.equals(ta))
				{
					//FIXME: allow loops
					return "";
				}
				sparent = sparent.getParent();
			}
			
			if (error == null && (sparent instanceof VSubProcess || tparent instanceof VSubProcess) && !sparent.equals(tparent))
			{
				error = "No direct sequence edges allowed across subprocesses.";
			}
		}
		
		return error;
	}
	
	/**
	 *  Validates a messaging edge connection.
	 *  
	 *  @param source The proposed source of the edge.
	 *  @param target The proposed target of the edge.
	 *  @return Error message explaining why the connection is invalid or null if valid.
	 */
	public static String getMessagingEdgeValidationError(Object source, Object target)
	{
		if (source instanceof VActivity && target instanceof VActivity)
		{
			MActivity sact = (MActivity) ((VActivity) source).getBpmnElement();
			MActivity tact = (MActivity) ((VActivity) target).getBpmnElement();
//			if (((sact.getActivityType().startsWith("Event") &&
//				sact.getActivityType().endsWith("Message") &&
//				sact.isThrowing() &&
//				tact.getActivityType().startsWith("Event") &&
//				tact.getActivityType().endsWith("Message") &&
//				!tact.isThrowing()) ||
//				MBpmnModel.TASK.equals(sact.getActivityType()) &&
//				MBpmnModel.TASK.equals(tact.getActivityType())) ||
//				SValidation.areMessageEventsConnectable(source, target))
			if (((sact.getActivityType().startsWith("Event") &&
					sact.getActivityType().endsWith("Message") &&
					sact.isThrowing() &&
					tact.getActivityType().startsWith("Event") &&
					tact.getActivityType().endsWith("Message") &&
					!tact.isThrowing()) ||
					sact instanceof MTask &&
					tact instanceof MTask) ||
					SValidation.areMessageEventsConnectable(source, target))
			{
				return null;
			}
		}
		return "Message edges can only be drawn between throwing message events and non-throwing message events.";
	}
	
	/**
	 *  Validates a data edge connection.
	 *  
	 *  @param source The proposed source of the edge.
	 *  @param target The proposed target of the edge.
	 *  @return Error message explaining why the connection is invalid or null if valid.
	 */
	public static String getDataEdgeValidationError(Object source, Object target)
	{
		String error = null;
//		if (!(source instanceof VOutParameter) || !(target instanceof VInParameter))
		if (!((source instanceof VOutParameter && target instanceof VInParameter) ||
			 ((source instanceof VOutParameter || target instanceof VInParameter) &&
			  (SHelper.isVisualEvent(source) || SHelper.isVisualEvent(target)) ||
			 (SHelper.isVisualEvent(source) || SHelper.isVisualEvent(target)))))
		{
			error = "Data edges can only connect an output parameter with an input parameter.";
		}
		
		if (error == null)
		{
			mxICell sp = (mxICell) source;
			while (!(sp instanceof VPool))
			{
				sp = sp.getParent();
			}
			mxICell tp = (mxICell) target;
			if (tp != null)
			{
				while (!(tp instanceof VPool))
				{
					tp = tp.getParent();
				}
			}
			if (tp != null && !tp.equals(sp))
			{
				error = "No data edges allowed between pools.";
			}
			else if (tp == null)
			{
				error = "Unknown target pool for data edge.";
			}
		}
		
//		if (error == null)
//		{
//			if (((mxICell) target).getEdgeCount() > 0)
//			{
//				error = "Only one incoming data edge allowed.";
//			}
//		}
		
		return error;
	}
	
	/**
	 *  Returns if one of two message events are connectable directly or through conversion to throwing.
	 *  
	 *  @param evt1 First event.
	 *  @param evt2 Second event.
	 *  @return True if they are connectable in some direction.
	 */
	public static final boolean areMessageEventsConnectable(Object source, Object target)
	{
		return (areMessageEventsConnectableInThisDirection(source, target) ||
				areMessageEventsConnectableInThisDirection(target, source));
	}
	
	/**
	 *  Returns if one of two message events are connectable directly or through conversion to throwing
	 *  in the given direction.
	 *  
	 *  @param evt1 First event.
	 *  @param evt2 Second event.
	 *  @return True if they are connectable in the given direction.
	 */
	public static final boolean areMessageEventsConnectableInThisDirection(Object source, Object target)
	{
		boolean ret = false;
		if (source instanceof VActivity && target instanceof VActivity)
		{
			VActivity evt1 = (VActivity) source;
			VActivity evt2 = (VActivity) target;
			if (evt1.getBpmnElement() != null && evt2.getBpmnElement() != null)
			{
				MActivity mevt1 = (MActivity) evt1.getBpmnElement();
				MActivity mevt2 = (MActivity) evt2.getBpmnElement();
				
				if (mevt1.getActivityType() != null && mevt1.getActivityType().endsWith("Message") &&
					mevt2.getActivityType() != null && mevt2.getActivityType().endsWith("Message"))
				{
					boolean conv1 = mevt1.isThrowing();
					conv1 |= (mevt1.getIncomingMessagingEdges() == null? 0 : mevt1.getIncomingMessagingEdges().size()) == 0;
					boolean conv2 = !mevt2.isThrowing();
					conv2 |= (mevt1.getOutgoingMessagingEdges() == null? 0 : mevt1.getOutgoingMessagingEdges().size()) == 0;
					ret = conv1 && conv2 && (mevt1.getPool() != mevt2.getPool());
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Returns if one of two message events are connectable directly or through conversion to throwing
	 *  in the given direction.
	 *  
	 *  @param source First event.
	 *  @param target Second event.
	 *  @return True if the direction needs to be flipped.
	 */
	public static final boolean convertMessageEventsForConnection(Object source, Object target)
	{
		boolean ret = false;
		
		if (areMessageEventsConnectableInThisDirection(target, source) &&
			!((MActivity) (((VActivity) source).getBpmnElement())).isThrowing() &&
			((MActivity) (((VActivity) target).getBpmnElement())).isThrowing())
		{
			VActivity tmp = (VActivity) source;
			source = target;
			target = tmp;
			ret = true;
		}
		
		if (!areMessageEventsConnectable(source, target))
		{
			if (areMessageEventsConnectable(target, source))
			{
				VActivity tmp = (VActivity) source;
				source = target;
				target = tmp;
				ret = true;
			}
			else
			{
				throw new RuntimeException("Cannot convert events: " + source + " " + target);
			}
		}
		
		MActivity mevt1 = (MActivity) ((VActivity) source).getBpmnElement();
		MActivity mevt2 = (MActivity) ((VActivity) target).getBpmnElement();
		
		if (!mevt1.isThrowing())
		{
			mevt1.setThrowing(true);
		}
		
		if (mevt2.isThrowing())
		{
			mevt2.setThrowing(false);
		}
		
		return ret;
	}
}
