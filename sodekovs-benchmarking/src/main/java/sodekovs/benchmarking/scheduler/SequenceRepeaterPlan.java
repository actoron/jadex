package sodekovs.benchmarking.scheduler;


import java.text.DecimalFormat;
import java.util.ArrayList;

import sodekovs.benchmarking.helper.Constants;
import sodekovs.benchmarking.helper.Methods;
import sodekovs.benchmarking.logger.ScheduleLogger;
import sodekovs.benchmarking.model.Action;
import sodekovs.benchmarking.model.Sequence;
import sodekovs.benchmarking.model.SuTinfo;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

public class SequenceRepeaterPlan extends AbstractSchedulerPlan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3611992896584144687L;
	
	
	// Sequence to be executed
	private Sequence sequence = null;

	public void body() {
//		System.out.println("Started repeater seq goal...");
		this.sequence = (Sequence) getParameter("sequence").getValue();
		this.scheduleLogger = (ScheduleLogger) getParameter("scheduleLogger").getValue();

		init((SuTinfo) getParameter("args").getValue());

		// execute first run, since it does not depend on the repeater type
		executeSequence();

		// Next executions of sequence depend on the repeater (type)
		if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.SPACE)) {
			startSpaceSequenceRepeater();
		} else if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.LIST)) {
			startListSequenceRepeater();
		} else if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.STOCHASTIC_DISTRIBUTION)) {
			startStochasticDistributionRepeater();
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

	/**
	 * Responsible for executing a sequence according to a defined stochastic distribution.
	 */
	private void startStochasticDistributionRepeater() {
		String typeOfStochDistr = sequence.getRepeatConfiguration().getTypeOfStochasticDistribution();		
		//end time can be optionally defined
		boolean end = sequence.getRepeatConfiguration().getEnd() == null ? true : false;
		long currentValue = sequence.getStarttime();
		
		DecimalFormat df = new DecimalFormat("0.00");
		double waitingTime = 0.0;
		Normal normal = null;
		Poisson poisson = null;
		
		//init distributions
		if (typeOfStochDistr.equalsIgnoreCase(Constants.NORMAL)) {
			double alpha =  Double.valueOf(sequence.getRepeatConfiguration().getAlpha());
			double sigma =  Double.valueOf(sequence.getRepeatConfiguration().getSigma());
			normal =  new Normal(alpha, sigma, RandomEngine.makeDefault());
		}else if (typeOfStochDistr.equalsIgnoreCase(Constants.POISSON)) {
			double lambda =  Double.valueOf(sequence.getRepeatConfiguration().getLambda());
			poisson = new Poisson(lambda, RandomEngine.makeDefault());
		}else{
			System.err.println("Type of stochastic distribution is not supported: " + typeOfStochDistr);
		}
			
		
		
		do {
//			System.out.println("#loop#: currentValue: " + currentValue);
			// compute waiting time according to stochastic distribution.
			if (typeOfStochDistr.equalsIgnoreCase(Constants.NORMAL)) {
				// Type: "Normal Stochastic Distribution"
				waitingTime = Math.abs(normal.nextDouble());			
				System.out.println("#normdistr: " + df.format(waitingTime) + "currentValue: " + currentValue );
			} else if (typeOfStochDistr.equalsIgnoreCase(Constants.POISSON)) {
				// Type: "Poisson Stochastic Distribution"
				waitingTime = poisson.nextDouble();			
				System.out.println("#poissondistr: " + df.format(waitingTime));
			} else {
				System.err.println("Type of stochastic distribution is not supported: " + typeOfStochDistr);
			}
			
			// wait for next step
			waitFor(Math.round(waitingTime));
			// execute sequence again
			executeSequence();
			currentValue += waitingTime;
			System.out.println("DistrubtionRepeater: Executed -> " + waitingTime + "currentValue: " + currentValue );
			// execute till benchmark terminates.
		} while (end || (currentValue < Long.valueOf(sequence.getRepeatConfiguration().getEnd()).longValue()));		
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