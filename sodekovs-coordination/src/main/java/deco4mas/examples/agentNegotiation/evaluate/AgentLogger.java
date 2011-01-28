package deco4mas.examples.agentNegotiation.evaluate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class AgentLogger
{
	private static Map<String, Logger> loggers = new HashMap<String, Logger>();
	private static List<String> saOrder = new LinkedList<String>();
	private static boolean log = false;

	public synchronized static Logger getTimeEvent(String name)
	{
		Logger agentLogger;

		if (!loggers.containsKey(name))
		{
			agentLogger = Logger.getLogger(name);
			FileHandler fh = null;

			try
			{
				String dir = System.getProperty("user.home") + "\\agentLogs";
				File f = new File(dir);
				if (!f.isDirectory())
					f.mkdir();
				fh = new FileHandler("%h/agentLogs/" + name + ".log", true);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			fh.setFormatter(new TimeEventFormatter());
			agentLogger.addHandler(fh);
			agentLogger.setLevel(Level.ALL);
			agentLogger.setUseParentHandlers(false);

			agentLogger.info("new");
			agentLogger.info("component start");
			loggers.put(name, agentLogger);
		} else
		{
			agentLogger = loggers.get(name);
		}
		if (!log)
		{
			agentLogger.setLevel(Level.OFF);
		}

		return agentLogger;
	}

	public synchronized static Logger getTimeDiffEventForSa(String name)
	{
		Logger agentLogger;

		if (!loggers.containsKey(name))
		{
			LogManager manager = LogManager.getLogManager();
			manager.addLogger(new ParameterLogger(name, null));
			agentLogger = (ParameterLogger) Logger.getLogger(name);

			FileHandler fh = null;
			try
			{
				String dir = System.getProperty("user.home") + "\\agentLogs\\plotLogs";
				File f = new File(dir);
				if (!f.isDirectory())
					f.mkdir();
				fh = new FileHandler("%h/agentLogs/plotLogs/" + name + ".dat", false);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			fh.setFormatter(new TimeDiffEventForSaFormatter());
			agentLogger.addHandler(fh);
			agentLogger.setLevel(Level.ALL);
			agentLogger.setUseParentHandlers(false);
			loggers.put(name, agentLogger);
			agentLogger.info("titles");
		} else
		{
			agentLogger = (ParameterLogger) loggers.get(name);
		}
		if (!log)
		{
			agentLogger.setLevel(Level.OFF);
		}

		return agentLogger;
	}

	public synchronized static Logger getDataTable(String name, Boolean append)
	{
		Logger agentLogger;

		if (!loggers.containsKey(name))
		{
			agentLogger = Logger.getLogger(name);
			FileHandler fh = null;

			try
			{
				String dir = System.getProperty("user.home") + "\\agentLogs\\plotLogs";
				File f = new File(dir);
				if (!f.isDirectory())
					f.mkdir();
				fh = new FileHandler("%h/agentLogs/plotLogs/" + name + ".log", append);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			fh.setFormatter(new EventFormatter());
			agentLogger.addHandler(fh);
			agentLogger.setLevel(Level.ALL);
			agentLogger.setUseParentHandlers(false);
			loggers.put(name, agentLogger);
		} else
		{
			agentLogger = loggers.get(name);
		}
		if (!log)
		{
			agentLogger.setLevel(Level.OFF);
		}

		return agentLogger;
	}

	public static void addSa(String sa)
	{
		saOrder.add(sa);
	}

	public static Integer getNumber(String sa)
	{
		return saOrder.indexOf(sa);
	}

	public static List<String> getAllSas()
	{
		return saOrder;
	}

}
