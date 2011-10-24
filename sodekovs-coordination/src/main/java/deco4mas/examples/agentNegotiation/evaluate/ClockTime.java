package deco4mas.examples.agentNegotiation.evaluate;

import jadex.bridge.service.types.clock.IClockService;

public class ClockTime {
	private static Long startTime = null;

	public static Long getStartTime(IClockService cs) {
		if (startTime == null)
			startTime = cs.getTime();
		return startTime;
	}
}
