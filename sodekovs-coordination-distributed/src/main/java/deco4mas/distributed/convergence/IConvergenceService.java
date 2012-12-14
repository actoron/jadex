/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import deco.distributed.lang.dynamics.convergence.Adaption;

/**
 * Convergence Service Interface offering methods to vote for or against given {@link Adaption}s of the Coordination Process.
 * 
 * @author Thomas Preisler
 */
public interface IConvergenceService {

	/**
	 * Votes for or against the given {@link Adaption}.
	 * 
	 * @param adaption
	 *            the given Adaption
	 * @param initiator
	 *            the voting initiator
	 * @return {@link Boolean#TRUE} as an {@link IFuture} if the service agrees on the {@link Adaption} or {@link Boolean#FALSE} if not
	 */
	public IFuture<Boolean> vote(Adaption adaption, IComponentIdentifier initiator);

	/**
	 * Used for distributed case: denotes the context of a distributed application. In order to recognize, all instances of a distributed application have to use the same CoordinationCotextID, which
	 * is defined in the *.application.xml.
	 * 
	 * @return the coordination context id
	 */
	public String getCoordinationContextID();

	/**
	 * Resets the values of the constraints referenced in the given {@link Adaption} for the services agent.
	 * 
	 * @param adaption
	 *            the given {@link Adaption}
	 */
	public void resetConstraint(Adaption adaption);
}
