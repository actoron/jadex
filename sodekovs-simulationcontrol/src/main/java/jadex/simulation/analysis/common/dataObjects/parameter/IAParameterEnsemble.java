package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.IADataObject;
import jadex.simulation.analysis.common.dataObjects.IADataObjectView;

import java.util.Map;

public interface IAParameterEnsemble extends IADataObject
{
	// TODO: Comment
	public void setName(String name);

	public String getName();

	public Boolean isFeasable();

	public void setValue(String name, Object value);

	public Object getValue(String name);

	public boolean containsParameter(String name);

	public void addParameter(IAParameter parameter);

	public void removeParameter(String name);

	public Map<String, IAParameter> getParameters();

	public IAParameter getParameter(String name);

	public void clearParameters();

	public Integer numberOfParameters();

	public boolean hasParameters();

}
