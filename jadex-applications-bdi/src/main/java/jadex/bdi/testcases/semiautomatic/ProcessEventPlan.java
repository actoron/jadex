package jadex.bdi;

import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.Plan;

public class ProcessEventPlan extends Plan
{
	public void body()
	{
		System.out.println("a: "+((IParameterElement)getReason()).getParameter("event").getValue());
		System.out.println("b: "+getParameter("event").getValue());
	}
}
