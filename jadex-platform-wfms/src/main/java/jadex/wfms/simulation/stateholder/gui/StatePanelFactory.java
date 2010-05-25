package jadex.wfms.simulation.stateholder.gui;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateholder.AbstractNumericStateSet;
import jadex.wfms.simulation.stateholder.BooleanStateSet;
import jadex.wfms.simulation.stateholder.ParameterStateSetFactory;
import jadex.wfms.simulation.stateholder.StringStateSet;

import javax.swing.JPanel;

public class StatePanelFactory
{
	public static final JPanel createStatePanel(MActivity task, MParameter parameter, SimulationWindow simWindow)
	{
		if (ParameterStateSetFactory.createStateHolder(parameter) instanceof BooleanStateSet)
			return new BooleanStatePanel(task.getName(), parameter.getName(), simWindow);
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof AbstractNumericStateSet)
			return new NumericPanel(task.getName(), parameter.getName(), simWindow);
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof StringStateSet)
			return new StringPanel(task.getName(), parameter.getName(), simWindow);
		else
			return null;
	}
}
