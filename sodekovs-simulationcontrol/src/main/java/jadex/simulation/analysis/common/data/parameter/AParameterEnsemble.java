package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * AParameterEnsemble, which saves a set of parameters
 * @author 5Haubeck
 *
 */
public class AParameterEnsemble extends ADataObject implements IAParameterEnsemble
{
	private Map<String, IAParameter> parametersMap;

	public AParameterEnsemble() {
		super();
		synchronized (mutex)
		{
//		view = new AParameterEnsembleView(this);
		}
	}
	
	public AParameterEnsemble(String name)
	{
		super(name);
		synchronized (mutex)
		{
			parametersMap = Collections.synchronizedMap(new HashMap<String, IAParameter>());
		}
//		view = new AParameterEnsembleView(this);
	}
	
	

	public Map<String, IAParameter> getParametersMap() {
		return parametersMap;
	}

	public void setParametersMap(Map<String, IAParameter> parametersMap) {
		synchronized (mutex) {
			this.parametersMap = parametersMap;
		}
	}

	// TODO: Add to view
	@Override
	public Boolean isFeasible()
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
		notify(new ADataEvent(this, AConstants.ENSEMBLE_ADD_PARAMETERS, parameter));
	}

	@Override
	public void removeParameter(String name)
	{
		synchronized (mutex)
		{
			parametersMap.remove(name);
		}
		notify(new ADataEvent(this, AConstants.ENSEMBLE_REMOVE_PARAMETERS, name));
	}

	@Override
	public void clearParameters()
	{
		synchronized (mutex)
		{
			parametersMap.clear();
		}
		notify(new ADataEvent(this, AConstants.ENSEMBLE_PARAMETERS, parametersMap));
	}

	@Override
	public Boolean containsParameter(String name)
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
	public Boolean hasParameters()
	{
		return parametersMap.isEmpty();
	}

	@Override
	public Integer numberOfParameters()
	{
		return parametersMap.size();
	}

	@Override
	public void notify(IAEvent event)
	{
		super.notify(event);
		if (parametersMap != null)
		{
			for (IAParameter parameter : parametersMap.values())
			{
				parameter.notify(event);
			}
		}
	}

	@Override
	public void setEditable(Boolean editable)
	{
		synchronized (mutex)
		{
			super.setEditable(editable);
			for (IAParameter parameter : getParameters().values())
			{
				parameter.setEditable(editable);
			}
		}
	}

	@Override
	public IADataView getView()
	{
		return view;
	}

	public ADataObject clonen()
	{
		synchronized (mutex)
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
}
