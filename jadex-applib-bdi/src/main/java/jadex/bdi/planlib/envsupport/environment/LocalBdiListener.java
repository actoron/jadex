package jadex.bdi.planlib.envsupport.environment;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.adapter.base.envsupport.environment.IObjectListener;
import jadex.adapter.base.envsupport.environment.ObjectEvent;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IInternalEvent;

public class LocalBdiListener implements IObjectListener
{
	public static final String EnvObjEventType = "envsupport_obj_event";
	
	private IExternalAccess agent;
	
	public LocalBdiListener(IExternalAccess agent)
	{
		this.agent = agent;
	}
	
	public void dispatchObjectEvent(final ObjectEvent event)
	{
		agent.invokeLater(new Runnable()
		{
			public void run()
			{
				IInternalEvent iEvent = agent.createInternalEvent(EnvObjEventType);
				Set parameters = event.getParameters();
				for (Iterator it = parameters.iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Entry) it.next();
					iEvent.getParameter((String) entry.getKey()).setValue(entry.getValue());
				}
				
				agent.dispatchInternalEvent(iEvent);
			}
		});
	}
}
