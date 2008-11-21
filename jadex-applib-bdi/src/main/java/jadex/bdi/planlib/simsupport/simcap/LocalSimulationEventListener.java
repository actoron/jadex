package jadex.bdi.planlib.simsupport.simcap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IInternalEvent;

/** A local ISimObjectStateListener.
 */
public class LocalSimulationEventListener implements ISimulationEventListener
{
	/** Access to the listening agent.
	 */
	private IExternalAccess agent_;
	
	/** Creates a new LocalSimObjectStateListener.
	 * 
	 * @param agent external access for triggering agent events.
	 */
	public LocalSimulationEventListener(IExternalAccess agent)
	{
		agent_ = agent;
	}
	
	public void simulationEvent(final SimulationEvent event)
	{
		agent_.invokeLater(new Runnable()
		{
			public void run()
			{
				IInternalEvent simEvent = 
					agent_.createInternalEvent("simulation_event");
				simEvent.getParameter("type").setValue(event.getType());
				if (event.hasParameters())
				{
					Set parameters = event.getParameters();
					for (Iterator it = parameters.iterator(); it.hasNext(); )
					{
						Map.Entry parameter = (Map.Entry) it.next();
						String name = (String) parameter.getKey();
						Object value = parameter.getValue();
						simEvent.getParameter(name).setValue(value);
					}
				}
				agent_.dispatchInternalEvent(simEvent);
			}
		});
	}
}
