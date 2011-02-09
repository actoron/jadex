package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.bdi.testcases.misc.GetExternalAccessPlan;
import jadex.simulation.analysis.common.dataObjects.ABasicDataObject;
import jadex.simulation.analysis.common.dataObjects.IADataObjectView;
import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class AParameterEnsemble extends ABasicDataObject implements IAParameterEnsemble
{

	private Map<String, IAParameter> parametersMap;
	private String name = "ParameterEnsemble";

	public AParameterEnsemble()
	{
		super();
		synchronized (mutex)
		{
			parametersMap = Collections.synchronizedMap(new HashMap<String, IAParameter>());
		}
//		view = new AParameterEnsembleView(this);
	}

	// TODO: Add to view
	@Override
	public Boolean isFeasable()
	{
		synchronized (mutex)
		{
			Boolean result = true;
			for (IAParameter para : getParameters().values())
			{
				if (!para.isFeasable())
					result = false;
			}
			return result;
		}
	}

	@Override
	public void addParameter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			parametersMap.put(parameter.getName(), parameter);
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_PARAMETERS));
	}

	@Override
	public void removeParameter(String name)
	{
		synchronized (mutex)
		{
			parametersMap.remove(name);
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_PARAMETERS));
	}

	@Override
	public void clearParameters()
	{
		synchronized (mutex)
		{
			parametersMap.clear();
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_PARAMETERS));
	}

	@Override
	public boolean containsParameter(String name)
	{
		return parametersMap.containsKey(name);
	}

	@Override
	public IAParameter getParameter(String name)
	{
		return parametersMap.get(name);
	}

	@Override
	public Map<String, IAParameter> getParameters()
	{
		return parametersMap;
	}

	@Override
	public boolean hasParameters()
	{
		return parametersMap.isEmpty();
	}

	@Override
	public Integer numberOfParameters()
	{
		return parametersMap.size();
	}

	@Override
	public Object getValue(String name)
	{
		return parametersMap.get(name).getValue();
	}

	@Override
	public void setValue(String name, Object value)
	{
		synchronized (mutex)
		{
			parametersMap.get(name).setValue(value);
		}
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void dataChanged(ADataEvent e)
	{
		super.dataChanged(e);
		for (IAParameter parameter : parametersMap.values())
		{
			parameter.dataChanged(e);
		}
	}

	@Override
	public void setName(String name)
	{
		synchronized (mutex)
		{
			this.name = name;
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_NAME));
	}

	@Override
	public void setEditable(Boolean editable)
	{
		super.setEditable(editable);
		for (IAParameter parameter : getParameters().values())
		{
			parameter.setEditable(editable);
		}
	}

	@Override
	public String toString()
	{
		return "AParamterEnsemble: " + "name=" + getName() + ", " + "parameters=" + getParameters();
	}
}
