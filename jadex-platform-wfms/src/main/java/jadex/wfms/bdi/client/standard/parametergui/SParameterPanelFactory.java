package jadex.wfms.bdi.client.standard.parametergui;

import jadex.commons.SGUI;
import jadex.commons.SReflect;

import javax.swing.JPanel;

public class SParameterPanelFactory
{
	public static final AbstractParameterPanel createParameterPanel(String parameterName, Class parameterType, Object parameterValue, boolean readOnly)
	{
		if (parameterType.isPrimitive())
			parameterType = SReflect.getWrappedType(parameterType);
		if (SReflect.isSupertype(Number.class, parameterType))
		{
			return new NumericParameterPanel(parameterName, parameterType, (Number) parameterValue, readOnly);
		}
		else if (parameterType.equals(String.class))
		{
			return new StringParameterPanel(parameterName, (String) parameterValue, readOnly);
		}
		else if (parameterType.equals(Boolean.class))
		{
			return new BooleanParameterPanel(parameterName, (Boolean) parameterValue, readOnly);
		}
		
		throw new RuntimeException("Unknown Parameter Type: " + parameterType.getCanonicalName());
	}
}
