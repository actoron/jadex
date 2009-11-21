package jadex.wfms.simulation.stateholder;

import jadex.bpmn.model.MParameter;

public class ParameterStateSetFactory
{
	public static final IParameterStateSet createStateHolder(MParameter parameter)
	{
		if ((parameter.getClazz().equals(Boolean.class)) || (parameter.getClazz().equals(Boolean.TYPE)))
			return new BooleanStateSet(parameter.getName());
		else if ((parameter.getClazz().equals(Byte.class)) || (parameter.getClazz().equals(Byte.TYPE)))
			return new ByteStateSet(parameter.getName());
		else if ((parameter.getClazz().equals(Short.class)) || (parameter.getClazz().equals(Short.TYPE)))
			return new ShortStateSet(parameter.getName());
		else if ((parameter.getClazz().equals(Integer.class)) || (parameter.getClazz().equals(Integer.TYPE)))
			return new IntegerStateSet(parameter.getName());
		else if ((parameter.getClazz().equals(Long.class)) || (parameter.getClazz().equals(Long.TYPE)))
			return new LongStateSet(parameter.getName());
		else if (parameter.getClazz().equals(String.class))
			return new StringStateSet(parameter.getName());
		else
			return null;
	}
}
