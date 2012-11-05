package jadex.platform.service.bpmnstarter;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  This monitoring service can be used to start bpmn processes
 *  according to its defined start events (currently time, rule).
 */
@Service
public interface IMonitoringStarterService
{
	/**
	 *  Add a bpmn model that is monitored for start events.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 */
	public IFuture<Void> addBpmnModel(@CheckNotNull String model, IResourceIdentifier rid);
	
	/**
	 *  Remove a bpmn model.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 */
	public IFuture<Void> removeBpmnModel(@CheckNotNull String model, IResourceIdentifier urid);

	/**
	 *  Get the currently monitored processes.
	 *  @return The currently observed bpmn models.
	 */
	public IIntermediateFuture<Tuple2<String, IResourceIdentifier>> getBpmnModels();
}
