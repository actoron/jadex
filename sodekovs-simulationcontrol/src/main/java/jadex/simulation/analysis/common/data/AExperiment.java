package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class AExperiment extends ADataObject implements IAExperiment
{

	private IAModel model;
	private IAParameterEnsemble expParameters = new AParameterEnsemble("Experiment Parameter");
	private IAParameterEnsemble inputParameters = new AParameterEnsemble("Input Parameter");
	private IAParameterEnsemble outputParameters = new AParameterEnsemble("Output Parameter");
	private Boolean evaluated = false;

	public AExperiment(String name, IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		super(name);
		synchronized (mutex)
		{
			expParameters.setName("Experimentparameter");
			inputParameters.setName("Inputparameter");
			outputParameters.setName("Outputparameter");

			setModel(model);
			setExperimentParamters(expParameters);
			setInputParamters(inputParameters);
			setOutputParamters(outputParameters);
			view = new AExperimentView(this);
		}
	}

	// ------ IAExperiment ------

	// model

	public IAModel getModel()
	{
		return model;
	}

	public void setModel(IAModel model)
	{
		synchronized (mutex)
		{
			model.setEditable(false);
			this.model = model;
		}
	}

	// Input

	@Override
	public void removeInputParamter(String name)
	{
		synchronized (mutex)
		{
			inputParameters.removeParameter(name);
		}
	}

	@Override
	public void setInputParamters(IAParameterEnsemble parameters)
	{
		synchronized (mutex)
		{
			inputParameters = parameters;
		}
	}

	@Override
	public IAParameter getInputParameter(String name)
	{
		return inputParameters.getParameter(name);
	}

	@Override
	public IAParameterEnsemble getInputParameters()
	{
		return inputParameters;
	}

	@Override
	public void addInputParamter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			inputParameters.addParameter(parameter);
		}
	}

	// Output
	@Override
	public IAParameterEnsemble getOutputParameters()
	{
		return outputParameters;
	}

	@Override
	public void setOutputParamters(IAParameterEnsemble parameters)
	{
		synchronized (mutex)
		{
			outputParameters = parameters;
		}
	}

	@Override
	public void addOutputParamter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			outputParameters.addParameter(parameter);
		}
	}

	@Override
	public void removeOutputParamter(String name)
	{
		synchronized (mutex)
		{
			outputParameters.removeParameter(name);
		}
	}

	@Override
	public IAParameter getOutputParameter(String name)
	{
		return outputParameters.getParameter(name);
	}

	// Experiment
	@Override
	public void addExperimentParamter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			expParameters.addParameter(parameter);
		}
	}

	@Override
	public void removeExperimentParamter(String name)
	{
		synchronized (mutex)
		{
			expParameters.removeParameter(name);
		}
	}

	@Override
	public IAParameter getExperimentParameter(String name)
	{
		return expParameters.getParameter(name);
	}

	@Override
	public IAParameterEnsemble getExperimentParameters()
	{
		return expParameters;
	}

	@Override
	public void setExperimentParamters(IAParameterEnsemble parameters)
	{
		synchronized (mutex)
		{
			expParameters = parameters;
		}
	}

	@Override
	public void notify(IAEvent event)
	{
		super.notify(event);

		if (model != null) model.notify(event);
		if (expParameters != null) expParameters.notify(event);
		if (inputParameters != null) inputParameters.notify(event);
		if (outputParameters != null) outputParameters.notify(event);
	}

	@Override
	public void setEditable(Boolean editable)
	{
		synchronized (mutex)
		{
			super.setEditable(editable);

			model.setEditable(editable);
			expParameters.setEditable(editable);
			inputParameters.setEditable(editable);
			outputParameters.setEditable(editable);
			notify(new ADataEvent(this, AConstants.DATA_EDITABLE, editable));
		}
	}

	@Override
	public Boolean isEvaluated()
	{
		return evaluated;
	}

	@Override
	public void setEvaluated(Boolean evaluated)
	{
		synchronized (mutex)
		{
			this.evaluated = evaluated;
			notify(new ADataEvent(this, AConstants.EXPERIMENT_EVA, evaluated));
		}
	}
	
	@Override
	public ADataObject clonen()
	{
		AExperiment clone = new AExperiment(name, model, (IAParameterEnsemble)expParameters.clonen(), (IAParameterEnsemble)inputParameters.clonen(), (IAParameterEnsemble)outputParameters.clonen());
		clone.setEditable(editable);
		clone.setEvaluated(evaluated);
		return clone;
	}
}
