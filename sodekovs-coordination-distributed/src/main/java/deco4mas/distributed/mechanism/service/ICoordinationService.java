package deco4mas.distributed.mechanism.service;

import jadex.commons.future.IFuture;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * A distributed coordination service interface used to publish {@link CoordinationInfo}s in a distributed Jadex application.
 * 
 * @author Thomas Preisler
 */
public interface ICoordinationService {

	/**
	 * Publishes the given {@link CoordinationInfo} to a local {@link CoordinationSpace}.
	 * 
	 * @param ci
	 *            the given {@link CoordinationSpace}
	 * @return {@link IFuture#DONE}
	 */
	public IFuture<Void> publish(CoordinationInfo ci);

	/**
	 * Used for distributed case: denotes the context of a distributed application. In order to recognize, all instances of a distributed application have to use the same CoordinationCotextID, which
	 * is defined in the *.application.xml.
	 * 
	 * @param id
	 */
	public void setCoordinationContextID(String id);

	/**
	 * Used for distributed case: denotes the context of a distributed application. In order to recognize, all instances of a distributed application have to use the same CoordinationCotextID, which
	 * is defined in the *.application.xml.
	 * 
	 * @return
	 */
	public String getCoordinationContextID();

}
