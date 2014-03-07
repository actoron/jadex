package jadex.bpmn.runtime;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Services to be called from BPMN process instances
 *  to some super-ordinated process engine, if any.
 */
public interface IInternalProcessEngineService
{
	/**
	 *  Register an event description to be notified, when the event happens.
	 *  @return An id to be used for deregistration.
	 */
	IFuture<String>	addEventMatcher(String[] eventtypes, UnparsedExpression expression, String[] imports, Map<String, Object> params, ICommand<Object> command);
	
	/**
	 *  Register an event description to be notified, when the event happens.
	 */
	IFuture<Void>	removeEventMatcher(String id);
}
