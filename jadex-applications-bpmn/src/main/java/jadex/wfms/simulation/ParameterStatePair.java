package jadex.wfms.simulation;

import jadex.bpmn.model.MParameter;

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
