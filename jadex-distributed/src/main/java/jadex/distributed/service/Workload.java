package jadex.distributed.service;

/**
 * A class representing the current workload of a machine
 * @author daniel
 *
 */
public class Workload {
	// for now the workload of a machine is only determined by the cpu and ram usage; in the near future this may change
	private byte cpuUsage;
	private byte ramUsage;
	
	public Workload(byte cpuUsage, byte ramUsage) {
		super(); // not necessary because following the Java specification the no-argument constructor of the parent class is always inserted by the compiler if missing, see also http://forums.sun.com/thread.jspa?threadID=5209503; but it is also not bad that eclipse automatically inserted the super(); line, so I stick with it here ^_^
		this.cpuUsage = cpuUsage;
		this.ramUsage = ramUsage;
	}

	public Workload() {
		this((byte)0, (byte)0);
	}
	
}
