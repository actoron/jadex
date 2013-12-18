package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSubProcess;

import com.mxgraph.model.mxICell;

/**
 *  Class for validating operations.
 *
 */
public class SValidation
{
	public static String getMoveValidationError(Object[] cells, Object target)
	{
		for (int i = 0; i < cells.length; ++i)
		{
			if (cells[i] instanceof VActivity)
			{
				VActivity vactivity = (VActivity) cells[i];
				MActivity mactivity = (MActivity) vactivity.getBpmnElement();
				
				int sedgecount = mactivity.getOutgoingSequenceEdges() != null? mactivity.getOutgoingSequenceEdges().size() : 0;
				sedgecount += mactivity.getIncomingSequenceEdges() != null? mactivity.getIncomingSequenceEdges().size() : 0;
				
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
					
					if (vactivity.getParent() instanceof VSubProcess && sedgecount > 0)
					{
						return "Activities transferred out of sub-processes cannot have sequence edges.";
					}
					
					// Only allow cross-pool activity transfers if no sequence edges are connected
					if (!targetpool.getBpmnElement().equals(mactivity.getPool()) && sedgecount > 0)
					{
						return "Activities transferred between pools cannot have sequence edges.";
					}
				}
				else if (target instanceof VSubProcess)
				{
					MSubProcess msubproc = (MSubProcess) ((VSubProcess) target).getBpmnElement();
					
					if (!msubproc.getPool().getId().equals(mactivity.getPool().getId()) && sedgecount > 0)
					{
						return "Activities transferred between pools cannot have sequence edges."; 
					}
					
					if (!(msubproc.getActivities() != null &&
						msubproc.getActivities().contains(mactivity)) &&
						sedgecount > 0)
					{
						return "Activities transferred into sub-processes cannot have sequence edges.";
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
			if ((sact.getActivityType().startsWith("Event") &&
				sact.getActivityType().endsWith("Message") &&
				sact.isThrowing() &&
				tact.getActivityType().startsWith("Event") &&
				tact.getActivityType().endsWith("Message") &&
				!tact.isThrowing()) ||
				MBpmnModel.TASK.equals(sact.getActivityType()) &&
				MBpmnModel.TASK.equals(tact.getActivityType()))
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
		if (!(source instanceof VOutParameter) || !(target instanceof VInParameter))
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
		
		if (error == null)
		{
			if (((VInParameter) target).getEdgeCount() > 0)
			{
				error = "Only one incoming data edge allowed.";
			}
		}
		
		return error;
	}
}
