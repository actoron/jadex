package jadex.distributed.service.monitor;


import java.net.InetSocketAddress;
import java.util.Map;

public interface IMonitorServiceListener {
	
	/**
	 * Called by the IMonitorService to update the current workload
	 * information of a single machine.
	 * 
	 * WARNING: the purpose of this method is not only to update current
	 * information, but also to publish the current workload of a newly
	 * discovered machine. So it must be checked if the <code>machine</code>
	 * is in the list of known machines or not.
	 * 
	 * @param machine - the (possibly new) machine
	 * @param workload - the current workload of the (possibly new) machine
	 */
	public void updateWorkloadSingle(InetSocketAddress machine, Workload workload);
	
	/**
	 * Called by the IMonitorService to update the current workload information
	 * of every currently known machine.
	 * 
	 * This method has the purpose of a bootstrap method give the listener a
	 * initial list of currently known machines and the workload of those
	 * machines.
	 * 
	 * @param machineWorkloads - map collection of the currently known machines
	 *                           with their current workloads
	 */
	public void updateWorkloadAll(Map<InetSocketAddress, Workload> machineWorkloads);
	
	/**
	 * A currently known machine may is not available anymore. In that case the
	 * IMonitorService needs to inform the listener about this.
	 * @param machine - the machine which is no longer available
	 * @param workload TODO
	 */
	public void removeWorkloadSingle(InetSocketAddress machine, Workload workload);
	
}