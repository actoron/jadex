package jadex.wfms.simulation.stateset;

import jadex.wfms.parametertypes.Text;

public class TextStateSet extends StringStateSet
{
	public TextStateSet(String parameterName)
	{
		super(parameterName);
	}
	
	public Object getState(long index)
	{
		return new Text((String) super.getState(index));
	}
}
