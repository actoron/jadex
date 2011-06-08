package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer.trustOwner;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

import java.util.Iterator;
import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.trustInformation.TrustExecutionInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ServiceAgentHistory;

/**
 * adapt the trust
 */
public class TrustAdaptPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			IInternalEvent event = (IInternalEvent) getReason();
			TrustExecutionInformation exe = ((TrustExecutionInformation) event.getParameter("information").getValue());

			// LOG
			System.out.println(getComponentName() + ": Trust for " + exe);
			smaLogger.info("Adapt Trust: " + exe);

			// more/less trust to Sa
			ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
			smaLogger.info("history: "+ exe);
			history.addEvent(exe.getSa().getLocalName(), getClock().getTime(), exe.getEvent());
			ValueLogger.addValue(exe.getEvent() + "_" + exe.getSa(), 1.0);
			((HistorytimeTrustFunction) getBeliefbase().getBelief("trustFunction").getFact()).logTrust(getTime());
			
			//Hack for this special Negotation.application.xml
			AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("mycoordspace"));
			HistorytimeTrustFunction trustFunction = (HistorytimeTrustFunction) getBeliefbase().getBelief("trustFunction").getFact();
			Iterator<String> it = trustFunction.getHistory().getSas().iterator();
			IClockService clock = (IClockService)SServiceProvider.getServiceUpwards(space.getContext().getServiceContainer(), IClockService.class).get(this);
			while (it.hasNext()) {				
				String saId = it.next();
				double trust = trustFunction.getTrust(saId, clock.getTime());				
				//substring: geht the "right" part of the id -> only the type: billig, normal, teuer
				String keyOfSA = saId.substring(saId.indexOf("(")+1, saId.lastIndexOf(")"));
				keyOfSA = keyOfSA.replace("-", "");																
				space.getSpaceObjectsByType("KIVSeval")[0].setProperty(keyOfSA + "TrustValue", trust);				
			}

//			trustFunction.getTrust(serviceProposal.getOwner().getLocalName(), ((IClockService)space.getContext().getServiceContainer().getService(IClockService.class)).getTime());
			
			
//			final IClockService clockservice = (IClockService) container.getService(IClockService.class);
			
//			bid.put("trust", trustFunction.getTrust(serviceProposal.getOwner().getLocalName(), thetime));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
