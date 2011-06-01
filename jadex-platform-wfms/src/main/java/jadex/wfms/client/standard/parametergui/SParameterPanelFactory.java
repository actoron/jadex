package jadex.wfms.client.standard.parametergui;

import jadex.commons.SReflect;
import jadex.wfms.parametertypes.Document;
import jadex.wfms.parametertypes.ListChoice;
import jadex.wfms.parametertypes.MultiListChoice;
import jadex.wfms.parametertypes.Text;

import java.util.Map;

public class SParameterPanelFactory
{
	public static final AbstractParameterPanel createParameterPanel(String parameterName, Class parameterType, Object parameterValue, Map metaProperties, boolean readOnly)
	{
		if (parameterType.isPrimitive())
			parameterType = SReflect.getWrappedType(parameterType);
		if (SReflect.isSupertype(Number.class, parameterType))
		{
			return new NumericParameterPanel(parameterName, parameterType, (Number) parameterValue, readOnly);
		}
		else if (parameterType.equals(String.class))
		{
			if ((readOnly) && (metaProperties != null) && (!Boolean.TRUE.equals(metaProperties.get("string_panel"))))
				return new TextParameterPanel(parameterName, new Text((String) parameterValue), metaProperties, readOnly);
			return new StringParameterPanel(parameterName, (String) parameterValue, readOnly);
		}
		else if (parameterType.equals(String[].class))
		{
			return new StringArrayParameterPanel(parameterName, (String[]) parameterValue, metaProperties, readOnly); 
		}
		else if (parameterType.equals(Boolean.class))
		{
			return new BooleanParameterPanel(parameterName, (Boolean) parameterValue, readOnly);
		}
		else if (parameterType.equals(Document.class))
		{
			return new DocumentParameterPanel(parameterName, (Document) parameterValue, metaProperties, readOnly);
		}
		else if (parameterType.equals(Text.class))
		{
			return new TextParameterPanel(parameterName, (Text) parameterValue, metaProperties, readOnly);
		}
		else if (parameterType.equals(ListChoice.class))
		{
			return new ListChoiceParameterPanel(parameterName, (ListChoice) parameterValue, readOnly);
		}
		else if (parameterType.equals(MultiListChoice.class))
		{
			return new MultiListChoiceParameterPanel(parameterName, (MultiListChoice) parameterValue, readOnly);
		}
		
		throw new RuntimeException("GUI is unable to interpret parameter [" + parameterName + "] type: " + parameterType.getCanonicalName());
	}
}
