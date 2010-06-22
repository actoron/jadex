package deco4mas.examples.agentNegotiation.evaluate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ValueLogger
{
	private static Map<String, List<Double>> values = new HashMap<String, List<Double>>();

	public synchronized static void addValue(String name, Double value)
	{
		if (!values.containsKey(name))
			values.put(name, new LinkedList<Double>());
		List<Double> list = values.get(name);
		list.add(value);
	}

	public static void log()
	{
		Logger logger = AgentLogger.getTimeEvent("ValueLog");
		for (Iterator iterator = values.keySet().iterator(); iterator.hasNext();)
		{
			Double sum = 0.0;
			Double mean;
			String name = (String) iterator.next();
			List<Double> list = values.get(name);
			for (Double value : list)
			{
				sum += value;
			}
			mean = sum / list.size();
			String info = name + " " + sum + "(" + mean + ")";
			logger.info(info);
		}
	}
}
