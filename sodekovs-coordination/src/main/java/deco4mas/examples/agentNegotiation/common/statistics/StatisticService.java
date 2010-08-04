package deco4mas.examples.agentNegotiation.common.statistics;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.TimeEventFormatter;

public class StatisticService
{
	static Map<String, IStatistic> statistics = Collections.synchronizedMap(new HashMap<String, IStatistic>());
	
	public synchronized static void serializeData()
	{
//		for (Map.Entry<String, IStatistic> statistic : statistics.entrySet())
//		{
//				try
//				{
//					String dir = System.getProperty("user.home") + "\\agentLogs\\statistics";
//					File f = new File(dir);
//					if (!f.isDirectory())
//						f.mkdir();
//					File statisticFile = 
////					fh = new FileHandler("%h/agentLogs/" + name + ".log", true);
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//		}
		
	}
	
	public synchronized static void clear()
	{
		statistics = Collections.synchronizedMap(new HashMap<String, IStatistic>());
	}
	
	public synchronized static void register(String name, IStatistic statistic)
	{
		statistics.put(name, statistic);
	}
	
	public synchronized static void getStatistic(String name)
	{
		statistics.get(name);
	}
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args)
	{
		BasicSummary summary = new BasicSummary();
		summary.addValue(10.0);
		summary.addValue(30.0);
		summary.addValue(20.0);
		summary.addValue(5.0);
		summary.addValue(15.0);
		summary.addValue(11.0);
		summary.addValue(12.0);
		System.out.println(summary);
	}
}
