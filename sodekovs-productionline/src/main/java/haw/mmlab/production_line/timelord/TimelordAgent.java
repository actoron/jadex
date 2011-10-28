package haw.mmlab.production_line.timelord;

import haw.mmlab.production_line.logging.database.DatabaseLogger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

/**
 * The almighty timelord agent!
 * 
 * @author thomas
 */
@SuppressWarnings("unchecked")
@Description("This is the almighty timelord agent!")
@Arguments({ @Argument(clazz = Integer.class, name = "interval") })
public class TimelordAgent extends MicroAgent {

	/** The "time" */
	private Integer time = 0;

	private Integer interval = 0;

	private DatabaseLogger databaseLogger = null;

	@Override
	public IFuture<Void> agentCreated() {
		this.interval = (Integer) getArgument("interval");
		this.databaseLogger = new DatabaseLogger();

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
		databaseLogger.setIntervalTime(time);
		time++;

		LoggingStep step = new LoggingStep();

		if (interval.equals(0)) {
			waitForTick(step);
		} else {
			waitFor(interval, step);
		}
	}

	/**
	 * Private class which periodically increments the interval time in the database.
	 * 
	 * @author thomas
	 */
	private class LoggingStep implements IComponentStep<Void> {

		public IFuture<Void> execute(IInternalAccess ia) {
			databaseLogger.setIntervalTime(time);
			time++;

			if (interval.equals(0)) {
				waitForTick(this);
			} else {
				waitFor(interval, this);
			}

			return IFuture.DONE;
		}
	}
}