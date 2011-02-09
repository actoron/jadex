package jadex.simulation.analysis.common.dataObjects;

import javax.swing.JFrame;

import jadex.simulation.analysis.common.dataObjects.Factories.AExperimentFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsembleView;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.ADataEvent;

public class AExperiment extends ABasicDataObject implements IAExperiment
{

	private IAModel model;
	private IAParameterEnsemble expParameters = new AParameterEnsemble();
	private IAParameterEnsemble inputParameters = new AParameterEnsemble();
	private IAParameterEnsemble outputParameters = new AParameterEnsemble();

	public AExperiment(IAModel model, IAParameterEnsemble expParameters, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		expParameters.setName("Experimentparameter");
		inputParameters.setName("Inputparameter");
		outputParameters.setName("Outputparameter");
		
		setModel(model);
		setExperimentParamters(expParameters);
		setInputParamters(inputParameters);
		setOutputParamters(outputParameters);
//		view = new AExperimentView(this);
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
	public void dataChanged(ADataEvent e)
	{
		super.dataChanged(e);
		
		model.dataChanged(e);
		expParameters.dataChanged(e);
		inputParameters.dataChanged(e);
		outputParameters.dataChanged(e);
	}
}
