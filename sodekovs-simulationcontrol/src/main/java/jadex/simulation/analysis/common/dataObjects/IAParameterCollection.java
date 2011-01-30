package jadex.simulation.analysis.common.dataObjects;

import java.util.Map;

import javax.swing.JComponent;

public interface IAParameterCollection extends Map<String, IAParameter>
{
	public Boolean isFeasable();
	
	public void add(IAParameter parameter);
	
	public JComponent getView(Boolean option); 
}
