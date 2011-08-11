package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.IADataView;

import java.util.Map;
import java.util.Set;

public interface IAParameterEnsemble extends IADataObject
{
	// TODO: Comment
	public void setName(String name);

	public String getName();

	public Boolean isFeasable();

//	public void setValue(String name, Object value);
//
//	public Object getValue(String name);

	public boolean containsParameter(String name);

	public void addParameter(IAParameter parameter);

	public void removeParameter(String name);

	public Map<String, IAParameter> getParameters();

	public IAParameter getParameter(String name);

	public void clearParameters();

	public Integer numberOfParameters();

	public boolean hasParameters();

	

}
