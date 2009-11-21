package jadex.wfms.simulation.stateholder.gui;

import javax.swing.JPanel;

import jadex.wfms.simulation.stateholder.AbstractNumericStateSet;
import jadex.wfms.simulation.stateholder.BooleanStateSet;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.StringStateSet;

public class StatePanelFactory
{
	public static final JPanel createStatePanel(IParameterStateSet stateSet)
	{
		if (stateSet instanceof BooleanStateSet)
			return new BooleanStatePanel((BooleanStateSet) stateSet);
		else if (stateSet instanceof AbstractNumericStateSet)
			return new NumericPanel((AbstractNumericStateSet) stateSet);
		else if (stateSet instanceof StringStateSet)
			return new StringPanel((StringStateSet) stateSet);
		else
			return null;
	}
}
