package jadex.wfms.bdi.client.standard;

import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.wfms.bdi.client.standard.parametergui.AbstractParameterPanel;
import jadex.wfms.bdi.client.standard.parametergui.NumericParameterPanel;
import jadex.wfms.bdi.client.standard.parametergui.StringParameterPanel;

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
		
		throw new RuntimeException("Unknown Parameter Type: " + parameterType.getCanonicalName());
	}
}
