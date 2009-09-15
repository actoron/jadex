package jadex.wfms.simulation;

import jadex.bpmn.model.MParameter;
import jadex.wfms.simulation.stateholder.IParameterStateHolder;

public class ParameterStatePair
{
	private MParameter parameter;
	
	private IParameterStateHolder stateHolder;
	
	public ParameterStatePair(MParameter parameter, IParameterStateHolder stateHolder)
	{
		this.parameter = parameter;
		this.stateHolder = stateHolder;
	}
	
	public MParameter getParameter()
	{
		return parameter;
	}
	
	public IParameterStateHolder getStateHolder()
	{
		return stateHolder;
	}
}
