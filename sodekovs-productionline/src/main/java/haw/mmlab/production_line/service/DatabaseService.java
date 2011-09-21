/**
 * 
 */
package haw.mmlab.production_line.service;

import haw.mmlab.production_line.domain.HelpRequest;
import haw.mmlab.production_line.logging.database.DatabaseAgent;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * @author thomas
 * 
 */
@Service
public class DatabaseService implements IDatabaseService {

	@ServiceComponent
	private DatabaseAgent agent = null;

	@Override
	public IFuture<Void> insertMetadata(double redundancyRate, int robotCount, int transportCount, int capabilityCount, int roleCount, String strategy, double workload) {
		agent.insertMetadata(redundancyRate, robotCount, transportCount, capabilityCount, roleCount, strategy, workload);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> insertLog(String agentId, String agentType, int intervalTime, int mainState, int deficientState, int noRoles, int bufferLoad, int bufferCapacity) {
		agent.insertLog(agentId, agentType, intervalTime, mainState, deficientState, noRoles, bufferLoad, bufferCapacity);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> incrementHopCount(int increment) {
		agent.incrementHopCount(increment);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> incrementMessageCountBy(int increment) {
		agent.incrementMessageCountBy(increment);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> incrementRoleChangeAction(int count) {
		agent.incrementRoleChangeAction(count);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> setErrorRun() {
		agent.setErrorRun();
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> setIntervalTime(int time) {
		agent.setIntervalTime(time);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Void> storeRoleChangeDistance(HelpRequest request) {
		agent.storeRoleChangeDistance(request);
		return IFuture.DONE;
	}

	@Override
	public IFuture<Integer> getCurrentTime() {
		return new Future<Integer>(agent.getCurrentTime());
	}
}