package jadex.platform.service.cron.bpmn;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public interface IStartService
{
	/**
	 *  Add a bpmn model that is monitored for start events.
	 */
	public IFuture<Void> addBpmnModel(@CheckNotNull String model, IResourceIdentifier rid);
	
	/**
	 *  Remove a bpmn model.
	 */
	public IFuture<Void> removeBpmnModel(@CheckNotNull String model, IResourceIdentifier urid);
}
