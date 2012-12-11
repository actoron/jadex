/**
 * 
 */
package deco4mas.distributed.convergence;

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
	 * @return {@link Boolean#TRUE} as an {@link IFuture} if the service agrees on the {@link Adaption} or {@link Boolean#FALSE} if not
	 */
	public IFuture<Boolean> vote(Adaption adaption);
	
	/**
	 * Used for distributed case: denotes the context of a distributed application. In order to recognize, all instances of a distributed application have to use the same CoordinationCotextID, which
	 * is defined in the *.application.xml.
	 * 
	 * @return
	 */
	public String getCoordinationContextID();
}
