package jadex.distributed.jmx;

public interface SysMonMBean {
	/**
	 * Convenient method to get the cpu usage. The cpu usage is usually
	 * calculated by dividing work cycles by a time frame, e.g. 5 minutes. This
	 * method uses a dynamic time frame in the sense that the time frame is
	 * determined when the method was called the last time.
	 * @return float representing the current cpu usage between 0 and 1.
	 *         0 represents 0%, 1 represents a cpu usage of 100%.
	 */
	public float getCpuUsage();
}
