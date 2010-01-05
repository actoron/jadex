package jadex.bdi.wfms.client.basic;

import jadex.commons.SGUI;
import jadex.commons.SReflect;

import javax.swing.JPanel;

public class SParameterPanelFactory
{
	public static final AbstractParameterPanel createParameterPanel(String parameterName, Class parameterType, Object parameterValue)
	{
		if (parameterType.isPrimitive())
			parameterType = SReflect.getWrappedType(parameterType);
		if (SReflect.isSupertype(Number.class, parameterType))
		{
			return new NumericParameterPanel(parameterName, parameterType, (Number) parameterValue);
		}
		
		throw new RuntimeException("Unknown Parameter Type: " + parameterType.getCanonicalName());
	}
}
