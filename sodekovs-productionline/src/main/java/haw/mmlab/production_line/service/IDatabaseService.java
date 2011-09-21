/**
 * 
 */
package haw.mmlab.production_line.service;

import haw.mmlab.production_line.domain.HelpRequest;
import jadex.commons.future.IFuture;

/**
 * @author thomas
 * 
 */
public interface IDatabaseService {

	/**
	 * Inserts the meta data of a run into the database.
	 * 
	 * @param redundancyRate
	 *            The redundancy rate of the capabilities.
	 * @param robotCount
	 *            The count of robots
	 * @param transportCount
	 *            The count of transports
	 * @param capabilityCount
	 *            The count of available capabilities
	 * @param roleCount
	 *            The count of roles
	 * @param strategy
	 *            The applied strategy
	 * @param workload
	 *            The middled workload
	 * 
	 * @return <code>true</code> if the meta data was inserted.
	 */
	public IFuture<Void> insertMetadata(double redundancyRate, int robotCount, int transportCount, int capabilityCount, int roleCount, String strategy, double workload);

	/**
	 * Inserts the given data in the Log table in the database.
	 * 
	 * @param agentId
	 *            - the agent id.
	 * @param agentType
	 *            - the agent's type.
	 * @param intervalTime
	 *            - the logical time.
	 * @param mainState
	 *            - the agent's main state.
	 * @param deficientState
	 *            - the agent's deficient state.
	 * @param noRoles
	 *            - the number of role the agent has
	 * @param bufferLoad
	 *            - the number of elements in the buffer
	 * @param bufferCapacity
	 *            - the capacity (max size) of the buffer
	 * @return true if the data was successfully logged in the database, else false.
	 */
	public IFuture<Void> insertLog(String agentId, String agentType, int intervalTime, int mainState, int deficientState, int noRoles, int bufferLoad, int bufferCapacity);

	/**
	 * Increment the overall hopCount value by the given increment value in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param increment
	 *            the value by which should be incremented
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public IFuture<Void> incrementHopCount(int increment);

	/**
	 * Increments the messageCount value by the given increment value in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param increment
	 *            the value by which should by incremented
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public IFuture<Void> incrementMessageCountBy(int increment);

	/**
	 * Increments the roleChangeCount value by 1 in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param count
	 *            Number of roes that have been changed.
	 * 
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public IFuture<Void> incrementRoleChangeAction(int count);

	/**
	 * Marks the current run as a error run (a run which ended with an error condition).
	 * 
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public IFuture<Void> setErrorRun();

	/**
	 * Updates the currentTime value in the Logical_Time table to keep track of the logical time.
	 * 
	 * @param time
	 *            - the new logical time value.
	 * @return true if the new value was successfully inserted, else false.
	 */
	public IFuture<Void> setIntervalTime(int time);

	/**
	 * Stores the distance (hop count) of the given {@link HelpRequest} in the database.
	 * 
	 * @param request
	 *            - the given {@link HelpRequest}
	 * @return <code>true</code> if the role change distance was inserted, else <code>false</code>
	 */
	public IFuture<Void> storeRoleChangeDistance(final HelpRequest request);

	/**
	 * Returns the current Time from the Logical_Time Table.
	 * 
	 * @return the current logical time
	 */
	public IFuture<Integer> getCurrentTime();
}
