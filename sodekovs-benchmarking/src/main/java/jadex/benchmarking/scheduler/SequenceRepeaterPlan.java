package jadex.benchmarking.scheduler;

import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.helper.Methods;
import jadex.benchmarking.logger.ScheduleLogger;
import jadex.benchmarking.model.Action;
import jadex.benchmarking.model.Sequence;
import jadex.benchmarking.model.SuTinfo;

import java.util.ArrayList;

public class SequenceRepeaterPlan extends AbstractSchedulerPlan {


	// Sequence to be executed
	private Sequence sequence = null;

	public void body() {
		System.out.println("Started repeater seq goal...");
		this.sequence =  (Sequence) getParameter("sequence").getValue();
		this.scheduleLogger =  (ScheduleLogger) getParameter("scheduleLogger").getValue();
		
		
		init((SuTinfo) getParameter("args").getValue());

		// execute first run, since it does not depend on the repeater type
		executeSequence();

		// Next executions of sequence depend on the repeater (type)
		if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.SPACE)) {
			startSpaceSequenceRepeater();
		} else if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.LIST)) {
			startListSequenceRepeater();
		} else {
			System.out.println("SequenceRepeaterPlan# : Error: RepeatConfiguration -> type unknown!");
		}
	}

	/**
	 * Responsible for starting a sequence, defined as a space of values
	 */
	private void startSpaceSequenceRepeater() {
		long currentValue = sequence.getStarttime();
		long stepSize = Long.valueOf(sequence.getRepeatConfiguration().getStep()).longValue();
		boolean end = sequence.getRepeatConfiguration().getEnd() == null ? true : false;

		do {
			// wait for next step
			waitFor(stepSize);
			// execute sequence again
			executeSequence();
			currentValue += stepSize;
			System.out.println("SpaceRepeater: Executed -> " + currentValue);
			// execute till "end condition" has been reached. if no end is specified, continue till benchmark terminates.
		} while (end || (currentValue < Long.valueOf(sequence.getRepeatConfiguration().getEnd()).longValue()));
	}

	/**
	 * Responsible for starting a sequence, defined as a list of values
	 */
	private void startListSequenceRepeater() {
		ArrayList<String> values = Methods.getValuesAsList(sequence.getRepeatConfiguration().getValues());
		long currentValue = sequence.getStarttime();

		for (int i = 0; i < values.size(); i++) {
			// wait for next step
			waitFor(Long.valueOf(values.get(i)).longValue() - currentValue);
			// execute sequence again
			executeSequence();
			currentValue = Long.valueOf(values.get(i)).longValue();
			System.out.println("ListRepeater: Executed -> " + currentValue);
		}
	}

	private void executeSequence() {
		for (Action nextAction : sequence.getActions().getAction()) {
			if (sequence.getActiontype().equalsIgnoreCase(Constants.CREATE)) {
				createComponent(nextAction);
			} else if (sequence.getActiontype().equalsIgnoreCase(Constants.DELETE)) {
				deleteComponent(nextAction);
			}
		}

	}
}