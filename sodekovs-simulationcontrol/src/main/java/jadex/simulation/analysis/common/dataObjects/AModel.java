package jadex.simulation.analysis.common.dataObjects;

import com.sun.media.datasink.BasicDataSink;

import EDU.oswego.cs.dl.util.concurrent.Mutex;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

public class AModel extends ABasicDataObject implements IAModel
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
		}
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
		}
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
}
