package jadex.distributed.jmx;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

/**
 * Wrapper class to make useful features of the javasysmon library available
 * as a MBean and to retrieve useful information from it.
 * @author daniel
 */
public class SysMon implements SysMonMBean {
	
	private JavaSysMon _javasysmon;
	private CpuTimes _previous;
	
	public SysMon() {
		_javasysmon = new JavaSysMon();
		_previous = _javasysmon.cpuTimes();
	}
	
	@Override
	public float getCpuUsage() {
		CpuTimes current = _javasysmon.cpuTimes();
		float usage = current.getCpuUsage(_previous);
		_previous = current;
		return usage;
	}
	
	
	
}
