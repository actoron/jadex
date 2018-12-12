package jadex.bpmn.features.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.MonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;

/**
 *  Overrides the monitoring feature to add the logic for current state.
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
		List<IMonitoringEvent> ret = super.getCurrentStateEvents();
		if(ret==null)
			ret = new ArrayList<IMonitoringEvent>();
		
		IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getFeature(IBpmnComponentFeature.class);

		for(Iterator<ProcessThread> it= bcf.getTopLevelThread().getAllThreads().iterator(); it.hasNext(); )
		{
			ret.add(bcf.createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, it.next()));
		}
		return ret;
	}
}
