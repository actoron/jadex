package jadex.distributed.service.monitor;

import java.net.InetAddress;

/**
 * A PlatformInfo object describes the current status of a platform, characterized by its IP address and operating system name.
 * Both, a MonitorService and the DistributionMonitorPlugin, use a PlatformInfo object together to. The MonitorService updates
 * the fields of the object with current data, while the DistributionMonitorPlugin reads those data. The MonitorService notifies
 * the DistributionMonitorPlugin when the values of the fields changed. Unfortunately there is no way to determine the caller of
 * method call in Java. But the object needs both, getter and setter methods. A solution to this would be the use of an
 * intermediate snapshot, read-only object. This is a TODO 
 * @author daniel
 *
 */
public class PlatformInfo {

	private InetAddress _ip;
	private double _cpuLoad;
	private int _processors; // how many processors available: 1, 2, ...; unbelievable, but can change at runtime: http://java.sun.com/javase/6/docs/api/java/lang/management/OperatingSystemMXBean.html#getAvailableProcessors()
	private long _heapCommited;
	private long _heapUsed;
	private String _name; // Linux, Windows, ...
	
	/**
	 * Create a new object to represent the current status of a platform. A
	 * platform is characterized by its IP address and operating system.
	 * @param ip the IP of the platform
	 * @param name operating system name of the platform, e.g. 'Linux'
	 */
	public PlatformInfo(InetAddress ip, String name) {
		_ip = ip;
		_name = name;
		_cpuLoad = 0;
		_processors = 0;
		_heapCommited = 0;
		_heapUsed = 0;
		
	}

	/**
	 * Returns  0.0 and 1.0
	 * @return
	 */
	public double getCpuLoad() {
		return _cpuLoad;
	}

	/**
	 * Set the current cpu load of the platform represented by this
	 * PlatformInfo object.
	 * @param cpuLoad current cpu load between 0.0 and 1.0
	 */
	public void setCpuLoad(double cpuLoad) {
		_cpuLoad = cpuLoad;
	}

	/**
	 * Returns the (current) maximum size of the heap in byte. For example
	 * 345212346 means that 345 212 346 Bytes = 337 121 KiB = 329 MiB are free.
	 * @return
	 */
	public long getHeapCommited() {
		return _heapCommited;
	}

	public void setHeapCommited(long heapCommited) {
		_heapCommited = heapCommited;
	}

	/**
	 * Usage of the heap memory in byte. For example 1342345 means that
	 * 1 342 345 Bytes = 1310 KiB = 1.28 MiB is used.
	 * @return
	 */
	public long getHeapUsed() {
		return _heapUsed;
	}
	
	/**
	 * Convenient method to return the current heap usage. The range is 0..100.
	 * For example 34 means that 34% of the heap is used, 66% is free.
	 * @return current heap usage in percent
	 */
	public int getHeapUsage() {
		int usage;
		if(_heapCommited == 0) // unusual but may possible in some cases
			usage = 0;
		else
			usage = (int) (_heapUsed / _heapCommited);
		
		if( usage < 0) // very inlikely, but possible
			usage = 0;
		/*if(usage > 100) // impossible 
			usage = 100;*/
		return usage;
	}

	public void setHeapUsed(long heapUsed) {
		_heapUsed = heapUsed;
	}

	public InetAddress getIp() {
		return _ip;
	}

	public int getProcessors() {
		return _processors;
	}

	public void setProcessors(int processors) {
		_processors = processors;
	}
	
	public String getName() {
		return _name;
	}	
}
