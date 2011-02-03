package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.IADataObject;

import java.util.Map;

import javax.swing.JComponent;

public interface IAParameterEnsemble extends IADataObject
{
	// TODO: Comment
	public Boolean isFeasable();

	public JComponent getView();

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
