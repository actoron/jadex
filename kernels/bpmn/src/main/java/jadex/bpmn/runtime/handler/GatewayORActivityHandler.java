package jadex.bpmn.runtime.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.IInternalAccess;
import jadex.commons.IValueFetcher;
import jadex.javaparser.IParsedExpression;


/**
 *  Handler for or split and join gateways.
 */
public class GatewayORActivityHandler	extends AbstractGatewayActivityHandler	 implements IActivityHandler
{
	/**
	 *  Perform a split.
	 *  @return All resulting threads after the split.
	 */
	protected Collection<ProcessThread>	performSplit(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
		
		List<ProcessThread> threads = new ArrayList<ProcessThread>();
		IValueFetcher fetcher = new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
		for(int i=0; i<outgoing.size(); i++)
		{
			MSequenceEdge edge = (MSequenceEdge)outgoing.get(i);
			IParsedExpression exp = edge.getParsedCondition();
			boolean follow = true;
			if(exp!=null)
			{
				if(edge.isDefault())
					throw new RuntimeException("Default edge must not have a condition: "+activity+", "+instance+", "+thread+", "+exp);
				follow = isValid(thread, exp, fetcher);
			}

			if(follow)
			{
				if(threads.size()==0)
				{
					thread.setLastEdge((MSequenceEdge)outgoing.get(i));
					threads.add(thread);
				}
				else
				{
					ProcessThread	newthread	= thread.createCopy();
					newthread.setLastEdge((MSequenceEdge)outgoing.get(i));
					thread.getParent().addThread(newthread);
//					ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
//						thread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(newthread));
//					instance.notifyListeners(cce);
					threads.add(newthread);
				}
			}
		}
		
		return threads;
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
	