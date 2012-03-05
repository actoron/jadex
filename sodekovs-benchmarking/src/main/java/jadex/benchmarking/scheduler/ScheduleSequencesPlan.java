package jadex.benchmarking.scheduler;

import jadex.bdi.runtime.IGoal;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.logger.ScheduleLogger;
import jadex.benchmarking.model.Action;
import jadex.benchmarking.model.Sequence;
import jadex.benchmarking.model.SuTinfo;

public class ScheduleSequencesPlan extends AbstractSchedulerPlan {

	public void body() {
		
		init((SuTinfo) getBeliefbase().getBelief(Constants.SUT_INFO).getFact());

		//init logger
		scheduleLogger = (ScheduleLogger) getBeliefbase().getBelief(Constants.SCHEDULE_LOGGER).getFact();
		scheduleLogger.setClockService(clockservice);
		scheduleLogger.setStarttime(((Long)sutSpace.getProperty("REAL_START_TIME_OF_SIMULATION")).longValue());
		scheduleLogger.init();
		scheduleLogger.log(Constants.PREPARE_GNUPLOT_PREFIX);
		
		
		// responsible to start the components
		startScheduler();
	}

	/**
	 * Responsible for starting sequences in time-order according to benchmark
	 */
	private void startScheduler() {
		// sequenceCounter = denotes current sequence being executed
		for (int sequenceCounter = 0; sequenceCounter < sortedSequenceList.size(); sequenceCounter++) {
			Sequence nextSequence = sortedSequenceList.get(sequenceCounter);
			// wait till next sequence has to be performed.
			waitFor(computeStartTime(sequenceCounter));

			// If this sequence has to be repeated, start a goal that handles this sequences separately.
			if (nextSequence.getRepeatConfiguration() != null) {
				// Dispatch separate goal to handle sequence 
				IGoal eval = (IGoal) getGoalbase().createGoal("SequenceRepeaterGoal");
				eval.getParameter("args").setValue(new SuTinfo(sortedSequenceList, null, sutCID, sutExta, sutSpace));		
				eval.getParameter("sequence").setValue(nextSequence);				
				eval.getParameter("scheduleLogger").setValue(scheduleLogger);
				getGoalbase().dispatchTopLevelGoal(eval);		
			} else {
				for (Action nextAction : nextSequence.getActions().getAction()) {

					if (nextSequence.getActiontype().equalsIgnoreCase(Constants.CREATE)) {
						createComponent(nextAction);
					} else if (nextSequence.getActiontype().equalsIgnoreCase(Constants.DELETE)) {
						deleteComponent(nextAction);
					}
				}
			}
		}
	}
}
