package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.IADataObject;

public interface IAParameter extends IADataObject
{
	// TODO: Comment
	void setName(String name);

	public String getName();

	public Class getValueClass();

	void setValueClass(Class type);

	public boolean isFeasable();

	public void setValue(Object value);

	public Object getValue();

}
