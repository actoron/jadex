package jadex.simulation.analysis.common.dataObjects;

import javax.swing.JComponent;

public interface IAParameter {
	
	public String getName();
	
	public void setValue(Object value);
	
	public Object getValue();
	
	public Class getClazz();
	
	public Boolean isFeasable();
	
	public Boolean isVariable();
	
	public JComponent getView(Boolean option); 
	
	public Boolean isResult();
}
