package jadex.simulation.analysis.common.data.parameter;

import jadex.bdi.runtime.IParameter;
import jadex.simulation.analysis.common.data.IADataObject;

public interface IAParameter extends IADataObject
{
	// TODO: Comment
	public String getName();

	public Class getValueClass();

	public boolean isFeasable();

	public void setValue(Object value);

	public Object getValue();

//	public Boolean isUsage();

//	public void setUsage(Boolean usage);

	public void setValueClass(Class type);
	
	public void setValueEditable(Boolean editable);

	public Boolean isValueEditable();

	
}
