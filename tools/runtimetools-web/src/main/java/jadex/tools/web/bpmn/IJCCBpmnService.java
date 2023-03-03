package jadex.tools.web.bpmn;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the jcc security service.
 */
@Service(system=true)
public interface IJCCBpmnService extends IJCCPluginService
{
	/**
	 *  Get all available BPMN models.
	 *  
	 *  @return The BPMN models.
	 */
	public IFuture<String[]> getBpmnModels();
	
	/**
	 *  Get all available Jadex service interfaces.
	 *  @return List of Jadex service interfaces.
	 */
	public IFuture<String[]> getServiceInterfaces();
}
