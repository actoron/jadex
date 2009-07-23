package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;

import java.util.List;


/**
 *  Handler for xor split and join gateways.
 */
public class GatewayXORActivityHandler implements IActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		List	incoming	= activity.getIncomingSequenceEdges();
		List	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Split
		if(incoming!=null /* && incoming.size()==1 */ && outgoing!=null && outgoing.size()>1)
		{
			MSequenceEdge def = null;
			IValueFetcher fetcher = new ProcessThreadValueFetcher(thread, false, instance.getValueFetcher());
			for(int i=0; i<outgoing.size(); i++)
			{
				// Take first out edge that is satisfied and not the default edge (without condition)
				MSequenceEdge edge = (MSequenceEdge)outgoing.get(i);
				IParsedExpression exp = edge.getCondition();
				if(exp!=null)
				{
					if(((Boolean)exp.getValue(fetcher)).booleanValue())
					{
						thread.setLastEdge(edge);
						def = null;
						break;
					}
				}
				else
				{
					def = edge;
				}
			}
			
			if(def!=null)
			{
				thread.setLastEdge(def);
			}
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Only one thread arrives.
			thread.setLastEdge((MSequenceEdge)outgoing.get(0));
		}
		
		else
		{
			throw new UnsupportedOperationException("Invalid number of edges for xor split/join: "+activity+", "+instance);
		}
	}
}

