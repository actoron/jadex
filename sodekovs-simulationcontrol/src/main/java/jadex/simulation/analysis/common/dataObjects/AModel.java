package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class AModel extends ADataObject implements IAModel
{

	private String name;
	private String type;
	// private String identifier;

	private IAParameterEnsemble inputParameters = new AParameterEnsemble();
	private IAParameterEnsemble outputParameters = new AParameterEnsemble();

	public AModel(String name, String type, IAParameterEnsemble inputParameters, IAParameterEnsemble outputParameters)
	{
		this.name = name;
		this.type = type;
		if (inputParameters != null)
			this.inputParameters = inputParameters;
		if (outputParameters != null)
			this.outputParameters = outputParameters;
		
		this.inputParameters.setName("Inputparameter");
		this.outputParameters.setName("Outputparameter");
//		view = new AModelView(this);
	}

	public AModel(String name, String type)
	{
		this(name, type, null, null);
	}

	// ------ Interface IAModel ------

	// Name

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		synchronized (name)
		{
			this.name = name;
			System.out.println(getName() + ": name=" + name);
		}
		dataChanged(new ADataEvent(this, AConstants.MODEL_NAME));
	}

	// Type

	@Override
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		synchronized (name)
		{
			this.type = type;
			System.out.println(getName() + ": type=" + type);
		}
		dataChanged(new ADataEvent(this, AConstants.MODEL_TYPE));
	}

	// Input

	@Override
	public void addInputParameter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			inputParameters.addParameter(parameter);
		}
	}

	@Override
	public void removeInputParameter(String name)
	{
		synchronized (mutex)
		{
			inputParameters.removeParameter(name);
		}
	}

	@Override
	public void setInputParameters(IAParameterEnsemble parameters)
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

	// Output

	@Override
	public IAParameter getOutputParameter(String name)
	{
		return outputParameters.getParameter(name);
	}

	@Override
	public IAParameterEnsemble getOutputParameters()
	{
		return outputParameters;
	}

	@Override
	public void removeOutputParameter(String name)
	{
		synchronized (mutex)
		{
			outputParameters.removeParameter(name);
		}
	}

	@Override
	public void setOutputParameters(IAParameterEnsemble parameters)
	{
		synchronized (mutex)
		{
			outputParameters = parameters;
		}

	}

	@Override
	public void addOutputParameter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			outputParameters.addParameter(parameter);
		}
	}
	
	@Override
	public void dataChanged(ADataEvent e)
	{
		super.dataChanged(e);
		
		inputParameters.dataChanged(e);
		outputParameters.dataChanged(e);
	}
}
