package haw.mmlab.production_line.timelord;

import haw.mmlab.production_line.service.IDatabaseService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * The almighty timelord agent!
 * 
 * @author thomas
 */
@Description("This is the almighty timelord agent!")
@Arguments({ @Argument(clazz = Integer.class, name = "interval") })
@RequiredServices(@RequiredService(name = "databaseService", type = IDatabaseService.class))
public class TimelordAgent extends MicroAgent {

	/** The "time" */
	private Integer time = 0;

	private Integer interval = 0;

	private IDatabaseService dbService = null;

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> agentCreated() {
		this.getRequiredService("databaseService").addResultListener(new DefaultResultListener<IDatabaseService>() {

			@Override
			public void resultAvailable(IDatabaseService result) {
				dbService = result;
			}

		});

		this.interval = (Integer) getArgument("interval");

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
		dbService.setIntervalTime(time);
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
			dbService.setIntervalTime(time);
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