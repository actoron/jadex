package deco4mas.examples.agentNegotiation.evaluate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ParameterLogger extends Logger
{
	public ParameterLogger(String name, String resourceBundleName)
	{
		super(name, resourceBundleName);
	}

	public void gnuInfo(Object[] parameter, String msg)
	{
		log(Level.INFO, msg, parameter);
	}
}
