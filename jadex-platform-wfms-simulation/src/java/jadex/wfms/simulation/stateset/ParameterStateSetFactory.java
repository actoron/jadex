package jadex.wfms.simulation.stateset;

import jadex.bpmn.model.MParameter;
import jadex.javaparser.SimpleValueFetcher;
import jadex.wfms.parametertypes.Document;
import jadex.wfms.parametertypes.ListChoice;
import jadex.wfms.parametertypes.MultiListChoice;
import jadex.wfms.parametertypes.Text;

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
			return Integer.valueOfStateSet(parameter.getName());
		else if ((parameter.getClazz().equals(Long.class)) || (parameter.getClazz().equals(Long.TYPE)))
			return new LongStateSet(parameter.getName());
		else if (parameter.getClazz().equals(String[].class))
			return new StringArrayStateSet(parameter.getName());
		else if (parameter.getClazz().equals(String.class))
			return new StringStateSet(parameter.getName());
		else if (parameter.getClazz().equals(Text.class))
			return new TextStateSet(parameter.getName());
		else if (parameter.getClazz().equals(Document.class))
			return new DocumentStateSet(parameter.getName());
		else if (parameter.getClazz().equals(ListChoice.class) && resolveInitial(parameter) != null)
			return new ResolvableListChoiceStateSet(parameter.getName(), ((ListChoice) resolveInitial(parameter)).getChoices());
		else if (parameter.getClazz().equals(MultiListChoice.class) && resolveInitial(parameter) != null)
			return new ResolvableMultiListChoiceStateSet(parameter.getName(), ((MultiListChoice) resolveInitial(parameter)).getChoices());
		else
		{
			System.err.println("WARNING: Unknown Parameter Type \"" + parameter.getClazz().getCanonicalName() + "\".");
			return null;
		}
	}
	
	private static Object resolveInitial(MParameter parameter)
	{
		try
		{
			Object ret = parameter.getInitialValue().getValue(new SimpleValueFetcher());
			if (ret != null && ret.getClass().equals(parameter.getClazz()))
				return ret;
			/*else
				System.err.println("WARNING: Type mismatch while resolving initial value of " +
								   parameter.getName() + ", resolved type was " +
								   parameter.getInitialValue().getValue(new SimpleValueFetcher()).getClass().getCanonicalName() +
								   ", should have been " + parameter.getClazz().getCanonicalName() +  ".");*/
		}
		catch (Exception e)
		{
			e.printStackTrace();;
		}
		return null;
	}
}
