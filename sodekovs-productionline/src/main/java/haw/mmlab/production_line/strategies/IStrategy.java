/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.domain.HelpRequest;

/**
 * Strategy Interface for implementing role change strategies.
 * 
 * @author Peter
 */
public interface IStrategy {

	/**
	 * Returns the maximum escalation level (number of rounds) for this strategy.
	 * 
	 * @return the maximum escalation level (number of rounds) for this strategy
	 */
	public int getMaximumEscalationLevel();

	/**
	 * Evaluates the given {@link HelpRequest} concerning the given information about the receiving agent ({@link AgentData}).
	 * 
	 * @param request
	 *            the given {@link HelpRequest}
	 * @param data
	 *            the given information about the receiving agent
	 * @return the {@link EvaluationResult}
	 */
	public EvaluationResult evaluate(HelpRequest request, AgentData data);
}
