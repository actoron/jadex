package haw.mmlab.production_line.timelord;

import haw.mmlab.production_line.logging.database.DatabaseLogger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 * The almighty timelord agent!
 * 
 * @author thomas
 */
public class TimelordAgent extends MicroAgent {

	/** The interval in which the time should by increased */
	private int interval = 10;

	/** The "time" */
	private int time = 0;;

	@Override
	public void executeBody() {
		DatabaseLogger logger = DatabaseLogger.getInstance();

		logger.setIntervalTime(time);
		time++;

		LoggingStep step = new LoggingStep();

		waitFor(interval, step);
	}

	/**
	 * Returns the {@link MicroAgentMetaInfo}.
	 * 
	 * @return the {@link MicroAgentMetaInfo}
	 */
	public static MicroAgentMetaInfo getMetaInfo() {
		return new MicroAgentMetaInfo("This is the almighty timelord agent!", null, null, null, null, null, null, null);
	}

	/**
	 * Private class which periodically increments the interval time in the
	 * database.
	 * 
	 * @author thomas
	 */
	private class LoggingStep implements IComponentStep {

		private DatabaseLogger logger = DatabaseLogger.getInstance();

		public Object execute(IInternalAccess ia) {
			logger.setIntervalTime(time);
			time++;

			IFuture result = waitFor(interval, this);
			return result;
		}
	}
}