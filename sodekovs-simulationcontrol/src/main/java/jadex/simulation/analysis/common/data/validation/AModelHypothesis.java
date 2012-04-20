package jadex.simulation.analysis.common.data.validation;

import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

/**
 * AModelHypothesis Impl
 * 
 * @author 5Haubeck
 * 
 */
public class AModelHypothesis extends ADataObject implements IAModelHypothesis {
	// TODO: Add Evaluation Flag
	private IAParameter inputParameter; // = new ABasicParameter("defaultIn",
										// Double.class, new Double(0));
	private IAParameter outputParameter; // = new ABasicParameter("defaultOut",
											// Double.class, new Double(0));
	private Boolean correlation = true;

	public AModelHypothesis() {
		super();
		synchronized (mutex) {

			view = new AModelHypothesisView(this);
		}
	}

	public AModelHypothesis(String name, IAParameter inputParameter,
			IAParameter outputParameter, Boolean correlation) {
		super(name);
		synchronized (mutex) {

			this.inputParameter = inputParameter;
			this.outputParameter = outputParameter;
			view = new AModelHypothesisView(this);
		}
	}
	
	

	// ------ Interface IAModel ------

	@Override
	public void setCorrelation(Boolean correlation) {
		synchronized (mutex) {
			this.correlation = correlation;
		}
		notify(new ADataEvent(this, AConstants.HYPO_CORRELATION, correlation));
	}

	@Override
	public Boolean getCorrelation() {
		return correlation;
	}

	@Override
	public void setOutputParameter(IAParameter parameter) {
		synchronized (mutex) {
			this.outputParameter = parameter;
		}
		notify(new ADataEvent(this, AConstants.HYPO_OUTPUT, parameter));
	}

	@Override
	public IAParameter getOutputParameter() {
		return outputParameter;
	}

	@Override
	public void setInputParameter(IAParameter parameter) {
		synchronized (mutex) {
			this.inputParameter = parameter;
		}
		notify(new ADataEvent(this, AConstants.HYPO_INPUT, parameter));
	}

	@Override
	public IAParameter getInputParameter() {
		return inputParameter;
	}

	@Override
	public void notify(IAEvent event) {
		super.notify(event);

		if (inputParameter != null)
			inputParameter.notify(event);
		if (outputParameter != null)
			outputParameter.notify(event);
	}
}
