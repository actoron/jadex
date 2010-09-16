package deco4mas.examples.agentNegotiation.sa.application;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.Random;
import java.util.logging.Logger;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentType;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;

/**
 * Sometimes produce Blackouts agentType.getBlackoutCharakter definite frequenz
 * and duration
 */
public class BlackoutPlan extends Plan
{
	private Random rnd = new Random();
	private ParameterLogger blackoutLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("Blackout_"
		+ ((ServiceType) this.getBeliefbase().getBelief("providedService").getFact()).getName());

	public void body()
	{
		try
		{
			Object[] param = new Object[3];
			param[0] = ClockTime.getStartTime(getClock());
			param[1] = getTime();
			param[2] = this.getComponentIdentifier().getLocalName();
			Double start = rnd.nextDouble();

			while (true)
			{
				Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

				ServiceAgentType agentType = (ServiceAgentType) getBeliefbase().getBelief("serviceAgentType").getFact();
				Double blackoutCharakter = agentType.getBlackoutCharacter();

				// use a expotential distribution
				Double nextrnd = rnd.nextDouble();
				Double mean = 34000 - (10000 * (blackoutCharakter * 3));
				Double nextblackout = -java.lang.Math.log(1 - nextrnd) * mean;

				// better Start
				if (start != 0.0)
				{
					nextblackout = nextblackout * start;
					start = 0.0;
				}

				waitFor(nextblackout.longValue());

				getBeliefbase().getBelief("blackout").setFact(Boolean.TRUE);

				param[1] = getTime();
				blackoutLogger.gnuInfo(param, "");

				Double waitTime = 1000 + 500 * (blackoutCharakter * 3);
				saLogger.info("blackout for " + waitTime);
				System.out.println(this.getComponentName() + ": Blackout for " + waitTime);


				//HACK: For evaluation via automated simulation component.
				//*************************************************************
				// This is a hack for this special application.xml -> AgentNegotiation
				//: save result also to space in order to enable evaluation by automated simulation component
				AbstractEnvironmentSpace space = ((AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("mycoordspace"));
				//substring: geht the "right" part of the id -> only the type: billig, normal, teuer
				String timeConstant = "BlackoutTIME";
				String numberConstant = "BlackoutNUMBER";
				IComponentIdentifier currentSa = this.getComponentIdentifier();
				String keyOfSA = currentSa.getLocalName().substring(currentSa.getLocalName().indexOf("(")+1, currentSa.getLocalName().lastIndexOf(")"));
				keyOfSA = keyOfSA.replace("-", "");						
				//increment blackout counter -> counts the number of blackouts
				int numberOfBlackouts = (Integer) space.getSpaceObjectsByType("KIVSeval")[0].getProperty(keyOfSA+numberConstant);						
				space.getSpaceObjectsByType("KIVSeval")[0].setProperty(keyOfSA+numberConstant, numberOfBlackouts+1);
				//increment blackout time counter -> counts the whole duration (time) of blackouts
				double timeOfBlackouts = (Double) space.getSpaceObjectsByType("KIVSeval")[0].getProperty(keyOfSA+timeConstant);						
				space.getSpaceObjectsByType("KIVSeval")[0].setProperty(keyOfSA+timeConstant, timeOfBlackouts+waitTime);										
				//*******************************************************************************
				//************************************************************************
				waitFor(waitTime.longValue());

				param[1] = getTime();
				blackoutLogger.gnuInfo(param, "");
				param[1] = getTime() + 500L;
				blackoutLogger.gnuInfo(param, "NA");

				getBeliefbase().getBelief("blackout").setFact(Boolean.FALSE);
				System.out.println(this.getComponentName() + ": Blackout end!");
				saLogger.info("blackout end");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
