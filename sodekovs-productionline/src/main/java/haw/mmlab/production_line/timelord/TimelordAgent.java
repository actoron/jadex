package haw.mmlab.production_line.timelord;

import haw.mmlab.production_line.service.IDatabaseService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * The almighty timelord agent!
 * 
 * @author thomas
 */
@Description("This is the almighty timelord agent!")
@RequiredServices(@RequiredService(name = "databaseService", type = IDatabaseService.class))
public class TimelordAgent extends MicroAgent {

	/** The interval in which the time should by increased */
	private int interval = 10;

	/** The "time" */
	private int time = 0;

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

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
		dbService.setIntervalTime(time);
		time++;

		LoggingStep step = new LoggingStep();

		waitFor(interval, step);
	}

	/**
	 * Private class which periodically increments the interval time in the database.
	 * 
	 * @author thomas
	 */
	private class LoggingStep implements IComponentStep {

		public Object execute(IInternalAccess ia) {
			dbService.setIntervalTime(time);
			time++;

			IFuture<?> result = waitFor(interval, this);
			return result;
		}
	}
}