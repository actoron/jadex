package jadex.bpmn.editor.gui.controllers;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;

import com.mxgraph.model.mxICell;

/**
 *  Class for validating edge connections.
 *
 */
public class SEdgeValidation
{
	/**
	 *  Validates an edge connection.
	 *  
	 *  @param edge The proposed edge.
	 *  @param source The proposed source of the edge.
	 *  @param target The proposed target of the edge.
	 *  @return Error message explaining why the connection is invalid or null if valid.
	 */
	public static String getEdgeValidationError(Object edge, Object source,
			Object target)
	{
		String error = null;
		if (edge instanceof VSequenceEdge)
		{
			if (!(source instanceof VActivity) || !(target instanceof VActivity))
			{
				error = "Sequence edges can only connect activities.";
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
					tp = ta;
					while (!(tp instanceof VPool))
					{
						tp = tp.getParent();
					}
				}
				if (tp != sp)
				{
					error = "No sequence edges allowed between pools.";
				}
			}
		}
		
		return error;
	}
}
