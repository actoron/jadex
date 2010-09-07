package jadex.wfms.simulation.stateset.gui;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.AbstractNumericStateSet;
import jadex.wfms.simulation.stateset.BooleanStateSet;
import jadex.wfms.simulation.stateset.DocumentStateSet;
import jadex.wfms.simulation.stateset.ParameterStateSetFactory;
import jadex.wfms.simulation.stateset.ResolvableListChoiceStateSet;
import jadex.wfms.simulation.stateset.ResolvableMultiListChoiceStateSet;
import jadex.wfms.simulation.stateset.StringArrayStateSet;
import jadex.wfms.simulation.stateset.StringStateSet;

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
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof StringArrayStateSet)
			return new StringArrayStatePanel(task.getName(), parameter, simWindow);
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof ResolvableListChoiceStateSet)
			return new ResolvableListChoiceStatePanel(task.getName(), parameter, simWindow);
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof ResolvableMultiListChoiceStateSet)
			return new ResolvableMultiListChoiceStatePanel(task.getName(), parameter, simWindow);
		else if (ParameterStateSetFactory.createStateHolder(parameter) instanceof DocumentStateSet)
			return new DocumentStatePanel(task.getName(), parameter.getName(), simWindow);
		else
		{
			System.err.println("WARNING: No suitable panel found for parameter type " + parameter.getClazz().getCanonicalName() + ".");
			return null;
		}
	}
}
