package jadex.distributed.service;

/**
 * A class representing the current workload of a machine
 * 
 * @author daniel
 * 
 */
public class Workload {

	// for now the workload of a machine is only determined by the cpu and ram usage; in the near future this may change
	private int cpuUsage;
	private int ramUsage;

	/**
	 * @param cpuUsage - int in range 0..100 representing the cpu load
	 * @param ramUsage - int in range 0..100 representing the ram load
	 */
	public Workload(int cpuUsage, int ramUsage) {
		super(); // not necessary because following the Java specification the no-argument constructor of the parent class is always inserted by the compiler if missing, see also
					// http://forums.sun.com/thread.jspa?threadID=5209503; but it is also not bad that eclipse automatically inserted the super(); line, so I stick with it here ^_^
		
		// take care that the given parameters are not out of range
		if(cpuUsage < 0)
			this.cpuUsage = 0;
		else if (cpuUsage > 100)
			this.cpuUsage = 100;
		else
			this.cpuUsage = cpuUsage;
		
		if(ramUsage < 0)
			this.ramUsage = 0;
		else if (ramUsage > 100)
			this.ramUsage = 100;
		else
			this.ramUsage = ramUsage;
	}

	// should never be used, but it is always good to have a standard non-arg constructor for those stupid component-oriented frameworks
	public Workload() {
		this(0, 0);
	}

	
	public int getCpuUsage() {
		return cpuUsage;
	}

	public int getRamUsage() {
		return ramUsage;
	}

}
