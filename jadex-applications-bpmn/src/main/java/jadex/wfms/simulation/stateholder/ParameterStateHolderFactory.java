package jadex.wfms.simulation.stateholder;

import jadex.bpmn.model.MParameter;

public class ParameterStateHolderFactory
{
	public static final IParameterStateHolder createStateHolder(MParameter parameter)
	{
		if ((parameter.getClazz().equals(Boolean.class)) || (parameter.getClazz().equals(Boolean.TYPE)))
			return new BooleanStateHolder();
		else if ((parameter.getClazz().equals(Byte.class)) || (parameter.getClazz().equals(Byte.TYPE)))
			return new ByteStateHolder();
		else if ((parameter.getClazz().equals(Short.class)) || (parameter.getClazz().equals(Short.TYPE)))
			return new ShortStateHolder();
		else if ((parameter.getClazz().equals(Integer.class)) || (parameter.getClazz().equals(Integer.TYPE)))
			return new IntegerStateHolder();
		else if ((parameter.getClazz().equals(Long.class)) || (parameter.getClazz().equals(Long.TYPE)))
			return new LongStateHolder();
		else if (parameter.getClazz().equals(String.class))
			return new StringStateHolder();
		else
			return null;
	}
}
