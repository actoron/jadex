package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.Plan;
import java.util.Random;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ClockTime;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.sa.masterSa.AgentType;

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

				AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();
				Double blackoutCharakter = agentType.getBlackoutCharacter();

				// use a expotential distribution
				Double nextrnd = rnd.nextDouble();
				Double mean = 36000 - (10000 * (blackoutCharakter * 3));
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

				Double waitTime = 1000 + 500 * (blackoutCharakter * 4);
				saLogger.info("blackout for " + waitTime);
				System.out.println(this.getComponentName() + ": Blackout for " + waitTime);

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
