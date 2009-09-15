package jadex.wfms.simulation.stateholder.gui;

import javax.swing.JPanel;

import jadex.wfms.simulation.stateholder.AbstractNumericStateHolder;
import jadex.wfms.simulation.stateholder.BooleanStateHolder;
import jadex.wfms.simulation.stateholder.IParameterStateHolder;
import jadex.wfms.simulation.stateholder.StringStateHolder;

public class StatePanelFactory
{
	public static final JPanel createStatePanel(IParameterStateHolder stateHolder)
	{
		if (stateHolder instanceof BooleanStateHolder)
			return new BooleanStatePanel((BooleanStateHolder) stateHolder);
		else if (stateHolder instanceof AbstractNumericStateHolder)
			return new NumericPanel((AbstractNumericStateHolder) stateHolder);
		else if (stateHolder instanceof StringStateHolder)
			return new StringPanel((StringStateHolder) stateHolder);
		else
			return null;
	}
}
