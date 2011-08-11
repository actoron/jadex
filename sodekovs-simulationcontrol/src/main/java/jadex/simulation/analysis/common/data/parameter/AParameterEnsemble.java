package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.events.data.IADataListener;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AParameterEnsemble extends ADataObject implements IAParameterEnsemble
{

	private Map<String, IAParameter> parametersMap;
//	private String name = "ParameterEnsemble";

	public AParameterEnsemble(String name)
	{
		super(name);
		synchronized (mutex)
		{
			parametersMap = Collections.synchronizedMap(new HashMap<String, IAParameter>());
		}
		view = new AParameterEnsembleView(this);
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
			parameter.setEditable(false);
			if (isEditable())	parameter.setValueEditable(true);
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_ADD_PARAMETERS, parameter));

	}

	@Override
	public void removeParameter(String name)
	{
		synchronized (mutex)
		{
			parametersMap.remove(name);
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_REMOVE_PARAMETERS, name));


	}

	@Override
	public void clearParameters()
	{
		synchronized (mutex)
		{
			parametersMap.clear();
		}
		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_PARAMETERS, parametersMap));
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

//	@Override
//	public Object getValue(String name)
//	{
//		return parametersMap.get(name).getValue();
//	}
//
//	@Override
//	public void setValue(String name, Object value)
//	{
//		synchronized (mutex)
//		{
//			parametersMap.get(name).setValue(value);
//		}
//	}

	@Override
	public void dataChanged(ADataEvent e)
	{
		super.dataChanged(e);
		if (parametersMap != null)
		{
			for (IAParameter parameter : parametersMap.values())
			{
				parameter.dataChanged(e);
			}
		}
	}

//	@Override
//	public void setName(String name)
//	{
//		synchronized (mutex)
//		{
//			this.name = name;
//		}
//		dataChanged(new ADataEvent(this, AConstants.ENSEMBLE_NAME, name));
//	}

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
	
	@Override
	public IADataView getView()
	{
		return view;
	}

	public ADataObject clonen()
	{
		AParameterEnsemble ens = new AParameterEnsemble(name);
		for (IAParameter para : getParameters().values())
		{
			ens.addParameter((IAParameter) para.clonen());
		}
		ens.setEditable(editable);
		return ens;
			
	}
}
