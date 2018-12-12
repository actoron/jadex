package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IValueFetcher;
import jadex.javaparser.IParsedExpression;


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
	public void execute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		// Notify listeners as gateways are not followed by step handler execution
		if(instance.getFeature0(IMonitoringComponentFeature.class)!=null && instance.getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{
			instance.getFeature(IMonitoringComponentFeature.class).publishEvent(DefaultActivityHandler.getBpmnFeature(instance).createActivityEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread, activity), PublishTarget.TOALL);
		}
		
		List<MSequenceEdge>	incoming	= activity.getIncomingSequenceEdges();
		List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Split
		if(incoming!=null /* && incoming.size()==1 */ && outgoing!=null && outgoing.size()>1)
		{
			MSequenceEdge def = null;
			IValueFetcher fetcher = new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
			for(int i=0; i<outgoing.size(); i++)
			{
				// Take first out edge that is satisfied and not the default edge (without condition)
				MSequenceEdge edge = (MSequenceEdge)outgoing.get(i);
				IParsedExpression exp = (IParsedExpression)edge.getParsedCondition();
				if(exp!=null)
				{
					if(edge.isDefault())
						throw new RuntimeException("Default edge must not have a condition: "+activity+", "+instance+", "+thread+", "+exp);
					if(isValid(thread, exp, fetcher))
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
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
	}
	
	/**
	 *  Safely evaluate a branch expression.
	 */
	protected boolean isValid(ProcessThread thread, IParsedExpression exp, IValueFetcher fetcher)
	{
		boolean ret = false;
		try
		{
//			System.out.println("Evaluating: "+thread.getInstance()+", "+exp.getExpressionText());
			ret = ((Boolean)exp.getValue(fetcher)).booleanValue();
//			System.out.println("Evaluated: "+ret);
		}
		catch(Exception e)
		{
			thread.getInstance().getLogger().warning("Error in branch condition: "+thread+", "+exp+", "+e);
//			e.printStackTrace();
		}
		return ret;
	}
}

