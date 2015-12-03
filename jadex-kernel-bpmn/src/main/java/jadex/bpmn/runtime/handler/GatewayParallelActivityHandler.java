package jadex.bpmn.runtime.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;


/**
 *  Handler for parallel split and join gateways.
 */
public class GatewayParallelActivityHandler	extends AbstractGatewayActivityHandler	implements IActivityHandler
{
	/**
	 *  Perform a split.
	 *  @return All resulting threads after the split.
	 */
	protected Collection<ProcessThread>	performSplit(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		Collection<ProcessThread>	threads	= new ArrayList<ProcessThread>();
		
		List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
		
		for(int i=0; i<outgoing.size(); i++)
		{
			if(i==0)
			{
				thread.setLastEdge((MSequenceEdge)outgoing.get(i));
				threads.add(thread);
			}
			else
			{
				ProcessThread	newthread	= thread.createCopy();
				newthread.setLastEdge((MSequenceEdge)outgoing.get(i));
				thread.getParent().addThread(newthread);
				threads.add(newthread);
//				ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
//					thread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(newthread));
//				instance.notifyListeners(cce);
			}
		}
		
		return threads;
	}
}
