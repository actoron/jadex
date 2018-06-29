package jadex.commons;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;


/**
 *  Reflective facade for the sun class OperatingSystemMXBeanFacade.
 */
public class OperatingSystemMXBeanFacade
{
	protected static Object	bean;

	protected static Map<String, Method> methods;

	static
	{
		try
		{
			MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
			Class<?> cl = Class.forName("com.sun.management.OperatingSystemMXBean");
			bean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, cl);
		}
		catch(Exception e)
		{
		}
	}

	public static long getCommittedVirtualMemorySize()
	{
		Object ret = invokeMethod("getCommittedVirtualMemorySize");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static long getTotalSwapSpaceSize()
	{
		Object ret = invokeMethod("getTotalSwapSpaceSize");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static long getFreeSwapSpaceSize()
	{
		Object ret = invokeMethod("getFreeSwapSpaceSize");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static long getProcessCpuTime()
	{
		Object ret = invokeMethod("getProcessCpuTime");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static long getFreePhysicalMemorySize()
	{
		Object ret = invokeMethod("getFreePhysicalMemorySize");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static long getTotalPhysicalMemorySize()
	{
		Object ret = invokeMethod("getTotalPhysicalMemorySize");
		return ret!=null? ((Long)ret).longValue(): -1;
	}

	public static double getSystemCpuLoad()
	{
		Object ret = invokeMethod("getSystemCpuLoad");
		return ret!=null? ((Double)ret).doubleValue(): -1;
	}

	public double getProcessCpuLoad()
	{
		Object ret = invokeMethod("getProcessCpuLoad");
		return ret!=null? ((Double)ret).doubleValue(): -1;
	}

	/**
	 *  Invoke a method.
	 */
	protected static Object invokeMethod(String name)
	{
		if(bean==null)
			throw new RuntimeException("Management bean not available.");
		
		Object ret = null;
		try
		{
			if(methods==null)
			{
				methods = new HashMap<String, Method>();
			}
			Method m = methods.get(name);
			if(m==null)
			{
				m = bean.getClass().getMethod(name, new Class[0]);
				ret = m.invoke(bean, new Object[0]);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException();
		}
		return ret;
	}
}
