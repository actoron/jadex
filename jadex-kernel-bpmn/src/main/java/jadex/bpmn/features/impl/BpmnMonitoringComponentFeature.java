package jadex.bpmn.features.impl;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.MonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class BpmnMonitoringComponentFeature extends MonitoringComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public BpmnMonitoringComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Get the current state as monitoring events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);

		final List<IMonitoringEvent>	events	= new ArrayList<IMonitoringEvent>();
		for(Iterator<ProcessThread> it= bcf.getTopLevelThread().getAllThreads().iterator(); it.hasNext(); )
		{
			events.add(bcf.createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, it.next()));
		}
		return events;
	}
}
