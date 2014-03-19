package jadex.bpmn.runtime;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.annotation.Service;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Services to be called from BPMN process instances
 *  to some super-ordinated process engine, if any.
 */
@Service
public interface IInternalProcessEngineService
{
	/**
	 *  Register an event description to be notified, when the event happens.
	 *  @return An id to be used for deregistration.
	 */
	public IFuture<String>	addEventMatcher(String[] eventtypes, UnparsedExpression expression, String[] imports, 
		Map<String, Object> params, boolean remove, ICommand<Object> command);
	
	/**
	 *  Register an event description to be notified, when the event happens.
	 */
	public IFuture<Void>	removeEventMatcher(String id);
}
